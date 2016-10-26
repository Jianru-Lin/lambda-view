#!/usr/bin/env node
var helper = require('./helper')
var path = require('path')
var fmtjs = require('fmtjs')
var argv = process.argv.slice(2)
var args = require('minimist')(argv)
var log = helper.log

run()

function run() {
	if (argv.length === 0 || args.help || args.h) {
		print_usage()
	}
	else if (args.version || args.v) {
		print_version()
	}
	else {
		var tasks = args._.map(function(file) {
			return function(cb) {
				compile(file, cb)
			}
		})
		run_tasks(tasks)
	}
}

function run_tasks(tasks) {
	if (tasks.length < 1) return
	var curr = tasks[0]
	var rest = tasks.slice(1)
	curr(function(err) {debugger
		if (err) return
		run_tasks(rest)
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