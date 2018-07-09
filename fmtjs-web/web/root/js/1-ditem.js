;(function() {
	var ditem = window.ditem = {}
	ditem.get = function(id, name, cb) {
		load_cache(id, name, cb)
	}

	// 加载数据
	function load_cache(id, name, cb) {
		var url = id + '/' + name + '.json'
		$.getJSON(url, function(data) {
			cb(null, data)
		})
	}
})();