// Library for chirp

var hostname = location.hostname;
var apiServerPrefix = (hostname=='localhost')?"http://localhost:8081":"";
console.log("Using server prefix "+apiServerPrefix);

$(document).ready(function () {


        $.ajax({
        type: "GET",
        url: apiServerPrefix+"/api/v1/info/server",
        contentType: "application/json; charset=utf-8",
        success: function (res, statusText, xhr, $form) {
            $("#serverTime").html(new Date(res.serverTime));
        },
        error: function (res, statusText, xhr, $form) {
            $("#serverTime").html("<h1>Error trying to lookup time: "+statusText+"</h1>");
        },
        dataType: 'json'
    });

    $.ajax({
        type: "GET",
        url: "/api/v1/chirp/count",
        contentType: "application/json; charset=utf-8",
        success: function (res, statusText, xhr, $form) {
            $("#chirpCount").html(res);
        },
        error: function (res, statusText, xhr, $form) {
            $("#chirpCount").html("<h1>Error trying to lookup time: "+statusText+"</h1>");
        },
        dataType: 'json'
    });

});