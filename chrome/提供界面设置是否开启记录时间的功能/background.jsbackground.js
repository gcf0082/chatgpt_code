chrome.storage.sync.get("recordTime", function(data) {

  if (data.recordTime) {

    chrome.webRequest.onBeforeRequest.addListener(

      function(details) {

        details.startTime = new Date().getTime();

        return {cancel: false};

      },

      {urls: ["<all_urls>"]},

      ["blocking"]

    );

    chrome.webRequest.onCompleted.addListener(

      function(details) {

        var endTime = new Date().getTime();

        var responseTime = endTime - details.startTime;

        var headers = details.responseHeaders;

        headers.push({name: "Response-Time", value: responseTime.toString()});

        return {responseHeaders: headers};

      },

      {urls: ["<all_urls>"]},

      ["responseHeaders", "blocking"]

    );

  }

});
