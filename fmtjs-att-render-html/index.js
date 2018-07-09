var assert = require('assert')
var pkg = require('../package.json')
var dmgr = require('./dmgr')

module.exports = render

function render(att, options) {
	// generate index data
	var id = dmgr.save({
		filename: options.filename || 'unknown',
		version: options.version || pkg.version,
		att: att
	})
	assert(typeof id === 'string')
	var result = {
		id: id
	}
	return result
}
