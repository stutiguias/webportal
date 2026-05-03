WebPortalVue3.mountApp({
    el: '#app',
    data: () => ({
        username: '',
        password: '',
        headers: [],
        items: [],
        resultado: '', 
        errorMessage: '',
        sessionid: '',
        qtd: 10,
        isLoading: false,
        itemExpiryHours: 168
    }),
    mounted() {
        this.checkAndSetSessionId();
        this.getauction(0, this.qtd);
    },
    methods: {
        loginform() {
        },              
        translate(key) {
            return window.langLogin[key] || key;
        },
        getauction(from, qtd) {
            fetch(window.qualifyURL(`/web/auction?from=${from}&qtd=${qtd}`))
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
            const firstKey = Object.keys(data).find(key => data[key] instanceof Array && data[key].length > 0);
            const upgradeHtml = window.WebPortalItemImageHelper?.upgradeHtml || (value => value);

            if (!firstKey) {
                this.headers = [];
                this.items = [];
                return;
            }

            this.headers = Object.values(data[firstKey][0])
                .filter(field => field.Title.toLowerCase() !== 'created')
                .map(field => ({
                    text: field.Title,
                    value: field.Title.toLowerCase().replace(/\s+/g, '_')
                }));

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
        },
        getTimeRemaining(created) {
            if (!created || created === 0) return 'Never';
            
            const createdTime = created * 1000;
            const expiryTime = createdTime + (this.itemExpiryHours * 3600 * 1000);
            const remaining = expiryTime - Date.now();
            
            if (remaining <= 0) return 'Expired';
            
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
        areCookiesEnabled() {
            document.cookie = "cookietest=1";
            const ret = document.cookie.indexOf("cookietest=") !== -1;
            document.cookie = "cookietest=; expires=Thu, 01-Jan-1970 00:00:01 GMT";
            return ret;
        },
        setCookie(name, value, days = 7) {
            const d = new Date();
            d.setTime(d.getTime() + (days * 24 * 60 * 60 * 1000));
            let expires = "expires=" + d.toUTCString();
            document.cookie = `${name}=${value};${expires};path=/`;
        },
        getCookie(name) {
            let nameEQ = name + "=";
            let ca = document.cookie.split(';');
            for(let i=0;i < ca.length;i++) {
                let c = ca[i];
                while (c.charAt(0) === ' ') c = c.substring(1,c.length);
                if (c.indexOf(nameEQ) === 0) return c.substring(nameEQ.length,c.length);
            }
            return null;
        },
        makeid(length = 10) {
            let result = '';
            let characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
            let charactersLength = characters.length;
            for (let i = 0; i < length; i++) {
                result += characters.charAt(Math.floor(Math.random() * charactersLength));
            }
            return result;
        },
        checkAndSetSessionId() {
            if (!this.areCookiesEnabled()) {
                this.errorMessage = 'Cookies must be enabled to sign in.';
                return;
            }
            const existingSessionId = this.getCookie('sessionid');
            this.sessionid = existingSessionId || this.makeid();
            this.setCookie("sessionid", this.sessionid); 
        },
        async login() {
            this.errorMessage = '';

            if (!this.username || !this.password) {
                this.errorMessage = 'Fill in username and password before signing in.';
                return;
            }

            if (!this.sessionid) {
                this.checkAndSetSessionId();
                if (!this.sessionid) {
                    return;
                }
            }

            const queryParams = new URLSearchParams({ Username: this.username, Password: this.password, sessionid: this.sessionid }).toString();
            const url = window.qualifyURL(`/web/login?${queryParams}`);

            try {
                this.isLoading = true;
                const response = await fetch(url);
                if (!response.ok) {
                    throw new Error('Unable to reach the login service.');
                }

                const data = await response.text();
                if (data.trim() === "ok") {
                    window.location = 'index.html';
                } else if (data.trim() === "no") {
                    this.errorMessage = 'The username is case-sensitive. Check your password and try again.';
                } else {
                    this.errorMessage = data || 'Unexpected login response.';
                }
            } catch (error) {
                this.errorMessage = error.message || 'Login failed.';
            } finally {
                this.isLoading = false;
            }
        },
    }
})
