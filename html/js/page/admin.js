const vue = new Vue({
    el: '#app',
    vuetify: new Vuetify(),
    data: () => ({
        to: 0,
        from: 10,
        showInfoPlayer: false,
        showAddItemShop: false,
        showListAdmShop: false,
        nick: '',
        information: '',
        infoOptions: [
            { text: 'Player Info', value: 'playerinfo' },
            { text: 'Player Items', value: 'playeritems' },
            // Complete with other options
        ],
        itemId: '',
        quantity: '',
        price: '',
        resultado: '',
        tableHeaders: [
            { text: 'Delete', value: 'delete', sortable: false },
            { text: 'Item Name', value: 'item_name' },
            { text: 'Price', value: 'price' },
            { text: 'Quantity', value: 'quantity' }
        ],
        tableItems: [],
        userInfo: [],
        monitor: "",
        resultcmd: "",
        nickname: "",
        msg: "",
        selectedCmd: "Select Command",
        cmdOptions: [
            { text: 'whois', value: 'whois' },
            { text: 'mail', value: 'mail' },
        ]
    }),
    methods: {
        async get(url) {
            try {
                const response = await fetch(window.qualifyURL(url));
                const data = await response.json();
                this.processData(data);
            } catch (error) {
                this.resultado = error;
            }
        },
        async sendEssentialsCmd() {
            if(this.selectedCmd === "mail") url = "/adm/essentials/mail?nickname=" + this.nickname + "&msg=" + this.msg;;
            if(this.selectedCmd === "whois") url = "/adm/essentials/whois?nickname=" + this.nickname;
            const response = await fetch(window.qualifyURL(url + "&sessionid=" + this.getCookie("sessionid")));
            const data = await response.json();
            this.resultcmd = data[0].result;
        },
        async getMonitor() {
            const response = await fetch(window.qualifyURL("/adm/getMonitor?sessionid=" + this.getCookie("sessionid")));
            const data = await response.json();
            this.monitor = data;
        },
        processData(data) {
            this.loadTable(data)
        },
        loadTable(data, from, qtd) {
            if(data[0] != null) return;
            const firstKey = Object.keys(data).find(key => Array.isArray(data[key]) && data[key].length > 0);
            if (firstKey) {
                this.tableItems = data[firstKey].map(item => {
                    return {
                        delete: this.extractIdFromDeleteForm(item.Delete),
                        item_name: item["Item Name"],
                        price: item.Price,
                        quantity: item.Quantity
                    };
                });
            }
        },
        extractIdFromDeleteForm(htmlString) {
            const parser = new DOMParser();
            const doc = parser.parseFromString(htmlString, 'text/html');
            const input = doc.querySelector("input[type='hidden'][name='ID']");
            return input ? input.value : null;
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
        translate(key) {
            return window.langIndex[key] || key;
        },
        listshop() {
            this.resultado = null;
            this.showListAdmShop = true;
            this.showAddItemShop = false;
            this.showInfoPlayer = false;
            this.get(window.qualifyURL("/adm/shoplist?DisplayStart=" + this.to + "&DisplayLength=" + this.from + "&sessionid=" + this.getCookie("sessionid")));
        },
        admshop() {
            this.resultado = null;
            this.showAddItemShop = true;
            this.showListAdmShop = false;
            this.showInfoPlayer = false;

        },
        infoplayer() {
            this.resultado = null;
            this.showInfoPlayer = true;
            this.showListAdmShop = false;
            this.showAddItemShop = false;

        },
        async deleteItem(itemId) {
            const response = await fetch(window.qualifyURL("/adm/deleteshop?ID=" + itemId + "&sessionid=" + this.getCookie("sessionid")));
            const data = await response.text();
            this.resultado = data;
        },
        async adm() {
            console.log("Fetching player info:", this.nick, "Info type:", this.information);
            const response = await fetch(window.qualifyURL("/adm/search?nick=" + this.nick + "&information=" + this.information + "&sessionid=" + this.getCookie("sessionid")));
            const data = await response.json();
            this.userInfo = data.map(user => ({
                banned: user["Banned ?"],
                canSell: user["Can Sell ?"],
                isAdmin: user["is Admin ?"],
                ip: user["Ip"],
                canBuy: user["Can Buy ?"],
                name: user["Name"],
                websiteBanForm: user["WebSite Ban"]
            }));
        },
        async additem() {
            console.log("Adding item to shop:", this.itemId, "Quantity:", this.quantity, "Price:", this.price);
            const response = await fetch(window.qualifyURL("/adm/addshop?&itemId=" + this.itemId + "&quantity=" + this.quantity + "&price=" + this.price + "&sessionid=" + this.getCookie("sessionid")));
            const data = await response.text();
            this.resultado = data;
            this.resetAddItemForm();
        },
        resetAddItemForm() {
            this.itemId = '';
            this.quantity = '';
            this.price = '';
        },
        async websiteban(formEvent) {
            try {
                let formDataObj = {};
                for (let element of event.target.elements) {
                    if (element.name && element.value) {
                        formDataObj[element.name] = element.value;
                    }
                }
                formDataObj['sessionid'] = this.getCookie("sessionid");
                const params = new URLSearchParams(formDataObj);
                const url = window.qualifyURL("/adm/webban") + "?" + params.toString();

                try {
                    const response = await fetch(url);

                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }

                    const data = await response.text();
                    this.resultado = data;
                } catch (error) {
                    this.resultado = 'Error: ' + error.message;
                }

            } catch (error) {
                this.resultado = 'Error: ' + error.message;
            }
        },
        async websiteunban(formEvent) {
            try {
                let formDataObj = {};
                for (let element of event.target.elements) {
                    if (element.name && element.value) {
                        formDataObj[element.name] = element.value;
                    }
                }
                formDataObj['sessionid'] = this.getCookie("sessionid");
                const params = new URLSearchParams(formDataObj);
                const url = window.qualifyURL("/adm/webunban") + "?" + params.toString();

                try {
                    const response = await fetch(url);

                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }

                    const data = await response.text();
                    this.resultado = data;
                } catch (error) {
                    this.resultado = 'Error: ' + error.message;
                }

            } catch (error) {
                this.resultado = 'Error: ' + error.message;
            }
        },

    },
    mounted() {
        this.getMonitor();
    }
});

window.vueApp = vue;
function websiteban(form) {
    window.vueApp.websiteban(form);
    return false;
}

function websiteunban(form) {
    window.vueApp.websiteunban(form);
    return false;
}