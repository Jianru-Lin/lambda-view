var Vast = require('./Vast')

module.exports = exports = translate

function translate(ast) {

	var astStack = new Stack()
	var vastStack = new Stack()

	astStack.push(ast)
	vastStack.push(Vast.div('vast'))

	var priority = {
		'PrimaryExpression': 1,
		'FunctionExpression': 1,
		'MemberExpression': 3,
		'NewExpression': 3,
		'CallExpression': 3,
		'LeftHandSideExpression': 3,
		'PostfixExpression': 4,
		'UnaryExpression': 5,
		'MultiplicativeExpression': 6,
		'AdditiveExpression': 7,
		'ShiftExpression': 8,
		'RelationalExpression': 9,
		'EqualityExpression': 10,
		'BitwiseANDExpression': 11,
		'BitwiseXORExpression': 12,
		'BitwiseORExpression': 13,
		'LogicalANDExpression': 14,
		'LogicalORExpression': 15,
		'ConditionalExpression': 16,
		'AssignmentExpression': 17,
		'Expression': 18 // SequenceExpression
	}

	var ruleTable = {
		'Program': function() {
			children('body')
		},

		'EmptyStatement': undefined,

		'BlockStatement': function() {
			children('body')
		},

		'ExpressionStatement': function() {
			children('expression'), semicolon(), br()
		},

		'IfStatement': function() {
			
			var ast = astStack.top()

			keyword('if'), sp_opt(), bracket(lz_children('test')), sp_opt(), brace(br, lz_indent(lz_children('consequent'))), br()
			if (ast.alternate) {
				if (ast.alternate.type !== 'IfStatement') {
					keyword('else'), sp_opt(), brace(br, lz_indent(lz_children('alternate'))), br()
				}
				else {
					keyword('else'), sp(), children('alternate')
				}
			}
		},

		'LabeledStatement': function() {
			children('label'), colon(), sp_opt(), children('body')
		},

		'ContinueStatement': function() {
			keyword('continue'), semicolon(), br()
		},

		'WithStatement': function() {
			keyword('with'), sp_opt(), bracket(lz_children('object')), sp_opt(), brace(br, lz_indent(lz_children('body'))), br()
		},

		'SwitchStatement': function() {
			keyword('switch'), sp_opt(), bracket(lz_children('discriminant')), sp_opt(), brace(br, lz_indent(lz_children('cases'))), br()
		},

		'ReturnStatement': function () {
			
			var ast = astStack.top()
			
			if (ast.argument) {
				keyword('return'), sp(), children('argument'), semicolon(), br()
			}
			else {
				keyword('return'), semicolon(), br()
			}
		},

		'ThrowStatement': function() {
			keyword('throw'), sp(), children('argument'), semicolon(), br()
		},

		'TryStatement': function() {
			
			var ast = astStack.top()

			keyword('try'), sp_opt(), brace(br, lz_indent(lz_children('block'))), br()
			children('handlers')
			if (ast.finalizer) {
				keyword('finally'), sp_opt(), brace(br, lz_indent(lz_children('finalizer'))), br()
			}
		},

		'WhileStatement': function() {
			keyword('while'), sp_opt(), bracket(lz_children('test')), sp_opt(), brace(br, lz_indent(lz_children('body'))), br()
		},

		'DoWhileStatement': function() {
			keyword('do'), sp_opt(), brace(br, lz_indent(lz_children('body'))), sp_opt(), keyword('while'), sp_opt(), bracket(lz_children('test')), semicolon(), br()
		},

		'ForStatement': function() {
			
			var ast = astStack.top()

			if (ast.init && ast.init.type === 'VariableDeclaration') {
				ast.init.parentIsForInStatement = true
			}

			keyword('for'), sp_opt(), 
			bracket(
				lz_children('init'), semicolon, 
				test_leading_sp, lz_children('test'), semicolon, 
				update_leading_sp, lz_children('update')), sp_opt(), 
			brace(br, lz_indent(lz_children('body'))), br()

			function test_leading_sp() {
				if (ast.test) {
					sp_opt()
				}
			}

			function update_leading_sp() {
				if (ast.update) {
					sp_opt()
				}
			}
		},

		'ForInStatement': function () {
			
			var ast = astStack.top()

			if (ast.left.type === 'VariableDeclaration') {
				ast.left.parentIsForInStatement = true
			}
			
			keyword('for'), sp_opt(), bracket(lz_children('left'), sp, lz_keyword('in'), sp, lz_children('right')), sp_opt(), brace(br, lz_indent(lz_children('body'))), br()
		},

		'ForOfStatement': undefined,

		'LetStatement': undefined,

		'DebuggerStatement': function() {
			keyword('debugger'), semicolon(), br()
		},

		'FunctionDeclaration': function() {
			keyword('function'), sp(), children('id'), sp_opt(), bracket(lz_children('params', [comma, sp_opt])), sp_opt(), brace(br, lz_indent(lz_children('body'))), br()
		},

		'VariableDeclaration': function() {
			
			var ast = astStack.top()

			if (ast.parentIsForInStatement) {
				keyword('var'), sp(), children('declarations', [comma, sp_opt])
			}
			else {
				keyword('var'), sp(), children('declarations', [comma, br, sp_opt, sp_opt, sp_opt, sp_opt]), semicolon(), br()
			}
		},

		'VariableDeclarator': function () {
			
			var ast = astStack.top()

			if (ast.init) {
				children('id'), sp_opt(), operator('='), sp_opt(), pchildren(priority.AssignmentExpression, 'init')
			}
			else {
				children('id')
			}
		},

		'ThisExpression': function() {
			keyword('this')
		},

		'ArrayExpression': function() {
			square_bracket(lz_pchildren(priority.PrimaryExpression, 'elements', [comma, sp_opt]))
		},

		'ObjectExpression': function() {
			
			var ast = astStack.top()

			if (ast.properties && ast.properties.length > 0) {
				brace(br, lz_indent(lz_children('properties', [comma, br])), br)
			}
			else {
				return brace()
			}
		},

		'Property': function() {
			// get/set not implemented
			var ast = astStack.top()

			if (ast.kind === 'get' || ast.kind === 'set') {
				generateGetSet();
			}
			else {
				children('key'), colon(), sp_opt(), pchildren(priority.Expression, 'value')					
			}

			function generateGetSet() {
				
				keyword(ast.kind), sp(), children('key'), sp_opt()

				var functionExp = ast.value
				astStack.push(functionExp)
				bracket(lz_children('params', [comma, sp_opt])),  sp_opt(), brace(br, lz_indent(lz_children('body')))
				astStack.pop()
			}
		},

		'FunctionExpression': function () {
			
			var ast = astStack.top()

			if (ast.id) {
				keyword('function'), sp(), children('id'), sp_opt(), bracket(lz_children('params', [comma, sp_opt])), sp_opt(), brace(br, lz_indent(lz_children('body')))
			}
			else {
				keyword('function'), sp(), bracket(lz_children('params', [comma, sp_opt])), sp_opt(), brace(br, lz_indent(lz_children('body')))
			}
		},

		'ArrowExpression': undefined,

		'SequenceExpression': function () {
			pchildren(priority.AssignmentExpression, 'expressions', [comma, sp_opt])
		},

		'UnaryExpression': function() {
			
			var ast = astStack.top()

			if (ast.prefix) {
				operator_prop(), sp(), pchildren(priority.UnaryExpression, 'argument')
			}
			else {
				pchildren(priority.UnaryExpression, 'argument'), sp(), operator_prop()
			}
		},

		'BinaryExpression': function() {

			var ast = astStack.top()
			var desiredPriority = calcDesiredPriority()

			pchildren(desiredPriority.left, 'left'), sp_opt(), operator_prop(), sp_opt(), pchildren(desiredPriority.right, 'right')

			function calcDesiredPriority() {
				var t = toECMA262_5E(ast)
				switch (t) {
					case 'MultiplicativeExpression':
						return {
							left: priority.MultiplicativeExpression,
							right: priority.UnaryExpression
						}
					case 'AdditiveExpression':
						return {
							left: priority.AdditiveExpression,
							right: priority.MultiplicativeExpression
						}
					case 'ShiftExpression':
						return {
							left: priority.ShiftExpression,
							right: priority.AdditiveExpression
						}
					case 'RelationalExpression':
						return {
							left: priority.RelationalExpression,
							right: priority.ShiftExpression
						}
					case 'EqualityExpression':
						return {
							left: priority.EqualityExpression,
							right: priority.RelationalExpression
						}
					case 'BitwiseANDExpression':
						return {
							left: priority.BitwiseANDExpression,
							right: priority.EqualityExpression
						}
					case 'BitwiseXORExpression':
						return {
							left: priority.BitwiseXORExpression,
							right: priority.BitwiseANDExpression
						}
					case 'BitwiseORExpression':
						return {
							left: priority.BitwiseORExpression,
							right: priority.BitwiseXORExpression
						}
					default:
						throw new Error('I can\'t calc desired priority of ' + t)
				}
			}
		},

		'AssignmentExpression': function() {
			pchildren(priority.LeftHandSideExpression, 'left'), sp_opt(), operator_prop(), sp_opt(), pchildren(priority.AssignmentExpression, 'right')
		},

		'UpdateExpression': function() {
			
			var ast = astStack.top()
			var desiredPriority = calcDesiredPriority()

			if (ast.prefix) {
				operator_prop(), sp_opt(), pchildren(desiredPriority, 'argument')
			}
			else {
				pchildren(desiredPriority, 'argument'), sp_opt(), operator_prop()
			}

			function calcDesiredPriority() {
				var t = toECMA262_5E(ast)
				switch (t) {
					case 'UnaryExpression':
						return priority.UnaryExpression
					case 'PostfixExpression':
						return priority.LeftHandSideExpression
					default:
						throw new Error('I can\'t calc desired priority of ' + t)
				}
			}
		},

		'LogicalExpression': function() {

			var ast = astStack.top()
			var desiredPriority = calcDesiredPriority()

			pchildren(desiredPriority.left, 'left'), sp_opt(), operator_prop(), sp_opt(), pchildren(desiredPriority.right, 'right')

			function calcDesiredPriority() {
				var t = toECMA262_5E(ast)
				switch (t) {
					case 'LogicalANDExpression':
						return {
							left: priority.LogicalANDExpression,
							right: priority.BitwiseORExpression
						}
					case 'LogicalORExpression':
						return {
							left: priority.LogicalORExpression,
							right: priority.LogicalANDExpression
						}
					default:
						throw new Error('I can\'t calc desired priority of ' + t)
				}
			}
		},

		'ConditionalExpression': function() {
			pchildren(priority.LogicalORExpression, 'test'), sp_opt(), operator('?'), sp_opt(), 
			pchildren(priority.AssignmentExpression, 'consequent'), sp_opt(), 
			operator(':'), sp_opt(), 
			pchildren(priority.AssignmentExpression, 'alternate')
		},

		'NewExpression': function() {
			keyword('new'), sp(), pchildren(priority.NewExpression, 'callee'), sp_opt(), bracket(lz_children('arguments', [comma, sp_opt]))
		},

		'CallExpression': function() {
			// TODO recheck the priority problem here
			var ast = astStack.top()
			if (ast.callee.type === 'FunctionExpression') {
				bracket(lz_pchildren(priority.CallExpression, 'callee')), sp_opt(), bracket(lz_children('arguments', [comma, sp_opt]))	
			}
			else {
				pchildren(priority.CallExpression, 'callee'), sp_opt(), bracket(lz_children('arguments', [comma, sp_opt]))						
			}
		},

		'MemberExpression': function () {
			// TODO recheck the priority problem here
			
			var ast = astStack.top()

			if (ast.computed) {
				if (ast.object.type === 'FunctionExpression') {
					bracket(lz_children('object')), square_bracket(lz_children('property'))
				}
				else {
					pchildren(priority.MemberExpression, 'object'), square_bracket(lz_children('property'))
				}
			}
			else {
				if (ast.object.type === 'FunctionExpression') {
					bracket(lz_children('object')), operator('.'), children('property')
				}
				else {
					pchildren(priority.MemberExpression, 'object'), operator('.'), children('property')
				}
			}
		},

		'YieldExpression': undefined,

		'ComprehensionExpression': undefined,

		'GeneratorExpression': undefined,

		'GraphExpression': undefined,

		'GraphIndexExpression': undefined,

		'LetExpression': undefined,

		'ObjectPattern': undefined, // check Mozilla Doc before implement this

		'ArrayPattern': undefined,

		'SwitchCase': function() {
			
			var ast = astStack.top()

			if (ast.test) {
				keyword('case'), sp(), children('test'), colon(), br(), indent(lz_children('consequent'))
			}
			else {
				keyword('default'), colon(), br(), indent(lz_children('consequent'))
			}
		},

		'BreakStatement': function() {
			keyword('break'), semicolon(), br()
		},

		'CatchClause': function() {
			keyword('catch'), sp_opt(), bracket(lz_children('param')), sp_opt(), brace(br, lz_indent(lz_children('body'))), br()
		},

		'ComprehensionBlock': undefined,

		'Identifier': function() {
			// esprima says undefined is an identifier not a literal. see issue #1
			var astNode = astStack.top()
			var parentVast = vastStack.top()
			var vast = Vast.span('identifier', astNode.name)
			parentVast.children.push(vast)
		},

		'Literal': function() {
			var astNode = astStack.top()
			var parentVast = vastStack.top()
			var raw = astNode.raw
			var value = astNode.value

			switch (typeof value) {
				case 'string':
					var vast = Vast.span('literal string', raw)
					break
				case 'boolean':
					var vast = Vast.span('literal boolean', raw)
					break
				case 'number':
					var vast = Vast.span('literal number', raw)
					break
				case 'undefined':
					// here won't be reached cause issue #1
					var vast = Vast.span('literal undefined', 'undefined')
					break
				case 'object':
					if (value === null) {
						var vast = Vast.span('literal null', 'null')
					}
					else if (value.constructor === RegExp) {
						var vast = Vast.span('literal regexp', value.toString())
					}
					else {
						throw new Error('unsupported type of literal: ' + typeof value)
					}
					break
				default:
					throw new Error('unsupported type of literal: ' + typeof value)
			}

			parentVast.children.push(vast)
		}
	}

	ruleTable[ast.type]()
	return vastStack.top()

	function priorityOf(target) {
		var value = priority[toECMA262_5E(target)]
		if (value !== undefined) {
			return value
		}
		else {
			throw new Error('I dont\'t know how to determine the priority for: ' + target.type)
		}
	}

	function toECMA262_5E (target) {
		switch (target.type) {
			case 'ThisExpression':
			case 'ArrayExpression':
			case 'ObjectExpression':
			case 'Identifier':
			case 'Literal':
				return 'PrimaryExpression'
			case 'UpdateExpression':
				if (target.prefix) {
					return 'UnaryExpression'
				}
				else {
					return 'PostfixExpression'
				}
			case 'BinaryExpression':
				switch (target.operator) {
					case '*':
					case '/':
					case '%':
						return 'MultiplicativeExpression'
					case '+':
					case '-':
						return 'AdditiveExpression'
					case '<<':
					case '>>':
					case '>>>':
						return 'ShiftExpression'
					case '<':
					case '>':
					case '<=':
					case '>=':
					case 'instanceof':
					case 'in':
						return 'RelationalExpression'
					case '==':
					case '!=':
					case '===':
					case '!==':
						return 'EqualityExpression'
					case '&':
						return 'BitwiseANDExpression'
					case '^':
						return 'BitwiseXORExpression'
					case '|':
						return 'BitwiseORExpression'
					default:
						throw new Error('unknown BinaryExpression operator: ' + target.operator)
				}
			case 'LogicalExpression':
				switch (target.operator) {
					case '&&':
						return 'LogicalANDExpression'
					case '||':
						return 'LogicalORExpression'
					default:
						throw new Error('unknown LogicalExpression operator: ' + target.operator)
				}
			case 'SequenceExpression':
				return 'Expression'
			default:
				// eg. UnaryExpression
				return target.type
		}			
	}

	// generate child and consider priority problem
	// desiredPriority use the ECMA262_5E define, not current parser
	function pchildren(desiredPriority, name, between) {

		var children = astStack.top()[name]

		var between_proxy = (function() {
			if (!between) {
				return function() {}
			}
			else if (Array.isArray(between)) {
				return function() {
					between.forEach(function(item) {
						item()
					})
				}
			}
			else {
				return function() {
					between()
				}
			}
		})()

		if (children === null || children === undefined) {
			// ignore
			return
		}

		if (Array.isArray(children)) {
			children.forEach(function(child, i) {
				if (priorityOf(child) <= desiredPriority) {
					access(child)
				}
				else {
					bracket(lz_access(child))
				}

				if (i < children.length - 1) {
					between_proxy()
				}
			})
		}
		else if (typeof children === 'object') {
			var child = children
			if (priorityOf(child) <= desiredPriority) {
				access(child)
			}
			else {
				bracket(lz_access(child))
			}
		}
		else {
			debugger
			throw new Error('children(): unknown children type: ' + typeof children)
		}

		function access(ast) {
			var rule = ruleTable[ast.type]
			if (!rule) {
				console.log('rule not found: ' + ast.type)
				return
			}
			astStack.push(ast)
			rule()
			astStack.pop()
		}

		function lz_access(ast) {
			return function() {
				access(ast)
			}
		}
	}

	function lz_pchildren(desiredPriority, name, between) {
		return function() {
			pchildren(desiredPriority, name, between)
		}
	}

	function children(name, between) {
		
		var children = astStack.top()[name]

		var between_proxy = (function() {
			if (!between) {
				return function() {}
			}
			else if (Array.isArray(between)) {
				return function() {
					between.forEach(function(item) {
						item()
					})
				}
			}
			else {
				return function() {
					between()
				}
			}
		})()

		if (children === null || children === undefined) {
			// ignore
			return
		}

		if (Array.isArray(children)) {
			children.forEach(function(c, i) {
				access(c)
				if (i < children.length - 1) {
					between_proxy()
				}
			})
		}
		else if (typeof children === 'object') {
			access(children)
		}
		else {
			debugger
			throw new Error('children(): unknown children type: ' + typeof children)
		}

		function access(ast) {
			var rule = ruleTable[ast.type]
			if (!rule) {
				console.log('rule not found: ' + ast.type)
				return
			}
			astStack.push(ast)
			rule()
			astStack.pop()
		}
	}

	function lz_children(name, between) {
		return function() {
			children(name, between)
		}
	}

	function operator_prop() {
		var ast = astStack.top()
		var parentVast = vastStack.top()
		var vast = Vast.span('operator', ast.operator)
		parentVast.children.push(vast)
	}

	function keyword(text) {
		vastStack.top().children.push(Vast.span('keyword ' + text, text))
	}

	function lz_keyword(text) {
		return function() {
			keyword(text)
		}
	}

	function br() {
		vastStack.top().children.push(Vast.br())
	}

	function sp() {
		vastStack.top().children.push(Vast.span('space', ' '))
	}

	function sp_opt() {
		vastStack.top().children.push(Vast.span('space optional', ' '))
	}

	function operator(text) {
		vastStack.top().children.push(Vast.span('operator', text))
	}

	function lz_operator(text) {
		return function() {
			operator(text)
		}
	}

	function semicolon() {
		vastStack.top().children.push(Vast.span('semicolon', ';'))
	}

	function brace() {
		_folding_pair('brace', '{', '}', toArray(arguments))
	}

	function bracket() {
		_folding_pair('bracket', '(', ')', toArray(arguments))
	}

	function square_bracket() {
		_folding_pair('square_bracket', '[', ']', toArray(arguments))
	}

	function _folding_pair(name, pair_left, pair_right, funcs) {
		var left = Vast.span(name + ' left', pair_left)
		var right = Vast.span(name + ' right', pair_right)
		left.metaData = {
			folding: {
				to: right.id
			}
		}
		right.metaData = {
			folding: {
				backward: true,
				to: left.id
			}
		}
		vastStack.top().children.push(left)
		if (funcs) {
			if (Array.isArray(funcs)) {
				funcs.forEach(function(f) {
					try {
						f()
					}
					catch (err) {
						debugger
					}
				})
			}
			else {
				funcs()
			}
		}
		vastStack.top().children.push(right)
	}

	function comma() {
		vastStack.top().children.push(Vast.span('comma', ','))				
	}

	function colon() {
		vastStack.top().children.push(Vast.span('colon', ':'))				
	}

	// funcs...
	function indent() {
		var funcs = toArray(arguments)
		var indentSection = Vast.sectionMark('indent')
		vastStack.top().children.push(indentSection.enter)
		if (funcs && funcs.length > 0) {
			funcs.forEach(function(func) {
				func()
			})
		}
		vastStack.top().children.push(indentSection.leave)
	}

	function lz_indent() {
		var funcs = toArray(arguments)
		return function() {
			indent.apply(undefined, funcs)
		}
	}

	function toArray(args) {
		return [].slice.apply(args)
	}
}

function Stack() {
	this.list = []
}

Stack.prototype.push = function(e) {
	this.list.push(e)
}

Stack.prototype.pop = function() {
	this.list.pop()
}

Stack.prototype.top = function() {
	return this.list[this.list.length - 1]
}

Stack.prototype.bottom = function() {
	return this.list[0]
}