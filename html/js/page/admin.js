var get = function (form, url) {
    $(".table").show();
    $(".pagination").html("");
    hideall();
    $.ajax({
        url: window.qualifyURL(url),
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
    $(".GetInfoPlayer").hide();
    $(".listadmshop").hide();
    $(".addItemShop").show();
    return false;
}

function hideall() {
    $(".GetInfoPlayer").hide();
    $(".addItemShop").hide();
}

function infoplayer() {
    $(".table").show();
    $(".addItemShop").hide();
    $(".listadmshop").hide();
    $(".GetInfoPlayer").show();
    return false;
}

function replaceAll(find, replace, str) {
    var regex = new RegExp(find, "igm");
    return str.replace( regex , replace);
}

function websiteban(form) {
    hideall();
    $.ajax({
        url: window.qualifyURL("/adm/webban"),
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

function websiteunban(form) {
    hideall();
    $.ajax({
        url: window.qualifyURL("/adm/webunban"),
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


function adm(form) {
    $(".table").show();
    $(".pagination").html("");
    $.ajax({
        url: window.qualifyURL("/adm/search"),
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
        error: function (error,msg) {
            $('#resultado').html(error);
        },
        dataType: "json"
    });
    $(".GetInfoPlayer").hide();
    return false;
};

function additem(form) {
    $(".table").show();
    $.ajax({
        url: window.qualifyURL("/adm/addshop"),
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
        url: window.qualifyURL("/adm/deleteshop"),
        data: $(form).serialize(),
        success: function (data) {
            $('#resultado').html(data);
        },
        error: function (error) {
            $('#resultado').html(error);
        },
        dataType: "text"
    });
    $("#resultado").show();
    return false;
};

function listshop(form) {
    $("#resultado").hide();
    $(".table").show();
    hideall();
    list(0, 10);
    return false;
};

var to = 10;

function list(to, from) {
    $(".pagination").html("");
    $.ajax({
        url: window.qualifyURL("/adm/shoplist"),
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
    
        

    $(".pagination").html("<a id='prev' href='#'>< Previous</a> Showing " + (to - 9) + " - " + to + " of " + total + " <a id='next' href='#'>Next ></a>");

    if (total <= 10) {
        $("#next").hide();
        $("#prev").hide();
    }
        
    
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