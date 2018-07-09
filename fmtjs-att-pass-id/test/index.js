var assert = require('chai').assert
var demo = require('fmtjs-demo')
var mark_fmtjs_id = require('../index')

describe('index', function() {
	it('run without any exception', function() {
		var ast = demo.load_ast('empty')
		assert.isTrue(JSON.stringify(ast).indexOf('fmtjs_id') === -1)
		var ret = mark_fmtjs_id(ast)
		assert.isTrue(ret === ast)
		assert.isTrue(JSON.stringify(ast).indexOf('fmtjs_id') !== -1)
	})
})