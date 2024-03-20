
new Vue({
    el: '#app',
    vuetify: new Vuetify(),
    data: {
      user: '',
      money: '',
      mail: '',
      avatarUrl: '',
      isAdmin: false,
      sessionid: this.getCookie("sessionid"),
      box01: ''
    },
    methods: {
      translate(key) {
          return window.langIndex[key] || key;
      },
      async getBox(n){
        const response = await fetch("/box/1?sessionid=" + this.sessionid)
        const data = await response.text();
        this.box01 = data;
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
      getContentBox(n){
        if(n == 1) return this.box01;
      },
      getUserInfo() {
          fetch("/server/username/info?sessionid=" + this.sessionid)
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
      logout() {
          fetch("/logout?sessionid=" + this.sessionid)
          .then(() => {
              document.cookie = encodeURIComponent("sessionid") + "=deleted; expires=" + new Date(0).toUTCString();
              window.location = "login.html";
          });
      }
    },
    mounted() {
        this.getUserInfo();
        this.getBox(1);
    }
});