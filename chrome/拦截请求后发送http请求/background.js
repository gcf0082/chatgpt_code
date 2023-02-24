chrome.webRequest.onBeforeSendHeaders.addListener(
  function(details) {
    details.requestHeaders.push({name: "MyCustomHeader", value: "MyCustomValue"});

    // 发送请求到其他Web服务
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "http://localhost:3000/");
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xhr.send(JSON.stringify(details));

    return {requestHeaders: details.requestHeaders};
  },
  {urls: ["<all_urls>"]},
  ["blocking", "requestHeaders"]
);
