// process.env['DEBUG'] = [
// 	'fmtjs-web',
// 	'fmtjs-web:*',
// ]

exports.start = require('./api/start')
exports.stop = require('./api/stop')
exports.status = require('./api/status')
exports.compile = require('./api/compile')
