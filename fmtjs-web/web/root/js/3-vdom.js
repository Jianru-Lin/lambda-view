function VDom(name, attrs, children) {
	this.name = name
	this.attrs = attrs
	this.children = children
}

VDom.prototype.toDom = function() {
	var e = document.createElement(this.name)
	var attrs = norm_attrs(this.attrs)
	// console.log(attrs)
	var children = norm_children(this.children)
	if (attrs) {
		for (var attr_name in attrs) {
			var attr_value = attrs[attr_name]
			e.setAttribute(attr_name, attr_value)
		}		
	}
	if (children) {
		// 把单元素统一转为数组形式
		if (!Array.isArray(children)) {
			children = [children]
		}

		// 过滤掉所有无意义的元素 null、undefined 之类
		children = children.filter(function(child) {
			if (child === null || child === undefined) {
				return false
			}
			else {
				// 断言：当前数组里要么是字符串，要么是 VDom 节点，不会有函数或者数组等
				assert(typeof child === 'string' || child instanceof VDom)
				return true				
			}
		})

		// 逐个处理
		children.forEach(function(child) {
			if (typeof child === 'string') {
				var text = child
				var span = document.createElement('span')
				span.textContent = child
				e.appendChild(span)
			}
			else if (child instanceof VDom) {
				e.appendChild(child.toDom())
			}
			else {
				throw new Error('invalid child: ' + child)
			}
		})
	}
	return e
}

function norm_attrs(attrs) {
	if (attrs === null || attrs === undefined) return {}
	switch (typeof attrs) {
		case 'function':
			return norm_attrs(attrs())
		case 'object':
			var o = {}
			for (var k in attrs) {
				o[k] = normalize_attr_value(attrs[k])
			}
			return o
		default:
			throw new Error('invalid attributes: ' + attrs)
	}

	function normalize_attr_value(attr_value) {
		if (attr_value === null || attr_value === undefined) return ''
		switch (typeof attr_value) {
			case 'function':
				return normalize_attr_value(attr_value())
			case 'string':
				return attr_value
			default:
				throw new Error('invalid attribute value: ' + attr_value)
		}
	}
}

// 输入定义如下
// Children ::= null || undefined || String || VDom || Function || [Children]
// 这个函数的主要作用是，执行所有 Function 同时展开所有形如 [Children] 的嵌套数据
// 输出定义如下
// Children ::= null || undefined || String || VDom || [Child]
// Child    ::= null || undefined || String || VDom
function norm_children(children) {
	if (children === null || 
		children === undefined ||
		typeof children === 'string' ||
		children instanceof VDom) return children

	if (typeof children === 'function') {
		var f = children
		return norm_children(f())
	}
	else if (Array.isArray(children)) {
		children =  children.map(function(child) {
			return norm_children(child)
		})
		var list = []
		flat(children, list)
		return list
	}
	else {
		throw new Error('invalid children: ' + children)
	}

	function flat(array_tree, out_list) {
		array_tree.forEach(function(item) {
			if (Array.isArray(item)) {
				flat(item, out_list)
			}
			else {
				out_list.push(item)
			}
		})
	}
}

function vdom(name, attrs, children) {
	if (typeof attrs === 'string') {
		attrs = {
			'class': attrs
		}
	}
	else if (Array.isArray(attrs)) {
		attrs = {
			'class': attrs.join(' ')
		}
	}
	return new VDom(name, attrs, children)
}

// vdom package reference
function vpkgref(pkg_name, children) {
	return vdom(
		'a', 
		{
			'class': 'pkg-ref',
			'pkg-name': pkg_name
		},
		children
	)
}

// vdom import module reference
function vpkgref_i(pkg_name, children) {
	return vpkgref('./' + pkg_name, children)
}

// vdom keyword
function vkeyword(text) {
	return vdom('span', ['keyword', text], text)
}

// vdom operator
function voperator(text) {
	return vdom('span', 'operator', text)
}

// vdom space
function vsp() {
	return vdom('span', 'sp', ' ')
}

// vdom br
function vbr() {
	return vdom('br', '')
}

// vdom semicolon
function vsemi() {
	return vdom('span', 'semicolon', ';')
}

// vdom comma
function vcomma() {
	return vdom('span', 'comma', ',')
}

// vdom colon
function vcolon() {
	return vdom('span', 'colon', ':')
}

// vdom left & right brace
function vbrace(children) {
	return [
		vdom('span', ['left-coll', 'brace'], '('),
		vdom('span', ['collapsable', 'brace'], children),
		vdom('span', ['collapsable-switcher', 'hidden'], '...'),
		vdom('span', ['right-coll', 'brace'], ')')
	]
}

// vdom left & right brace for expression
function v_exp_brace(children) {
	return vdom('span', ['exp-brace'], [
		vdom('span', ['left-coll'], '('),
		vdom('span', ['collapsable'], children),
		vdom('span', ['collapsable-switcher', 'hidden'], '...'),
		vdom('span', ['right-coll'], ')')
	])
}

// vdom left & right bracket
function vbracket(children) {
	return [
		vdom('span', ['left-coll', 'bracket'], '{'),
		vdom('span', ['collapsable', 'bracket'], children),
		vdom('span', ['collapsable-switcher', 'bracket', 'hidden'], '...'),
		vdom('span', ['right-coll', 'bracket'], '}')
	]
}

// vdom left & right square bracket
function vsqbracket(children) {
	return [
		vdom('span', ['left-coll', 'square-bracket'], '['),
		vdom('span', ['collapsable', 'square-bracket'], children),
		vdom('span', ['collapsable-switcher', 'square-bracket', 'hidden'], '...'),
		vdom('span', ['right-coll', 'square-bracket'], ']')
	]
}

// vdom template expression
function v_texp(children) {
	return vdom('span', 'template-expression', [
		vdom('span', ['left-coll', 'prefix'], '${'),
		vdom('span', ['collapsable', 'exp'], children),
		vdom('span', ['collapsable-switcher', 'square-bracket', 'hidden'], '...'),
		vdom('span', ['right-coll', 'postfix'], '}')
	])
}

// join
// 限制：children 必须是数组，而且其中每个 child 元素就是代表最终的一个 VDom
function vjoin(children, j_child) {
	if (!Array.isArray(children)) return children
	var list = []
	children.forEach(function(child, i) {
		list.push(child)
		if (i < (children.length - 1)) {
			list.push(j_child)
		}
	})
	return list
}

// 生成三个 id ，用于辅助实现折叠特性
function tri_id() {
	if (!window._next_tri_id) {
		window._next_tri_id = 1
	}
	var id = window._next_tri_id
	var result = [id, id+1, id+2].map(function(i) {
		return 'collapsable-' + i
	})
	window._next_tri_id += 3
	return result
}