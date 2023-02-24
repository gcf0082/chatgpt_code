let sidebarActive = false;

const toggleSidebarButton = document.getElementById("toggleSidebar");

const sidebar = document.querySelector(".sidebar");

toggleSidebarButton.addEventListener("change", () => {

  sidebarActive = !sidebarActive;

  if (sidebarActive) {

    sidebar.classList.add("active");

  } else {

    sidebar.classList.remove("active");

  }

});

chrome.runtime.sendMessage({ action: "getRequests" }, (requests) => {

  const requestList = document.getElementById("requestList");

  requests.forEach((request) => {

    const li = document.createElement("li");

    const url = document.createTextNode(request.url);

    li.appendChild(url);

    requestList.appendChild(li);

  });

});
