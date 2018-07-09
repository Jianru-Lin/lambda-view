var fs = require('fs')
var path = require('path')
var share = require('../share/')

module.exports = function(name) {
	var package_dir = path.resolve(share.node_modules_dir, name)
	return fs.existsSync(package_dir)
}
