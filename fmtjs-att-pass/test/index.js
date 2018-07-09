var assert = require('chai').assert
var demo = require('../fmtjs-demo')
var index = require('../index')

describe('index', function() {
	it('run without exception', function() {
		var ast = demo.load_ast('empty')
		var ctx = index(ast)
		assert.isObject(ctx)
	})
})