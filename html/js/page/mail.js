var qtd = 10;
var qtd = 0;

$(function () {
    window.AjaxTable(0, 10);
});

var AjaxTable = function (from, qtd) {
    $.ajax({
        url: window.qualifyURL("/mail/get"),
        data: "from=" + from + "&qtd=" + qtd + "&sessionid=" + getCookie("sessionid"),
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