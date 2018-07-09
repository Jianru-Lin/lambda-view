var share = require('../share/')
var app = require('../index')
var os = require('os')

module.exports = function(app) {
	app.get('/status', function(req, res) {
		res.json({
			ok: true,
			version: require('../../package').version,
			url: share.url(),
			timestamp: new Date().toISOString()
		})
	})
}