var stack = []

var nav = {
	parent: function() {
		if (stack[0]) {
			return stack[0].ast
		}
		else {
			return undefined
		}
	},
	rel: function() {
		if (stack[0]) {
			return stack[0].rel
		}
		else {
			return undefined
		}
	},
	// parent_at: function(i) {
	// 	return stack[i]
	// },
	// parent_len: function() {
	// 	return stack.length
	// },
	// parent_each: function(cb) {
	// 	stack.forEach(cb)
	// }
}

var callout = {
	enter: function(ast, parent) {
		console.log('callout.enter ' + ast.type)
	},
	leave: function(ast, parent) {
		console.log('callout.leave ' + ast.type)
	}
}

var type_handler = {}

type_handler['Program'] = function(ast) {
	walk_in(ast, 'body')
}

type_handler['ImportDeclaration'] = function(ast) {
	walk_in(ast, 'specifiers')
	walk_in(ast, 'source')
}

type_handler['ImportDefaultSpecifier'] = function(ast) {
	walk_in(ast, 'local')
}

type_handler['ImportSpecifier'] = function(ast) {
	assert(ast.local.type === ast.imported.type)
	assert(ast.local.type === 'Identifier')
	walk_in(ast, 'imported')
	walk_in(ast, 'local')
}

type_handler['ImportNamespaceSpecifier'] = function(ast) {
	walk_in(ast, 'local')
}

type_handler['ExportAllDeclaration'] = function(ast) {
	walk_in(ast, 'source')
}

type_handler['ExportNamedDeclaration'] = function(ast) {
	if (ast.declaration) {
		// 断言：有 declaration 必然就不可能有 specifiers
		assert(Array.isArray(ast.specifiers) && ast.specifiers.length === 0)
		walk_in(ast, 'declaration')
	}
	else {
		walk_in(ast, 'specifiers')
		walk_in(ast, 'source')
	}
}

type_handler['ExportDefaultDeclaration'] = function(ast) {
	walk_in(ast, 'declaration')
}

type_handler['ExportSpecifier'] = function(ast) {
	walk_in(ast, 'local')
	walk_in(ast, 'exported')
}

type_handler['EmptyStatement'] = function(ast) {
	// 无事可做
}

type_handler['DebuggerStatement'] = function(ast) {
	// 无事可做
}

type_handler['FunctionDeclaration'] = function(ast) {
	walk_in(ast, 'id')
	walk_in(ast, 'params')
	walk_in(ast, 'body')
}

type_handler['FunctionExpression'] = function(ast) {
	walk_in(ast, 'id')
	walk_in(ast, 'params')
	walk_in(ast, 'body')
}

type_handler['ArrowFunctionExpression'] = function(ast) {
	walk_in(ast, 'id')
	walk_in(ast, 'params')
	walk_in(ast, 'body')
}

type_handler['RestElement'] = function(ast) {
	walk_in(ast, 'argument')
}

type_handler['SpreadElement'] = function(ast) {
	walk_in(ast, 'argument')
}

type_handler['ExpressionStatement'] = function(ast) {
	walk_in(ast, 'expression')
}

type_handler['BlockStatement'] = function(ast) {
	walk_in(ast, 'body')
}

type_handler['ClassDeclaration'] = function(ast) {
	walk_in(ast, 'id')
	walk_in(ast, 'superClass')
	walk_in(ast, 'body')
}

type_handler['ClassExpression'] = function(ast) {
	walk_in(ast, 'id')
	walk_in(ast, 'superClass')
	walk_in(ast, 'body')
}

type_handler['ClassBody'] = function(ast) {
	walk_in(ast, 'body')
}

type_handler['MethodDefinition'] = function(ast) {
	walk_in(ast, 'key')
	walk_in(ast, 'value')
}

type_handler['Super'] = function(ast) {
	// 无事可做
}

type_handler['VariableDeclaration'] = function(ast) {
	walk_in(ast, 'declarations')
}

type_handler['VariableDeclarator'] = function(ast) {
	walk_in(ast, 'id')
	if (ast.init) {
		walk_in(ast, 'init')
	}
}

type_handler['WithStatement'] = function(ast) {
	walk_in(ast, 'object')
	walk_in(ast, 'body')
}

type_handler['ReturnStatement'] = function(ast) {
	walk_in(ast, 'argument')
}

type_handler['YieldExpression'] = function(ast) {
	walk_in(ast, 'argument')
}

type_handler['IfStatement'] = function(ast) {
	walk_in(ast, 'test')
	walk_in(ast, 'consequent')
	walk_in(ast, 'alternate')
}

type_handler['WhileStatement'] = function(ast) {
	walk_in(ast, 'test')
	walk_in(ast, 'body')
}

type_handler['DoWhileStatement'] = function(ast) {
	walk_in(ast, 'body')
	walk_in(ast, 'test')
}

type_handler['TryStatement'] = function(ast) {
	// console.log(ast)
	if (ast.guardedHandlers) assert(ast.guardedHandlers.length === 0)
	if (ast.handlers) assert(ast.handlers.length === 0 || ast.handlers.length === 1)
	walk_in(ast, 'block')
	walk_in(ast, 'handler')
	walk_in(ast, 'finalizer')
}

type_handler['CatchClause'] = function(ast) {
	walk_in(ast, 'param')
	walk_in(ast, 'body')
}

type_handler['ForStatement'] = function(ast) {
	walk_in(ast, 'init')
	walk_in(ast, 'test')
	walk_in(ast, 'update')
	walk_in(ast, 'body')
}

type_handler['ForInStatement'] = function(ast) {
	walk_in(ast, 'left')
	walk_in(ast, 'right')
	walk_in(ast, 'body')
}

type_handler['ForOfStatement'] = function(ast) {
	walk_in(ast, 'left')
	walk_in(ast, 'right')
	walk_in(ast, 'body')
}

type_handler['ContinueStatement'] = function(ast) {
	walk_in(ast, 'label')
}

type_handler['BreakStatement'] = function(ast) {
	walk_in(ast, 'label')
}

type_handler['LabeledStatement'] = function(ast) {
	walk_in(ast, 'label')
	walk_in(ast, 'body')
}

type_handler['ThrowStatement'] = function(ast) {
	walk_in(ast, 'argument')
}

type_handler['SwitchStatement'] = function(ast) {
	walk_in(ast, 'discriminant')
	walk_in(ast, 'cases')
}

type_handler['SwitchCase'] = function(ast) {
	// 没有 test 部分的是 default 分句
	walk_in(ast, 'test')
	walk_in(ast, 'consequent')
}

type_handler['CallExpression'] = function(ast) {
	walk_in(ast, 'callee')
	walk_in(ast, 'arguments')
}

type_handler['AssignmentExpression'] = function(ast) {
	walk_in(ast, 'left')
	walk_in(ast, 'right')
}

type_handler['MemberExpression'] = function(ast) {
	walk_in(ast, 'object')
	walk_in(ast, 'property')
}

type_handler['NewExpression'] = function(ast) {
	walk_in(ast, 'callee')
	walk_in(ast, 'arguments')
}

type_handler['MetaProperty'] = function(ast) {
	walk_in(ast, 'meta')
	walk_in(ast, 'property')
}

type_handler['ConditionalExpression'] = function(ast) {
	walk_in(ast, 'test')
	walk_in(ast, 'consequent')
	walk_in(ast, 'alternate')
}

type_handler['BinaryExpression'] = function(ast) {
	walk_in(ast, 'left')
	walk_in(ast, 'right')
}

type_handler['UpdateExpression'] = function(ast) {
	walk_in(ast, 'argument')
}

type_handler['LogicalExpression'] = function(ast) {
	walk_in(ast, 'left')
	walk_in(ast, 'right')
}

type_handler['UnaryExpression'] = function(ast) {
	walk_in(ast, 'argument')
}

type_handler['SequenceExpression'] = function(ast) {
	walk_in(ast, 'expressions')
}

type_handler['ArrayExpression'] = function(ast) {
	walk_in(ast, 'elements')
}

type_handler['ObjectExpression'] = function(ast) {
	walk_in(ast, 'properties')
}

type_handler['Property'] = function(ast) {
	walk_in(ast, 'key')
	walk_in(ast, 'value')
}

type_handler['AssignmentPattern'] = function(ast) {
	walk_in(ast, 'left')
	walk_in(ast, 'right')
}

type_handler['ArrayPattern'] = function(ast) {
	walk_in(ast, 'elements')
}

type_handler['ObjectPattern'] = function(ast) {
	walk_in(ast, 'properties')
}

type_handler['ThisExpression'] = function(ast) {
	// 无事可做
}

type_handler['Identifier'] = function(ast) {
	// 无事可做
}

type_handler['TemplateElement'] = function(ast) {
	// 无事可做
}

type_handler['TemplateLiteral'] = function(ast) {
	// 这里分别遍历似乎不是很好的方案
	// 如果交织起来便利似乎会更好？
	walk_in(ast, 'quasis')
	walk_in(ast, 'expressions')
}

type_handler['TaggedTemplateExpression'] = function(ast) {
	walk_in(ast, 'tag')
	walk_in(ast, 'quasi')
}

type_handler['Literal'] = function(ast) {
	// 无事可做
}

function walk_in(ast, rel) {
	var children = ast[rel]
	if (!children) return
	if (Array.isArray(children)) {
		if (children.length < 1) return
		children.forEach(function(child) {
			if (!child) return
			fire(ast, rel, child)
		})
	}
	else {
		var child = children
		fire(ast, rel, child)
	}

	function fire(parent, rel, child) {
		assert(child)
		assert(!Array.isArray(child))
		assert(type_handler[child.type])

		try {
			stack.unshift({
				ast: parent,
				rel: rel
			})
			callout.enter(child, nav)
			type_handler[child.type](child)
			callout.leave(child, nav)
		}
		finally {
			stack.shift()
		}
	}
}

function assert(v) {
	if (!v) {
		debugger
		throw new Error('assert failed')
	}
}

function walk(ast, user_callout) {
	assert(ast)
	assert(type_handler[ast.type])

	if (user_callout) {
		if (!user_callout.enter) {
			user_callout.enter = function() {}
		}
		if (!user_callout.leave) {
			user_callout.leave = function() {}
		}
		callout = user_callout
	}

	callout.enter(ast, nav)
	type_handler[ast.type](ast, nav)
	callout.leave(ast, nav)
}

function walk_type(ast, type_callout) {
	assert(ast)
	assert(type_handler[ast.type])

	type_handler = type_handler || {}

	walk(ast, {
		enter: function(ast, nav) {
			var type = ast.type
			var handler = type_callout[ast.type]
			if (handler && handler.enter) {
				handler.enter(ast, nav)
			}
		},
		leave: function(ast, nav) {
			var type = ast.type
			var handler = type_callout[ast.type]
			if (handler && handler.leave) {
				handler.leave(ast, nav)
			}
		}
	})
}

exports.walk = walk
exports.walk_type = walk_type
