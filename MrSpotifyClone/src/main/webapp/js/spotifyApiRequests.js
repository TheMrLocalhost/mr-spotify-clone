function putRequest(url,data) {
	var xhr = new XMLHttpRequest();
    xhr.open("PUT", url, false);
    xhr.setRequestHeader("Authorization", "Bearer "+localStorage.getItem("accessToken"));
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify(data));
}
function getRequest(url) {
    var xhr = new XMLHttpRequest();
    xhr.open("GET", url, false);
    xhr.setRequestHeader("Authorization", "Bearer "+localStorage.getItem("accessToken"));
    xhr.setRequestHeader("Accept", "application/json");
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send();
    return JSON.parse(xhr.responseText);
}