var dbg = require('debug')('fmtjs-web:status')
var app = require('../web')
var config = require('../config')
var request = require('request')

// # cb(err, status)
module.exports = function(cb) {
	cb = cb || function() {}

	var status_url = config.url() + '/status'
	dbg('get ' + status_url)
	request.get(status_url, function(err, res, body) {
		if (err) {
			cb(err)
			return
		}

		if (res.statusCode !== 200) {
			var err = new Error('Status code of response is not 200')
			dbg(err.message)
			dbg('res.stausCode: ' + res.stausCode)
			cb(err)
			return
		}

		try {
			body = JSON.parse(body)
		}
		catch (err) {
			var err = 'Parsing body as JSON format failed'
			dbg(err.message)
			dbg('body: ' + body)
			cb(err)
			return
		}

		cb(undefined, body)
	})
}