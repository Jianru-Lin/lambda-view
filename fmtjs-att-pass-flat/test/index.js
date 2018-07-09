var assert = require('chai').assert
var demo = require('fmtjs-demo')
var flat = require('../index')

describe('index', function() {
	it('run without any exception', function() {
		var ast = demo.load_ast('empty')
		var ret = flat(ast)
		assert.isObject(ret)
		assert.isArray(ret.list)
		assert.isObject(ret.parent)
	})
})