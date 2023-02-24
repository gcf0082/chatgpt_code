document.addEventListener("submit", (event) => {
  const form = event.target;
  if (form.method.toLowerCase() === "post") {
    chrome.runtime.sendMessage({
      action: "postRequest",
      url: form.action,
    });
  }
});
