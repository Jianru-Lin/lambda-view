var fs = require('fs')
var path = require('path')

// load specific file as utf-8 file and return
// the file content without bom and shebang
function load_utf8_file(filename) {
	filename = path.resolve(filename)
	var content = fs.readFileSync(filename, 'utf8')
	return strip_shebang(strip_bom(content))
}

// save content to specific file as utf-8 encoding
// without bom
function save_utf8_file(filename, content) {
	filename = path.resolve(filename)
	fs.writeFileSync(filename, content)
}

function strip_bom(content) {
	if (content.charCodeAt(0) === 65279) {
		content = content.slice(1)
	}
	return content
}

function strip_shebang(content) {
	return content.replace(/^\#\!.*/, "")
}

function tmp_dir() {
	return require('os').tmpdir()
}

function hash(text) {
	var algorithm = require('crypto').createHash('sha256')
	algorithm.update(text)
	return algorithm.digest('hex')
}

function open_html_file(filename) {
	var exec = require('child_process').exec
	switch (require('os').platform()) {
		case 'darwin':
			exec('open ' + JSON.stringify(filename))
			return true
		case 'win32':
			exec(JSON.stringify(filename))
			return true
		default:
			return false
	}
}

function date() {
	var d = new Date()
	return d.toLocaleDateString() + ' ' + d.toLocaleTimeString()
}

exports.load_utf8_file = load_utf8_file
exports.save_utf8_file = save_utf8_file
exports.tmp_dir = tmp_dir
exports.hash = hash
exports.open_html_file = open_html_file
exports.log = {
	info: function(text) {
		console.log('[?d] INFO '.replace('?d', date()) + text)
	},
	error: function(text) {
		console.log('[?d] ERROR '.replace('?d', date()) + text)
	}
}