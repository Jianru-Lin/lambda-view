var fs = require('fs')
var path = require('path')
var share = require('../share/')

module.exports = function() {
	try {
		return fs.readdirSync(share.node_modules_dir).filter(function(item) {
			var fullname = path.resolve(share.node_modules_dir, item)
			try {
				if (fs.lstatSync(fullname).isDirectory()) {
					return true
				}
				else {
					return false
				}
			}
			catch (err) {
				console.error(err)
				return false
			}
		})
	}
	catch (err) {
		console.error(err)
		return []
	}
}

