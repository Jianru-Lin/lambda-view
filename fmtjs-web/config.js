exports.host = '127.0.0.1'
exports.port = 23400

exports.url = function() {
	return 'http://127.0.0.1' + (exports.port === 80 ? '' : (':' + exports.port))
}