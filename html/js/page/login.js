new Vue({
    el: '#app',
    vuetify: new Vuetify(),
    data: () => ({
        username: '',
        password: '',
        headers: [],
        items: [],
        resultado: '', 
        sessionid: '',
        qtd: 10
    }),
    mounted() {
        this.getauction(0, this.qtd);
        this.checkAndSetSessionId();
    },
    methods: {
        loginform() {
        },              
        translate(key) {
            return window.langLogin[key] || key;
        },
        getauction(from, qtd) {
            fetch(`/get/auction?from=${from}&qtd=${qtd}`)
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
                alert("ENABLE COOKIE");
                return;
            }
            this.sessionid = this.makeid();
            this.setCookie("sessionid", this.sessionid); 
        },
        async login() {
            const queryParams = new URLSearchParams({ Username: this.username, Password: this.password, sessionid: this.sessionid }).toString();
            const url = `/web/login?${queryParams}`;
            try {
                const response = await fetch(url);
                const data = await response.text();
                if (data === "ok") {
                    window.location = 'index.html';
                } else if (data === "no") {
                    this.errorMessage = 'Error trying to login:<br>The username must be case sensitive or check your password';
                }
            } catch (error) {
                console.error('Login failed:', error);
            }
        },
    }
})