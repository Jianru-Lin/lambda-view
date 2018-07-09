var dbg = require('debug')('fmtjs-web:start')
var assert = require('assert')
var app = require('../web')
var config = require('../config')

// # cb(err, status)
module.exports = function(opt, cb) {
	var self = this

	opt = opt || {}

	if (opt.background) {
		// run in a new process, not this
		run_in_background(opt, cb)
		return
	}

	var host = opt.public ? '0.0.0.0' : config.host
	var port = config.port
	dbg('host=' + host + ', port=' + port)

	var onetime_cb = (function() {
		var invoked = false
		return function() {
			if (invoked) return
			if (cb) cb.apply(this, arguments)
		}
	})();

	app.start(port, host, function() {
		self.status(function(err, status) {
			assert(!err)
			onetime_cb(undefined, status)
		})
	}).once('error', function(err) {
		onetime_cb(err)
	})
}

// # cb(err, status)
function run_in_background(opt, cb) {
	// check if it is runing already?
	is_web_server_started(
		function yes() {
			cb(new Error('another instance exists already'))
		},
		function no() {
			opt = JSON.parse(JSON.stringify(opt))
			delete opt.background

			var path = require('path')
			var spawn = require('child_process').spawn
			var child = spawn('node', [__filename, JSON.stringify(opt)], {detached: true})
			child.stdin.unref()
			child.stdout.unref()
			child.stderr.unref()
			child.unref()

			// wait for a short time
			// then retrieve status info to check
			setTimeout(function() {
				require('../index').status(function(err, status) {
					assert(!err)
					cb(undefined, status)
				})
			}, 1000)
		}
	)

	function is_web_server_started(yes_cb, no_cb) {
		require('../index').status(function(err, status) {
			if (err) {
				no_cb()
			}
			else {
				yes_cb()
			}
		})
	}
}

// trick...

;(function() {
	if (require.main !== module) return

	if (process.argv.length !== 3) {
		console.log('Do not invoke this program by hand.')
		return
	}

	var opt_str = process.argv[2]

	try {
		var opt = JSON.parse(opt_str)
	}
	catch (err) {
		console.error('Parsing as JSON failed: ' + opt_str)
		return
	}

	require('../index').start(opt)
})();