var laypage =null;
var layer =null;
var f=null;
var pn=null;
var pageN=1;
function initPage(count,callBackFun){
    f=callBackFun;
    layui.use(['laypage', 'layer','form'],function(){
        laypage= layui.laypage;
        layer=layui.layer;
        var form = layui.form;
        laypage.render({
            elem: 'pages',
            skip: true,
            count:count,//总页数,
            theme: '#fb9337',
            layout: ['prev','page','next', 'skip','count'],
            curr: function(){ //通过url获取当前页，也可以同上（pages）方式获取
                var page = location.search.match(/pageNo=(\d+)/);
                page=page==null?pageN:page;
                return page!=null && page!=undefined? page : 1;
            }(),
            jump: function(e, first){ //触发分页后的回调
                pageN=e.curr;
                if(!first){ //一定要加此判断，否则初始时会无限刷新

                    if(callBackFun==undefined){
                        var currentUrl=window.parent.location.href;
                        if(currentUrl.indexOf("?")==-1){
                            top.location.href=currentUrl+"?pageNo="+e.curr;
                            return ;
                        }else{
                            if(currentUrl.indexOf("pageNo")==-1){
                                top.location.href=currentUrl+"&pageNo="+e.curr;
                            }else{
                                top.location.href=currentUrl.replace(/pageNo=[^&]+/,"pageNo="+e.curr);
                            }
                        }
                    }else{
                        callBackFun(e.curr);
                    }


                    //location.href = '?pageNo='+e.curr;
                }
            }
        });

    })
}



function initForm(url,formObject,evenfunction){


    layui.use('form', function(){

        var form= layui.form;
        lf=form;
        if(evenfunction!=undefined){
            evenfunction(form);
        }
        form.verify({
            required:function(value,item){
                var msg=item.getAttribute("placeholder");
                if(value!=null && value.replace(/[ ]/g,"")==""){
                    return msg==null?"请输入必填项":msg;
                }else{
                    return false;
                }
                return msg==null?"请输入必填项":msg;
            }
        })

        $(formObject==undefined?".layui-form":formObject).submit(function(){

            return  commonSubmit(url,formObject);

        })
        //各种基于事件的操作，下面会有进一步介绍
    });
}
function jQueryCommonSubmitCallback(url,formObject,callback){
    jQueryCommonSubmit(url,formObject,null,callback);
}
var j
function jQueryCommonSubmit(url,formObject,reqData,callback){

    j=(isFunction(jQuery)?jQuery:$);
    j.ajax(commonAjaxFunction(url,reqData==undefined?(j(formObject==undefined?".layui-form":formObject).serializeArray()):reqData,null,callback));
    return false;
}
var index =null;
function commonJsonAjaxFunction(url,params,callback){
    j=(isFunction(jQuery)?jQuery:$);
    return {
        timeout: 20 * 1000,
        url: url,
        type: "post",
        data:params,
        dataType: "JSON",
        beforeSend: function(xhr, settings) {
            // xhr.setRequestHeader("If-Modified-Since", "0");

            index= layer.load();
            j(".layui-btn,#btn").attr("disabled",true);

        },
        success: function(data, textStatus, jqXHR) {
            var msgIndex=layer.msg(data.reMsg);

            if(data.reCode==1){

                window.setTimeout(function(){
                    layer.close(msgIndex);
                    if(callback!=undefined && isFunction(callback)){
                        callback(data);
                    }else{
                        if(data.data!=null && data.data.jumpUrl!=undefined){
                            if(window.parent.f!=null && window.parent.f!=undefined){
                                window.parent.closeDialog();
                                window.parent.ajaxPage(1,"search");
                            }else if(f!=null && f!=undefined){

                                ajaxPage(1,"search");
                            }else{
                                top.location.href= data.data.jumpUrl;
                            }

                        }else{
                            if(window.parent.f!=null && window.parent.f!=undefined){
                                window.parent.closeDialog();
                                window.parent.ajaxPage(1,"search");
                            }else if(f!=null && f!=undefined){

                                ajaxPage(1,"search");
                            }else{
                                top.location.href=window.parent.location.href;
                            }



                        }
                    }

                },500)
            }
        },
        error: function(e, xhr, type) {

        },
        complete: function(xhr, status) {
            layer.close(index);
            j(".layui-btn,#btn").removeAttr("disabled");
        }}
}
function commonAjaxFunction(url,params,isUpload,callback){
    j=(isFunction(jQuery)?jQuery:$);
    return {
        timeout: isUpload==undefined || isUpload==null?20 * 1000:120*1000,
        url: url,
        type: "post",
        data:params,
        dataType: "JSON",
        processData: isUpload==undefined || isUpload==null?true:false,
        contentType: isUpload==undefined || isUpload==null?'application/x-www-form-urlencoded; charset=UTF-8':false,
        beforeSend: function(xhr, settings) {
            // xhr.setRequestHeader("If-Modified-Since", "0");

            index= layer.load();
            j(".layui-btn,#btn").attr("disabled",true);

        },
        success: function(data, textStatus, jqXHR) {
            var msgIndex=layer.msg(data.reMsg);

            if(data.reCode==1){

                window.setTimeout(function(){
                    if(callback!=undefined && isFunction(callback)){
                        callback(data);
                    }else{
                        layer.close(msgIndex);
                        if(data.data!=null && data.data.jumpUrl!=undefined){

                            if(window.parent.f!=null && window.parent.f!=undefined){
                                window.parent.closeDialog();
                                window.parent.ajaxPage(1,"search");
                            }else if(f!=null && f!=undefined){

                                ajaxPage(1,"search");
                            }else{
                                top.location.href= data.data.jumpUrl;
                            }
                        }else{

                            if(window.parent.f!=null && window.parent.f!=undefined){
                                window.parent.closeDialog();
                                window.parent.ajaxPage(1,"search");
                            }else if(f!=null && f!=undefined){

                                ajaxPage(1,"search");
                            }else{
                                top.location.href=window.parent.location.href;
                            }

                        }
                    }

                },500)
            }
        },
        error: function(e, xhr, type) {

        },
        complete: function(xhr, status) {

            layer.close(index);
            j(".layui-btn,#btn").removeAttr("disabled");


        }}
}

function commonSubmit(url,formObject,reqData){
    //alert(url+","+$("div").length);


    $.ajax(commonAjaxFunction(url,reqData==undefined?($(formObject==undefined?".layui-form":formObject).serializeArray()):reqData))

    return false;

}
function viewDialog(width,height,title,url){
    layer.open({
        id:"mydialog",
        type: 2,
        area: [width,height],
        title:title,
        content:url //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']

    });
}
function closeDialog(){
    layer.close(openIndex)
}
var openIndex=null;
function openDialog(width,height,title,url){
    openIndex=layer.open({
        id:"mydialog",
        type: 2,
        area: [width,height],
        title:title,
        content:url, //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
        btn:["提交","重置"],
        yes:function(index, layero){
            j=(isFunction(jQuery)?jQuery:$)
            var win=j("#mydialog iframe").get(0).contentWindow
            var subbtn=j(win.document).find("[lay-submit='lay-submit']");
            subbtn.click();
            return false;

        },
        btn2:function(index, layero){
            j=(isFunction(jQuery)?jQuery:$)
            var win=j("#mydialog iframe").get(0).contentWindow
            var subbtn=j(win.document).find("[type='reset']");
            subbtn.click()
            return false;
        }

    });
}
function isFunction(fn) {
    return Object.prototype.toString.call(fn)=== '[object Function]';
}
function ajaxPage(pageNo,search,callFn){
    pageN=pageNo;
    var url=document.URL;
    if(url.indexOf("?")>-1){
        url=url+"&pageNo="+pageNo;
    }else{
        url=url+"?pageNo="+pageNo;
    }
    url+="&isAjax=true";
    j=(isFunction(jQuery)?jQuery:$)
    var pindex= layer.load();
    j.get(url,j(".layui-form").serializeArray(),function(data){
        var ajaxObjTbody=j(data).find("tbody");
        if(ajaxObjTbody==null　|| ajaxObjTbody.length==0){
            document.write(data);
            return ;
        }
        j("tbody").html(ajaxObjTbody.html());
        var attrTotal=ajaxObjTbody.attr("total");
        if(attrTotal!=null && attrTotal!=undefined){
            initPage(attrTotal,function(curr){
                ajaxPage(curr);
            });
        }

        layer.close(pindex);
        /*if(data.reCode==1){
            var result=data.data[pn].result;
            if(result.length>0){
                var htmls="";
                for(var i=0;i<result.length;i++) {
                    htmls+=f(result[i]);
                }
                j("tbody").html(htmls);
                layer.close(pindex);
                if(search!=undefined){

                }

            }
        }*/

    },"HTML")
}
Date.prototype.Format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "H+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}