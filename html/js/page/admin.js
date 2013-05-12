function adm(form) {
    $.ajax({
        url: admsearch,
        data: $(form).serialize(),
        success: function (data) {
            try {
                var result = "";
                $(".theadInput").html("");
                $.each(data, function (key, val) {
                    $.each(val, function (key, val) {
                        $(".theadInput").html($(".theadInput").html() + "<th>" + key + "</th>");
                    });
                    return false;
                });
         
                $.each(data, function (key, val) {
                    result += "<tr>";
                    $.each(val, function (key, val) {
                        result += "<td>" + val + "</td>";
                    });
                    result += "</tr>";
                });
                
                $(".tbodyInput").html(result);
            } catch (err) {
                $('#resultado').html(err);
            };
        },
        error: function (error) {
            $('#resultado').html(error);
        },
        dataType: "json"
    });

    return false;
};

function additem(form) {
    $.ajax({
        url: "/adm/addshop",
        data: $(form).serialize(),
        success: function (data) {
            if (data.indexOf("ok") == -1) {
                $('#resultado').html(data);
            } else {
                $('#resultado').html("Sucess Create Server Auction");
            }
        },
        error: function (error) {
            $('#resultado').html(error);
        },
        dataType: "text"
    });

    return false;
};

function del(form) {
    $(form).hide();
    $.ajax({
        url: "/adm/deleteshop",
        data: $(form).serialize(),
        success: function (data) {
            $('#resultado').html(data);
        },
        error: function (error) {
            $('#resultado').html(error);
        },
        dataType: "text"
    });

    return false;
};

function listshop(form) {
    list(0, 10);
    return false;
};

var to = 10;

function list(to,from) {
    $.ajax({
        url: "/adm/shoplist",
        data: "DisplayStart=" + to + "&DisplayLength=" + from,
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
}

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
                result += "<td>" + val + "</td>";
            });
            result += "</tr>";
        });

    });
    $("#resultado").html("<a id='prev' href='#'>< Previous</a> Showing " + (to - 10) + " - " + to + " of " + total + " <a id='next' href='#'>Next ></a>");
    $("#next").click(function (e) {
        e.preventDefault();
        if (total > to) {
            list(to, to + 10);
            to = to + 10;
        }
    });
    $("#prev").click(function (e) {
        e.preventDefault();
        if (to > 10) {
            to = to - 10;
            list(to - 10, to);
        }
    });
    $(".tbodyInput").html(result);
};