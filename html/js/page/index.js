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
        box01: '',
        box02: '',
    },
    methods: {
      translate(key) {
          return window.langIndex[key] || key;
      },
      async getBox(n){
        const response = await fetch("/box/"+n+"?sessionid=" + this.sessionid)
        if(n === 1) this.box01 = await response.text();
        if(n === 2) this.box02 = await response.text();
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
      async getContentBox(n){
        if(n === 1) return await this.getBox(1);
        if(n === 2) return await this.getBox(2);
      },
      getUserInfo() {
          fetch("/user/info?sessionid=" + this.sessionid)
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
    }
});