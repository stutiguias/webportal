function loginform(form) {
    $.ajax({
        url: login,
        data: $(form).serialize(),
        success: function (data) {
            if (data == "ok") {
                window.location = '/index.html';
            } else if (data == "no") {
                $('.error').show();
                $('.error').html('Error trying to login:<br>The username must be case sensitive or check your password');
            }
        },
        error: function (error) {

        },
        dataType: "text"
    });

    return false;
};

for (var prop in langLogin) {
    var patt = new RegExp(prop, "g");
    $('.replace').each(function () {
        $(this).html($(this).html().replace(patt, langLogin[prop]));
    });
}

for (var prop in langIndex) {
    var patt = new RegExp(prop, "g");
    $('.replace').each(function () {
        $(this).html($(this).html().replace(patt, langIndex[prop]));
    });
}

$(document).ready(function () {
    $.ajax({
        url: getAuction,
        success: function (data) {
            try {
                var result = "";
                $.each(data, function (key, val) {
                    result += "<tr>";
                    $.each(val,function(key2,val2) {
                        result += "<td>" + val2 + "</td>";
                    });
                    result += "</tr>";
                });
                $(".tbodyInput").html(result);
            } catch (err) {
                $('.result').html(err);
            };
        },
        error: function (data) {
            $('.result').html(data);
        },
        dataType: "json"
    });
});