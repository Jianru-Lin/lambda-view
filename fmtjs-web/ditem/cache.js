var fs = require('fs')
var path = require('path')
var os = require('os')

var cache_dir_path = path.resolve(os.tmpdir(), 'lambda-view-cache')
mkdir(cache_dir_path)

exports.root = function() {
	return cache_dir_path
}

exports.mkdir = function(name) {
	var fullname = path.resolve(cache_dir_path, name)
	mkdir(fullname)
}

exports.write_file = function(name, content) {
	var fullname = path.resolve(cache_dir_path, name)
	// console.log('write ' + fullname)
	fs.writeFileSync(fullname, content)
}

function mkdir(fullname) {
	try {
		fs.mkdirSync(fullname)
	}
	catch (err) {
		// ignore
	}
}