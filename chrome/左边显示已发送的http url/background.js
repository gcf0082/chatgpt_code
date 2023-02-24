let requests = [];

chrome.webRequest.onCompleted.addListener(
  (details) => {
    requests.push({ url: details.url, timestamp: new Date().getTime() });
  },
  { urls: ["<all_urls>"] }
);

chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
  if (message.action === "getRequests") {
    sendResponse(requests);
  }
});
