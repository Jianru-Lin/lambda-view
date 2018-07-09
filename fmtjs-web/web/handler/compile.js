var fmtjs = require('../fmtjs')
var loader = require('../fmtjs-loader')
var version = require('../../package.json').version
var pkgs = require('../../pkgs/')
var share = require('../share/')
var enc = require('../internal/enc')
var is_url = require('../internal/is_url')
var is_npm_package_name = require('../internal/is_npm_package_name')
var native_module_source = process.binding ("natives")

var compile_service = {}

compile_service.text = function(info, cb) {
	if (typeof info.content !== 'string') {
		cb(new Error('content required'))
		return
	}

	compile({
		filename: info.filename,
		content: info.content
	}, cb)
}

compile_service.package = function(info, cb) {
	if (typeof info.name !== 'string') {
		cb(new Error('name required'))
		return
	}

	// url?
	if (is_url(info.name)) {
		loader.load(info.name, function(err, result) {
			if (err) {
				cb(err)
				return
			}
			compile({
				filename: result.filename,
				content: result.content
			}, cb)
		})
		return
	}

	// native module?
	if (typeof native_module_source[info.name] === 'string') {
		compile({
			filename: info.name,
			content: native_module_source[info.name]
		}, cb)
		return
	}

	var target = pkgs.resolve(info.name)
	if (!target) {
		if (is_npm_package_name(info.name)) {
			cb(new Error('Package ' + JSON.stringify(info.name) + ' not found, maybe you didn\'t installed it yet.'))
		}
		else {
			cb(new Error('Not found: ' + info.name))
		}
		return
	}

	loader.load(target, function(err, result) {
		if (err) {
			cb(err)
			return
		}
		compile({
			filename: target,
			content: result.content
		}, cb)
	})
}

compile_service.file = function(info, cb) {
	if (typeof info.filename !== 'string') {
		cb(new Error('filename required'))
		return
	}

	loader.load(info.filename, function(err, result) {
		if (err) {
			cb(err)
			return
		}
		compile({
			filename: info.filename,
			content: result.content
		}, cb)
	})
}

module.exports = function(app) {
	app.post('/compile', function(req, res) {
		if (!req.body || typeof req.body.type !== 'string') {
			res.json({
				ok: false,
				error: 'invalid request'
			})
			return
		}

		var handler = compile_service[req.body.type]
		if (!handler) {
			res.json({
				ok: false,
				error: 'type required'
			})
			return
		}

		handler(req.body, function(err, result) {
			if (err) {
				res.json({
					ok: false,
					error: err.message
				})
			}
			else {
				res.json({
					ok: true,
					result: result
				})
			}
		})
	})
}

function compile(target, cb) {
	var filename = target.filename
	var content = target.content

	content = preprocess(content)

	try {
		var fmtjs_result = fmtjs(content, {
			mode: 'html', 
			filename: name_part_of(filename),
			source: filename, 
			version: version
		})

		var result = {
			id: fmtjs_result.id,
			id_url: share.url() + '/lv.html?id=' + fmtjs_result.id,
			url: share.url() + '/lv.html?package=' + enc(filename)
		}

		cb(null, result)
	}
	catch (err) {
		cb(err)
	}

	function name_part_of(filename) {
		var pathname = null
		if (is_url(filename)) {
			pathname = require('url').parse(filename).pathname
		}
		else {
			pathname = filename
		}
		return require('path').basename(pathname)
	}
}

function preprocess(content) {
	return strip_shebang(strip_bom(content))
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