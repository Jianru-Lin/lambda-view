module.exports = function(app) {
	app.post('/stop', function(req, res) {
		res.json({
			ok: true
		})
		setTimeout(function() {
			process.exit()
		}, 500)
	})
}