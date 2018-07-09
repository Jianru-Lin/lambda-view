module.exports = enc

function enc(str) {
	return encodeURIComponent(str).replace(/%2F/g, '/')
}