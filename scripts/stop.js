try {
	console.log('stopping lambda-view web server...')
	require('fmtjs-web').stop(function(err) {
		if (err) {
			console.error(err.message)
			return
		}
		console.log('done.')
	})	
}
catch (err) {
	// console.error(err.message)
}
