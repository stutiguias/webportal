function read() {
    if(document.getElementById("mailread") == null) return;
    document.getElementById("mailread").addEventListener("click", function(){
        document.getElementById("mail").style.display = "block";
    });
    if(document.getElementById("mailclose") == null) return;
    document.getElementById("mailclose").addEventListener("click", function(){
        document.getElementById("mail").style.display = "none";
    });
}


WebPortalVue3.mountApp({
    el: '#app',
    data() {
        return {
            user: '',
            money: '',
            mail: '',
            avatarUrl: '',
            isAdmin: false,
            sessionid: '',
            dashboardCards: [
                { id: 1, content: '' },
                { id: 2, content: '' }
            ]
        };
    },
    methods: {
      translate(key) {
          return window.langIndex[key] || key;
      },
      formatMoney(value) {
          if (value === null || value === undefined || value === '') {
              return '--';
          }

          const amount = Number(value);
          if (!Number.isFinite(amount)) {
              return '--';
          }

          return new Intl.NumberFormat('pt-BR', {
              minimumFractionDigits: 2,
              maximumFractionDigits: 2
          }).format(amount);
      },
      async fetchText(url, errorMessage) {
          const response = await fetch(window.qualifyURL(url), { credentials: 'include' });
          if (!response.ok) {
              throw new Error(errorMessage);
          }

          return response.text();
      },
      async fetchJson(url, errorMessage) {
          const response = await fetch(window.qualifyURL(url), { credentials: 'include' });
          if (!response.ok) {
              throw new Error(errorMessage);
          }

          return response.json();
      },
      async getBox(n) {
          const content = await this.fetchText(
              "/box/" + n + "?sessionid=" + this.sessionid,
              "Unable to load dashboard panel " + n
          );

          const card = this.dashboardCards.find(item => item.id === n);
          if (card) {
              card.content = content;
          }

          if (n === 2) {
              this.$nextTick(() => read());
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
      async getUserInfo() {
          try {
              const data = await this.fetchJson(
                  "/user/info?sessionid=" + this.sessionid,
                  "Unable to load user data"
              );

              this.user = data["Name"];
              this.money = data["Money"];
              this.mail = data["Mail"];
              this.isAdmin = data["Admin"].toString() === "1";
              this.avatarUrl = data["Avatarurl"];
          } catch (error) {
              this.user = "Error loading data";
          }
      },
      async logout() {
          try {
              await this.fetchText(
                  "/logout?sessionid=" + this.sessionid,
                  "Unable to log out"
              );
          } finally {
              document.cookie = encodeURIComponent("sessionid") + "=deleted; expires=" + new Date(0).toUTCString();
              window.location = "login.html";
          }
      }
    },
    mounted() {
        this.sessionid = this.getCookie("sessionid") || "";

        this.getUserInfo();

        this.dashboardCards.forEach(card => {
            this.getBox(card.id).catch(() => {
                card.content = "<p>Unable to load this panel right now.</p>";
            });
        });
    }
});
