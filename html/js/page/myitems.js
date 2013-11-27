$(document).ready(function () {

    $("#btnCreateAuction").click(function () {
        getItems();
        $(".hideform").show();
        $('#result').html("");
    });

    $("#btnSendMail").click(function () {
        getItems();
        $(".hideform").show();
        $('.result').html("");
    });

});

var getItems = function () {
    $.ajax({
        url: window.qualifyURL("/myitems/get"),
        success: function (data) {
            $(".selectItem").empty();
            $(".selectItemMail").empty();
            $.each(data, function (itemId, nameImage) {
                select(itemId, nameImage, ".selectItem", ".img", ".enchants");
                select(itemId, nameImage, ".selectItemMail", ".imgMail", ".enchantsMail");
            });

        },
        error: function (error) {
            $('#resultado').html(error);
        },
        dataType: "json"
    });
};

var select = function (itemId, nameImage, selector, img, enchants) {
    $(selector).change(function () {
        var imgsrc = "images/" + nameImage[$(selector + ' :selected').text()];
        if ($(selector + ' :selected').val().indexOf(itemId) != -1) {
            $(img).attr("src", imgsrc);
            $(enchants).html(nameImage["enchant"]);
        }
    });

    $.each(nameImage, function (name, image) {
        if (name.indexOf("enchant") == -1) {
            $(selector).append(new Option(name, itemId));
        }
        var imgsrc = "images/" + nameImage[$(selector + ' :selected').text()];
        if (imgsrc.indexOf("undefined") == -1)
            $(img).attr("src", imgsrc);
    });
};

var qtd = 10;

$(function () {
    window.AjaxTable(0, 10);
});

var AjaxTable = function (from, qtd) {
    $.ajax({
        url: window.qualifyURL("/myitems/dataTable"),
        data: "from=" + from + "&qtd=" + qtd,
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

function postauction(form) {
    var ar = $(form).serializeArray();
    $.ajax({
        url: window.qualifyURL("/myitems/postauction"),
        data: $(form).serialize(),
        success: function (data) {
            if (data == "no") {
                $('#error').html('Invalid');
            } else {
                $(".hideform").hide();
                $('#result').html(data);
            }
        },
        error: function (error) {

        },
        dataType: "text"
    });

    return false;
};

function mail(form) {
    var ar = $(form).serializeArray();
    $.ajax({
        url: window.qualifyURL("/mail/send"),
        data: $(form).serialize(),
        success: function (data) {
            $(".hideform").hide();
            $('.result').html(data);
        },
        error: function (error) {
            $(form).hide();
            $('.result').html(error);
        },
        dataType: "text"
    });

    return false;
};