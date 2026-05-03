WebPortalVue3.mountApp({
    el: '#app',
    data() {
        return {
            qtd: 10,
            user: '',
            money: '',
            mail: '',
            avatarUrl: 'https://minotar.net/avatar/',
            isAdmin: false,
            shopId: '',
            shopQuantity: '',
            sessionid: '',
            activeCategory: 'all',
            formData: {
                ID: '',
                quantity: '',
            },
            selectedListingId: '',
            formResult: '',
            resultado: '',
            isSubmittingPurchase: false,
            isLoadingListings: false,
            categories: [
                { name: window.langIndex.langAll || 'All', filter: 'all' },
                { name: window.langIndex.langBlocks || 'Blocks', filter: 'block' },
                { name: window.langIndex.langCombat || 'Combat', filter: 'combat' },
                { name: window.langIndex.langTools || 'Tools', filter: 'tools' },
                { name: window.langIndex.langFood || 'Food', filter: 'food' },
                { name: window.langIndex.langDecoration || 'Decoration', filter: 'decoration' },
                { name: window.langIndex.langMaterials || 'Materials', filter: 'materials' },
                { name: window.langIndex.langOthers || 'Others', filter: 'others' },
            ],
            headers: [],
            items: [],
            itemExpiryHours: 168, // Deve corresponder ao config.yml
        };
    },
    computed: {
        activeCategoryLabel() {
            const active = this.categories.find(item => item.filter === this.activeCategory);
            return active ? active.name : (window.langIndex.langAll || 'All');
        },
        selectedListing() {
            return this.items.find(item => String(item.id) === String(this.selectedListingId)) || null;
        },
        formResultType() {
            return this.isErrorMessage(this.formResult) ? 'error' : 'info';
        }
    },
    methods: {
        shop() {
            if (!this.formData.ID || !this.formData.quantity) {
                this.formResult = 'Inform the item ID and quantity.';
                return;
            }

            const params = new URLSearchParams({
                ID: this.formData.ID,
                quantity: this.formData.quantity,
                sessionid: this.getCookie("sessionid")
            }).toString();

            const url = window.qualifyURL(`/auction/shop?${params}`);
            this.formResult = '';
            this.isSubmittingPurchase = true;

            return fetch(url, {
                method: 'GET',
            })
            .then(response => response.text())
            .then(data => {
                this.formResult = data;
                this.formData.quantity = '';
                return this.getauction(0, this.qtd, this.activeCategory);
            })
            .catch(error => {
                this.formResult = "Error: " + error;
            })
            .finally(() => {
                this.isSubmittingPurchase = false;
            });
        },
        translate(key) {
            return window.langIndex[key] || key;
        },
        isErrorMessage(message) {
            return /(error|invalid|erro|falha|failed)/i.test(message || '');
        },
        selectListing(item) {
            this.selectedListingId = item.id;
            this.formData.ID = item.id;
            this.formData.quantity = '1';
            this.formResult = '';
        },
        clearSelectedListing() {
            this.selectedListingId = '';
            this.formData.ID = '';
            this.formData.quantity = '';
        },
        filterCategory(filter) {
            this.activeCategory = filter;
            this.getauction(0, this.qtd, filter);
        },
        getauction(from, qtd, category = this.activeCategory) {
            const endpoint = category === 'all' ? 'byall' : `by${category}`;
            this.resultado = '';
            this.isLoadingListings = true;

            return fetch(window.qualifyURL(`/auction/get/${endpoint}?from=${from}&qtd=${qtd}&sessionid=${this.sessionid}`))
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network error or invalid response');
                    }

                    return response.json();
                })
                .then(data => {
                    this.loadTable(data);
                })
                .catch(error => {
                    this.resultado = error.message || 'Unknown error';
                    if (category !== 'all') {
                        this.activeCategory = 'all';
                        return this.getauction(from, qtd, 'all');
                    }
                })
                .finally(() => {
                    this.isLoadingListings = false;
                });
        },
        loadTable(data) {
            this.headers = [];
            this.items = [];

            if (!data || data[0] != null) return;

            const firstKey = Object.keys(data).find(key => data[key] instanceof Array && data[key].length > 0);
            if (!firstKey) return;

            const upgradeHtml = window.WebPortalItemImageHelper?.upgradeHtml || (value => value);

            this.headers = Object.values(data[firstKey][0])
                .filter(field => field.Title.toLowerCase() !== 'created')
                .map(field => ({
                    text: field.Title,
                    value: field.Title.toLowerCase().replace(/\s+/g, '_')
                }));
            this.headers.push({ text: 'Quick buy', value: 'actions' });
            this.items = data[firstKey].map(item => {
                const newItem = {};
                Object.values(item).forEach(field => {
                    const key = field.Title.toLowerCase().replace(/\s+/g, '_');
                    newItem[key] = upgradeHtml(field.Val);
                });
                // Extract created timestamp if exists
                if (item.created && item.created.Val) {
                    newItem.created = parseInt(item.created.Val) || 0;
                }
                return newItem;
            });

            if (this.selectedListingId && !this.items.some(item => String(item.id) === String(this.selectedListingId))) {
                this.clearSelectedListing();
            }
        },
        getCookie(szName) {
            var szValue = null;
            if (document.cookie) {
                var arr = document.cookie.split((escape(szName) + '='));
                if (2 <= arr.length) {
                    var arr2 = arr[1].split(';');
                    szValue = unescape(arr2[0]);
                }
            }
            return szValue;
        },
        getUserInfo() {
            fetch(window.qualifyURL("/user/info?sessionid=" + this.sessionid))
            .then(response => response.json())
            .then(data => {
                this.user = data["Name"];
                this.money = parseFloat(data["Money"]).toFixed(2);
                this.mail = data["Mail"];
                this.isAdmin = data["Admin"].toString() === "1";
                this.avatarUrl = data["Avatarurl"];
            })
            .catch(error => {
                this.user = "Error loading data";
            });
        },
        getTimeRemaining(created) {
            if (!created || created === 0) return this.translate('langNever') || 'Never';
            
            const createdTime = created * 1000;
            const expiryTime = createdTime + (this.itemExpiryHours * 3600 * 1000);
            const remaining = expiryTime - Date.now();
            
            if (remaining <= 0) return this.translate('langExpired') || 'Expired';
            
            const days = Math.floor(remaining / (1000 * 60 * 60 * 24));
            const hours = Math.floor((remaining % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
            const minutes = Math.floor((remaining % (1000 * 60 * 60)) / (1000 * 60));
            
            if (days > 0) return `${days}d ${hours}h`;
            if (hours > 0) return `${hours}h ${minutes}m`;
            return `${minutes}m`;
        },
        formatCreatedDate(created) {
            if (!created || created === 0) return 'N/A';
            const date = new Date(created * 1000);
            return date.toLocaleString();
        },
    },
    mounted() {
        this.sessionid = this.getCookie("sessionid");
        this.getUserInfo();
        this.getauction(0, this.qtd, this.activeCategory);
    },
});
