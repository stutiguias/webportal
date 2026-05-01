WebPortalVue3.mountApp({
    el: '#app',
    data() {
        return {
            from: 0,
            qtd: 10,
            dialogCreateSale: false,
            dialogSendMail: false,
            items: [],
            headers: [],
            sessionid: this.getCookie("sessionid"),
            user: '',
            money: '',
            mail: '',
            avatarUrl: 'https://minotar.net/avatar/',
            isAdmin: false,
            itemNames: [],
            formData: {
                ID: '',
                quantity: '',
                price: ''
            },
            formResult: '',
            resultado: '',
            isLoading: false,
            isSubmittingSale: false,
            isSubmittingMail: false,
        };
    },
    computed: {
        inventoryCount() {
            return this.items.length;
        },
        hasItems() {
            return this.inventoryCount > 0;
        },
        selectedItemLabel() {
            const active = this.itemNames.find(item => String(item.value) === String(this.formData.ID));
            return active ? active.text : '';
        },
        formResultType() {
            return this.isErrorMessage(this.formResult) ? 'error' : 'success';
        },
        loadResultType() {
            return this.isErrorMessage(this.resultado) ? 'error' : 'info';
        },
    },
    methods: {
        stripHtml(value) {
            return String(value || '').replace(/<[^>]*>/g, '').trim();
        },
        processData() {
            this.itemNames = this.items.map(item => ({
                text: this.stripHtml(item.item_name) || `Item ${item.id}`,
                value: item.id
            }));
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
            .catch(() => {
                this.user = "Error loading data";
            });
        },
        translate(key) {
            return window.langIndex[key] || key;
        },
        isErrorMessage(message) {
            return /(error|invalid|erro|falha|failed)/i.test(message || '');
        },
        resetForm() {
            this.formData.ID = '';
            this.formData.quantity = '';
            this.formData.price = '';
        },
        openCreateSale() {
            this.formResult = '';
            this.dialogCreateSale = true;
            this.dialogSendMail = false;
        },
        openSendMail() {
            this.formResult = '';
            this.dialogSendMail = true;
            this.dialogCreateSale = false;
        },
        closeCreateSale() {
            this.dialogCreateSale = false;
            this.resetForm();
        },
        closeSendMail() {
            this.dialogSendMail = false;
            this.resetForm();
        },
        submitSale() {
            if (!this.formData.ID || !this.formData.quantity || !this.formData.price) {
                this.formResult = 'Select an item, quantity and price before creating a sale.';
                return;
            }

            const params = new URLSearchParams({
                ID: this.formData.ID,
                Quantity: this.formData.quantity,
                Price: this.formData.price,
                sessionid: this.getCookie("sessionid")
            }).toString();

            const url = window.qualifyURL(`/myitems/postauction?${params}`);
            this.formResult = '';
            this.isSubmittingSale = true;

            return fetch(url, {
                method: 'GET',
            })
            .then(response => response.text())
            .then(data => {
                this.formResult = data;
                this.closeCreateSale();
                return this.getMyItens(this.from, this.qtd);
            })
            .catch(error => {
                this.formResult = "Error: " + error;
            })
            .finally(() => {
                this.isSubmittingSale = false;
            });
        },
        sendMail() {
            if (!this.formData.ID || !this.formData.quantity) {
                this.formResult = 'Select an item and quantity before sending mail.';
                return;
            }

            const params = new URLSearchParams({
                ID: this.formData.ID,
                Quantity: this.formData.quantity,
                Price: this.formData.price,
                sessionid: this.getCookie("sessionid")
            }).toString();

            const url = window.qualifyURL(`/mail/send?${params}`);
            this.formResult = '';
            this.isSubmittingMail = true;

            return fetch(url, {
                method: 'GET',
            })
            .then(response => response.text())
            .then(data => {
                this.formResult = data;
                this.closeSendMail();
                return this.getMyItens(this.from, this.qtd);
            })
            .catch(error => {
                this.formResult = "Error: " + error;
            })
            .finally(() => {
                this.isSubmittingMail = false;
            });
        },
        refreshItems() {
            return this.getMyItens(this.from, this.qtd);
        },
        getMyItens(from = this.from, qtd = this.qtd) {
            this.from = from;
            this.qtd = qtd;
            this.resultado = '';
            this.isLoading = true;

            return fetch(window.qualifyURL(`/myitems/dataTable?from=${this.from}&qtd=${this.qtd}&sessionid=${this.sessionid}`))
                .then(response => {
                if (!response.ok) {
                    throw new Error('Erro na rede ou resposta não OK');
                }

                return response.json();
                })
                .then(data => {
                    this.loadTable(data, from, qtd);
                    this.processData();
                })
                .catch(error => {
                    this.headers = [];
                    this.items = [];
                    this.itemNames = [];
                    this.resultado = error.message || 'Erro desconhecido';
                })
                .finally(() => {
                    this.isLoading = false;
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
    },
    mounted() {
        this.refreshItems();
        this.getUserInfo();
    }
});
