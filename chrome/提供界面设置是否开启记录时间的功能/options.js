var recordTimeCheckbox = document.getElementById("record-time");

recordTimeCheckbox.addEventListener("change", function() {
  var isEnabled = recordTimeCheckbox.checked;
  chrome.storage.sync.set({recordTime: isEnabled});
});

chrome.storage.sync.get("recordTime", function(data) {
  recordTimeCheckbox.checked = data.recordTime;
});
