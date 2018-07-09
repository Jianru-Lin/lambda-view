var esprima = require('esprima')
var att = require('../fmtjs-att')
var render = require('../fmtjs-att-render')

function fmtjs(text, options) {
	try {
		var ast = esprima.parse(text, {
			sourceType: 'module',
			comment: true,
			attachComment: true
		})
	}
	catch (err) {
		if (err.message.indexOf('strict mode') !== -1) {
			// retry as normal mode again
			var ast = esprima.parse(text, {
				comment: true,
				attachComment: true
			})
		}
		else {
			// JSON file?
			try {
				JSON.parse(text)
				var fixed_text = '(' + text + ')'
				var ast = esprima.parse(fixed_text)
			}
			catch (_) {

			}
		}
	}
	var output = render(att(ast, options), options)
	return output
}

module.exports = exports = fmtjs