var path = require('path')
var cache = require('../ditem/cache')
var express = require('express')
var body_parser = require('body-parser')
var share = require('./share/')
var app = express()

var web_root_dir = path.resolve(__dirname, 'root')
var cache_root_dir = cache.root()

app.use(body_parser.json())
app.use(express.static(web_root_dir, {fallthrough: true}))
app.use(express.static(cache_root_dir))

require('./handler/compile')(app)
require('./handler/status')(app)
require('./handler/stop')(app)
require('./handler/require')(app)

exports.start = function(port, host, cb) {
	share.host = host
	share.port = port
	return app.listen(port, host, cb)
}