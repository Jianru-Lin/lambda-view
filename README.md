# lambda-view 新的 JavaScript 源代码阅读工具

大型项目的 JavaScript 代码很难阅读？试试 lambda-view，它能解析指定的 JavaScript 源代码文件，转换为适合阅读和分析的形式助你度过难关。

## 如何安装

### 支持哪些平台？

lambda-view 可以在 Windows、MacOS、Linux 环境下使用。

### 注意：lambda-view 的运行依赖于 Node.js

lambda-view 基于 Node.js 开发，因此如果你的系统中还没有安装，建议[前往 Node.js 官网](https://nodejs.org/)下载安装。建议选择 LTS（长期支持）版。

### 准备好了？执行这条命令即可完成安装！

打开命令行，执行下面的命令便能将 lambda-view 安装到你的系统中（对于某些系统可能需要管理员权限）：

```
npm install -g lambda-view
```

### 现在你的系统获得了 ```lv``` 命令，旅程开始！

当你完成安装后，系统中将会额外增加一个 lv 命令。之所以叫 lv 是因为它是 lambda-view 的首字母缩写，比较容易记忆和使用。使用这个命令，也就是在使用 lambda-view 了。

## ```lv``` 命令能做什么？

### 分析本地文件

如果 JavaScript 文件就在本地磁盘上，那么执行如下命令即可：

```
lv /path/somewhere/xyz.js
```

这一命令执行完成后，将会自动弹出浏览器，把转换后的结果展示出来。

### 分析网络文件

如果 JavaScript 文件位于网络上，那么可以通过提供 URL 的方式打开，例如：

```
lv http://vuejs.org/js/vue.js
```

同样，这一命令执行完成后，将会自动弹出浏览器，把转换后的结果展示出来。

这一例子中的 ```http://vuejs.org/js/vue.js``` 是著名的 Vue 框架下载地址。你可以替换成任何 URL，例如某个网站引用的某个 .js 文件之类的。

***注意***，lambda-view 对于 URL 指向的文件不会做缓存，每次执行都会重新下载。因此这个方法主要用于方便临时性查看，如果你需要反复查看，建议自己手动下载到本地再分析。

（这一功能是 [@Nullizer](https://github.com/Nullizer) 在 [issue#14](https://github.com/Jianru-Lin/lambda-view/issues/14) 中提出的，在此表示感谢）

### [小技巧] 如何一次打开多个文件？

注意：此功能不适用于 Windows 的 cmd 环境。但适用于 Linux 和 Mac。

有时候，你可能会想一次打开某个目录下的所有 .js 文件，那么你可以使用通配符，像下面这样：

```
lv /path/somewhere/*.js
```

你也可以不使用通配符，而具体一一指明：

```
lv /path/somewhere/a.js /path/elsewhere/b.js
```

而且，这一批量打开的逻辑对 URL 也成立，下面的命令就依次打开了 vue.js 的 1.0 版本和 2.0 版本：

```
lv http://v1.vuejs.org/js/vue.js http://vuejs.org/js/vue.js 
```

特别提醒，通配符对 URL 是无效的，下面这个例子***行不通***：

```
lv http://vuejs.org/*.js
```

### [小技巧] 如何免 clone 直接远程打开 github 项目里的文件？

这是一个非常实用的技巧。

有时候看到一个 Github 上一个项目里的某个 .js 文件，想用 lambda-view 查看。其实想看的只是这一个 .js 文件，却不得不 clone 下整个项目，很麻烦。这时候就可以用到这个小技巧了，例如我想用 lv 打开 [https://github.com/nodejs/node/blob/v7.x/lib/http.js](https://github.com/nodejs/node/blob/v7.x/lib/http.js) 这个文件，错误的做法是直接把这个文件页面对应的 URL 作为参数传递给 lv：

```
lv https://github.com/nodejs/node/blob/v7.x/lib/http.js
```

为什么不行呢？因为这个地址对应的实际上不是这个 .js 文件本身，而是一个 HTML 文件，里面间接的包含了这个 .js 文件的内容。因此当 lambda-view 试图把它解析为 JavaScript 语法的时候自然会失败。

那该怎么办呢？其实不用担心，Github 乃良心站点，每个这样的页面里都有一个 "Raw" 按钮，点击这个按钮后将跳转到一个新的 URL，对应的将是原始的文件内容！具体来说，步骤如下：

* 用浏览器打开 [https://github.com/nodejs/node/blob/v7.x/lib/http.js](https://github.com/nodejs/node/blob/v7.x/lib/http.js)
* 在页面中找到 "Raw" 按钮，点击后页面跳转
* 跳转后的页面里你将会看到原始的 .js 文件内容，拷贝下这个页面的 URL
* 使用 lv 打开刚刚拷贝的 URL，就成功了

例如，[https://github.com/nodejs/node/blob/v7.x/lib/http.js](https://github.com/nodejs/node/blob/v7.x/lib/http.js) 对应的原始文件内容 URL 为 [https://raw.githubusercontent.com/nodejs/node/v7.x/lib/http.js](https://raw.githubusercontent.com/nodejs/node/v7.x/lib/http.js) 注意到域名部分的不同了吗？

所以我们执行：

```
lv https://raw.githubusercontent.com/nodejs/node/v7.x/lib/http.js
```

就大功告成了！

## 重要功能提示

### 同名标识符高亮

对于任意标识符，可以点击高亮。这时所有与其同名的标识符也会高亮。这是一个很方便的功能，不要错过。

### 纵横排列切换

数组默认是横排的，但是点击数组中的逗号，可以切换为纵排，当数组很长或者很复杂时，这是一个特别有用的功能！

不仅是数组，以下情形也都支持这样的纵/横排列切换：

* 对象
* 逗号表达式
* 函数参数列表
* ……

## 常见问题解答

### 支持 ECMAScript 2016 吗？

支持，请放心使用。但 async 和 await 目前尚不支持，因为这两个特性属于 ECMAScript 2017。

### 能否支持点击跳转到定义功能？

已在开发计划中，但目前还不支持。

### 能否以 Pakcage 为单位分析，而不只是单个文件？

已在开发计划中，但目前还不支持。

### 接下来打算实现什么功能？

未来最核心的功能是代码评注功能。但在此之前会专注于做好一些最基本的基础功能。

例如针对一个变量，我们希望能够分析出：

- 这个变量在哪里声明的？（作用域绑定问题）
- 哪些地方用到了这个变量？对于没有被使用到，以及使用特别广泛的我们都可以标注出来
- 这个变量是如何被使用的？仅仅是读取吗？是否做了修改，修改时会改变类型吗？
- 能够推断出这个变量的类型的可能范围？

### 代码评注功能指的是什么？

可以对代码中的 module/class/function/statement/expression 等进行评注。因为阅读大型代码，光靠记忆是不行的，还是需要动笔做一些记录。和传统的在博客上写笔记的方式不同，这种评注是和代码绑定在一起的，而且严格的与代码的版本、结构绑定在一起。这和松散的博客笔记相比要更加深入得多。

## 别忘了常常更新

由于这个项目还在频繁改进中，如果遇到了莫名其妙的故障，不妨试试更新到最新版。虽然 npm 自带了 update 操作，但根据实践，最佳的方式是首先卸载：

```
npm uninstall -g lambda-view
```

然后重装：

```
npm install -g lambda-view
```

如果还是解决不了，那么请告知我（建议在[这里](https://github.com/Jianru-Lin/lambda-view/issues)提出，其它人也能看到），我会尽快处理。非常希望得到反馈，真的。

## 主要优势

lambda-view 的主要目标是用于“阅读代码”，它和你使用的 Sublime Text 或者 Atom 等编辑器不同，lambda-view 没有编辑功能。但它具备如下优势：

* 能够将 JavaScript 源代码的“结构”展现出来，而不仅仅是文本
* 表达式计算顺序通过括号原样呈现出来，能够很容易的看清楚而不会弄错
* 基于 Grammar 的高亮，准确无歧义
* 随心所欲的折叠，不仅是函数块，非常方便
* 转换为单个 Html 文件，无外部依赖，可以在移动端（特别是 ipad 上）方便的阅读代码

## ECMAScript 2016 语法支持

截止到 2016-09-15 已经基本完成了对 ECMAScript 2016 语法的支持。可以放心使用。

## 已知缺陷

* 对于万行级别 JavaScript 代码文件，转换后的 Html 页面消耗的资源较高，以 jQuery3 slime 为例，在 Chrome 中大致需要消耗超过 150MB 的内存，而微软的 Edge 浏览器则需要超过 400MB 的内存

性能方面，我在设计时已经有所考虑。实际上有一些办法可以大大降低资源消耗。但我会把这项工作推迟一些。因为虽然内存消耗看起来有些吓人，我在实际使用中并未遇到任何问题（使用 Chrome 浏览器）。

## 历史回顾 & 未来计划

我从 2014 年中开始萌生开发 lambda-view 的想法，目的是解决 JavaScript 代码难以阅读的问题，主要是无处不在的嵌套函数让人眩晕。后来我花了一周多写出了第一个粗糙的原型，在这里可以了解我当时的情况：[阅读大型 JavaScript 源码时有什么好用的工具？](https://www.zhihu.com/question/25490540/answer/30883710)

由于工作繁忙，不久后，我就停止继续开发了。我关闭了服务器上的相关页面，只是在自己的计算机上安装了本地版本继续使用。在这两年时间里，早期那个粗糙的 lambda-view 一次又一次帮助我理解了一些复杂项目内部的工作机制。我对它的价值有了新的认识。所以我又萌生出了继续开发的念头。于是这个项目又复活了。

在新的 lambda-view 中我的计划是实现以下目标：

* 利用复杂的布局更充分的展现出 JavaScript 源代码的结构，跳出传统编辑器思维方式
* 能够针对语句、表达式作注解（如 Markdown 格式），把对代码的理解和疑惑与代码结构直接衔接起来，为持续性的研究提供支持
* 对代码本身进行尽可能多的辅助分析，例如变量作用域分析、变量生命周期分析、（有限程度的）类型推导、结构相似性分析等
* 提供一些常用的代码重写工具，例如规范化重写、常量折叠重写、加密变换重写、解密变换重写等
* 必须对移动端提供良好的支持（我非常喜欢在 ipad 上看资料，包括代码）

希望你会喜欢这个工具，如果遇到什么问题，也欢迎到 issue 中提出。
