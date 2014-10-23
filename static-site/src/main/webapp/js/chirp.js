// Library for chirp

var hostname = location.hostname;
var apiServerPrefix = (hostname=='localhost')?"http://localhost:8081":"";
console.log("Using server prefix "+apiServerPrefix);

function postChirp()
{
    var value = $("#chirp-text").val();
    if (!!value) // Only if its non-empty
    {
        var formDataString = JSON.stringify({message: value});

        $.ajax({
            type: "POST",
            url: apiServerPrefix+"/api/v1/chirp/new",
            data: formDataString,

            contentType: "application/json; charset=utf-8",
            success: function (res, statusText, xhr, $form) {
                console.log("Success posting");
                reloadChirps();
            },
            error: function (res, statusText, xhr, $form) {
                alert("Error posting:"+statusText);
            },
            dataType: 'json'
        });
    }
    else
    {
        console.log("Skipping post, nothing found");
    }
}

function reloadChirps()
{
    var contain = $("#chirps");
    contain.html("<h1>Loading chirps...</h1>");

    $.ajax({
        type: "GET",
        url: apiServerPrefix+"/api/v1/chirp/list",
        contentType: "application/json; charset=utf-8",
        success: function (res, statusText, xhr, $form) {
            contain.html("");
            $.each(res, function (index, value)
            {
                contain.append("<div>At "+new Date(value.created)+" : "+value.message+"</div></hr>");
            });
        },
        error: function (res, statusText, xhr, $form) {
            alert("Error reading : "+statusText);
        },
        dataType: 'json'
    });
}

$(document).ready(function () {

    // Bind postChirp to the submit button
    $("#submitChirpButton").click(postChirp);
    $("#refreshChirpButton").click(reloadChirps);

    reloadChirps();

});