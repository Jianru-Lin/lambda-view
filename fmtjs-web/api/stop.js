var dbg = require('debug')('fmtjs-web:stop')
var app = require('../web')
var config = require('../config')
var request = require('request')

module.exports = function(cb) {
	cb = cb || function() {}

	var stop_url = config.url() + '/stop'
	dbg('post ' + stop_url)
	request.post(stop_url, function(err, res, body) {
		if (err) {
			dbg(err.message)
			cb(err)
			return
		}

		if (res.statusCode !== 200) {
			var err = new Error('Status code of response is not 200')
			dbg(err.message)
			dbg('res.statusCode: ' + res.statusCode)
			cb(err)
			return
		}

		dbg('done.')
		cb()
	})
}