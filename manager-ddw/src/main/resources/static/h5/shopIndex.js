var layer;
var o=[];
var t=[];
var flag=true;
var vm_spec_pop;
layui.use(['layer','form'],function(){
    layer=layui.layer;

    requestDataHandle();
})

function requestDataHandle(){


    var indexLoad=layer.load();
    // alert( window.screen.height+","+$("html").height());
    var headH=$(".shop_head_style").height();
    var footH=$(".shop_foot_style").height();
    $(".shop_body_style,.body_left,.body_right").css("height",$("html").height()-headH-footH);
    $.post("/ddwapp/goods/h5shoplist",{storeId:1},function(callBackData){
        handleData(callBackData);

        $("img").on("load",function(){
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
                var lo=layer.open({
                    id:"mydialog",
                    type: 1,
                    area: ["90%","450px"],
                    title:false,
                    closeBtn:false,
                    content:$("#spec_pop") //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']

                });
                $(".layui-layer").css({"border-radius":"30px"})
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
                    layer.close(lo);
                    $("#spec_pop").hide();
                })
                $(".content_option").each(function(){
                    var co=$(this);
                    var code=co.attr("code");
                    var n=getPriceByCookie(code);
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
        handleGcodeNum();
        $("#shop_main").css("visibility","visible");
        layer.close(indexLoad)
    },"JSON")
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
            for(var s=0;s<shopCar.length;s++){
                var t=0;
                if((obj=$("[cacheSpecGcode="+shopCar[s].gcode+"]")).length>0){
                    t=obj.eq(0).text();


                }else if((obj=$("[cacheMidGcode="+shopCar[s].gcode+"]")).length>0){

                    t=obj.text();
                    obj.prev().show()
                }
                obj.show();
                t=t==""?0:parseInt(t);
                obj.text(t+parseInt(shopCar[s].num));
                countNum+=parseInt(shopCar[s].num);
                countPrice+=parseInt(shopCar[s].money);
            }
            $("#car_show_num").show().text(countNum);
            $("#foot_style_price").text("￥"+countPrice/100);
            $("#foot_style_price").attr("money",countPrice);
        }
    }
}
function getPriceByCookie(code){
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
function popAdd(obj){
    var bo=$(".content_option_blue");
    var n=commonMidAdd(JSON.parse(bo.attr("datas")),$(".sure_mid"),bo.attr("title"),bo.attr("gcode"));
    bo.next().show().text(n);
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
    handleCooke(dsObj.code,midObj.text(),money-price);
    return midObj.text()
}
function commonMidAdd(dsObj,midObj,title,gcode){
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
    handleCooke(dsObj.code,midObj.text(),money+price,title+"-"+dsObj.name,gcode);
    carObj.show();
    priceObj.show();
    return midObj.text();

}
function popSubs(obj){
    var bo=$(".content_option_blue");
    var sm=$(".sure_mid");
    if(sm.text()==1){
        $(".foot_sure").removeClass("displayStyle");
        $(".foot_btn").addClass("displayStyle");
        bo.next().hide();
    }
    var n=commonMidSub(JSON.parse(bo.attr("datas")),sm);
    bo.next().text(n);
    handleGcodeNum(bo.attr("gcode"));

}
function optionClickHandle(obj){
    $(".content_option").removeClass("content_option_blue").addClass("content_option_white");
    $(obj).addClass("content_option_blue").removeClass("content_option_white");
    var jsonData=JSON.parse($(obj).attr("datas"));
    var n=getPriceByCookie(jsonData.code);
    if(n>0){
        $(".foot_sure").addClass("displayStyle");
        $(".foot_btn").removeClass("displayStyle").find(".sure_mid").text(n);
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
        layer.msg(data.reMsg);
    }
}
function handleImg(imgObj){
    // var imgs=document.getElementsByTagName("img");
    var img=imgObj;
    var logDivW=$(img.parentNode).width();
    var logDivh=$(img.parentNode).height();
    var realWidth  = img.width;//获取图片实际宽度
    var realHeight  = img.height;//获取图片实际高度
    if(realWidth>logDivW*2){
        realWidth=realWidth/2;
        img.width=realWidth;
        img.height=realHeight;
    }
    if(realHeight>logDivh*2){
        realHeight=realHeight/2;
        img.height=realHeight;
        img.width=realWidth;
    }
    //让img的宽高相当于图片实际宽高的等比缩放，然后再偏移


    if(logDivW>realWidth){
        img.style.width=logDivW+"px";
    }else{
        img.style.marginLeft = '-' + (realWidth-logDivW)/2 + 'px';
    }

    if(logDivh>realHeight){
        img.style.height=logDivh+"px";
    }else{
        img.style.marginTop = '-' + (realHeight-logDivh)/2 + 'px';
    }
    img.style.visibility="visible";



}