
var wsoj = jQuery.ws.conn({
  url : "ws://localhost/ws/",
  onopen : function(e){
    jQuery("#messages").prepend("conected");
  },
  onmessage : function(msg, wsoj){
    if (msg != "") {
        jQuery("#messages").append(msg + "<br />");
    }
  },
  onclose : function(e){
    jQuery("#messages").prepend("closed");
  }
});

jQuery("#send").click(function(){
  var user = jQuery("#user").val();
  var message = jQuery("#message").val();
  if (message != "") {
    jQuery(wsoj).wssend(user + "> " + message);
  }
});

jQuery("#switch").toggle(
    function() {
      jQuery("#switch").val("disconnect");
      wsoj.onopen();
    },
    function() {
      jQuery("#switch").val("connect");
      wsoj.onclose();
    }
);