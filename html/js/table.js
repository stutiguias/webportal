var LoadTable = function (data,from,qtd) {

    var total;

    var result;
    $("#thead").html("");
    $("#tbody").html("");
 
    $.each(data, function (totalItems, items) {
        
        total = totalItems;

        if (total == 0) return;

        $.each(items[0], function (key, val) {
            $("#thead").html($("#thead").html() + "<th>" + val.Title + "</th>");
        });

        for (var i = 0; i < items.length; i++) {
            result = "<tr>";
            $.each(items[i], function (key, val) {
                result += "<td>" + val.Val + "</td>";
            });
            result += "</tr>";
            $("#tbody").html($("#tbody").html() + result);
        }
        
    });

    var to = from + qtd;
    
    $("#resultado").html("<a id='prev' href='#'>< " + langIndex["langPrevious"] + "</a> " + langIndex["langShowing"] + " " + (to - 9) + " - " + to + " " + langIndex["langOf"] + " " + total + " <a id='next' href='#'>" + langIndex["langNext"] + " ></a>");

    if (total <= 10) {
        $("#next").hide();
        $("#prev").hide();
    }

    $("#next").click(function (e) {
        e.preventDefault();
        if (total > to) {
            AjaxTable(from + 10, qtd);
        }
    });
    $("#prev").click(function (e) {
        e.preventDefault();
        if (to > 10) {
            AjaxTable(from - 10, qtd);
        }
    });
    
};