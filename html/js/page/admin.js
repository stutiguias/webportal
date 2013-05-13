var Interval = setInterval(function () {
    get("", "/adm/getinfo");
    seeconsole();
}, 5000);

$(function () {
    get("", "/adm/getinfo");
    seeconsole();
});

function intervalstart() {
    $(".serverinfo").show();
    Interval = setInterval(function () {
        get("", "/adm/getinfo");
        seeconsole();
    }, 5000);
}

function getinfo() {
    intervalstart();
    get("", "/adm/getinfo");
    return false;
}

function svpllist() {
    get("", "/adm/playerlist");
    return false;
}

function consoleinfohide() {
    $(".serverinfo").hide();
    clearInterval(Interval);
}

var get = function (form, url) {
    $(".table").show();
    $(".pagination").html("");
    hideall();
    $.ajax({
        url: url,
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
                $('#resultado').html(err.message);
            };
        },
        error: function (error,data) {
            $('#resultado').html(error);
        },
        dataType: "json"
    });

    return false;
}

function admshop() {
    $(".table").show();
    consoleinfohide();
    $(".GetInfoPlayer").hide();
    $(".addItemShop").show();
    return false;
}

function hideall() {
    $(".GetInfoPlayer").hide();
    $(".addItemShop").hide();
}

function infoplayer() {
    $(".table").show();
    consoleinfohide();
    $(".addItemShop").hide();
    $(".GetInfoPlayer").show();
    return false;
}

function viewplugins(form) {
    $(".table").show();
    hideall();
    consoleinfohide();
    $(".pagination").html("");
    get(form, "/adm/viewplugins");
    return false;
}

function sendmsg(form) {
    $.ajax({
        url: "/adm/sendmsg",
        data: $(form).serialize(),
        success: function (data) {
            $('#resultado').html(data);
        },
        error: function (error, data) {
            $('#resultado').html(error);
        },
        dataType: "text"
    });
    return false;
}

function seeconsole() {
    $.ajax({
        url: "/adm/seeconsole",
        success: function (data) {
            var result = "";
            $.each(data, function (key, val) {
                result += "<br />" + replace(val);
            });
            $('.console').html(result);
        },
        error: function (error, data) {
            $('#resultado').html(error);
        },
        dataType: "json"
    });
    return false;
}


function replace(val) {
    val = replaceAll("\\[m", "", val);
    val = replaceAll("<", "[", val);
    val = replaceAll(">", "]", val);
    val = replaceAll("\\[0;31;1m", "", val);
    val = replaceAll("\\[0;33;1m", "", val);
    val = replaceAll("\\[0;33;22m", "", val);
    val = replaceAll("\\[0;32;1m", "", val);
    return val;
}

function replaceAll(find, replace, str) {
    var regex = new RegExp(find, "igm");
    return str.replace( regex , replace);
}

function sendcmd(form) {
    $.ajax({
        url: "/adm/sendcmd",
        data: $(form).serialize(),
        success: function (data) {
            var result = "";
            $.each(data, function (key, val) {
                result += "<br />" + replace(val);
            });
            $('.console').html(result);
        },
        error: function (error, data) {
            $('#resultado').html(error);
        },
        dataType: "json"
    });
    return false;
}

function shutdown(form) {
    hideall();
    $.ajax({
        url: "/adm/shutdown",
        success: function (data) {
            $('#resultado').html(data);
        },
        error: function (error, data) {
            $('#resultado').html(error);
        },
        dataType: "text"
    });
    return false;
}


function reload(form) {
    hideall();
    $.ajax({
        url: "/adm/reload",
        success: function (data) {
            $('#resultado').html(data);
        },
        error: function (error, data) {
            $('#resultado').html(error);
        },
        dataType: "text"
    });
    return false;
}


function adm(form) {
    $(".table").show();
    $(".pagination").html("");
    consoleinfohide();
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
    $(".GetInfoPlayer").hide();
    return false;
};

function additem(form) {
    consoleinfohide();
    $(".table").show();
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
    $(".addItemShop").hide();
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
    consoleinfohide();
    $(".table").show();
    hideall();
    list(0, 10);
    return false;
};

var to = 10;

function list(to, from) {
    $(".pagination").html("");
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
    $(".pagination").html("<a id='prev' href='#'>< Previous</a> Showing " + (to - 10) + " - " + to + " of " + total + " <a id='next' href='#'>Next ></a>");
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