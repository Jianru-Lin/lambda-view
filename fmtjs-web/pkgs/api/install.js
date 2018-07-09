var fs = require('fs')
var path = require('path')
var share = require('../share/')

// # cb(err)
module.exports = function(name, cb) {
	cb = cb || function() {}
	var package_dir = path.resolve(share.node_modules_dir, name)
	if (fs.existsSync(package_dir)) {
		// installed already
		setTimeout(function() {
			cb(null)
		}, 0)
	}
	else {
		// install now...
		install(name, cb)
	}
}

function install(name, cb) {
	// BUG: reenter possibly
	// Security: check name
	var exec = require('child_process').exec
	var cmd = 'npm install ' + name
	var opt = {
		cwd: path.resolve(__dirname, '..')
	}
	exec(cmd, opt, function(err, stdout, stderr) {
		if (err) {
			cb(err)
		}
		else {
			cb(null)
		}
	})
}
