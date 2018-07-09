var pass = require('../fmtjs-att-pass')

function translate(ast) {
	var ctx = pass(ast)
	return {
		ast: ast,
		ctx: ctx
	}
}


module.exports = translate
