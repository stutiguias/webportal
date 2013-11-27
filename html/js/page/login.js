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

function are_cookies_enabled()
{
	var cookieEnabled = (navigator.cookieEnabled) ? true : false;

	if (typeof navigator.cookieEnabled == "undefined" && !cookieEnabled)
	{ 
		document.cookie="testcookie";
		cookieEnabled = (document.cookie.indexOf("testcookie") != -1) ? true : false;
	}
	return (cookieEnabled);
}

function loginform(form) {
	if(!are_cookies_enabled()) {
	 alert("ENABLE COOKIE");
	 return;
	}
    setCookie("sessionid", makeid());
    $("#sessionid").val(getCookie("sessionid"));
    
    $.ajax({
        url: window.qualifyURL("/web/login"),
        data: $(form).serialize(),
        success: function (data) {
            if (data == "ok") {
                window.location = 'index.html';
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

$(function () {
    window.AjaxTable(0, 10);
});

var qtd = 10;

var AjaxTable = function (from, qtd) {
    $.ajax({
        url: window.qualifyURL("/get/auction"),
        data: "from=" + from + "&qtd=" + qtd,
        success: function (data) {
            try {
                window.LoadTable(data, from, qtd);
            } catch (err) {
                $('#resultado').html(err);
            };
        },
        error: function (error) {
            $('#resultado').html(error);
        },
        dataType: "json"
    });
};