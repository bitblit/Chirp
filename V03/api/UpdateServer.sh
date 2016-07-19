#!/usr/bin/env bash

deploy_function(){
    #echo "1 is -$1- and 2 is -$2-"
    if [ -z $1 ] || [ $1 = $2 ]
    then
        echo "deploying $2"
        aws lambda update-function-code --function-name $2 --s3-bucket my-bucket --s3-key LambdaDeploy.zip
    else
        echo "skipping $2"
    fi
}

echo 'Creating zip file'
cd src
zip -r ../LambdaDeploy.zip .
cd ..

echo 'File created, uploading to S3'
aws s3 cp LambdaDeploy.zip s3://my-bucket/LambdaDeploy.zip

echo 'Uploaded, updating lambda'

deploy_function "$1" 'argon-server-info' 

echo 'Updates complete'
echo 'Cleaning up local file'
rm LambdaDeploy.zip

echo 'Cleaning up s3'
aws s3 rm s3://my-bucket/Argon/LambdaDeploy.zip

echo 'Finished'

