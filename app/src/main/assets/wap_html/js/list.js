/*
tableid:绑定列表tableId，外层css为ui-refresh的div id为div_tableId
url:请求路径
params：json格式参数 {xxx:xxxxx}
*/  	
function sys_renderList(tableid,url,params,_refresh){
	var refreshDown = $("#div_"+tableid).find(".ui-refresh-down");
	if(refreshDown.length>0){
		refreshDown.hide();
	}
	if(!tableid){
		alert("请设置tableid");
		return false;
	}
	if(!url){
		alert("请设置url");
		return false;
	}
	var refreshDIV = "div_"+tableid;
	var height = getListHeight();
	/*	  
	  var toolbarBlank = $(".toolbar-blank");
	  if(toolbarBlank.length>0){
	  	height = toolbarBlank.eq(0).height();
	  }
	  var tabsBlank = $(".div_tabs_blank");
	  if(tabsBlank.length>0){
	  	height = height+tabsBlank.eq(0).height();
	  }
	  var bottomMenuBlank = $(".div_bottomMenu_blank");
	  if(bottomMenuBlank.length>0){
	  	height = height+bottomMenuBlank.eq(0).height();
	  }
	  */
	var removeStyle = (typeof(_refresh.removeStyle)!='undefined')?_refresh.removeStyle:true;
    $('#'+refreshDIV).css('height',window.innerHeight-parseInt(height)).refresh({
		  load:function(dir,type){
               var me = this;
               var page_goto = sys_getPageNum(tableid,dir);
               if(!page_goto){
               		me.afterDataLoading(dir);  
               		me.disable('down',false);
               		return ;
               }
               var URLParams = {
               		url:url+page_goto,
               		dir:dir
               }
               sys_ajaxGet(URLParams,params?params:"",function(json){
               		bind(json,{'dir':dir,'removeStyle':removeStyle});
					_refresh[tableid] = me._options.iScroll;
               	 	if(!sys_hideLoadMore[tableid]){	
	               	 	if(refreshDown.length>0){
							refreshDown.show();
						}
					}
			        me.afterDataLoading(dir);  
               });
           },
           ready:function(){
           		this.trigger("load");
           },
           statechange:function(event,elem,state,dir){
	          
          }
      });
      
}
  	
  
  	