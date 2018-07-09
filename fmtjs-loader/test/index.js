var assert = require('chai').assert
var lib = require('../lib')

// fake http server
var fake_http = (function() {
	var js_code = 'console.log("hello, world")'
	var http = require('http')
	var server = http.createServer()
	server.on('request', function(req, res) {
		// request any url is ok, we just care the query parameter
		// - test=
		// - test=not_found
		// - test=filename_in_header&filename=xxx
		var url = require('url').parse(req.url, true)
		var query = url.query
		if (!query.test) {
			res.end(js_code)
		}
		else if (query.test === 'not_found') {
			res.statusCode = 400
			res.end()
		}
		else if (query.test === 'filename_in_header') {
			var filename = query.filename
			res.setHeader('content-disposition', query.filename)
			res.end(js_code)
		}
	})
	server.listen(51200)

	return {
		url: 'http://localhost:51200'
	}
})();

describe('lib', function() {

	describe('#is_url()', function() {
		
		it('return false for ""', function() {
			assert.isFalse(lib.is_url(''))
		})
		
		it('return true for "http://"', function() {
			assert.isTrue(lib.is_url('http://'))
		})
		
		it('return true for "HTTP://"', function() {
			assert.isTrue(lib.is_url('HTTP://'))
		})
		
		it('return true for "https://"', function() {
			assert.isTrue(lib.is_url('https://'))
		})
		
		it('return true for "HTTPS://"', function() {
			assert.isTrue(lib.is_url('https://'))
		})
	})

	describe('#guess_filename()', function() {

		it('return "file.js" for "/a/b/c/file.js"', function() {
			assert.isTrue(lib.guess_filename('/a/b/c/file.js') === 'file.js')
		})

		it('return "file.js" for "/a/b/c/file.js#blabla"', function() {
			assert.isTrue(lib.guess_filename('/a/b/c/file.js#blabla') === 'file.js')
		})

		it('return "file.js" for "/a/b/c/file.js?p=v"', function() {
			assert.isTrue(lib.guess_filename('/a/b/c/file.js?p=v') === 'file.js')
		})

		it('return "file.js" for "/a/b/c/file.js?p=v#blabla"', function() {
			assert.isTrue(lib.guess_filename('/a/b/c/file.js?p=v#blabla') === 'file.js')
		})

		it('return "file.js" for "/file.js"', function() {
			assert.isTrue(lib.guess_filename('/file.js') === 'file.js')
		})

		it('return "" for "/"', function() {
			assert.isTrue(lib.guess_filename('/') === '')
		})

		it('prefer values in headers', function() {
			var ret = lib.guess_filename('/a.js', {
				'content-disposition': 'b.js'
			})
			assert.isTrue(ret === 'b.js')
		})
	})

	describe('#load_from_url()', function() {
		var url_normal = fake_http.url + '/path/file.js?test'
		it('can load file from ' + JSON.stringify(url_normal), function(done) {
			lib.load_from_url(url_normal, function(err, result) {
				assert.isUndefined(err)
				assert.strictEqual(result.filename, 'file.js')
				assert.isString(result.content)
				assert.isTrue(result.content.length > 0)
				done()
			})
		})

		var url_400 = fake_http.url + '/path/file.js?test=not_found'
		it('report error if status code is not 200 ' + JSON.stringify(url_400), function(done) {
			lib.load_from_url(url_400, function(err, result) {
				assert.isTrue(err !== null && err !== undefined)
				assert.isUndefined(result)
				done()
			})
		})

		var url_special = fake_http.url + '/path/file.js?test=filename_in_header&filename=target.js'
		it('prefer content-disposition header ' + JSON.stringify(url_special), function(done) {
			lib.load_from_url(url_special, function(err, result) {
				assert.isUndefined(err)
				assert.strictEqual(result.filename, 'target.js')
				assert.isString(result.content)
				assert.isTrue(result.content.length > 0)
				done()
			})
		})
	})
})