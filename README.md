# lambda-view: An New Tool for Reading JavaScript Code

lambda-view provids you a better way to READ JavaScript source code. You can use it to read any ES5/ES6 JavaScript code.

# Screenshots

![](screenshots/lambda-view-demo.js.png)

# Get start in two steps

STEP-1 install it via npm (sudo required maybe):

```
npm install -g lambda-view
```

STEP-2 use lv command to open any JavaScript file:

```
lv target.js
```

Then (if everything is ok), your web browser will be opened automatically and shows you the result. Pretty easy, right? 

# Helpful tips

### Can I open remote file by URL?

Yes, you can try:

```
lv https://code.jquery.com/jquery-3.1.1.js
```

### Can I open multiple files in one command?

Yes, you can provide multiple filenames to lv command:

```
lv file1.js file2.js file3.js
```

Further more, you can use the wildcard (For Linux/Mac only, Not Windows):

```
lv *.js
```

Combine all of this together, you will find its useful.

### Can I use lambda-view on my iphone/ipad?

Yes, but this is an experimental feature. You can achieve it in three steps.

STEP-1 stop current lambda-view web server:

```
lv-svr stop
```

STEP-2 start lambda-view web server with public mode:

```
lv-svr start --public
```

STEP-3 open any JavaScript file you want to read on your iphone/ipad:

```
lv some-file.js
```

Now, the web browser will be opened, and you can copy it's URL to your iphone/ipad's safari. Then you can read it.
