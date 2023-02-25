// 获取“填充密码”按钮
var passwordButton = document.getElementById('password-button');

// 获取“关闭标签页”按钮
var closeButton = document.getElementById('close-button');

// 当用户单击“填充密码”按钮时
passwordButton.addEventListener('click', function() {
  // 查找所有密码输入框
  var inputs = document.querySelectorAll('input[type="password"]');

  // 遍历所有密码输入框
  for (var i = 0; i < inputs.length; i++) {
    // 输入密码
    inputs[i].value = "123";
  }
});

// 当用户单击“关闭标签页”按钮时
closeButton.addEventListener('click', function() {
  // 获取当前标签页
  chrome.tabs.getCurrent(function(tab) {
    // 关闭标签页
    chrome.tabs.remove(tab.id);
  });
});
