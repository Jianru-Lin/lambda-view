var fs = require('fs')
var path = require('path')
var chalk = require('chalk')
var loader = require('fmtjs-loader')
var open = require('open')

function load_utf8_file(filename, cb) {
	loader.load(filename, function(err, result) {
		if (!err) {
			result.content = strip_shebang(strip_bom(result.content))
		}
		cb(err, result)
	})
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
  open(filename)
  return true
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
		console.log(
			[
				chalk.blue('[?d] INFO'.replace('?d', date())),
				text
			].join(' ')
		)
	},
	error: function(text) {
		console.log(
			[
				chalk.blue('[?d] ERROR'.replace('?d', date())),
				chalk.red(text)
			].join(' ')
		)
	}
}