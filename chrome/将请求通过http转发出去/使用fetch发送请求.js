chrome.runtime.onInstalled.addListener(function() {
  // 监听请求和响应事件
  chrome.webRequest.onBeforeRequest.addListener(
    async function(details) {
      // 将请求信息发送到HTTP服务端
      const response = await fetch('http://example.com/api/requests', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          url: details.url,
          method: details.method,
          headers: details.requestHeaders,
          body: details.requestBody ? details.requestBody.formData : null
        })
      });

      return {cancel: false};
    },
    {urls: ["<all_urls>"]},
    ["blocking", "requestHeaders", "requestBody"]
  );

  chrome.webRequest.onCompleted.addListener(
    async function(details) {
      // 将响应信息发送到HTTP服务端
      const response = await fetch('http://example.com/api/responses', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          url: details.url,
          method: details.method,
          headers: details.responseHeaders,
          body: details.responseBody
        })
      });

      return {responseHeaders: details.responseHeaders};
    },
    {urls: ["<all_urls>"]},
    ["responseHeaders", "blocking", "requestHeaders"]
  );
});
