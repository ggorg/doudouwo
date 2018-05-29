var laypage =null;
var layer =null;
function initPage(count,callBackFun){
    layui.use(['laypage', 'layer'],function(){
        laypage= layui.laypage
        layer=layui.layer;

        laypage.render({
            elem: 'pages',
            skip: true,
            count:count,//总页数,
            theme: '#fb9337',
            layout: ['prev','page','next', 'skip','count'],
            curr: function(){ //通过url获取当前页，也可以同上（pages）方式获取
                var page = location.search.match(/pageNo=(\d+)/);

                return page ? page[1] : 1;
            }(),
            jump: function(e, first){ //触发分页后的回调
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
            layer.msg(data.reMsg);

            if(data.reCode==1){

                window.setTimeout(function(){
                    if(callback!=undefined && isFunction(callback)){
                        callback(data);
                    }else{
                        if(data.data!=null && data.data.jumpUrl!=undefined){
                            top.location.href= data.data.jumpUrl;
                        }else{
                            top.location.href=window.parent.location.href;

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
function openDialog(width,height,title,url){
    layer.open({
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