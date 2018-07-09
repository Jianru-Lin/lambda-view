var fmtjs = require("../");

if (!process.argv[2]) {
  console.log("Usage: node fmtjs.js <js-file>");
  process.exit(0);
}

var text = file(process.argv[2]);
var result = fmtjs(text, {
  mode: "html"
});

console.log(JSON.stringify(result, null, 4));

function file(filename) {
  try {
    var fs = require("fs");
    return fs.readFileSync(filename, "utf8");
  } catch (err) {
    console.error("Load File Fail: " + err.message);
  }
}
