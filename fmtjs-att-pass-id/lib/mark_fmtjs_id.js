var walk = require('fmtjs-ast-walker').walk

function mark_fmtjs_id(ast) {
	var next_id = 0
	walk(ast, {
		enter: function(node, nav) {
			var v = next_id++
			node.fmtjs_id = v.toString()
		}
	})
	return ast
}

module.exports = mark_fmtjs_id