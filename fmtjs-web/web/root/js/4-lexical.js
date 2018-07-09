// 这个模块提供了 Lexical 操作相关的 API
// 由于原始的 Lexical 内部数据结构比较复杂，而且可能发生变化
// 因此建议使用这个模块提供的 API 进行操作

var lexical = (function() {

	return {
		// 返回与某个 Identifier 有相同引用的所有 Identifier
		same: function(ast_id) {
			var lexical = window.att.ctx.lexical
			return [ast_id]
		}
	}
})();