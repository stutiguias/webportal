
var box = function () {
    $.ajax({
        url: urlbox1,
        success: function (data) {
            $('#box1').html(data);
        },
        error: function (error) {
            $('#box1').html(error);
        },
    });

    $.ajax({
        url: urlbox2,
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
