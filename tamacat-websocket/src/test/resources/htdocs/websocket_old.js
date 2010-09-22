var ws;
var onOpenWebSocket = function() {
  jQuery("#send").bind("click", sendMessage);
  var user = jQuery("#user").val();
  //dispMessage("connected [" + user + "]");
  ws.send("connected [" + user + "]");
};

var onCloseWebSocket = function() {
  var user = jQuery("#user").val();
  //displayMessage("disconnected [" + user + "]");
  ws.send("disconnected [" + user + "]");
  ws.close();
  jQuery("#send").unbind("click", sendMessage);
};

var onMessageWebSocket = function(event) {
  var msg = event.data;
  if (msg != "") {
    dispMessage(msg);
  }
};

var onUnload = function() {
  ws.close();
};

var dispMessage = function(msg) {
  jQuery("#messages").append(msg + "<br />");
};

var sendMessage = function() {
  var user = jQuery("#user").val();
  var message = jQuery("#message").val();
  if (message != "") {
    ws.send(user + "> " + message);
    jQuery("#message").val("");
  }
};

var initial = function() {
  var protocol = "ws";
  if (location.protocol == "https:") {protocol = "wss";}
  var url = protocol + "://" + location.host + "/";
  ws = new WebSocket(url, "sample");
  //ws.addEventListener("open", onOpenWebSocket, false);
  ws.addEventListener("close", onCloseWebSocket, false);
  ws.addEventListener("message", onMessageWebSocket, false);
  window.addEventListener("unload", onUnload, false);
};

jQuery(document).ready(function() {
  window.addEventListener("load", initial, false);
  jQuery("#switch").toggle(
    function() {
      jQuery("#switch").val("disconnect");
      onOpenWebSocket();
    },
    function() {
      jQuery("#switch").val("connect");
      onCloseWebSocket();
    }
  );
});