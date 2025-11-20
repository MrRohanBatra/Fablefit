#!/bin/bash

# Move to frontend and build
echo "🔧 Building frontend..."
cd ../frontend || exit
npm run build

# Move back to server and start it
echo "🚀 Starting server with nodemon..."
cd ../server || exit
nodemon server.js
