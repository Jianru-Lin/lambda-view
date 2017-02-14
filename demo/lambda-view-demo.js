log.info('read file from ' + jstr(target) + "...")
helper.load_utf8_file(target, function(err, input) {
	if (err) {
		log.error(err)
		cb(err)
		return
	}
	try {
		log.info('parsing javascript source code and generating html content...')
		var result = fmtjs(input.content, {
			mode: 'html', 
			filename: input.filename, 
			version: require('./package.json').version
		})

		assert(typeof result.id === 'string')
		log.info('try opening it using your default web browser...')
		var ok = helper.open_html_file(g.url + '/lv.html?id=' + result.id)
		log.info(ok ? 'done :)' : 'failed, try opening it manually please :(')
		cb()
	}
	catch (err) {
		log.error(err.message)
		console.log(err.stack)
		cb(err)
	}
})