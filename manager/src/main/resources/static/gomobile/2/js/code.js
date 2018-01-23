var $ = jQuery.noConflict();
$(function(){
    $('.form').find('input, select, textarea').on('touchstart mousedown click', function(e){
        e.stopPropagation();
    })
})
var swiperParent = new Swiper('.swiper-parent',{
pagination: '.pagination',
paginationClickable: true,
onSlideChangeEnd : function() {
  //Do something when you touch the slide
  if (swiperParent.activeIndex != 0){
  $('#header').animate({'top':'0px'},400);
  }
  if (swiperParent.activeIndex == 0){
  $('#header').animate({'top':'-100px'},400);
  }  
}
})


var swiperNested4 = new Swiper('.swiper-nested4', {
scrollContainer:true,

mousewheelControl : true,
mode:'vertical',

//Enable Scrollbar
scrollbar: {
  container :'.swiper-scrollbar4',
  hide: true,
  draggable: false  
},
onReachEnd: function(swiper){
    alert();
   }
})

var swiperNestedsingle = new Swiper('.swiper-nestedsingle', {
scrollContainer:true,
mousewheelControl : true,
mode:'vertical',
//Enable Scrollbar
scrollbar: {
  container :'.swiper-scrollbarsingle',
  hide: true,
  draggable: false  
}
})

$('.scrolltop4').click(function(){
 swiperNested4.swipeTo(0);
});

$('.scrolltopsingle').click(function(){
 swiperNestedsingle.swipeTo(0);
});
$('.gohome').click(function(){
 swiperParent.swipeTo(0);
});
jQuery(function($) {
$(".swipebox").swipebox();
});
$(function() {

$(".toggle_container").hide();
$(".toggle_container_blog").hide();
$(".trigger").click(function(){
	$(this).toggleClass("active").next().slideToggle("slow",function(){
		if(swiperNested4!=undefined)swiperNested4.reInit();
	});
	
	return false;
});
$(".trigger_blog").click(function(){
	$(this).toggleClass("activeb").next().slideToggle("slow");
	return false;
});
$(".post_more").click(function(){

	$(this).toggleClass("activep").next().slideToggle("slow");
	return false;
});
});