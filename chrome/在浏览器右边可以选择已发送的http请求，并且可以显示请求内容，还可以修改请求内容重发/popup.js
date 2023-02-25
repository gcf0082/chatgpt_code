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
});
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
