var assert = require('chai').assert
var api = require('../index')

describe('API', function() {
	describe("#load_text()", function() {
		it('return string', function() {
			assert.isString(api.load_text('empty'))
		})
	})

	describe("#load_ast()", function() {
		it('return object', function() {
			assert.isObject(api.load_ast('empty'))
		})
	})

	describe("#list()", function() {
		it('return list', function() {
			assert.isArray(api.list())
		})
	})
})