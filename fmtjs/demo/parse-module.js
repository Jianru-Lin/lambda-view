var esprima = require("esprima");

if (!process.argv[2]) {
  console.log("Usage: node parse-module.js <js-file>");
  process.exit(0);
}

var text = file(process.argv[2]);
var ast = esprima.parseModule(text, {
  sourceType: "module",
  comment: true,
  attachComment: true
});

console.log(JSON.stringify(ast, null, 4));

function file(filename) {
  try {
    var fs = require("fs");
    return fs.readFileSync(filename, "utf8");
  } catch (err) {
    console.error("Load File Fail: " + err.message);
  }
}
