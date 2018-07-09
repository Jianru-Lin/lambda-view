var fs = require('fs')
var path = require('path')
var request = require('request')

function is_url(str) {
	return /^http(s)?:\/\//i.test(str)
}

function guess_filename(url, res_headers) {
	if (res_headers && res_headers['content-disposition']) {
		return res_headers['content-disposition']
	}
	else {
		var pathname = require('url').parse(url).pathname
		var basename = require('path').basename(pathname)
		return basename
	}
}

// load specific file as utf-8 file and return
// the file content without bom and shebang
function load_utf8_file(filename) {
	filename = path.resolve(filename)
	var content = fs.readFileSync(filename, 'utf8')
	return content
}

function load_from_url(url, cb) {
	cb = cb || function(err, result) {}

	var opt = {
		gzip: true,
	}
	request(url, function(err, res, body) {
		if (err) {
			cb(err)
		}
		else if (res.statusCode !== 200) {
			cb(new Error('response status code is ' + res.statusCode))
		}
		else {
			var filename = guess_filename(url, res.headers) || hash(body)
			body = body.toString('utf8')
			var result = {
				filename: filename,
				content: body
			}
			cb(undefined, result)
		}
	})
}

function load(target, cb) {
	if (is_url(target)) {
		load_from_url(target, cb)
	}
	else {
		try {
			var pathname = path.resolve(target)
			var filename = path.basename(pathname)
			var content = load_utf8_file(pathname)
			cb(null, {
				filename: filename,
				content: content
			})
		}
		catch (err) {
			// console.error(err.message)
			cb(err)
		}
	}
}

exports.is_url = is_url
exports.guess_filename = guess_filename
exports.is_url = is_url
exports.load_utf8_file = load_utf8_file
exports.load_from_url = load_from_url
exports.load = load
