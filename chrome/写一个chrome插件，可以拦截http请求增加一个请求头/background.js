chrome.webRequest.onBeforeSendHeaders.addListener(

  function(details) {

    details.requestHeaders.push({name: "MyCustomHeader", value: "MyCustomValue"});

    return {requestHeaders: details.requestHeaders};

  },

  {urls: ["<all_urls>"]},

  ["blocking", "requestHeaders"]

);
