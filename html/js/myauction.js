$(document).ready(function () {

    oTable = $('#example').dataTable({
        "sDom": "<'row'<'span4'l><'span4'f>r>t<'row'<'span4'i><'span4'p>>",
        "sPaginationType": "bootstrap",
        "bProcessing": true,
        "bJQueryUI": true,
        "bStateSave": false,
        "bServerSide": true,
        "sAjaxSource": "fill/myauctions",
        "oLanguage": {
            "sProcessing": jsIndex['sProcessing'],
            "sLengthMenu": jsIndex['sLengthMenu'],
            "sZeroRecords": jsIndex['sZeroRecords'],
            "sInfo": jsIndex['sInfo'],
            "sInfoEmpty": jsIndex['sInfoEmpty'],
            "sInfoFiltered": jsIndex['sInfoFiltered'],
            "sSearch": jsIndex['sSearch'],
            "sInfoPostFix": "",
            "sUrl": "",
            "oPaginate": {
                "sFirst": jsIndex['sFirst'],
                "sPrevious": jsIndex['sPrevious'],
                "sNext": jsIndex['sNext'],
                "sLast": jsIndex['sLast']
            }
        },
        "fnServerData": function (sSource, aoData, fnCallback) {
            $.ajax({
                "dataType": 'json',
                "type": "GET",
                "url": sSource,
                "data": aoData,
                "success": fnCallback,
                "timeout": 15000,
                "error": handleAjaxError
            });
        }
    });
});

function cancel(form) {
    var ar = $(form).serializeArray();
    $.ajax({
        url: cancelUrl,
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
