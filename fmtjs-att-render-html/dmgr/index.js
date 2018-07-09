var assert = require('assert')
var web_ditem = require('../fmtjs-web/ditem/')

exports.save = function(data) {
	assert(data)
	assert(data.filename)
	assert(data.version)
	assert(data.att)
	assert(data.att.ast)
	assert(data.att.ctx)
	assert(data.att.ctx.flat)

	var ast = data.att.ast
	var ast_str = JSON.stringify(ast)
	var id = hash(ast_str)

	var ast_flatted = data.att.ctx.flat

	var data = {
		index: {
			id: id,
			filename: data.filename,
			version: data.version,
		},
		ast: data.att.ast,
	}

	ast_flatted.list.forEach(function(node) {
		data['ast-' + node.fmtjs_id] = node
	})

	web_ditem.create(id, data)
	return id
}

function hash(text) {
	var algorithm = require('crypto').createHash('sha256')
	algorithm.update(text)
	return algorithm.digest('hex').substring(0, 16)
}
