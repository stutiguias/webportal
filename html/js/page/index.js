var box = function () {
    $.ajax({
        url: window.qualifyURL("/box/1"),
        success: function (data) {
            $('#box1').html(data);
        },
        error: function (error) {
            $('#box1').html(error);
        },
    });

    $.ajax({
        url: window.qualifyURL("/box/2"),
        success: function (data) {
            $('#box2').html(data);
            $('#mailread').click(function () {
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
}();
