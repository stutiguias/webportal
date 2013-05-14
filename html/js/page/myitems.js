$(document).ready(function () {

    table();

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
        url: "/myitems/get",
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

var table = function () {
    oTable = $('#example').dataTable({
        "sDom": "<'row'<'span4'l><'span4'f>r>t<'row'<'span4'i><'span4'p>>",
        "sPaginationType": "bootstrap",
        "bProcessing": true,
        "bStateSave": false,
        "bServerSide": true,
        "bDestroy": true,
        "sAjaxSource": "/myitems/dataTable",
        "oLanguage": {
            "sProcessing": jsIndex['sProcessing'],
            "sLengthMenu": jsIndex['sLengthMenu'],
            "sZeroRecords": jsIndex['sZeroRecords'],
            "sInfo": jsIndex['sInfo'],
            "sInfoEmpty": jsIndex['sInfoEmpty'],
            "sInfoFiltered": jsIndex['sInfoFiltered'],
            "sSearch": jsIndex['sSearch'],
            "sInfoPostFix": "",
            "sUrl": "",
            "oPaginate": {
                "sFirst": jsIndex['sFirst'],
                "sPrevious": jsIndex['sPrevious'],
                "sNext": jsIndex['sNext'],
                "sLast": jsIndex['sLast']
            }
        },
        "fnServerData": function (sSource, aoData, fnCallback) {
            $.ajax({
                "dataType": 'json',
                "type": "POST",
                "url": sSource,
                "data": aoData,
                "success": fnCallback,
                "timeout": 15000,
                "error": handleAjaxError
            });
        }
    });
}


function postauction(form) {
    var ar = $(form).serializeArray();
    $.ajax({
        url: postAuctions,
        data: $(form).serialize(),
        success: function (data) {
            if (data == "no") {
                $('#error').html('Invalid');
            } else {
                $(".hideform").hide();
                table();
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
        url: postMail,
        data: $(form).serialize(),
        success: function (data) {
            $(".hideform").hide();
            table();
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