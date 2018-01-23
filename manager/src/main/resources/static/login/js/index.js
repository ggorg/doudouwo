var working = false;

$('.login').on('submit', function(e) {

  e.preventDefault();
  if (working) return;
  working = true;
  var $this = $(this),
    $state = $this.find('button > .state');
  $this.addClass('loading');
  $state.html('验证中...');
  setTimeout(function() {
	  
	  $.post("/ajaxLogin",$('.login').serializeArray(),function(data){
		 
		  if(data.retCode==1){
			    $this.addClass('ok');
			    $state.html('登录成功！!');
			    location.href=data.jumpurl;
		  }else{
			  
			  $this.find("button").addClass("redbg");
		      $state.html(data.retMsg);
		      setTimeout(function(){
		    	  $this.removeClass('ok loading');
		    	  $state.html("登　入");
		    	  working = false;
		      },"3000")
		      //
		     //
		  }
	  },"JSON")

  }, 3000);
});