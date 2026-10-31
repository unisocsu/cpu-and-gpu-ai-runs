console.log("Hello from Node.js!");
console.log("This app was compiled and run in the workspace environment.");

const fs = require('fs');
const data = {
    message: "Hello from the workspace!",
    timestamp: new Date().toISOString(),
    status: "success"
};

fs.writeFileSync('output.json', JSON.stringify(data, null, 2));
console.log("Output saved to output.json");
