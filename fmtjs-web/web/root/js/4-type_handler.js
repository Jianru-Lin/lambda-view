var config = {}
var type_handler = {}

////////////////////////////////////////////////////////////////
// 下面的各个类型处理函数专注于 VDOM 生成
////////////////////////////////////////////////////////////////

type_handler['Program'] = function(ast, ctx) {
	assert(ctx && true)

	ast.body = ordered_body(ast.body)

	return vdom(
		'div', 
		[ast.type],
		[
			comments(ast),
			vdom('span', 'body', process_ast_list(ast.body, ctx))
		]
	)
}

type_handler['ImportDeclaration'] = function(ast, ctx) {
	return vdom(
		'div',
		ast.type,
		[
			comments(ast),
			vkeyword('import'),
			function() {
				if (ast.specifiers && ast.specifiers.length > 0) {
					return [
						vsp(),
						vjoin(process_ast_list(ast.specifiers, ctx).map(wrap_vdom('span', 'specifier')), function() {
							return [vcomma(), vsp()]
						})
					]
				}
			},
			vsp(),
			vkeyword('from'),
			vsp(),
			vdom('span', 'source', vpkgref_i(ast.source.value, process_ast(ast.source, ctx))),
			vsp(),
			vsemi()
		]
	)
}

type_handler['ImportDefaultSpecifier'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		[
			vdom('span', 'local', process_ast(ast.local, ctx))
		]
	)
}

type_handler['ImportSpecifier'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		vbracket(function() {
			assert(ast.local.type === ast.imported.type)
			assert(ast.local.type === 'Identifier')
			if (ast.local.name === ast.imported.name) {
				return vdom('span', 'imported', process_ast(ast.imported, ctx))
			}
			else {
				return [
					vdom('span', 'imported', process_ast(ast.imported, ctx)),
					vsp(),
					vkeyword('as'),
					vsp(),
					vdom('span', 'local', process_ast(ast.local, ctx))
				]
			}
		})
	)
}

type_handler['ImportNamespaceSpecifier'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		[
			vdom('span', 'asterisk', '*'),
			vsp(),
			vkeyword('as'),
			vsp(),
			vdom('span', 'local', process_ast(ast.local, ctx))
		]
	)
}

type_handler['ExportAllDeclaration'] = function(ast, ctx) {
	return vdom(
		'div',
		ast.type,
		[
			comments(ast),
			vkeyword('export'),
			vsp(),
			vdom('span', 'asterisk', '*'),
			vsp(),
			vkeyword('from'),
			vsp(),
			vdom('span', 'source', process_ast(ast.source, ctx)),
			vsp(),
			vsemi()
		]
	)
}

type_handler['ExportNamedDeclaration'] = function(ast, ctx) {
	// 默认会在末尾生成分号，但这也许是没有必要的
	var semi_unnecessary = false
	return vdom(
		'div',
		ast.type,
		[
			comments(ast),
			vkeyword('export'),
			vsp(),
			function() {
				if (ast.declaration) {
					// 断言：有 declaration 必然就不可能有 specifiers
					assert(Array.isArray(ast.specifiers) && ast.specifiers.length === 0)
					// 凡是声明类型的，我们没必要在后面生成分号
					semi_unnecessary = true
					return vdom('span', 'declaration', function() {
						return process_ast(ast.declaration, ctx)
					})
				}
				else if (ast.specifiers) {
					return vbracket(vdom('span', 'specifiers', vjoin(process_ast_list(ast.specifiers, ctx).map(wrap_vdom('span', 'specifier')), function() {
						return [vcomma(), vsp()]
					})))
				}
			},
			vdom('span', 'source', function() {
				if (ast.source) {
					return [
						vsp(),
						vkeyword('from'),
						vsp(),
						process_ast(ast.source, ctx)
					]
				}
			}),
			function() {
				if (semi_unnecessary) return
				else return [vsp(), vsemi()]
			}
		]
	)
}

type_handler['ExportDefaultDeclaration'] = function(ast, ctx) {
	return vdom(
		'div',
		ast.type,
		[
			comments(ast),
			vkeyword('export'),
			vsp(),
			vkeyword('default'),
			vsp(),
			vdom('span', 'declaration', process_ast(ast.declaration, ctx)),
			function() {
				var semi_unnecessary = (ast.declaration.type === 'ClassDeclaration' ||
										ast.declaration.type === 'FunctionDeclaration' ||
										ast.declaration.type === 'VariableDeclaration')
				if (!semi_unnecessary) {
					return [vsp(), vsemi()]
				}
			}
		]
	)
}

type_handler['ExportSpecifier'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		[
			vdom('span', 'local', process_ast(ast.local, ctx)),
			function() {
				assert(ast.local.type === ast.exported.type)
				assert(ast.local.type === 'Identifier')
				var is_same = ast.local.name === ast.exported.name
				if (!is_same) {
					return [
						vsp(),
						vkeyword('as'),
						vsp(),
						vdom('span', 'exported', process_ast(ast.exported, ctx))
					]
				}
			}
		]
	)
}

type_handler['EmptyStatement'] = function(ast, ctx) {
	return vdom(
		'div',
		ast.type,
		[
			comments(ast),
			vsemi()
		]
	)
}

type_handler['DebuggerStatement'] = function(ast, ctx) {
	return vdom(
		'div',
		ast.type,
		[
			comments(ast),
			vkeyword('debugger'),
			vsp(),
			vsemi()
		]
	)
}

type_handler['FunctionDeclaration'] = function(ast, ctx) {
	// console.log(ast)
	// console.log(ast.body)

	return vdom(
		'div',
		[ast.type],
		[
			comments(ast),
			vdom('span', 'keyword','function'),
			vsp(),
			function() {
				if (ast.generator) {
					return [vkeyword('*'), vsp()]
				}
			},
			function() {
				// 用在 export 时 ast.id 确实可能为 null
				if (ast.id) {
					return vdom('span', 'id', [
						process_ast(ast.id, ctx),
						vsp()
					])
				}
			},
			vdom('span', 'params', function() {
				// 支持默认参数
				var params = ast.params || []
				var defaults = ast.defaults || []
				var params_and_defaults = zip(params, defaults)
				return vbrace(
					vjoin(
						params_and_defaults.map(function(item) {
							var param = item[0]
							var deflt = item[1]
							return vdom(
								'span',
								'param',
								[
									vdom('span', 'name', process_ast(param, ctx)),
									function() {
										if (deflt) {
											return [
												vsp(),
												vdom('span', 'eq', '='),
												vsp(),
												vdom('span', 'default', process_ast(deflt, ctx))
											]
										}
									}
								]
							)
						}), 
						function() {
							return [vcomma(), vsp()]
						}
					)
				)
			}),
			vsp(),
			vdom('span', 'body', [process_ast(ast.body, ctx)])
		]
	)
}

// ccfg = {no_function_keyword: true|false} 可配置是否生成 function 关键词
// ObjectExpression 下的 Property 会使用这个配置
// Class 下的 MethodDefinition 也会使用这个配置
type_handler['FunctionExpression'] = function(ast, ctx, ccfg) {
	return vdom(
		'div',
		ast.type,
		[
			function() {
				if (ccfg && ccfg.no_function_keyword) {
					return null
				}
				else {
					return [
						vdom('span', 'keyword','function'),
						vsp()
					]
				}
			},
			function() {
				if (ast.generator) {
					return [vkeyword('*'), vsp()]
				}
			},
			// id 部分不一定存在，可有可无
			function() {
				if (ast.id) {
					return [
						vdom('span', ['name'], process_ast(ast.id, ctx)),
						vsp()
					]
				}
			},
			vdom('span', 'params', function() {
				// 支持默认参数
				var params = ast.params || []
				var defaults = ast.defaults || []
				var params_and_defaults = zip(params, defaults)
				return vbrace(
					vjoin(
						params_and_defaults.map(function(item) {
							var param = item[0]
							var deflt = item[1]
							return vdom(
								'span',
								'param',
								[
									vdom('span', 'name', process_ast(param, ctx)),
									function() {
										if (deflt) {
											return [
												vsp(),
												vdom('span', 'eq', '='),
												vsp(),
												vdom('span', 'default', process_ast(deflt, ctx))
											]
										}
									}
								]
							)
						}), 
						function() {
							return [vcomma(), vsp()]
						}
					)
				)
			}),
			vsp(),
			vdom('span', 'body', [process_ast(ast.body, ctx)])
		]
	)
}

type_handler['ArrowFunctionExpression'] = function(ast, ctx) {
	assert(ast.id === null)
	return vdom(
		'div',
		ast.type,
		[
			vdom('span', 'params', function() {
				// 支持默认参数
				var params = ast.params || []
				var defaults = ast.defaults || []
				var params_and_defaults = zip(params, defaults)
				return vbrace(
					vjoin(
						params_and_defaults.map(function(item) {
							var param = item[0]
							var deflt = item[1]
							return vdom(
								'span',
								'param',
								[
									vdom('span', 'name', process_ast(param, ctx)),
									function() {
										if (deflt) {
											return [
												vsp(),
												vdom('span', 'eq', '='),
												vsp(),
												vdom('span', 'default', process_ast(deflt, ctx))
											]
										}
									}
								]
							)
						}),
						function() {
							return [vcomma(), vsp()]
						}
					)
				)
			}),
			vsp(),
			voperator('=>'),
			vsp(),
			vdom('span', 'body', function() {
				var body = ast.body
				if (body.type === 'ObjectExpression') {
					return v_exp_brace(process_ast(body, ctx))
				}
				else {
					return [process_ast(ast.body, ctx)]
				}
			})
		]
	)
}

type_handler['RestElement'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		[
			vkeyword('...'),
			vdom('span', 'argument', process_ast(ast.argument, ctx))
		]
	)
}

type_handler['SpreadElement'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		[
			vkeyword('...'),
			vdom('span', 'argument', process_ast(ast.argument, ctx))
		]
	)
}

type_handler['ExpressionStatement'] = function(ast, ctx) {
	return vdom(
		'div',
		ast.type,
		[
			comments(ast),
			vdom('span', 'expression', function() {
				// 如果是函数表达式、对象或者类表达式的话，我们要给它补上括号，其他则没必要
				if (ast.expression.type === 'FunctionExpression' ||
					ast.expression.type === 'ObjectExpression' ||
					ast.expression.type === 'ClassExpression') {
					return v_exp_brace(process_ast(ast.expression, ctx))
				}
				else {
					return process_ast(ast.expression, ctx)
				}
			}),
			vsp(),
			vsemi()
		]
	)
}

type_handler['BlockStatement'] = function(ast, ctx) {
	ast.body = ordered_body(ast.body)

	if (ast.fmtjs_ref_id) {
		return vdom(
			'a',
			{
				'class': [ast.type, 'ref'].join(' '),
				'data-ref-id': ast.fmtjs_ref_id
			},
			[
				comments(ast),
				vdom('span', ['left-coll', 'bracket'], '{'),
				// vdom('span', ['collapsable-switcher', 'bracket', 'hidden'], '...'),
				vdom('span', ['right-coll', 'bracket'], '}')
			]
		)
	}
	else {
		return vdom(
			'div',
			ast.type,
			[
				comments(ast),
				vdom('span', 'body', vbracket(process_ast_list(ast.body, ctx)))
			]
		)
	}
}

type_handler['ClassDeclaration'] = function(ast, ctx) {
	return vdom(
		'div',
		ast.type,
		[
			comments(ast),
			vkeyword('class'),
			vsp(),
			function() {
				// 如下情形时 id 会为 null
				// export default function () {}
				if (ast.id) {
					return vdom('span', 'id', [
						process_ast(ast.id, ctx),
						vsp()
					])
				}
			},
			function() {
				if (ast.superClass) {
					return [
						vkeyword('extends'),
						vsp(),
						vdom('span', 'superClass', process_ast(ast.superClass, ctx)),
						vsp()
					]
				}
			},
			vbracket(function() {
				return vdom('span', 'body', process_ast(ast.body, ctx))
			})
		]
	)
}

type_handler['ClassExpression'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		[
			vkeyword('class'),
			vsp(),
			function() {
				if (ast.id) {
					return [
						process_ast(ast.id, ctx),
						vsp(),
					]
				}
			},
			function() {
				if (ast.superClass) {
					return [
						vkeyword('extends'),
						vsp(),
						vdom('span', 'superClass', process_ast(ast.superClass, ctx)),
						vsp()
					]
				}
			},
			vbracket(function() {
				return vdom('span', 'body', process_ast(ast.body, ctx))
			})
		]
	)
}

type_handler['ClassBody'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		process_ast_list(ast.body, ctx).map(wrap_vdom('div', 'body-item'))
	)
}

type_handler['MethodDefinition'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		[
			comments(ast),
			function() {
				if (ast['static']) {
					return [
						vkeyword('static'),
						vsp()
					]
				}
			},
			function() {
				if (ast.kind === 'get' || ast.kind === 'set') {
					return [
						vkeyword(ast.kind),
						vsp()
					]
				}
				else {
					assert(ast.kind === 'method' || ast.kind === 'constructor')
					// do nothing
				}
			},
			function() {
				if (ast.computed) {
					return vdom('span', 'key', vsqbracket(process_ast(ast.key, ctx)))
				}
				else {
					return vdom('span', 'key', process_ast(ast.key, ctx))
				}
			},
			vsp(),
			function() {
				return vdom('span', 'value', process_ast(ast.value, ctx, {no_function_keyword: true})) // 注意传递了信息给 FunctionExpression 让它不要生成 function 关键字
			}
		]
	)
}

type_handler['Super'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		vkeyword('super')
	)
}

// ccfg = {nosemi: true|false} 可配置是否生成末尾分号
// ForStatement, ForInStatement 会使用这个配置
type_handler['VariableDeclaration'] = function(ast, ctx, ccfg) {
	// console.log(ast)
	assert(ast.kind === 'var' || ast.kind === 'const' || ast.kind === 'let')

	return vdom(
		'div',
		(function() {
			if (ast.declarations.length > 1) {
				return [ast.type, 'muti-declarations']
			}
			else {
				return ast.type
			}
		})(),
		[
			comments(ast),
			vdom('span', ['kind', ast.kind], vkeyword(ast.kind)),
			vsp(),
			vdom('span', 'declarations', function() {
				return [
					vjoin(process_ast_list(ast.declarations, ctx).map(wrap_vdom('span', 'declaration')), function() {
						return [vcomma(), vsp()]
					}),
					function() {
						if (ccfg && ccfg.nosemi) return undefined
						else return [vdom('span', 'sp-never-block', ' '), vsemi()]
					}
				]
			})
		]
	)
}

type_handler['VariableDeclarator'] = function(ast, ctx) {
	// console.log(ast)
	return vdom(
		'span',
		ast.type,
		[
			comments(ast),
			vdom('span', 'id', process_ast(ast.id, ctx)),
			function() {
				if (ast.init) {
					return vdom(
						'span',
						'init',
						[
							vsp(),
							vdom('span', 'eq', '='),
							vsp(),
							vdom('span', 'init', process_ast(ast.init, ctx))
						]
					)
				}
			}
		]
	)
}

type_handler['WithStatement'] = function(ast, ctx) {
	return vdom(
		'div',
		ast.type,
		[
			comments(ast),
			vkeyword('with'),
			vsp(),
			// 少见的括号在结构之上的例外
			vbrace(vdom('span', 'object', process_ast(ast.object, ctx))),
			vsp(),
			vdom('span', 'body', process_ast(ast.body, ctx))
		]
	)
}

type_handler['ReturnStatement'] = function(ast, ctx) {
	// console.log(ast)
	return vdom(
		'div',
		ast.type,
		[
			comments(ast),
			vdom('span', 'keyword', 'return'),
			function() {
				if (ast.argument) {
					return [
						vsp(),
						vdom('span', 'argument', process_ast(ast.argument, ctx))
					]
				}
			},
			vsp(),
			vsemi()
		]
	)
}

type_handler['YieldExpression'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		[
			comments(ast),
			vdom('span', 'keyword', 'yield'),
			function() {
				if (ast.delegate) {
					assert(ast.argument)
					return [
						vsp(),
						vkeyword('*')
					]
				}
			},
			function() {
				if (ast.argument) {
					return [
						vsp(),
						vdom('span', 'argument', process_ast(ast.argument, ctx))
					]
				}
			}
		]
	)
}

type_handler['IfStatement'] = function(ast, ctx) {
	var parts = flat(ast)
	return vdom(
		'div', 
		[ast.type, parts.length > 3 ? 'vertical-layout' : undefined], 
		[
			comments(ast),
			function() {
				return parts.map(function(part, i) {
					if (part.type === 'IfStatement') {
						return vdom('span', 'part', [
							vdom('span', 'keyword', i === 0 ? 'if' : (i === part.length - 1) ? 'else' : 'else if'),
							vsp(),
							vdom('span', 'test', vbrace(process_ast(part.test, ctx))),
							vsp(),
							vdom('span', 'consequent', process_ast(part.consequent, ctx)),
							vsp(),
						])
					}
					else {
						assert(i === parts.length - 1)
						return vdom('span', 'part', [
							vdom('span', 'keyword', 'else'),
							vsp(),
							vdom('span', 'alternate', process_ast(part, ctx))
						])
					}
				})
			}
		]
	)

	function flat(ast) {
		var parts = []
		parts.push(ast)
		flat_to(ast.alternate, parts)
		return parts

		function flat_to(node, parts) {
			if (!node) return
			parts.push(node)
			if (node.type === 'IfStatement') {
				flat_to(node.alternate, parts)
			}
		}
	}
}

type_handler['WhileStatement'] = function(ast, ctx) {
	// console.log(ast)
	return vdom(
		'div',
		ast.type,
		[
			comments(ast),
			vkeyword('while'),
			vsp(),
			vdom('span', 'test', vbrace(process_ast(ast.test, ctx))),
			vsp(),
			vdom('span', 'body', process_ast(ast.body, ctx))
		]
	)
}

type_handler['DoWhileStatement'] = function(ast, ctx) {
	// console.log(ast)
	return vdom(
		'div',
		ast.type,
		[
			comments(ast),
			vkeyword('do'),
			vsp(),
			vdom('span', 'body', process_ast(ast.body, ctx)),
			vsp(),
			vkeyword('while'),
			vsp(),
			vdom('span', 'test', vbrace(process_ast(ast.test, ctx)))
		]
	)
}

type_handler['TryStatement'] = function(ast, ctx) {
	// console.log(ast)
	if (ast.guardedHandlers) assert(ast.guardedHandlers.length === 0)
	if (ast.handlers) assert(ast.handlers.length === 0 || ast.handlers.length === 1)
	return vdom(
		'div',
		ast.type,
		[
			comments(ast),
			vkeyword('try'),
			vsp(),
			vdom('span', 'block', process_ast(ast.block, ctx)),
			function() {
				if (ast.handler) {
					return [
						vsp(),
						vdom('span', 'handler', process_ast(ast.handler, ctx)) // CatchClause
					]
				}
			},
			function() {
				if (ast.finalizer) {
					return [
						vsp(),
						vkeyword('finally'),
						vsp(),
						vdom('span', 'finalizer', process_ast(ast.finalizer, ctx))
					]
				}
			}
		]
	)
}

type_handler['CatchClause'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		[
			comments(ast),
			vkeyword('catch'),
			vsp(),
			vdom('span', 'param', vbrace(process_ast(ast.param, ctx))),
			vsp(),
			vdom('span', 'body', process_ast(ast.body, ctx))
		]
	)
}

type_handler['ForStatement'] = function(ast, ctx) {
	// console.log(ast)
	return vdom(
		'div',
		ast.type,
		[
			comments(ast),
			vkeyword('for'),
			vsp(),
			// 少见的括号在结构之上的例外
			vbrace([
				vdom('span', 'init', function() {
					// init 部分为 null 是可能的
					if (!ast.init) return
					// 命令 VariableDeclaration 不要生成末尾分号，因为这里会生成
					if (ast.init.type === 'VariableDeclaration') {
						return process_ast(ast.init, ctx, {nosemi: true})
					}
					else {
						return process_ast(ast.init, ctx)
					}
				}),
				vsp(), 
				vsemi(),
				function() {
					if (ast.test) {
						return [
							vsp(),
							vdom('span', 'test', process_ast(ast.test, ctx)),
						]
					}
				},
				vsp(),
				vsemi(),
				vsp(),
				function() {
					if (ast.update) {
						return vdom('span', 'update', process_ast(ast.update, ctx))
					}
				}
			]),
			vsp(),
			vdom('span', 'body', process_ast(ast.body, ctx))
		]
	)
}

type_handler['ForInStatement'] = function(ast, ctx) {
	// console.log(ast)
	return vdom(
		'div',
		ast.type,
		[
			comments(ast),
			vkeyword('for'),
			vsp(),
			// 少见的括号在结构之上的例外
			vbrace([
				vdom('span', 'left', function() {
					// 命令 VariableDeclaration 不要生成末尾分号，因为这里会生成
					if (ast.left && ast.left.type === 'VariableDeclaration') {
						return process_ast(ast.left, ctx, {nosemi: true})
					}
					else {
						return process_ast(ast.left, ctx)
					}
				}),
				vsp(),
				vkeyword('in'),
				vsp(),
				vdom('span', 'right', process_ast(ast.right, ctx))
			]),
			vsp(),
			vdom('span', 'body', process_ast(ast.body, ast))
		]
	)
}

type_handler['ForOfStatement'] = function(ast, ctx) {
	return vdom(
		'div',
		ast.type,
		[
			comments(ast),
			vkeyword('for'),
			vsp(),
			// 少见的括号在结构之上的例外
			vbrace([
				vdom('span', 'left', function() {
					// 命令 VariableDeclaration 不要生成末尾分号，因为这里不需要
					if (ast.left && ast.left.type === 'VariableDeclaration') {
						return process_ast(ast.left, ctx, {nosemi: true})
					}
					else {
						return process_ast(ast.left, ctx)
					}
				}),
				vsp(),
				vkeyword('of'),
				vsp(),
				vdom('span', 'right', process_ast(ast.right, ctx))
			]),
			vsp(),
			vdom('span', 'body', process_ast(ast.body, ast))
		]
	)
}

type_handler['ContinueStatement'] = function(ast, ctx) {
	return vdom(
		'div',
		ast.type,
		[
			comments(ast),
			vkeyword('continue'),
			function() {
				if (ast.label) {
					return [
						vsp(),
						vdom('span', 'label', process_ast(ast.label, ctx))
					]
				}
			},
			vsp(),
			vsemi()
		]
	)
}

type_handler['BreakStatement'] = function(ast, ctx) {
	return vdom(
		'div',
		ast.type,
		[
			comments(ast),
			vkeyword('break'),
			function() {
				if (ast.label) {
					return [
						vsp(),
						vdom('span', 'label', process_ast(ast.label, ctx))
					]
				}
			},
			vsp(),
			vsemi()
		]
	)
}

type_handler['LabeledStatement'] = function(ast, ctx) {
	// console.log(ast)
	return vdom(
		'div',
		ast.type,
		[
			comments(ast),
			vdom('span', 'label', process_ast(ast.label, ctx)),
			vcolon(),
			vsp(),
			vdom('span', 'body', process_ast(ast.body, ctx))
		]
	)
}

type_handler['ThrowStatement'] = function(ast, ctx) {
	return vdom(
		'div',
		ast.type,
		[
			comments(ast),
			vkeyword('throw'),
			vsp(),
			vdom('span', 'argument', process_ast(ast.argument, ctx)),
			vsp(),
			vsemi()
		]
	)
}

type_handler['SwitchStatement'] = function(ast, ctx) {
	return vdom(
		'div',
		ast.type,
		[
			comments(ast),
			vkeyword('switch'),
			vsp(),
			vdom('span', 'discriminant', function() {
				return vbrace(process_ast(ast.discriminant, ctx))
			}),
			vsp(),
			// 少见的括号在结构之上的例外
			vbracket(function() {
				return vdom('span', 'cases', function() {
					return process_ast_list(ast.cases, ctx).map(wrap_vdom('div', 'case'))
				})
			})
		]
	)
}

type_handler['SwitchCase'] = function(ast, ctx) {
	return vdom(
		'div',
		ast.type,
		[
			comments(ast),
			function() {
				// 一般的 case
				if (ast.test) {
					return [
						vkeyword('case'),
						vsp(),
						vdom('span', 'test', process_ast(ast.test, ctx))
					]
				}
				// 没有 test 部分的是 default 分句
				else {
					return [
						vkeyword('default')
					]
				}
			},
			vcolon(),
			vsp(),
			vdom('span', 'consequent', process_ast_list(ast.consequent, ctx))
		]
	)
}

type_handler['CallExpression'] = function(ast, ctx) {

	return vdom(
		'span',
		ast.type,
		[
			comments(ast),
			vdom('span', 'callee', function() {
				if (ast.callee.type === 'FunctionExpression') {
					return v_exp_brace(process_ast(ast.callee, ctx))
				}
				else {
					return process_ast(ast.callee, ctx)
				}
			}),
			vsp(),
			vdom('span', 'arguments', function() {

				// require('xxx') ?
				if (ast.callee.type === 'Identifier' && 
					ast.callee.name === 'require' &&
					ast.arguments.length === 1 &&
					ast.arguments[0].type === 'Literal' &&
					typeof ast.arguments[0].value === 'string') {
					var pkg_name = ast.arguments[0].value

					return vbrace(
						vpkgref(
							pkg_name, 
							vjoin(process_ast_list(ast.arguments, ctx).map(wrap_vdom('span', 'argument')), function() {
								return [vcomma(), vsp()]
							})
						)
					)
				}

				return vbrace(vjoin(process_ast_list(ast.arguments, ctx).map(wrap_vdom('span', 'argument')), function() {
					return [vcomma(), vsp()]
				}))
			})
		]
	)
}

type_handler['AssignmentExpression'] = function(ast, ctx) {
	// console.log(ast)
	return vdom(
		'span',
		ast.type,
		[
			// vdom('span', 'left', v_exp_brace(process_ast(ast.left, ctx))),
			vdom('span', 'left', process_ast(ast.left, ctx)),
			vsp(),
			voperator(ast.operator), // = | *= | += | ...
			vsp(),
			function() {
				// 赋值表达式的右侧如果为序列表达式则必须要补括号
				if (ast.right.type === 'SequenceExpression') {
					return vdom('span', 'right', v_exp_brace(process_ast(ast.right, ctx)))
				}
				else {
					return vdom('span', 'right', process_ast(ast.right, ctx))
				}
			}
		]
	)
}

type_handler['MemberExpression'] = function(ast, ctx) {
	// console.log(ast)
	if (ast.computed) {
		return vdom(
			'span',
			ast.type,
			[
				vdom('span', 'object', process_ast(ast.object, ctx)),
				vdom('span', 'property', vsqbracket(process_ast(ast.property, ctx)))
			]
		)
	}
	else {
		return vdom(
			'span',
			ast.type,
			[
				vdom('span', 'object', process_ast(ast.object, ctx)),
				vdom('span', 'dot', '.'),
				vdom('span', 'property', process_ast(ast.property, ctx))
			]
		)
	}
}

type_handler['NewExpression'] = function(ast, ctx) {
	// console.log(ast)
	assert(ast.callee)
	assert(ast.arguments)
	return vdom(
		'span',
		ast.type,
		[
			vkeyword('new'),
			vsp(),
			vdom('span', 'callee', process_ast(ast.callee, ctx)),
			vsp(),
			vdom('span', 'arguments', [
				vbrace(function() {
					return vjoin(process_ast_list(ast.arguments, ctx).map(wrap_vdom('span', 'argument')), function() {
						return [vcomma(), vsp()]
					})
				})
			]),
		]
	)
}

type_handler['MetaProperty'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		[
			vdom('span', 'meta', process_ast(ast.meta, ctx)),
			vdom('span', 'dot', '.'),
			vdom('span', 'property', process_ast(ast.property, ctx))
		]
	)
}

type_handler['ConditionalExpression'] = function(ast, ctx) {
	// return vdom(
	// 	'span',
	// 	ast.type,
	// 	[
	// 		vdom('span', 'test', v_exp_brace(process_ast(ast.test, ctx))),
	// 		vsp(),
	// 		voperator('?'),
	// 		vsp(),
	// 		vdom('span', 'consequent', v_exp_brace(process_ast(ast.consequent, ctx))),
	// 		vsp(),
	// 		voperator(':'),
	// 		vsp(),
	// 		vdom('span', 'alternate', v_exp_brace(process_ast(ast.alternate, ctx)))
	// 	]
	// )

	return vdom(
		'span',
		ast.type,
		[
			vdom('span', 'test', v_exp_brace(process_ast(ast.test, ctx))),
			vsp(),
			vdom('span', '_align', [
				vdom('div', '_align', [
					voperator('?'),
					vsp(),
					vdom('span', 'consequent', v_exp_brace(process_ast(ast.consequent, ctx))),
					// vsp()
				]),
				vdom('div', '_align', [
					voperator(':'),
					vsp(),
					vdom('span', 'alternate', v_exp_brace(process_ast(ast.alternate, ctx)))
				])
			])
		]
	)
}

type_handler['BinaryExpression'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		[
			vdom('span', 'left', v_exp_brace(process_ast(ast.left, ctx))),
			vsp(),
			voperator(ast.operator),
			vsp(),
			vdom('span', 'right', v_exp_brace(process_ast(ast.right, ctx))),
		]
	)
}

type_handler['UpdateExpression'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		function() {
			if (ast.prefix) {
				return [
					voperator(ast.operator),
					vsp(),
					vdom('span', 'argument', v_exp_brace(process_ast(ast.argument, ctx)))
				]
			}
			else {
				return [
					vdom('span', 'argument', v_exp_brace(process_ast(ast.argument, ctx))),
					vsp(),
					voperator(ast.operator)
				]
			}
		}
	)
}

type_handler['LogicalExpression'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		[
			vdom('span', 'left', v_exp_brace(process_ast(ast.left, ctx))),
			vsp(),
			voperator(ast.operator),
			vsp(),
			vdom('span', 'right', v_exp_brace(process_ast(ast.right, ctx))),			
		]
	)
}

type_handler['UnaryExpression'] = function(ast, ctx) {
	assert(ast.prefix === true)
	return vdom(
		'span',
		ast.type,
		[
			voperator(ast.operator),
			vsp(),
			vdom('span', 'argument', v_exp_brace(process_ast(ast.argument, ctx)))
		]
	)
}

type_handler['SequenceExpression'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		vdom('span', 'expressions', function() {
			return vjoin(process_ast_list(ast.expressions, ctx).map(v_exp_brace).map(wrap_vdom('span', 'expression')), function() {
				return [voperator(','), vsp()]
			})
		})
	)
}

type_handler['ArrayExpression'] = function(ast, ctx) {
	return vdom(
		'span',
		[ast.type, ast.elements && ast.elements.length >= 5 ? 'vertical-layout' : undefined],
		vdom('span', 'elements', vsqbracket(function() {
			if (!ast.elements || ast.elements.length < 1) return
			var elements = ast.elements.map(function(e) {
				if (e === null) {
					return {
						type: 'ArrayExpressionNullElement'
					}
				}
				else {
					return e
				}
			})
			return vjoin(process_ast_list(elements, ctx).map(wrap_vdom('span', 'element', add_element_index_tag)), function() {
				return [vcomma(), vsp()]
			})
		}))
	)
}

// 为了处理 ArrayExpression 中 null 元素而扩展出来的类型
// 不属于 esprima 解析结果
type_handler['ArrayExpressionNullElement'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		null
	)
}

type_handler['ObjectExpression'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		vdom('span', 'properties', vbracket(function() {
			if (!ast.properties || ast.properties.length < 1) return
			return vjoin(process_ast_list(ast.properties, ctx).map(wrap_vdom('span', 'property')), function() {
				return [vcomma(), vsp()]
			})
		}))
	)
}

type_handler['Property'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		function() {
			// ES6 属性简写？
			if (ast.shorthand) {
				assert(ast.method === false)
				assert(ast.computed === false)
				assert(ast.kind === 'init')
				assert(ast.key && ast.key.type === 'Identifier')

				if (ast.key.type === ast.value.type) {
					assert(ast.key.name === ast.value.name)
					return vdom('span', ['key', 'shorthand'], process_ast(ast.key, ctx))
				}
				else {
					assert(ast.value.type === 'AssignmentPattern')
					return vdom('span', ['key', 'shorthand'], process_ast(ast.value, ctx))
				}
			}
			// get/set ？
			else if (ast.kind === 'get' || ast.kind === 'set') {
				assert(ast.method === false)
				return [
					vkeyword(ast.kind),
					vsp(),
					function() {
						if (ast.computed) {
							return vdom('span', 'key', vsqbracket(process_ast(ast.key, ctx)))
						}
						else {
							return vdom('span', 'key', process_ast(ast.key, ctx))
						}
					},
					vdom('span', 'value', process_ast(ast.value, ctx, {no_function_keyword: true})) // 注意传递了信息给 FunctionExpression 让它不要生成 function 关键字
				]
			}
			else {
				// ES6 方法？
				if (ast.method) {
					assert(ast.shorthand === false)
					assert(ast.kind === 'init')
					return [
						function() {
							if (ast.computed) {
								return vdom('span', 'key', vsqbracket(process_ast(ast.key, ctx)))
							}
							else {
								return vdom('span', 'key', process_ast(ast.key, ctx))
							}
						},
						vsp(),
						vdom('span', 'value', process_ast(ast.value, ctx, {no_function_keyword: true})) // 注意传递了信息给 FunctionExpression 让它不要生成 function 关键字
					]
				}
				// 老的属性表达方式
				else {
					return [
						function() {
							if (ast.computed) {
								return vdom('span', 'key', vsqbracket(process_ast(ast.key, ctx)))
							}
							else {
								return vdom('span', 'key', process_ast(ast.key, ctx))
							}
						},
						vcolon(),
						vsp(),
						vdom('span', 'value', process_ast(ast.value, ctx))
					]
				}
			}
		}
	)
}

type_handler['AssignmentPattern'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		[
			vdom('span', 'left', process_ast(ast.left, ctx)),
			vsp(),
			voperator('='),
			vsp(),
			vdom('span', 'right', process_ast(ast.right, ctx)),
		]
	)
}

type_handler['ArrayPattern'] = function(ast, ctx) {
	return vdom(
		'span',
		[ast.type, ast.elements && ast.elements.length >= 5 ? 'vertical-layout' : undefined],
		vdom('span', 'elements', vsqbracket(function() {
			if (!ast.elements || ast.elements.length < 1) return
			// 把 elements 中为 null 的都转为 Array
			var elements = ast.elements.map(function(e) {
				if (e === null) {
					return {
						type: 'ArrayPatternNullElement'
					}
				}
				else {
					return e
				}
			})
			// 逐个转换
			return vjoin(process_ast_list(elements, ctx).map(wrap_vdom('span', 'element')), function() {
				return [vcomma(), vsp()]
			})
		}))
	)
}

// 为了处理 ArrayPattern 中 null 元素而扩展出来的类型
// 不属于 esprima 解析结果
type_handler['ArrayPatternNullElement'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		null
	)
}

type_handler['ObjectPattern'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		vdom('span', 'properties', vbracket(function() {
			if (!ast.properties || ast.properties.length < 1) return
			return process_ast_list(ast.properties, ctx).map(wrap_vdom('div', 'property'))
		}))
	)
}

type_handler['ThisExpression'] = function(ast, ctx) {
	return vdom('span', [ast.type, 'keyword'], 'this')
}

type_handler['Identifier'] = function(ast, ctx) {
	return vdom(
		'span', 
		{
			'class': [ast.type, 'identifier'].join(' '),
			'data-id': ast.fmtjs_id
		}, 
		ast.name
	)
}

type_handler['TemplateElement'] = function(ast, ctx) {
	// assert(ast.value.raw === ast.value.cooked)
	return vdom(
		'span',
		ast.type,
		ast.value.raw // 这里没有使用 ast.value.cooked，以后研究清楚后再重新考虑下
	)
}

type_handler['TemplateLiteral'] = function(ast, ctx) {
	// console.log(ast)
	return vdom(
		'span',
		ast.type,
		[
			vdom('span', '', '`'),
			function() {
				var children = []
				var quasis = ast.quasis
				var expressions = ast.expressions
				// 即使对于空模版字符串 `` 下面的条件也成立
				// 而且对于 `${e}` 这样只有表达式的空模版字符串也是成立的
				assert(quasis.length === (expressions.length + 1))
				for (var i = 0; i < quasis.length; ++i) {
					var q = quasis[i]
					var e = expressions[i]
					// 注意包装在 q 元素中
					children.push(vdom('span', 'q', process_ast(q, ctx)))
					// 当 q 是尾元素时，e 必然不存在
					if (q.tail === true) assert(e === undefined)
					if (e) {
						// 注意包装在 e 元素中
						children.push(vdom('span', 'e', v_texp(process_ast(e, ctx))))
					}
				}
				return children
			},
			vdom('span', '', '`')
		]
	)
}

type_handler['TaggedTemplateExpression'] = function(ast, ctx) {
	return vdom(
		'span',
		ast.type,
		[
			vdom('span', 'tag', process_ast(ast.tag, ctx)),
			vdom('span', 'quasi', process_ast(ast.quasi, ctx))
		]
	)
}

type_handler['Literal'] = function(ast, ctx) {
	// console.log(ast)
	if (ast.regex) {
		return vdom('span', [ast.type, 'regex'], ast.raw)
	}

	var value = ast.value
	var raw = ast.raw
	var value_type = typeof value
	switch (value_type) {
		case 'number':
			// 优先按照原始数值显示（维持进制）注意字母强制转为小写
			return vdom('span', [ast.type, value_type], [
				vdom('span', 'raw', raw.toLowerCase()),
				vdom('span', 'value', value.toString())
			])
		case 'boolean':
			return vdom('span', [ast.type, value_type], value.toString())
		case 'object':
			assert(value === null)
			return vdom('span', [ast.type, value_type], 'null')
		case 'undefined':
			return vdom('span', [ast.type, value_type], 'undefined')
		case 'string':
			return vdom('span', [ast.type, value_type], JSON.stringify(value))
		default:
			throw new Error('not implemented')
	}
}

////////////////////////////////////////////////////////////////
// 递归处理关键
////////////////////////////////////////////////////////////////

// 对指定的 AST 节点进行处理
// 参数：
// - ast: 目标 AST 节点（必填）
// - ctx: 上下文对象（必填），层层传递
// - ccfg: Child Config 子节点配置（必填）,这一参数只会传给直接下级节点，不会层层传递
function process_ast(ast, ctx, ccfg) {
	assert(ast && true)
	assert(ctx && true)

	// 调用 type_handler 上对应的处理函数
	if (type_handler[ast.type]) {
		log('info', 'processing type' + ast.type)
		return type_handler[ast.type](ast, ctx, ccfg)
	}
	else {
		log('warning', 'unknown type: ' + ast.type)
	}
}

function process_ast_list(ast_list, ctx) {
	return ast_list.map(function(ast) {
		return process_ast(ast, ctx)
	})
}

////////////////////////////////////////////////////////////////
// 辅助函数，很重要
////////////////////////////////////////////////////////////////

// 这个函数可以把一个 function/program 内部整理为有序的形式
// 把所有 sub-function 都排到末尾，而且以字母顺序排序
function ordered_body(ast_body) {
	if (!ast_body || ast_body.length < 1) return ast_body

	var statements = []
	var sub_functions = []

	ast_body.forEach(function(item) {
		if (item.type === 'FunctionDeclaration') {
			sub_functions.push(item)
		}
		else {
			statements.push(item)
		}
	})

	sub_functions.sort(function(a, b) {
		return a.id.name.toLowerCase() < b.id.name.toLowerCase() ? -1 : 1
	})
	return statements.concat(sub_functions)
}

// 这个函数用于显示注释
function comments(ast) {
	if (ast.leadingComments) {
		return vdom('pre', 'comment', ast.leadingComments.map(function(comment) {
			if (comment.type === 'Block') {
				var value = comment.value
				value = value.split('\n').map(function(line) {if (/^\s+\*/.test(line)) {
						return line.replace(/^\s+\*/, ' *')
					}
					else {
						return line
					}
				}).join('\n')
				var ret = '/*' + value + '*/'
				if (/\n\s\s+\*\/$/.test(ret)) {
					ret = ret.replace(/\n\s\s+\*\/$/, '\n */')
				}
				return ret
			}
			else {
				return '// ' + comment.value.trim()
			}
		}).join('\n'))
	}
}

////////////////////////////////////////////////////////////////
// 工具函数，很重要
////////////////////////////////////////////////////////////////

// 这个是用来和 [].map() 函数配合，进行节点包装的
function wrap_vdom(name, attrs, f) {
	return function(vdom_item, i) {
		if (!f) 
			return vdom(name, attrs, [vdom_item])
		else 
			return vdom(name, attrs, f(vdom_item, i))
	}
}

function zip(list_a, list_b) {
	assert(Array.isArray(list_a))
	assert(Array.isArray(list_b))
	// assert(list_a.length === list_b.length)
	var new_list = []
	for (var i = 0; i < list_a.length; ++i) {
		new_list.push([list_a[i], list_b[i]])
	}
	return new_list
}

function assert(v) {
	if (!v) {
		debugger
		throw new Error('assert failed')
	}
}

// 对指定名称的类型处理函数进行包装替换
// 这样我们就能在原有函数执行前后进行额外的处理
function wrap_type_handler(name, new_handler_factory) {
	assert(typeof type_handler[name] === 'function')
	var handler = type_handler[name]
	return new_handler_factory(handler)
}

// 为元素添加索引编号（常用于数组环境，与 wrap_vdom() 配合）
function add_element_index_tag(element, i) {
	var tag = vdom('span', {'class': 'index-tag'}, [i.toString()])
	var element_span = vdom('span', {'class': 'element-span'}, element)
	return [tag, element_span]
}