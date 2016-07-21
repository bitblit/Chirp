// Chirp things

var api = apigClientFactory.newClient();
var pingInterval = 500;
console.log("Api interface created");

$(document).ready(function () {
    console.log("Chirp page loaded");

    // Start the update pump
    updateChirps();
    $("#sendChirpBtn").click(postChirp);

    $("#chirpText").keyup(function(ev) {
        // 13 is ENTER
        if (ev.which === 13) {
            postChirp();
        }
    });

});

function postChirp()
{
    console.log("Post chirp");
    var text = $("#chirpText").val();
    if (!!text)
    {
        $("#chirpText").val("");
        api.chirpPost({},{'chirp-text':text},{}).then(function(data){
            console.log("Rval : "+JSON.stringify(data));
        },
        function(err){
           console.log("Error : "+err);
        });
    }
}

// Setup as a standalone function so we can do a timeout poll
function updateChirps()
{
    api.chirpGet({},{},{}).then(function(data){
            var allChirps = data.data.data.reverse(); // to get in time order

            var newHtml = "";
            $.each(allChirps, function (idx, chirp){
                newHtml+='<li class="list-group-item">';
                newHtml+='At '+new Date(chirp.timestamp*1000)+', '+chirp.userId+' said : '+chirp.chirp_text;
                newHtml+='</li>';
            });
            $("#chirpList").html(newHtml);
            setTimeout(updateChirps, pingInterval);
        },
        function(err){
            console.log("Error loading chirps! "+err);
            setTimeout(updateChirps, pingInterval);
        })

}
