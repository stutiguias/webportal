function buy(form) {
    var ar = $(form).serializeArray();
    $.ajax({
        url: buyUrl,
        data: $(form).serialize(),
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
            $.ajax({
                "dataType": 'json',
                "type": "GET",
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
    table("fill/auction/byall");
    $(".byall").addClass("active");

    $(".byall").click(function () {
        selectGroup("fill/auction/byall",".byall");
    });

    $(".byblock").click(function () {
        selectGroup("fill/auction/byblock", ".byblock");
    });

    $(".byCombat").click(function () {
        selectGroup("fill/auction/bycombat", ".bycombat");
    });

    $(".byDecoration").click(function () {
        selectGroup("fill/auction/bydecoration", ".bydecoration");
    });

    $(".byFood").click(function () {
        selectGroup("fill/auction/byfood", ".byfood");
    });

    $(".byMaterials").click(function () {
        selectGroup("fill/auction/bymaterials", ".bymaterials");
    });

    $(".byMicellaneous").click(function () {
        selectGroup("fill/auction/bymicellaneous", ".bymicellaneous");
    });

    $(".byOthers").click(function () {
        selectGroup("fill/auction/byothers", ".byothers");
    });

    $(".byRedstone").click(function () {
        selectGroup("fill/auction/byredstone", ".byredstone");
    });

    $(".byTools").click(function () {
        selectGroup("fill/auction/bytools", ".bytools");
    });

    $(".byTransportation").click(function () {
        selectGroup("fill/auction/bytransportation", ".bytransportation");
    });

    $(".byBrewing").click(function () {
        selectGroup("fill/auction/bybrewing", ".bybrewing");
    });
    
});
