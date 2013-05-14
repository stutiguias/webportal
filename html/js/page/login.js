function setCookie(szName, szValue, szExpires, szPath, szDomain, bSecure) {
    var szCookieText = escape(szName) + '=' + escape(szValue);
    szCookieText += (szExpires ? '; EXPIRES=' + szExpires.toGMTString() : '');
    szCookieText += (szPath ? '; PATH=' + szPath : '');
    szCookieText += (szDomain ? '; DOMAIN=' + szDomain : '');
    szCookieText += (bSecure ? '; SECURE' : '');

    document.cookie = szCookieText;
}function getCookie(szName) {
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
}function makeid() {
    var text = "";
    var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    for (var i = 0; i < 5; i++)
        text += possible.charAt(Math.floor(Math.random() * possible.length));

    return text;
}


function loginform(form) {
    $.ajax({
        url: login,
        data: $(form).serialize(),
        success: function (data) {
            if (data == "ok") {
                window.location = '/index.html';
            } else if (data == "no") {
                $('.error').show();
                $('.error').html('Error trying to login:<br>The username must be case sensitive or check your password');
            }
        },
        error: function (error) {

        },
        dataType: "text"
    });

    return false;
};

for (var prop in langLogin) {
    var patt = new RegExp(prop, "g");
    $('.replace').each(function () {
        $(this).html($(this).html().replace(patt, langLogin[prop]));
    });
}

for (var prop in langIndex) {
    var patt = new RegExp(prop, "g");
    $('.replace').each(function () {
        $(this).html($(this).html().replace(patt, langIndex[prop]));
    });
}

$(document).ready(function () {
    setCookie("sessionid", makeid());
    $("#Sessionid").val(getCookie("sessionid"));

    $.ajax({
        url: getAuction,
        success: function (data) {
            try {
                var result = "";
                $.each(data, function (key, val) {
                    result += "<tr>";
                    $.each(val,function(key2,val2) {
                        result += "<td>" + val2 + "</td>";
                    });
                    result += "</tr>";
                });
                $(".tbodyInput").html(result);
            } catch (err) {
                $('.result').html(err);
            };
        },
        error: function (data) {
            $('.result').html(data);
        },
        dataType: "json"
    });
});