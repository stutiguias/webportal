function replaceText() {
    for (var prop in langIndex) {
        var patt = new RegExp(prop, "g");
        $('.replace').each(function () {
            $(this).html($(this).html().replace(patt, langIndex[prop]));
        });
    }
}

function getCookie(szName) {
    var szValue = null;
    if (document.cookie)	   //only if exists
    {
        var arr = document.cookie.split((escape(szName) + '='));
        if (2 <= arr.length) {
            var arr2 = arr[1].split(';');
            szValue = unescape(arr2[0]);
        }
    }
    return szValue;
}
$.ajaxSetup({
    data: "sessionid=" + getCookie("sessionid"),
    type: "POST"
});

$(document).ready(function () {
    var template = Handlebars.templates['menu.html'];
    $(".menu").html(template);

    replaceText();

    $.ajax({
        url: window.qualifyURL("/server/username/info"),
        success: function (data) {
            try {
                var adm = " <a href='admin.html'>Admin Panel</a>";
                var isAdmin = (data["Admin"].toString().indexOf("1") != -1) ? adm : "";
                $('#user').html(data["Name"] + " " + isAdmin);
                $('#money').html(data["Money"].split(" ")[1]);
                $('#mail').html(data["Mail"]);
                $('#avatarimg').attr('src', data["Avatarurl"]);
            } catch (err) {
                $('#user').html(err);
            };
        },
        error: function (data) {
            $('#user').html("Null");
        },
        dataType: "json"
    });

});

var sessionid = getCookie("sessionid");
if (sessionid == null) {
    window.location = 'login.html';
}