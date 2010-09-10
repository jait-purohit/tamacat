var ws;
var onOpenWebSocket = function() {
  jQuery("#send").bind("click", sendMessage);
  dispMessage("connected");
};

var onCloseWebSocket = function() {
  jQuery("#send").unbind("click", sendMessage);
  dispMessage("disconnected");
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
  var user = jQuery("#user").val();
  jQuery("#messages").append(user + "> " + msg + "<br />");
};

var sendMessage = function() {
  var message = jQuery("#message").val();
  if (message != "") {
    ws.send(message);
    jQuery("#message").val("");
  }
};

var initial = function() {
  var protocol = "ws";
  if (location.protocol == "https:") {protocol = "wss";}
  var url = protocol + "://" + location.host + "/ws/";
  ws  =new WebSocket(url, 'sample');
  ws.addEventListener("open", onOpenWebSocket, false);
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