// Chirp things

$(document).ready(function () {
    console.log("Chirp page loaded");
    var api = apigClientFactory.newClient();
    console.log("Api interface created : "+api);

    api.serverStatusGet({'error':''}, {}, {}).then(function (data) {
        $("#output").html(JSON.stringify(data));
    }, function (err) {
        alert("There was an error!\n\n" + JSON.stringify(err));
    });

});