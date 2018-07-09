var assert = require('assert')
var walk_type = require('fmtjs-ast-walker').walk_type
var walk = require('fmtjs-ast-walker').walk

function flat(ast) {
	// copy ast...
	ast = cp(ast)

	var stack = {
		_stack: [],
		_parent: {},
		_list: [],
		_clean_cb: [],
		push: function(node, nav) {
			this._list.push(node)
			if (this._stack.length > 0) {
				this._parent[node.fmtjs_id] = this._stack[this._stack.length-1].fmtjs_id

				// 我们在此时记录下一个清理函数
				// 之后会调用
				this._clean_cb.push(gen_clean_cb())
			}
			this._stack.push(node)

			function gen_clean_cb() {
				var parent = nav.parent()
				var rel = nav.rel()
				assert(parent)
				// assert(rel === 'body')
				return function() {
					if (parent[rel] === node) {
						parent[rel] = dump_ref(node)
						return
					}
					var list = parent[rel]
					assert(Array.isArray(list))
					for (var i = 0; i < list.length; ++i) {
						if (list[i] === node) {
							// replace to reference version
							list[i] = dump_ref(node)
							break
						}
					}
				}
			}

		},
		pop: function() {
			this._stack.pop()
		}
	}

	walk_type(ast, {
		'Program': {
			enter: function(node, nav) {
				stack.push(node, nav)
			},
			leave: function() {
				stack.pop()
			}
		},
		'FunctionDeclaration': {
			enter: function(node, nav) {
				stack.push(node, nav)
			},
			leave: function() {
				stack.pop()
			}
		},
		'FunctionExpression': {
			enter: function(node, nav) {
				stack.push(node, nav)
			},
			leave: function() {
				stack.pop()
			}
		},
		// // ArrowFunctionExpression 的 body 可能不是 BlockStatement 所以要重新考虑处理方法
		// 'ArrowFunctionExpression': {
		// 	enter: function(node, nav) {
		// 		stack.push(node, nav)
		// 	},
		// 	leave: function() {
		// 		stack.pop()
		// 	}
		// }
	})

	// stack._list 中此时每一项都是完整的，我们现在把它们清理一下
	stack._clean_cb.forEach(function(f) {
		f()
	})

	var ret = {
		list: stack._list,
		parent: stack._parent
	}

	return ret
}

function dump_ref(node) {
	var new_node = {}
	for (var k in node) {
		if (k === 'body') {
			new_node[k] = cp_block_statement(node.fmtjs_id, node[k])
		}
		else {
			new_node[k] = node[k]
		}
	}
	return new_node
}

function cp_block_statement(target_id, node) {
	assert(node)
	assert(node.type === 'BlockStatement')
	var new_node = {}
	for (var k in node) {
		if (k === 'body') {
			// don't copy body
			assert(Array.isArray(node[k]))
			new_node[k] = []
		}
		else {
			new_node[k] = node[k]
		}
	}
	new_node.fmtjs_ref_id = target_id
	return new_node
}

function cp(ast) {
	return JSON.parse(JSON.stringify(ast))
}

module.exports = flat