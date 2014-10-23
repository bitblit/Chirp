// Library for chirp

$(document).ready(function () {
    $.ajax({
        type: "GET",
        url: "/api/v1/info/server",
        contentType: "application/json; charset=utf-8",
        success: function (res, statusText, xhr, $form) {
            $("serverTime").html(res.serverTime);
        },
        error: function (res, statusText, xhr, $form) {
            $("serverTime").html("<h1>Error trying to lookup time: "+statusText+"</h1>");
        },
        dataType: 'json'
    });
});