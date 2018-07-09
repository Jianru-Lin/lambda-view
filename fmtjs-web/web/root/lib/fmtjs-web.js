window.fmtjs_web = {
	compile_service: {
		package: function(info, cb) {
			cb = cb || function() {}
			$.ajax({
				method: 'POST',
				url: '/compile',
				cache: false,
				data: JSON.stringify({
					type: 'package',
					name: info.name
				}),
				contentType: 'application/json; charset=UTF-8',
				success: function(res) {
					if (res.ok) {
						cb(null, res.result)
					}
					else {
						cb(new Error(res.error))
					}
				},
				error: function() {
					cb(new Error('Request failed, reload page to retry.'))
				}
			})
		}
	}
}