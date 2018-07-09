;(function() {
	var utils = window.utils = {}

	var params = {}
	var str = location.search
	if (str && str.length > 1) {
		str.substring(1).split('&').forEach(function(item) {
			var parts = item.split('=')
			var name = decodeURIComponent(parts[0])
			var value = decodeURIComponent(parts[1])
			params[name] = value
		})
	}

	utils.url_params = function(name) {
		return params[name]
	}
})();
