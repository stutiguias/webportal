$(function () {
    window.AjaxTable(0, 10);
});

var qtd = 10;

var AjaxTable = function (from, qtd) {
    $.ajax({
        url: window.qualifyURL("/myauctions/get"),
        data: "from=" + from + "&qtd=" + qtd + "&sessionid=" + getCookie("sessionid"),
        success: function (data) {
            try {
                window.LoadTable(data,from,qtd);
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

function remitem(form) {
    $.ajax({
        url: window.qualifyURL("/myauctions/cancel"),
        data: $(form).serialize() + "&sessionid=" + getCookie("sessionid"),
        success: function (data) {
            $('#formresult').html(data);
            qtd = 10;
            window.AjaxTable(0, 10);
        },
        error: function (error) {
            $('#formresult').html(error);
        },
        dataType: "text"
    });
    return false;
};