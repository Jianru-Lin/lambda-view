var text_render = require('../fmtjs-att-render-text')
var html_render = require('../fmtjs-att-render-html')

function render(att, options) {
	options = fill_default_value(options)
	switch (options.mode) {
		// case 'text':
		// 	return text_render(att, options)
		case 'html':
			return html_render(att, options)
		default:
			throw new Error('invaid render mode: ' + options.mode)
	}

	function fill_default_value(options) {
		options = options || {}
		options.mode = options.mode || 'text'
		return options
	}
}

module.exports = exports = render