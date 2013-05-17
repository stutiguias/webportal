var to = 10;

$(function () {
    AjaxMail(0, 10);
});

var AjaxMail = function (to, from) {
    $.ajax({
        url: "/mail/get",
        data: "to=" + to + "&from=" + from + "&sessionid=" + getCookie("sessionid"),
        success: function (data) {
            try {
                LoadTable(data);
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

var LoadTable = function (data) {
    var result = "";
    var total;
    $(".theadInput").html("");
    $.each(data, function (key, val) {
        total = key;
        $.each(val, function (key, val) {
            $.each(val, function (key, val) {
                $(".theadInput").html($(".theadInput").html() + "<th>" + key + "</th>");
            });
            return false;
        });
    });

    $.each(data, function (key, val) {

        $.each(val, function (key, val) {
            result += "<tr>";
            $.each(val, function (key, val) {
                if (val.toString().indexOf(".png") != -1) {
                    result += "<td><img src='images/" + val + "' style='max-width:23px'></td>";
                } else {
                    result += "<td>" + val + "</td>";
                }
            });
            result += "</tr>";
        });

    });
    $("#resultado").html("<a id='prev' href='#'>< Previous</a> Showing "+( to-10 )+" - "+to+" of "+total+" <a id='next' href='#'>Next ></a>");
    $("#next").click(function (e) {
        e.preventDefault();
        if (total > to) {
            AjaxMail(to, to + 10);
            to = to + 10;
        }
    });
    $("#prev").click(function (e) {
        e.preventDefault();
        if (to > 10) {
            to = to - 10;
            AjaxMail(to - 10, to);
        }
    });
    $(".tbodyInput").html(result);
};