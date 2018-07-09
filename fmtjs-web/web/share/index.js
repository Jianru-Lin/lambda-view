var assert = require('assert')
var os = require('os')

exports.host = null
exports.port = null

exports.url = function() {
	assert(exports.host)
	assert(exports.port)

	var host = exports.host
	var port = exports.port

	if (host === '0.0.0.0') {
		host = main_ip() || '127.0.0.1'
	}
	
	return 'http://' + host + (port === 80 ? '' : (':' + port))
}

function main_ip() {
	var ipv4 = []
	var ipv6 = []
	var networkInterfaces = os.networkInterfaces()
	for (var name in networkInterfaces) {
		networkInterfaces[name].forEach(function(item) {
			if (item.internal) return
			if (item.family === 'IPv4') {
				ipv4.push(item)
			}
			else if (item.family === 'IPv6') {
				ipv6.push(item)
			}
		})
	}
	if (ipv4.length) {
		return ipv4[0].address
	}
	else if (ipv6.length) {
		return ipv6[0].address
	}
	else {
		// impossible
		return null
	}
}