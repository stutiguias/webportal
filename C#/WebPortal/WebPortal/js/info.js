$(document).ready(function () {

    $.ajax({
        url: '/server/username/info',
        success: function (data) {
            try {
                var vals = data.split(',');
                $('#user').html(vals[0] + vals[1]);
                $('#money').html(vals[2]);
                $('#mail').html(vals[3]);
                $('#avatarimg').attr('src', 'http://minotar.net/avatar/' + vals[0]);

            } catch (err) {
                $('#user').html(err);
            };
        },
        error: function (data) {
            $('#user').html("Null");
        },
        dataType: "text"
    });

});

function buy(form) {
    var ar = $(form).serializeArray();
    $.ajax({
        url: form.action,
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

function postauction(form) {
    var ar = $(form).serializeArray();
    $.ajax({
        url: form.action,
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

function mail(form) {
    var ar = $(form).serializeArray();
    $.ajax({
        url: form.action,
        data: $(form).serialize(),
        success: function (data) {
                $(form).hide();
                $('#' + 'M' + ar[0].value).html(data);
        },
        error: function (error) {
                $(form).hide();
                $('#' + 'M' + ar[0].value).html(error);
        },
        dataType: "text"
    });

    return false;
};

function cancel(form) {
    var ar = $(form).serializeArray();
    $.ajax({
        url: form.action,
        data: $(form).serialize(),
        success: function (data) {
            $(form).hide();
            $('#' + 'C' + ar[0].value).html(data);
        },
        error: function (error) {
            $(form).hide();
            $('#' + 'C' + ar[0].value).html(error);
        },
        dataType: "text"
    });

    return false;
};


function box() {
    $.ajax({
        url: '/box/1',
        success: function (data) {
            $('#box1').html(data);
        },
        error: function (error) {
            $('#box1').html(error);
        },
    });

    $.ajax({
        url: '/box/2',
        success: function (data) {
            $('#box2').html(data);
            $('#mailread').click(function  () {
                $('div#mail').show("slow");
            });
            $('#mailclose').click(function () {
                $('div#mail').hide("slow");
            });
        },
        error: function (error) {
            $('#box2').html(error);
        },
    });
};

