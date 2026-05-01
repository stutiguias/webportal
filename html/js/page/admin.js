const vue = WebPortalVue3.mountApp({
    el: '#app',
    data() {
        return {
            displayStart: 0,
            displayLength: 10,
            activePanel: 'shopList',
            nick: '',
            information: 'playerinfo',
            infoOptions: [
                { text: 'Player Info', value: 'playerinfo' },
                { text: 'Player Items', value: 'playeritems' },
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
            monitor: [],
            resultcmd: '',
            nickname: '',
            msg: '',
            selectedEssentialsCmd: '',
            selectedBaseCmd: '',
            isLoadingMonitor: false,
            isLoadingPanel: false,
            isSendingBaseCmd: false,
            isSendingEssentialsCmd: false,
            isSubmittingPlayerInfo: false,
            isSubmittingAddItem: false,
            cmdEssentialsOptions: [
                { text: 'whois', value: 'whois' },
                { text: 'mail', value: 'mail' },
            ],
            cmdBaseOptions: [
                { text: 'op', value: 'op' },
                { text: 'deop', value: 'deop' },
                { text: 'ban', value: 'ban' },
                { text: 'ipban', value: 'ipban' },
                { text: 'pardon', value: 'pardon' },
                { text: 'ippardon', value: 'ippardon' },
                { text: 'banlist', value: 'banlist' }
            ],
        };
    },
    computed: {
        showInfoPlayer() {
            return this.activePanel === 'playerInfo';
        },
        showAddItemShop() {
            return this.activePanel === 'addShop';
        },
        showListAdmShop() {
            return this.activePanel === 'shopList';
        },
        activePanelLabel() {
            return {
                shopList: this.translate('langAdmListItem'),
                addShop: this.translate('langAdmAddItem'),
                playerInfo: this.translate('langPlayerInfo')
            }[this.activePanel] || 'Admin';
        },
        panelResultType() {
            return this.isErrorMessage(this.resultado) ? 'error' : 'info';
        },
        commandResultType() {
            return this.isErrorMessage(this.resultcmd) ? 'error' : 'info';
        },
        monitorCount() {
            return Array.isArray(this.monitor) ? this.monitor.length : 0;
        },
    },
    methods: {
        apiUrl(url) {
            return window.qualifyURL(url);
        },
        isErrorMessage(message) {
            return /(error|invalid|erro|falha|failed)/i.test(message || '');
        },
        async get(url) {
            try {
                this.isLoadingPanel = true;
                const response = await fetch(this.apiUrl(url));
                const data = await response.json();
                this.processData(data);
            } catch (error) {
                this.resultado = error.message || String(error);
            } finally {
                this.isLoadingPanel = false;
            }
        },
        showBaseNickName() {
            return this.selectedBaseCmd === 'op' 
            || this.selectedBaseCmd === 'ban'
            || this.selectedBaseCmd === 'deop'
            || this.selectedBaseCmd === 'ipban'
            || this.selectedBaseCmd === 'pardon'
            || this.selectedBaseCmd === 'ippardon'
            ? true : false;
        },
        async sendEssentialsCmd() {
            if (!this.selectedEssentialsCmd) {
                this.resultcmd = 'Select an Essentials command first.';
                return;
            }
            if ((this.selectedEssentialsCmd === 'whois' || this.selectedEssentialsCmd === 'mail') && !this.nickname) {
                this.resultcmd = 'Enter the nickname for the selected Essentials command.';
                return;
            }
            if (this.selectedEssentialsCmd === 'mail' && !this.msg) {
                this.resultcmd = 'Enter the mail message before sending.';
                return;
            }

            let url = "";
            if (this.selectedEssentialsCmd === 'mail') url = "/adm/essentials/mail?nickname=" + this.nickname + "&msg=" + this.msg;
            if (this.selectedEssentialsCmd === 'whois') url = "/adm/essentials/whois?nickname=" + this.nickname;

            this.isSendingEssentialsCmd = true;
            try {
                const response = await fetch(this.apiUrl(url + "&sessionid=" + this.getCookie("sessionid")));
                const data = await response.json();
                this.resultcmd = data[0]?.result || 'No command output returned.';
            } catch (error) {
                this.resultcmd = 'Error: ' + error.message;
            } finally {
                this.isSendingEssentialsCmd = false;
            }
        },
        async sendBaseCmd() {
            if (!this.selectedBaseCmd) {
                this.resultcmd = 'Select a base command first.';
                return;
            }
            if (this.showBaseNickName() && !this.nickname) {
                this.resultcmd = 'Enter the nickname for the selected base command.';
                return;
            }

            let url = "";
            if (this.selectedBaseCmd === 'op') url = "/adm/base/op?param1=" + this.nickname;
            if (this.selectedBaseCmd === 'deop') url = "/adm/base/deop?param1=" + this.nickname;
            if (this.selectedBaseCmd === 'ban') url = "/adm/base/ban?param1=" + this.nickname;
            if (this.selectedBaseCmd === 'ipban') url = "/adm/base/ipban?param1=" + this.nickname;
            if (this.selectedBaseCmd === 'pardon') url = "/adm/base/pardon?param1=" + this.nickname;
            if (this.selectedBaseCmd === 'ippardon') url = "/adm/base/ippardon?param1=" + this.nickname;
            if (this.selectedBaseCmd === 'banlist') url = "/adm/base/banlist?param1=";

            this.isSendingBaseCmd = true;
            try {
                const response = await fetch(this.apiUrl(url + "&sessionid=" + this.getCookie("sessionid")));
                const data = await response.json();
                this.resultcmd = data[0]?.result || 'No command output returned.';
            } catch (error) {
                this.resultcmd = 'Error: ' + error.message;
            } finally {
                this.isSendingBaseCmd = false;
            }
        },
        async getMonitor() {
            this.isLoadingMonitor = true;
            try {
                const response = await fetch(this.apiUrl("/adm/getMonitor?sessionid=" + this.getCookie("sessionid")));
                const data = await response.json();
                this.monitor = Array.isArray(data) ? data : [];
            } catch (error) {
                this.resultado = 'Error: ' + error.message;
            } finally {
                this.isLoadingMonitor = false;
            }
        },
        processData(data) {
            this.loadTable(data);
        },
        loadTable(data) {
            this.tableItems = [];
            if (!data || data[0] != null) return;

            const firstKey = Object.keys(data).find(key => Array.isArray(data[key]) && data[key].length > 0);
            const upgradeHtml = window.WebPortalItemImageHelper?.upgradeHtml || (value => value);
            if (firstKey) {
                this.tableItems = data[firstKey].map(item => {
                    return {
                        delete: this.extractIdFromDeleteForm(item.Delete),
                        item_name: upgradeHtml(item["Item Name"]),
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
        secureApiHtml(htmlString) {
            const template = document.createElement('template');
            template.innerHTML = htmlString || '';

            template.content.querySelectorAll('form[action], a[href]').forEach(element => {
                const attr = element.hasAttribute('action') ? 'action' : 'href';
                const value = element.getAttribute(attr);
                if (value && (/^https?:\/\//i.test(value) || value.charAt(0) === '/')) {
                    element.setAttribute(attr, this.apiUrl(value));
                }
            });

            return template.innerHTML;
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
        setPanel(panel) {
            this.activePanel = panel;
            this.resultado = '';

            if (panel === 'shopList') {
                this.listshop();
            }
        },
        listshop() {
            this.activePanel = 'shopList';
            this.resultado = '';
            this.get("/adm/shoplist?DisplayStart=" + this.displayStart + "&DisplayLength=" + this.displayLength + "&sessionid=" + this.getCookie("sessionid"));
        },
        admshop() {
            this.activePanel = 'addShop';
            this.resultado = '';
        },
        infoplayer() {
            this.activePanel = 'playerInfo';
            this.resultado = '';
        },
        async deleteItem(itemId) {
            try {
                const response = await fetch(this.apiUrl("/adm/deleteshop?ID=" + itemId + "&sessionid=" + this.getCookie("sessionid")));
                const data = await response.text();
                this.resultado = data;
                await this.listshop();
            } catch (error) {
                this.resultado = 'Error: ' + error.message;
            }
        },
        async adm() {
            if (!this.nick || !this.information) {
                this.resultado = 'Fill player name and info type before searching.';
                return;
            }

            this.isSubmittingPlayerInfo = true;
            try {
                const response = await fetch(this.apiUrl("/adm/search?nick=" + this.nick + "&information=" + this.information + "&sessionid=" + this.getCookie("sessionid")));
                const data = await response.json();
                this.userInfo = data.map(user => ({
                    banned: user["Banned ?"],
                    canSell: user["Can Sell ?"],
                    isAdmin: user["is Admin ?"],
                    ip: user["Ip"],
                    canBuy: user["Can Buy ?"],
                    name: user["Name"],
                    websiteBanForm: this.secureApiHtml(user["WebSite Ban"])
                }));
            } catch (error) {
                this.resultado = 'Error: ' + error.message;
            } finally {
                this.isSubmittingPlayerInfo = false;
            }
        },
        async additem() {
            if (!this.itemId || !this.quantity || !this.price) {
                this.resultado = 'Fill material, quantity and price before adding an item to the admin shop.';
                return;
            }

            this.isSubmittingAddItem = true;
            try {
                const response = await fetch(this.apiUrl("/adm/addshop?itemId=" + this.itemId + "&quantity=" + this.quantity + "&price=" + this.price + "&sessionid=" + this.getCookie("sessionid")));
                const data = await response.text();
                this.resultado = data;
                this.resetAddItemForm();
            } catch (error) {
                this.resultado = 'Error: ' + error.message;
            } finally {
                this.isSubmittingAddItem = false;
            }
        },
        resetAddItemForm() {
            this.itemId = '';
            this.quantity = '';
            this.price = '';
        },
        async websiteban(formEvent) {
            try {
                let formDataObj = {};
                for (let element of formEvent.target.elements) {
                    if (element.name && element.value) {
                        formDataObj[element.name] = element.value;
                    }
                }
                formDataObj['sessionid'] = this.getCookie("sessionid");
                const params = new URLSearchParams(formDataObj);
                const url = this.apiUrl("/adm/webban?" + params.toString());

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
                for (let element of formEvent.target.elements) {
                    if (element.name && element.value) {
                        formDataObj[element.name] = element.value;
                    }
                }
                formDataObj['sessionid'] = this.getCookie("sessionid");
                const params = new URLSearchParams(formDataObj);
                const url = this.apiUrl("/adm/webunban?" + params.toString());

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
        this.listshop();
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
