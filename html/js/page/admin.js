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