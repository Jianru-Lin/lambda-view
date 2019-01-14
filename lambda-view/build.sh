#!/bin/bash

rm -rf resources/public/js/compiled-min
lein cljsbuild once min
cp resources/public/js/compiled-min/lambda_view.min.js /Users/Shared/repo/lambda-view-cloud/functions/file/render.js
cp resources/public/css/style.css /Users/Shared/repo/lambda-view-cloud/functions/file/render-style.css
