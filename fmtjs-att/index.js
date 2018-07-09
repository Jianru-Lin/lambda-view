var assert = require('assert')
var translate = require('./lib/translate')
var fillIndent = require('./lib/fillIndent')
var translate4html = require('./lib/translate4html')

// 把 AST 转换成 ATT
function att(ast, options) {
	assert(ast)	
	options = fill_default_value(options)
	switch (options.mode) {
		case 'text':
			return fillIndent(translate(ast))
		case 'html':
			return translate4html(ast)
		default:
			throw new Error('invalid mode: ' + options.mode)
	}

	function fill_default_value(options) {
		options = options || {}
		options.mode = options.mode || 'html'
		return options
	}
}

module.exports = exports = att