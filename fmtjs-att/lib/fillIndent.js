var Vast = require('./Vast')

module.exports = exports = fillIndent

function fillIndent(vast) {
	if (!vast.children || vast.children.length < 1) return vast

	var willIndentList = []

	calcIndentDetail()
	applyIndent()

	return vast

	function calcIndentDetail() {
		var ctx = {
			elementList: vast.children
		}

		var lastLineNo
		var lastIndentId

		while (lineOrientedEat(ctx)) {
			var currentLineNo = ctx.lineNo
			var currentIndentId = ctx.indentStack[ctx.indentStack.length - 1]
			if ((currentLineNo !== lastLineNo) || (currentIndentId !== lastIndentId)) {
				// this is where we want to do indent :)
				// but indent zero is meaningless so we will ignore it
				var indentLevel = ctx.indentStack.length
				var elementPos = ctx.nextElementPos - 1
				if (indentLevel > 0) {
					willIndentList.push({
						elementPos: elementPos,
						indentLevel: indentLevel
					})							
				}
			}
			lastLineNo = currentLineNo
			lastIndentId = currentIndentId
		}
	}

	function lineOrientedEat(ctx) {
		// ctx:
		// - [out]     lineNo
		// ...
		// (base on indentOrientedEat)

		if (ctx.lineNo === undefined)
			ctx.lineNo = 0

		while (indentOrientedEat(ctx)) {
			if (Vast.isBr(ctx.element)) {
				++ctx.lineNo
			}
			else {
				return true
			}
		}

		// meet the end
		return false
	}

	function indentOrientedEat(ctx) {
		// ctx:
		// - [out]     indentStack
		// - [*]       nextIndentId
		// ...
		// (base on eat)

		if (ctx.indentStack === undefined)
			ctx.indentStack = []

		while (eat(ctx)) {
			if (isIndentEnter(ctx.element)) {
				enter()
			}
			else if (isIndentLeave(ctx.element)) {
				leave()
			}
			else {
				// ok, done
				return true
			}
		}

		// end
		return false

		function enter() {
			if (ctx.nextIndentId === undefined)
				ctx.nextIndentId = 0

			ctx.indentStack.push(ctx.nextIndentId++)
		}

		function leave() {
			ctx.indentStack.pop()
		}
	}

	function applyIndent() {
		// do insert indent

		var indentMap = []
		willIndentList.forEach(function(willIndent) {
			indentMap[willIndent.elementPos] = Vast.span(undefined, makeIndentSpace(willIndent.indentLevel))
		})

		var newChildren = []
		vast.children.forEach(function(c, i) {
			var indent = indentMap[i]
			if (indent) newChildren.push(indent)
			newChildren.push(c)
		})
		vast.children = newChildren

		function makeIndentSpace(level) {
			if (level < 1) {
				return ''
			}
			else {
				var unit = '    '
				var list = []
				for (var i = 0; i < level; ++i) {
					list.push(unit)
				}
				return list.join('')
			}
		}
	}

	function eat(ctx) {
		// ctx:
		// - [in]      elementList
		// - [out]     element
		// - [*]       nextElementPos

		if (ctx.nextElementPos === undefined) {
			ctx.nextElementPos = 0
		}

		if (ctx.nextElementPos < ctx.elementList.length) {
			ctx.element = ctx.elementList[ctx.nextElementPos]
			++ctx.nextElementPos
			return true
		}
		else {
			return false
		}
	}

	// function peekNext(cb) {
	// 	if (!cb) debugger
	// 	var target = vast.children[i]
	// 	if (!target) return false
	// 	else return cb(target)
	// }

	function isIndentEnter(target) {
		return target.notDom && target.name === 'indent' && target.type === 'enter'
	}

	function isIndentLeave(target) {
		return target.notDom && target.name === 'indent' && target.type === 'leave'
	}

}