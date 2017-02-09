#!/usr/bin/env node
var helper = require('./helper')
var assert = require('assert')
var path = require('path')
var program = require('commander')
var async = require('async')
var fmtjs = require('fmtjs')
var log = helper.log

var g = {
	url: null
}

program
	.version(require('./package').version)
	// .option('-s, --simple-mode', 'analyze as simple mode')
	// .option('-p, --package-mode', 'analyze as package mode')

program.parse(process.argv)

if (!program.args.length) {
	program.help()
}
else {
	run(program.args)
}

function run(files) {
	// start web server automatically
	start_web_server(function(err, status) {
		if (err) return

		g.url = status.url

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
	})
}

function start_web_server(cb) {
	is_web_server_started(
		function yes(status) {
			cb(undefined, status)
		},
		function no() {
			var opt = {
				background: true,
				public: false
			}
			require('fmtjs-web').start(opt, function(err, status) {
				if (err) {
					log.error(err.message)
					cb(err)
					return
				}
				cb(undefined, status)
			})
		}
	)
}

function is_web_server_started(yes_cb, no_cb) {
	var web = require('fmtjs-web')
	web.status(function(err, status) {
		if (err) {
			no_cb()
		}
		else {
			yes_cb(status)
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