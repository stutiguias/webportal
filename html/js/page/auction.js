function buy(form) {
    var ar = $(form).serializeArray();
    $.ajax({
        url: buyUrl,
        data: $(form).serialize() + "&sessionid=" + getCookie("sessionid"),
        success: function (data) {
            if (data == "ok") {
                $(form).hide();
                $('#' + ar[1].value).html("Done");
            } else if (data == "no") {
                $('#error').html('Invalid');
            } else {
                $(form).hide();
                $('#' + ar[1].value).html(data);
            }
        },
        error: function (error) {

        },
        dataType: "text"
    });

    return false;
};

function sell(form) {
    var ar = $(form).serializeArray();
    $.ajax({
        url: "/auction/sell",
        data: $(form).serialize() + "&sessionid=" + getCookie("sessionid"),
        success: function (data) {
            if (data == "ok") {
                $(form).hide();
                $('#' + ar[1].value).html("Done");
            } else if (data == "no") {
                $('#error').html('Invalid');
            } else {
                $(form).hide();
                $('#' + ar[1].value).html(data);
            }
        },
        error: function (error) {

        },
        dataType: "text"
    });

    return false;
};

var table = function (by) {
    oTable = $('#mainTable').dataTable({
        "sDom": "<'row'<'span4'l><'span4'f>r>t<'row'<'span4'i><'span4'p>>",
        "sPaginationType": "bootstrap",
        "bProcessing": true,
        "bStateSave": false,
        "bDestroy": true,
        "oSearch": { "sSearch": "" },
        "bServerSide": true,
        "sAjaxSource": by,
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
            aoData.push({ name : "sessionid" , value : getCookie("sessionid") });
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
};

var selectGroup = function (group,t) {
    $(".nav li").each(function () {
        $(this).removeClass("active");
    });
    table(group);
    $(t).addClass("active");
};

$(document).ready(function () {
    table("/auction/get/byall");
    $(".byall").addClass("active");

    $(".byall").click(function () {
        selectGroup("/auction/get/byall",".byall");
    });

    $(".byblock").click(function () {
        selectGroup("/auction/get/byblock", ".byblock");
    });

    $(".byCombat").click(function () {
        selectGroup("/auction/get/bycombat", ".bycombat");
    });

    $(".byDecoration").click(function () {
        selectGroup("/auction/get/bydecoration", ".bydecoration");
    });

    $(".byFood").click(function () {
        selectGroup("/auction/get/byfood", ".byfood");
    });

    $(".byMaterials").click(function () {
        selectGroup("/auction/get/bymaterials", ".bymaterials");
    });

    $(".byMicellaneous").click(function () {
        selectGroup("/auction/get/bymicellaneous", ".bymicellaneous");
    });

    $(".byOthers").click(function () {
        selectGroup("/auction/get/byothers", ".byothers");
    });

    $(".byRedstone").click(function () {
        selectGroup("/auction/get/byredstone", ".byredstone");
    });

    $(".byTools").click(function () {
        selectGroup("/auction/get/bytools", ".bytools");
    });

    $(".byTransportation").click(function () {
        selectGroup("/auction/get/bytransportation", ".bytransportation");
    });

    $(".byBrewing").click(function () {
        selectGroup("/auction/get/bybrewing", ".bybrewing");
    });
    
});
