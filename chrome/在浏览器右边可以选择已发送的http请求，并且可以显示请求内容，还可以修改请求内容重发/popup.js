// 获取选择框
var requestSelect = document.getElementById('request-select');

// 获取文本框
var requestText = document.getElementById('request-text');

// 获取发送按钮
var sendButton = document.getElementById('send-button');

// 加载所有已发送请求
chrome.webRequest.getAll(function(requests) {
  // 遍历所有请求
  for (var i = 0; i < requests.length; i++) {
    // 创建一个选项
    var option = document.createElement('option');

    // 设置选项的文本
    option.text = requests[i].method + ' ' + requests[i].
// 将选项添加到选择框
requestSelect.add(option);
}

// 选择框变化时更新请求内容

requestSelect.onchange = function() {

// 获取选中的请求

var request = requests[requestSelect.selectedIndex];
  // 获取请求内容

chrome.webRequest.getContent(request.requestId, function(content) {

  // 在文本框中显示请求内容

  requestText.value = content;
  };
});

// 发送按钮点击事件
sendButton.onclick = function() {
// 获取选中的请求
var request = requests[requestSelect.selectedIndex];

// 获取修改后的请求内容
var content = requestText.value;

// 发送请求
var xhr = new XMLHttpRequest();
xhr.open(request.method, request.url, true);

// 复制请求头
for (var i = 0; i < request.requestHeaders.length; i++) {
xhr.setRequestHeader(request.requestHeaders[i].name, request.requestHeaders[i].value);
}

xhr.onreadystatechange = function() {
if (xhr.readyState == 4) {
// 显示响应内容
alert(xhr.responseText);
}
};

xhr.send(content);
};

css
Copy code

这个“popup.js”文件定义了JavaScript代码，用于获取已发送的请求、更新请求内容、发送修改后的请求等。

6. 创建背景脚本

在“my-extension”文件夹中创建一个名为“background.js”的文件，并将以下代码复制粘贴到文件中：

```javascript
chrome.webRequest.onBeforeRequest.addListener(
  function(details) {
    console.log(details.method + ' ' + details.url);
  },
  {urls: ["<all_urls>"]}
);
这个“background.js”文件定义了一个Chrome浏览器的背景脚本，用于拦截所有的HTTP请求，并在控制台中打印请求的方法和URL。

将插件加载到Chrome浏览器中
将“my-extension”文件夹拖放到Chrome浏览器的“扩展程序”页面（地址栏中输入chrome://extensions/即可进入该页面），然后点击“加载已解压的扩展程序”按钮，选择“my-extension”文件夹，即可将插件加载到Chrome浏览器中。

现在，您应该能够在Chrome浏览器的右上角看到一个包含您的插件名称和图标的按钮。单击该按钮将显示一个包含所有已发送的请求的选择框。选择一个请求，将在下面的文本框中显示请求内容。您可以修改请求内容，并单击“发送请求”按钮来发送修改后的请求，并在弹出的对话框中查看响应内容。
，可能需要进一步完善和修改，以满足您的需求。


ChatGPT F

});
