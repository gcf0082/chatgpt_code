chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
  if (message.action === "postRequest") {
    chrome.notifications.create({
      type: "basic",
      title: "Post Request Notification",
      message: `A POST request was sent to ${message.url}`,
      iconUrl: "icons/48.png",
    });
  }
});
