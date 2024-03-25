new Vue({
    el: '#app',
    vuetify: new Vuetify(),
    data: {
        from: 0,
        qtd: 10,
        dialogCreateSale: false,
        dialogSendMail: false,
        items: [], 
        mailItems: [],
        headers: [],
        sessionid: this.getCookie("sessionid"),
        user: '',
        money: '',
        mail: '',
        avatarUrl: 'http://minotar.net/avatar/',
        isAdmin: false,
        itemNames: [],
        formData: {
            ID: '',
            quantity: '',
            price: ''
        },
        formResult: '',
        sessionid: this.getCookie("sessionid"),
formResult: '',
    },
    methods: {
        processData() {
            this.itemNames = this.items.map(item => {
                return {
                        text: item.item_name,
                        value: item.id
                        };
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
        translate(key) {
            return window.langIndex[key] || key;
        },
        submitSale() {
            const params = new URLSearchParams({
                ID: this.formData.ID,
                Quantity: this.formData.quantity,
                Price: this.formData.price,
                sessionid: this.getCookie("sessionid")
            }).toString();

            const url = window.qualifyURL(`/myitems/postauction?${params}`);

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
            this.dialogCreateSale = false;
        },
        sendMail() {
            const params = new URLSearchParams({
                ID: this.formData.ID,
                Quantity: this.formData.quantity,
                Price: this.formData.price,
                sessionid: this.getCookie("sessionid")
            }).toString();

            const url = window.qualifyURL(`/mail/send?${params}`);

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
            this.dialogSendMail = false;
        },
        getMyItens(from, qtd) {
            fetch(window.qualifyURL(`/myitems/dataTable?from=${this.from}&qtd=${this.qtd}&sessionid=${this.sessionid}`))
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