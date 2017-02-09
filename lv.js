#!/usr/bin/env node
var helper = require('./helper')
var path = require('path')
var program = require('commander')
var async = require('async')
var fmtjs = require('fmtjs')
var log = helper.log

program
	.version(require('./package').version)

program
	.command('simple <file...>')
	.description('analyze file as simple mode')
	.action(function(files) {
		// check is web server started?
		is_web_server_started(
			function yes() {
				var funs = files.map(function(file) {
					return function(callback) {
						compile(file, function(err) {
							if (err) {
								callback(err)
							}
							else {
								console.log('')
								callback(err)
							}
						})
					}
				})
				async.series(funs, function(err, results) {
					if (err) {
						return
					}
				})
			},
			function no() {
				log.error('Lambda-view server not started yet. Run "lv-svr start" to start it then retry again.')
				log.error('See https://github.com/Jianru-Lin/lambda-view for more details.')
			}
		)

	})

if (process.argv.length <= 2) {
	program.parse(process.argv.concat(['--help']))
}
else {
	program.parse(process.argv)
}

function is_web_server_started(yes_cb, no_cb) {
	var web = require('fmtjs-web')
	web.status(function(err, status) {
		if (err) {
			no_cb()
		}
		else {
			yes_cb()
		}
	})
}

function compile(target, cb) {
	log.info('read file from ' + jstr(target) + "...")
	helper.load_utf8_file(target, function(err, input) {
		if (err) {
			log.error(err)
			cb(err)
			return
		}
		try {
			log.info('parsing javascript source code and generating html content...')
			var out_content = fmtjs(input.content, {
				mode: 'html', 
				filename: input.filename, 
				version: require('./package.json').version
			})
			// log.info('calculating hash...')
			var out_filename = 'lambda-view-' + helper.hash(out_content) + '-' + input.filename + '.html'
			out_filename = path.resolve(helper.tmp_dir(), out_filename)
			log.info('write file to ' + jstr(out_filename) + "...")
			helper.save_utf8_file(out_filename, out_content)
			log.info('try opening it using your default web browser...')
			var ok = helper.open_html_file(out_filename)
			log.info(ok ? 'done :)' : 'failed, try opening it manually please :(')
			cb()
		}
		catch (err) {
			log.error(err.message)
			console.log(err.stack)
			cb(err)
		}
	})
}

function jstr(o) {
	return JSON.stringify(o)
}

function print_version() {
	var p = require('./package.json')
	var author = (p.author && p.author.name) ? p.author.name : p.author
	console.log(p.name + ' v' + p.version + ' by ' + author + ' (?)'.replace('?', p.email))
}

function print_usage() {
	console.log('Usage: lv <file.js>')
}