var pass_id = require('../fmtjs-att-pass-id')
var pass_flat = require('../fmtjs-att-pass-flat')
// var pass_lexical = require('../fmtjs-att-pass-lexical')

function run(ast) {
	var ctx = {}
	pass_id(ast)
	ctx['flat'] = pass_flat(ast)
	// ctx['lexical'] = pass_lexical(ast)
	return ctx
}

module.exports = run