function adm(form) {
    $.ajax({
        url: admsearch,
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