chrome.runtime.onInstalled.addListener(function() {

  // 监听请求和响应事件

  chrome.webRequest.onBeforeRequest.addListener(

    function(details) {

      // 将请求信息发送到HTTP服务端

      var xhr = new XMLHttpRequest();

      xhr.open('POST', 'http://example.com/api/requests');

      xhr.setRequestHeader('Content-Type', 'application/json');

      xhr.send(JSON.stringify({

        url: details.url,

        method: details.method,

        headers: details.requestHeaders,

        body: details.requestBody ? details.requestBody.formData : null

      }));

      return {cancel: false};

    },

    {urls: ["<all_urls>"]},

    ["blocking", "requestHeaders", "requestBody"]

  );

  chrome.webRequest.onCompleted.addListener(

    function(details) {

      // 将响应信息发送到HTTP服务端

      var xhr = new XMLHttpRequest();

      xhr.open('POST', 'http://example.com/api/responses');

      xhr.setRequestHeader('Content-Type', 'application/json');

      xhr.send(JSON.stringify({

        url: details.url,

        method: details.method,

        headers: details.responseHeaders,

        body: details.responseBody

      }));

      return {responseHeaders: details.responseHeaders};

    },

    {urls: ["<all_urls>"]},

    ["responseHeaders", "blocking", "requestHeaders"]

  );

});
