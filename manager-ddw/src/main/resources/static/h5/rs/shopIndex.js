var mylayer;
var o=[];
var t=[];
var flag=true;
var vm_spec_pop;
var vm_car;
var lo;
layui.use(['layer','form'],function(){
    mylayer=layui.layer;


})
window.onload=function(){
    requestDataHandle();

}
function myLoad(){
    return mylayer.load(3, {
        shade: [0.4,'#fff']});
}
function requestDataHandle(){
   if(window.location.search.match(/^[?]param[=]/)!=null){
       $.cookie("shopToken",window.location.search.replace(/(.*param[=])([^&]+)(.*)/g,"$2"),{path:"/"});
   }

    var indexLoad=myLoad();
    // alert( window.screen.height+","+$("html").height());
    var headH=$(".shop_head_style").height();
    var footH=$(".shop_foot_style").height();
    $(".shop_body_style,.body_left,.body_right").css("height",$("html").height()-headH-footH);
    $(".main_right_content").css("height",$("html").height()-$(".main_right_top").height());
    $(".content_body_b").css("width",$("html").width()-$(".content_body_pic").width()-62);
    $.post("/ddwapp/goods/h5shoplist",{storeId:1},function(callBackData){
        handleData(callBackData);

        $("#shop_main img").on("load",function(){
            handleImg(this);
        })
        handleOffset();
        $(".right_end,.right_spec").on("click",function(){
            var ds=JSON.parse($(this).attr("datas"));
            var title=$(this).attr("title");
            var gcode=$(this).attr("gcode");
            var num=0;
            if(ds.length==1){
                var p=$(this).parent();
                var rm=p.find(".right_mid");
                rm.show();
                p.find(".right_start").show();
                commonMidAdd(ds[0],rm,title,gcode);


            }else{
                $(".content_option").addClass("content_option_white").removeClass("content_option_blue");
                $(".displayStyle").removeClass("displayStyle");
                $(".foot_btn").addClass("displayStyle");
                var lo=mylayer.open({
                    id:"mydialog",
                    type: 1,
                    area: ["90%","450px"],
                    title:false,
                    closeBtn:false,
                    shadeClose:true,
                    content:$("#spec_pop") //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']

                });
                mylayer.style(lo, {
                    "border-radius": '30px',
                });

                if(vm_spec_pop==null){
                    vm_spec_pop = new Vue({
                        el:"#spec_pop",
                        data:{list:ds,title:title,gcode:gcode}
                    })
                }else{
                    vm_spec_pop.list=ds;
                    vm_spec_pop.title=title;
                    vm_spec_pop.gcode=gcode;
                }

                $("#spec_pop").show();

                $(".foot_cancel").click(function(){
                    mylayer.close(lo);
                    $("#spec_pop").hide();
                })
                $(".content_option").each(function(){
                    var co=$(this);
                    var code=co.attr("code");
                    var n=getNumByCookie(code);
                    if(n>0){
                        co.next().show().text(n);
                    }else{
                        co.next().hide().text("");
                    }
                })
            }


        })
        $(".right_start").on("click",function(){
            var p=$(this).parent();
            var rm=p.find(".right_mid");
            var re=p.find(".right_end");

            if(rm.text()==1){
                $(this).hide();
                rm.hide();
            }
            commonMidSub(JSON.parse(re.attr("datas"))[0],rm);

        })



        $(".body_left li").click(function(){
            flag=false;
            var leftIdName=this.id;
            var n=leftIdName.match(/[0-9]/g);
            if($(this).hasClass("white_style"))return;
            $(".white_style").removeClass("white_style");
            $(this).addClass("white_style");
            if(n!=null){
                $(".body_right").animate({scrollTop:o["right_item_"+n]+"px"},function(){
                    flag=true;
                })
                //alert($(".body_right").offset().top+","+$("#right_item_"+n).offset().top);
            }

        })
        $(".body_right").scroll(function(){
            if(flag){
                var brTop=$(this).scrollTop();
                window.setTimeout(function(){
                    handleLeftOffset(brTop);

                },10);

            }
        })
        $(".shop_car").click(function(){
            //
            lo=mylayer.open({
                id:"mycarlist",
                type: 1,
                area: "100%",
                title:false,
                closeBtn:false,
                shadeClose:true,
                offset: 'b',
                zIndex:91,
                anim:2,
                content:$("#show_car_list") //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']

            });
            mylayer.style(lo, {
                "border-top-left-radius":"30px",
                "border-top-right-radius":"30px",
                position:"absolute",
                top:"auto",
                bottom:"90px"
            });
            if(vm_car==null){
                vm_car = new Vue({
                    el:"#show_car_list",
                    data:{
                        list:JSON.parse($.cookie("shopCar"))
                    }
                })
            }else{
                vm_car.list=JSON.parse($.cookie("shopCar"));
            }


            $("#show_car_list").show();
            $(".layui-layer-shade").css("z-index","90");
        })
        handleGcodeNum();
        $("#shop_main").hide();
        $("#shop_main").css("visibility","visible");
        $("#shop_main").fadeIn();


        mylayer.close(indexLoad)
    },"JSON")
}
var orderListVm=null;
var pageNo=1;
var requestIng=true;
var orderScrollTop=null;
function requestOrderList(){

    var ml=myLoad();
    $.post("/ddwapp/order/query/h5/list",{pageNo:pageNo},function(cd){
        mylayer.close(ml);
        if(cd.reCode<0){
            showMsg(cd.reMsg);
            return ;
        }
        if(cd.reCode==2){
            showMsg("没有更多数据了");
            return ;
        }
        if(orderListVm==null){
            orderListVm= new Vue({
                el:"#order_list",
                data:cd.data,
                methods: {
                    addItem: function (d) {
                        this.list.push(d)
                    }
                },
                created:function(){

                    toSetOrderItem()
                    pageNo++;

                },
                updated:function(){
                    toSetOrderItem();
                    requestIng=true;
                }
            })
            $(".main_right_content").on(
                " touchend",function(){
                    if($(this).scrollTop()>=($(".right_centent_item").length*290-$(this).height()) ){
                        requestIng=false;
                        requestOrderList();

                    }else if( $(this).scrollTop()<0){
                        requestIng=false;
                        pageNo=1;
                        requestOrderList();
                    }
            })

           /* $(".main_right_content").scroll(function(){
                if(!requestIng){
                    return ;
                }
                if($(this).scrollTop()>=($(".right_centent_item").length*290-$(this).height()) ){
                    requestIng=false;
                    requestOrderList();

                }else if( $(this).scrollTop()<=0){
                    showMsg(($(this).scrollTop()==0) +","+($(this).scrollTop()==""))
                    requestIng=false;
                    pageNo=1;
                    requestOrderList();
                }
            })*/
        }else{

            if(pageNo==1){
                orderListVm.list=cd.data.list;
            }else{
                for(var l=0;l<cd.data.list.length;l++){
                    orderListVm.addItem(cd.data.list[l]);
                }
            }

            pageNo++;



        }





    },"JSON")
}
function toSetOrderItem(){
    /*$(".content_body_pic img").on("load",function(){
        handleImg(this);

    })*/
    $(".right_centent_item").css("visibility","visible");

}
function toOrderList(){
    $("title").text("我的订单");
    $("#order_list").show();
    $("#order_list").animate({
        left:"0px"
    },250,function(){
        $("#shop_main").css("visibility","hidden");
        pageNo=1;
        requestOrderList();
    })

}
function toMain(){
    $("title").text("餐饮shop");
    $("#shop_main").css("visibility","visible");
    $("#order_list").animate({
        left:"1200px"
    },250,function(){
        $("#order_list").hide();
    })
}
function handleCooke(code,num,money,name,gcode){
    var shopCar=$.cookie("shopCar");
    if(shopCar!=null){
        shopCar=JSON.parse(shopCar);
        var flag=true;
        for(var s=0;s<shopCar.length;s++){
            if(shopCar[s]!=null && shopCar[s].code==code){
                flag=false;
                if(num==0){
                   shopCar.splice(s,1);
                    break;
                }else{
                    shopCar[s].num=num;
                    shopCar[s].money=money;

                    break;
                }

            }
        }
        if(flag){
            shopCar[shopCar.length]={
                name:name,
                num:num,
                money:money,
                code:code,
                gcode:gcode
            }
        }
    }else{
        shopCar=[];
        shopCar[0]={
            name:name,
            num:num,
            money:money,
            code:code,
            gcode:gcode
        }
    }
    console.log(JSON.stringify(shopCar));
    $.cookie("shopCar",shopCar.length>0?JSON.stringify(shopCar):null,{path:"/"});
}
function joinShopCar(obj){
    var obs=$(".content_option_blue");
    if(obs!=null && obs.length>0){
        $(".displayStyle").removeClass("displayStyle");
        $(obj).addClass("displayStyle");
        popAdd();
    }
}
function handleGcodeNum(gcode){
    if(gcode!=null && gcode!=undefined){
        var num=getNumByGcode(gcode);
        if(num>0){
            $(".right_spec[gcode="+gcode+"] + .right_spec_num").show().text(num);
        }else{
            $(".right_spec[gcode="+gcode+"] + .right_spec_num").hide().text("");

        }

    }else{
        var shopCar=$.cookie("shopCar");
        var gn=0;

        if(shopCar!=null){
            shopCar=JSON.parse(shopCar);
            var obj=null;
            var countNum=0;
            var countPrice=0;
            var nums=[];
            for(var s=0;s<shopCar.length;s++){
                var t=0;
                if((obj=$("[cacheSpecGcode="+shopCar[s].gcode+"]")).length>0){
                    t=obj.eq(0).text();


                }else if((obj=$("[cacheMidGcode="+shopCar[s].gcode+"]")).length>0){

                    t=obj.text();
                    obj.prev().show()
                }
                var numsValue=nums[shopCar[s].gcode];
                if(numsValue==undefined || numsValue==null){
                    nums[shopCar[s].gcode]=parseInt(shopCar[s].num);
                }else{
                    nums[shopCar[s].gcode]=numsValue+parseInt(shopCar[s].num);
                }
                obj.show();
                t=t==""?0:parseInt(t);
               // console.log(t+","+parseInt(shopCar[s].num));
                obj.text(nums[shopCar[s].gcode]);
                countNum+=parseInt(shopCar[s].num);
                countPrice+=parseInt(shopCar[s].money);
                console.log(shopCar[s].money+",");
            }
            $("#car_show_num").show().text(countNum);
            $("#foot_style_price").attr("money",countPrice).text("￥"+countPrice/100)

        }else{
            $("#car_show_num").hide().text("");
            $("#foot_style_price").attr("money",0).text("");

        }
    }
}
function getNumByCookie(code){
    var shopCar=$.cookie("shopCar");
    if(shopCar!=null){
        shopCar=JSON.parse(shopCar);
        for(var s=0;s<shopCar.length;s++){
            if(shopCar[s].code==code){
                return shopCar[s].num;
            }
        }
    }
    return 0;
}
function clearShopCar(){
    $.cookie("shopCar",null,{path:"/"});
    $(".right_spec_num").hide().text("");
    $(".right_mid").hide().text("");
    $(".right_mid").hide().text("");
    $(".car_show_num").hide().text("");
    $(".foot_style_price").attr("money",0).text("");
    $(".right_start").hide();
    mylayer.close(lo);

}
function getPriceByCookie(code){
    var shopCar=$.cookie("shopCar");
    if(shopCar!=null){
        shopCar=JSON.parse(shopCar);
        for(var s=0;s<shopCar.length;s++){
            if(shopCar[s].code==code){
                return shopCar[s].money;
            }
        }
    }
    return 0;
}
function getUnitPriceByCookie(code){
    var shopCar=$.cookie("shopCar");
    if(shopCar!=null){
        shopCar=JSON.parse(shopCar);
        for(var s=0;s<shopCar.length;s++){
            if(shopCar[s].code==code){
                return shopCar[s].money/shopCar[s].num;
            }
        }
    }
    return 0;
}
function getNumByGcode(gcode){
    var shopCar=$.cookie("shopCar");
    var gn=0;
    if(shopCar!=null){

        shopCar=JSON.parse(shopCar);
        for(var s=0;s<shopCar.length;s++){
            if(shopCar[s].gcode==gcode){
                gn=gn+parseInt(shopCar[s].num);
            }
        }
    }
    return gn;
}
function handleCarVue(obj,str){
    var op=$(obj.parentNode);
    var carCode=op.attr("carCode");
    var carGcode=op.attr("carGcode");
    var carUnitPrice=parseInt(op.attr("carUnitPrice"));
    var vcl=vm_car.list.length;
    for(var v=0;v<vcl;v++){
        if(vm_car.list[v].code==carCode){
            var money=vm_car.list[v].money;
            var num=vm_car.list[v].num;
            if(str=="-" && num==1){
                Vue.delete(vm_car.list,v);
                handleCooke(carCode,0,money-carUnitPrice,null,carGcode);
            }else{
                var m=eval(money+str+carUnitPrice);
                var n=eval(num+str+1);
                Vue.set(vm_car.list[v],"money",m)
                Vue.set(vm_car.list[v],"num",n)
                handleCooke(carCode,n,m,null,carGcode);
            }
            handleGcodeNum();
            return;
        }
    }
}
function carSub(obj){
    var op=$(obj.parentNode);
    var carGcode=op.attr("carGcode");


    var caObj=null;
    if((caObj=$("[cachemidgcode='"+carGcode+"']"))!=null && caObj.length>0){
        caObj.text("");
        caObj.prev().hide();
    }else if((caObj=$("[cacheSpecGcode='"+carGcode+"']"))!=null && caObj.length>0){
        caObj.hide().text("");
    }
    handleCarVue(obj,"-");

}
function carAdd(obj){

    handleCarVue(obj,"+");

}
function popAdd(obj){
    var bo=$(".content_option_blue");
    var nstr=commonMidAdd(JSON.parse(bo.attr("datas")),bo.next(),bo.attr("title"),bo.attr("gcode"),true);
    var ns=nstr.split("-");
    //alert(parseInt(ns[1])*parseInt(ns[0])/100);
    bo.next().show();
    $(".sure_mid").text("￥"+parseInt(ns[1])*parseInt(ns[0])/100);
    handleGcodeNum(bo.attr("gcode"));
}
function commonMidSub(dsObj,midObj){
    var carObj=$("#car_show_num");
    var priceObj=$("#foot_style_price");
    if(carObj.text()==1){
        carObj.hide();
        priceObj.hide();
    }
    var price=dsObj.actPrice!=undefined && dsObj.actPrice!=null?dsObj.actPrice:dsObj.price;

    midObj.text(midObj.text()-1);
    var carText=carObj.text()==""?0:parseInt(carObj.text());
    carObj.text(carText-1);
    var money=parseInt(priceObj.attr("money"));
    priceObj.text("￥"+(money-price)/100);
    priceObj.attr("money",money-price)
    handleCooke(dsObj.code,midObj.text(),parseInt(midObj.text())*price);
    return midObj.text()+"-"+price;
}
function commonMidAdd(dsObj,midObj,title,gcode,flag){
    var carObj=$("#car_show_num");
    var priceObj=$("#foot_style_price");
    var price=dsObj.actPrice!=undefined && dsObj.actPrice!=null?dsObj.actPrice:dsObj.price;
    var sm_text=midObj.text();
    sm_text=sm_text==""?0:parseInt(sm_text);

    midObj.text(sm_text+1);

    var carText=carObj.text()==""?0:parseInt(carObj.text());
    carObj.text(carText+1);
    var money=parseInt(priceObj.attr("money"));
    priceObj.text("￥"+(money+price)/100);
    priceObj.attr("money",money+price);
    handleCooke(dsObj.code,midObj.text(),parseInt(midObj.text())*price,title+"-"+dsObj.name,gcode);
    carObj.show();
    priceObj.show();
    return midObj.text()+"-"+price;

}
function popSubs(obj){
    var bo=$(".content_option_blue");
    var sm=$(".sure_mid");
    var bn=bo.next();
    if(bn.text()==1){
        $(".foot_sure").removeClass("displayStyle");
        $(".foot_btn").addClass("displayStyle");
        bn.hide();
    }
    var nstr=commonMidSub(JSON.parse(bo.attr("datas")),bn);
    var ns=nstr.split("-");
    $(".sure_mid").text("￥"+parseInt(ns[1])*parseInt(ns[0])/100);

    handleGcodeNum(bo.attr("gcode"));

}
function optionClickHandle(obj){
    $(".content_option").removeClass("content_option_blue").addClass("content_option_white");
    $(obj).addClass("content_option_blue").removeClass("content_option_white");
    var jsonData=JSON.parse($(obj).attr("datas"));
    var n=getPriceByCookie(jsonData.code);
    if(n>0){
        $(".foot_sure").addClass("displayStyle");
        $(".foot_btn").removeClass("displayStyle").find(".sure_mid").text("￥"+n/100);
    }else{
        $(".foot_sure").removeClass("displayStyle");
        $(".foot_btn").addClass("displayStyle").find(".sure_mid").text("");
    }
}
function handleLeftOffset(offsetScrollN){
    //alert(offsetScrollN);
    var ris=$(".right_item");

    for(var i=0;i<ris.length;i++){
        var ro=ris.get(i);
        var n=ro.id.match(/[0-9]/g);
        var lo=$("#left_item_"+n);
        var dstart=lo.attr("data-start");
        var dend=lo.attr("data-end");
        if(offsetScrollN>=dstart && offsetScrollN<=dend){
            $(".white_style").removeClass("white_style");
            lo.addClass("white_style");
            return ;
        }
    }
}
function handleOffset(){
    var br=$(".body_right");
    var brTop=br.offset().top;
    $(".right_item").each(function(){
        o[this.id]=$(this).offset().top-brTop;
        var n=this.id.match(/[0-9]/g);
        var scrollTopNum=$(this).offset().top-brTop;
        t[scrollTopNum]="#left_item_"+n;
        $("#left_item_"+n).attr("data-start",scrollTopNum);
        $("#left_item_"+n).attr("data-end",scrollTopNum+$(this).height());
        //  t[$(this).offset().top-brTop+$(this).height()]="#left_item_"+n;

    })
}
function handleData(json){
    var vm = new Vue({
        el:"#shop_main",
        data:json
    })
    if(json.reCode==1){

    }else{
        mylayer.msg(data.reMsg);
    }
}
function handleImg(imgObj){
    // var imgs=document.getElementsByTagName("img");
    var img=imgObj;
    var logDivW=$(img.parentNode).width();
    var logDivh=$(img.parentNode).height();
    var realWidth  = img.width;//获取图片实际宽度
    var realHeight  = img.height;//获取图片实际高度
    console.log(logDivW+","+realWidth)
    if(realWidth>logDivW*2 && realHeight>logDivh*2){
        realWidth=realWidth/2;
        img.style.width=realWidth+"px";
        img.style.height="auto";
        realHeight=$(img).height();

    }else if(realWidth>logDivW*2 && realHeight<logDivh*2){
        realWidth=realWidth/2*logDivh*2/realHeight;
        img.style.width=realWidth+"px";
        img.style.height="auto";
        realHeight=$(img).height();

    }else if(realWidth>logDivW*2 && realHeight<logDivh){
        realWidth=realWidth/2*logDivh/realHeight;
        img.style.width=realWidth+"px";
        img.style.height="auto";
        realHeight=$(img).height();

    }else if(realWidth>logDivW  && realHeight<logDivh){
        //realHeight=realHeight/2;
        img.style.width=$(img).width()*logDivh/realHeight+"px";
        img.style.height="auto";
        realWidth=$(img).width();
    }else if(logDivW>realWidth && logDivh>realHeight){
        realWidth=logDivW*1.2;
        img.style.width=realWidth+"px";
        img.style.height="auto";
        realHeight=$(img).height();
    }else if(logDivW>realWidth && logDivh<realHeight){
        realWidth=realWidth*realHeight/logDivh;
        img.style.width=realWidth+"px";
        img.style.height="auto";
        realHeight=$(img).height();
    }
    //让img的宽高相当于图片实际宽高的等比缩放，然后再偏移



    img.style.marginLeft = '-' + (realWidth-logDivW)/2 + 'px';
    img.style.marginTop = '-' + (realHeight-logDivh)/2 + 'px';
    img.style.visibility="visible";

}
function doPay(){
    var shopCar=$.cookie("shopCar");

    if(shopCar!=null){
        shopCar=JSON.parse(shopCar);
        var arrayObj = new Array();
        for(var a=0;a<shopCar.length;a++){
            for(var j=0;j<shopCar[a].num;j++){
                arrayObj.push(shopCar[a].code);
            }
        }
        if(arrayObj.length>0){
            var requestLoad=myLoad();
                $.ajax({
                    type: "POST",
                    url:"/ddwapp/paycenter/weixin/h5/pay",
                    contentType: "application/json; charset=utf-8",
                    data:JSON.stringify({codes:arrayObj,orderType:1}),
                    dataType: "json",
                    success: function (jsonD, textStatus) {

                        if(jsonD.reCode>0){
                            WeixinJSBridge.invoke(
                                'getBrandWCPayRequest', jsonD.data,
                                function(res){
                                    mylayer.close(requestLoad);
                                    if(res.err_msg == "get_brand_wcpay_request:ok" ){
                                        // 使用以上方式判断前端返回,微信团队郑重提示：
                                        //res.err_msg将在用户支付成功后返回ok，但并不保证它绝对可靠。
                                        showMsg("支付成功");
                                    }else{
                                        showMsg("支付失败");
                                    }
                                });
                        }else{
                            mylayer.close(requestLoad);
                            showMsg(jsonD.reMsg);
                        }

                    },
                    error: function (message) {
                        showMsg("支付失败");
                        mylayer.close(requestLoad);
                    }
                })

        }

    }

}
function showMsg(msg){
    mylayer.msg(msg);

    $(".layui-layer-content").css({"line-height":"45px","font-size":"2.1rem"})
    $(".layui-layer-hui").css("border-radius","15px")
}
function handlePay(){
    if (typeof WeixinJSBridge == "undefined"){
        if( document.addEventListener ){
            document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
        }else if (document.attachEvent){
            document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
            document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
        }
    }
doPay();

}