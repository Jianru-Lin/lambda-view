var assert = require('assert')
var cache = require('./cache')

exports.create = function(id, data) {
	cache.mkdir(id)
	for (var k in data) {
		var filename = id + '/' + k + '.json'
		// console.log('write ' + filename)
		cache.write_file(filename, JSON.stringify(data[k]))
	}
}

exports.url_of = function(id) {
	return require('../config').url() + '/lv.html?id=' + encodeURIComponent(id)
}