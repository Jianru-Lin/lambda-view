var fs = require('fs')
var path = require('path')
var esprima = require('esprima')

function load_text(name) {
	if (!/\.js$/i.test(name)) {
		name = name + '.js'
	}
	var filename = path.resolve(__dirname, 'demo', name)
	var text = fs.readFileSync(filename, 'utf8')
	return text
}

function load_ast(name) {
	var text = load_text(name)
	var ast = esprima.parse(text, {sourceType: 'module'})
	return ast
}

function list() {
	var demo_dir = path.resolve(__dirname, 'demo')
	var list = []
	files_in_dir(demo_dir, list)
	list = list.map(function(name) {
		return path.relative(demo_dir, name).replace(/\.js$/i, '')
	})
	return list

	function files_in_dir(dir, out_list) {
		fs.readdirSync(dir).map(function(item) {
			var name = path.resolve(dir, item)
			var stats = fs.statSync(name)
			if (stats.isFile()) {
				out_list.push(name)
			}
			else if (stats.isDirectory()) {
				files_in_dir(name, out_list)
			}
			else {
				// ignore
			}
		})
	}
}

exports.load_text = load_text
exports.load_ast = load_ast
exports.list = list
