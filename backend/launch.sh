#!/bin/bash

# Move to frontend and build
echo "🔧 Building frontend..."
cd ../frontend || exit
npm run build

# Move back to server and start it
echo "🚀 Starting server with nodemon..."
cd ../backend || exit
nodemon server_production.js
