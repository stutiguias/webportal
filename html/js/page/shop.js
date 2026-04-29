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
            formResult: '',
            resultado: '',
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
        };
    },
    computed: {
        activeCategoryLabel() {
            const active = this.categories.find(item => item.filter === this.activeCategory);
            return active ? active.name : (window.langIndex.langAll || 'All');
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

            fetch(url, {
                method: 'GET',
            })
            .then(response => response.text())
            .then(data => {
                this.formResult = data;
            })
            .catch(error => {
                this.formResult = "Error: " + error;
            });
        },
        translate(key) {
            return window.langIndex[key] || key;
        },
        filterCategory(filter) {
            this.activeCategory = filter;
            this.getauction(0, this.qtd, filter);
        },
        getauction(from, qtd, category = this.activeCategory) {
            const endpoint = category === 'all' ? 'byall' : category;

            fetch(window.qualifyURL(`/auction/get/${endpoint}?from=${from}&qtd=${qtd}&sessionid=${this.sessionid}`))
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
                        this.getauction(from, qtd, 'all');
                    }
                });
        },
        loadTable(data) {
            this.headers = [];
            this.items = [];

            if (!data || data[0] != null) return;

            const firstKey = Object.keys(data).find(key => data[key] instanceof Array && data[key].length > 0);
            if (!firstKey) return;

            const upgradeHtml = window.WebPortalItemImageHelper?.upgradeHtml || (value => value);

            this.headers = Object.values(data[firstKey][0]).map(field => ({
                text: field.Title,
                value: field.Title.toLowerCase().replace(/\s+/g, '_')
            }));
            this.items = data[firstKey].map(item => {
                const newItem = {};
                Object.values(item).forEach(field => {
                    const key = field.Title.toLowerCase().replace(/\s+/g, '_');
                    newItem[key] = upgradeHtml(field.Val);
                });
                return newItem;
            });
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
                this.money = data["Money"];
                this.mail = data["Mail"];
                this.isAdmin = data["Admin"].toString() === "1";
                this.avatarUrl = data["Avatarurl"];
            })
            .catch(error => {
                this.user = "Error loading data";
            });
        },
    },
    mounted() {
        this.sessionid = this.getCookie("sessionid");
        this.getUserInfo();
        this.getauction(0, this.qtd, this.activeCategory);
    },
});
