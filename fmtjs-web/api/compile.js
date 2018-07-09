var dbg = require('debug')('fmtjs-web:status')
var app = require('../web')
var config = require('../config')
var request = require('request')

// # cb(err, status)
module.exports = function(opt, cb) {
	cb = cb || function() {}

	var compile_url = config.url() + '/compile'
	dbg('post ' + compile_url)
	request(
		{
			method: 'POST',
			url: compile_url,
			json: true,
			body: opt
		},
		function(err, res, body) {
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
				if (typeof body === 'string') {
					body = JSON.parse(body)
				}
			}
			catch (err) {
				var err = 'Parsing body as JSON format failed'
				dbg(err.message)
				dbg('body: ' + body)
				cb(err)
				return
			}

			cb(undefined, body)
		}
	)
}