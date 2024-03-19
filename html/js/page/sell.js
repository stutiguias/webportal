new Vue({
    el: '#app',
    vuetify: new Vuetify(),
    data() {
        return {
            from: 0,
            qtd: 10,
            user: '',
            money: '',
            mail: '',
            isAdmin: '',
            avatarUrl: '',
            cancelId: '',
            formResult: '',
            resultado: '',
            sessionid: this.getCookie("sessionid"),
        };
    },
    methods: {
        remItem() {
            const params = new URLSearchParams({
                ID: this.cancelId,
                sessionid: this.getCookie("sessionid")
            }).toString();

            const url = window.qualifyURL(`/myauctions/cancel?${params}`);

            fetch(url, {
                method: 'GET',
            })
            .then(response => response.text())
            .then(data => {
                this.formResult = data;
            })
            .catch(error => {
                this.formResult = "Invalid id " + error;
            });
            this.dialogCreateSale = false;
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
            fetch(window.qualifyURL("/server/username/info?sessionid=" + this.sessionid))
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
        translate(key) {
            return window.langIndex[key] || key;
        },
        getMyItens(from, qtd) {
            fetch(window.qualifyURL(`/myauctions/get?from=${this.from}&qtd=${this.qtd}&sessionid=${this.sessionid}`))
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
                    this.resultado = error.message || 'Erro desconhecido';
                });
        },
        loadTable(data, from, qtd) {
            if(data[0] != null) return;
            const firstKey = Object.keys(data).find(key => data[key] instanceof Array && data[key].length > 0);

            this.headers = Object.values(data[firstKey][0]).map(field => ({
                text: field.Title,
                value: field.Title.toLowerCase().replace(/\s+/g, '_')
            }));
            this.items = data[firstKey].map(item => {
                const newItem = {};
                Object.values(item).forEach(field => {
                    const key = field.Title.toLowerCase().replace(/\s+/g, '_');
                    newItem[key] = field.Val;
                });
                return newItem;
            });
        },
    },
    mounted() {
        this.getMyItens();
        this.getUserInfo();
    }
});