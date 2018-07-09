var Vast = require('./lib/Vast')

function render(att, options) {
	return Vast.toText(att, options)
}

module.exports = exports = render