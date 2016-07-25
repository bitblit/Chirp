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
    var file = $("#chirpFile").val();
    if (!!text)
    {
        $("#waitTab").show();
        $("#postTab").hide();
        $("#chirpText").val("");

        if (!file)
        {
            // Post away!
            writeChirpToAPI(text,'');
        }
        else
        {
            // Grab a url
            api.chirpImageLocationGet({},{},{}).then(
                function(data){
                    // Send the file to S3
                    uploadFileToS3(data.data.data.url, file[0]).then(function(s3Done){
                            writeChirpToAPI(text, data.data.data.path);
                    },
                    defaultAPIFailure);
                },
                defaultAPIFailure
            );
        }

    }
}

function defaultAPIFailure(err)
{
    aler("There was an api failure! : "+JSON.stringify(err));
}


function writeChirpToAPI(text, image)
{
    api.chirpPost({},{'chirp-text':text, 'image-location': image},{}).then(function(data){
            console.log("Rval : "+JSON.stringify(data));

            $("#waitTab").hide();
            $("#postTab").show();

        },
        defaultAPIFailure);
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
                if (!!chirp.image_url)
                {
                    newHtml+='<img src="'+chirp.image_url+'" width="24" height="24" />';
                }
                newHtml+='</li>';
            });
            $("#chirpList").html(newHtml);
            setTimeout(updateChirps, pingInterval);
        },
        defaultAPIFailure);

}

function uploadProgress(val)
{
    console.log("Progress :"+val);
}


function uploadFileToS3(s3url, content) {
    return $.ajax({
        url: s3url,
        type: 'PUT',
        data: content,
        processData: false,
        contentType: 'image/jpg',
        xhr: function () {
            var xhr = new window.XMLHttpRequest();
            xhr.upload.addEventListener("progress", uploadProgress);
            return xhr;
        }
    });
};
