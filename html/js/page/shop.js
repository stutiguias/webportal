function shop(form) {
    $.ajax({
        url: window.qualifyURL("/auction/shop"),
        data: $(form).serialize(),
        success: function (data) {
            $("#formresult").html(data);
        },
        error: function (error) {
            $("#formresult").html(error);
        },
        dataType: "text"
    });

    return false;
};

var table = function (by) {
    qtd = 10;
    thisby = by;
    window.AjaxTable(0, 10);
};

var qtd = 10;
var thisby = "";

var AjaxTable = function (from, qtd) {
    $.ajax({
        url: window.qualifyURL(window.thisby),
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
