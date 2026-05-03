WebPortalVue3.mountApp({
    el: '#app',
    data() {
        return {
            from: 0,
            qtd: 10,
            user: '',
            money: '',
            mail: '',
            isAdmin: false,
            avatarUrl: 'https://minotar.net/avatar/',
            cancelId: '',
            formResult: '',
            resultado: '',
            headers: [],
            items: [],
            isLoading: false,
            isSubmittingCancel: false,
            sessionid: this.getCookie("sessionid"),
        };
    },
    computed: {
        auctionCount() {
            return this.items.length;
        },
        hasItems() {
            return this.auctionCount > 0;
        },
        formResultType() {
            return this.isErrorMessage(this.formResult) ? 'error' : 'success';
        },
        loadResultType() {
            return this.isErrorMessage(this.resultado) ? 'error' : 'info';
        },
    },
    methods: {
        remItem() {
            if (!this.cancelId) {
                this.formResult = 'Enter the auction ID you want to cancel.';
                return;
            }

            const params = new URLSearchParams({
                ID: this.cancelId,
                sessionid: this.getCookie("sessionid")
            }).toString();

            const url = window.qualifyURL(`/myauctions/cancel?${params}`);
            this.formResult = '';
            this.isSubmittingCancel = true;

            return fetch(url, {
                method: 'GET',
            })
            .then(response => response.text())
            .then(data => {
                this.formResult = data;
                this.cancelId = '';
                return this.getMyItens(this.from, this.qtd);
            })
            .catch(error => {
                this.formResult = "Invalid id " + error;
            })
            .finally(() => {
                this.isSubmittingCancel = false;
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
                this.money = parseFloat(data["Money"]).toFixed(2);
                this.mail = data["Mail"];
                this.isAdmin = data["Admin"].toString() === "1";
                this.avatarUrl = data["Avatarurl"];
            })
            .catch(error => {
                this.user = "Error loading data";
            });
        },
        translate(key) {
            return window.langIndex[key] || key;
        },
        isErrorMessage(message) {
            return /(error|invalid|erro|falha|failed)/i.test(message || '');
        },
        refreshAuctions() {
            return this.getMyItens(this.from, this.qtd);
        },
        getMyItens(from = this.from, qtd = this.qtd) {
            this.from = from;
            this.qtd = qtd;
            this.resultado = '';
            this.isLoading = true;

            return fetch(window.qualifyURL(`/myauctions/get?from=${this.from}&qtd=${this.qtd}&sessionid=${this.sessionid}`))
                .then(response => {
                if (!response.ok) {
                    throw new Error('Erro na rede ou resposta não OK');
                }

                return response.json();
                })
                .then(data => {
                    this.loadTable(data, from, qtd);
                })
                .catch(error => {
                    this.headers = [];
                    this.items = [];
                    this.resultado = error.message || 'Erro desconhecido';
                })
                .finally(() => {
                    this.isLoading = false;
                });
        },
        loadTable(data, from, qtd) {
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
        this.refreshAuctions();
        this.getUserInfo();
    }
});