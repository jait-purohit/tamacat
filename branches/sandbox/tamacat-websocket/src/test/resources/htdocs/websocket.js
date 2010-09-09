var ws;
function $(id){
  return document.getElementById(id);
}

function onOpenWebSocket() {
  $("send").addEventListener("click",sendMessage,false);
  dispMessage("connected");
  //alert("onOpenWebSocket()");
}

function onCloseWebSocket() {
  $("send").removeEventListener("click",sendMessage,false);
  dispMessage("disconnected");
  //alert("onCloseWebSocket()");
}

function onMessageWebSocket(event) {
  var msg = event.data;
  if (msg == "") {
      return;
  }
  dispMessage("> " + msg);
  //alert("onMessageWebSocket");
}

function onUnload() {
  ws.close();
}

function dispMessage(msg) {
  var elem=document.createElement("div");
  elem.appendChild(document.createTextNode(msg));
  if ($("messages").hasChildNodes()) {
    $("messages").insertBefore(elem,$("messages").firstChild);
  } else {
    $("messages").appendChild(elem);
  }
}

function sendMessage() {
  var message = $("message").value;
  if (message == "") {
      return;
  }
  ws.send(message);
  $("message").value = "";
}

function initial() {
  var protocol = (location.protocol == "https:")? "wss" : "ws";
  var host = location.host;
  var url = protocol + "://" + host + "/ws/";
  ws=new WebSocket(url, 'sample');

  ws.addEventListener("open", onOpenWebSocket, false);
  ws.addEventListener("close", onCloseWebSocket, false);
  ws.addEventListener("message", onMessageWebSocket, false);

  window.addEventListener("unload", onUnload, false);
}
window.addEventListener("load", initial, false);
