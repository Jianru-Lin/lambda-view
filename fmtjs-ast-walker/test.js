var walk = require('./index')

// console.log(process.argv)
var content = fs.readFileSync(process.argv[2], 'utf8')
walk(content)