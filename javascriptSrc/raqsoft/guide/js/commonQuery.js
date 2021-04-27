
/*
[
	{
		name : "" //把字段分组管理
		fields : [
			{
				name : '' //字段名
				,title : '' //中文名
				,dataType : 3 //1整数  2实数  3字符串  4日期  5时间  6日期时间， 默认字符串
				,editType : 0 //0自动  1复选开关  2日历控件  3下拉框   4下拉树， 默认0按照数据类型自动适配编辑类型
				//日历
				,dateFormat : ""
				,timeFormat : ""
				//下拉列表&下拉树
				,referData : [{id:1, pId:0, name:'北京',v:'1'}] //维值数据，只有两个时用三状态切换按钮，否则下拉树
				,multi : 2 //1多选  2单选  默认2
				,onlyLeaf : 1 //1只取叶子的值  2取所有选中的节点
			}
		]
	}
]


[
	{
		name : "group1" 
		fields : [ 
			{ 
				name : "field1" 
				,title : "中文字段名" 
				,dataType : 3 
				,dateFormat : "yy-MM-dd" 
				,timeFormat : "HH:mm:ss" 
				,referData : [
					{id:1, pId:0, name:'北京',v:'1'}
				] 
				,multi : 2 
			}
		]
	}
]



[
	{
		"name":"数值字段"
		,"fields":[
			{"name":"EMPID","dataType":1,"title":"员工编号","referData":null,"multi":1}
			,{"name":"DEPT","dataType":1,"title":"部门","referData":null,"multi":1}
			,{"name":"GENDER","dataType":1,"title":"性别","referData":[{"v":1,"d":"男"},{"v":2,"d":"女"}],"multi":1}
			,{"name":"DEGREE","dataType":1,"title":"学历","referData":[{"v":1,"d":"小学"},{"v":2,"d":"初中"},{"v":3,"d":"高中"},{"v":4,"d":"大学本科"},{"v":5,"d":"硕士研究生"}],"multi":1}
			,{"name":"NATIVE","dataType":1,"title":"户籍城市"
				,"referData":[
					{"id":1,"pId":0,"name":"北京","v":"1"},{"id":2,"pId":0,"name":"天津","v":"2"},{"id":3,"pId":0,"name":"上海","v":"3"},{"id":6,"pId":0,"name":"重庆","v":"4"},{"id":4,"pId":0,"name":"河北省","v":"5"},{"id":41,"pId":4,"name":"石家庄","v":"6"},{"id":42,"pId":4,"name":"保定","v":"7"},{"id":43,"pId":4,"name":"邯郸","v":"8"},{"id":44,"pId":4,"name":"承德","v":"9"},{"id":5,"pId":0,"name":"广东省","v":"10"},{"id":51,"pId":5,"name":"广州","v":"11"},{"id":52,"pId":5,"name":"深圳","v":"12"},{"id":53,"pId":5,"name":"东莞","v":"13"},{"id":54,"pId":5,"name":"佛山","v":"14"},{"id":6,"pId":0,"name":"福建省","v":"15"},{"id":61,"pId":6,"name":"福州","v":"16"},{"id":62,"pId":6,"name":"厦门","v":"17"},{"id":63,"pId":6,"name":"泉州","v":"18"},{"id":64,"pId":6,"name":"三明","v":"19"}
				],"multi":1
			}
		]
	}
	,{
		"name":"日期字段"
		,"fields":[
			{"name":"BIRTHDAY","dataType":4,"title":"生日","referData":null,"multi":1}
		]
	}
]


[
	{
		name : "数值字段" 
		fields : [...]
	}
	,
	{
		name : "字符字段" 
		fields : [...]
	}
	,
	{
		name : "日期字段" 
		fields : [...]
	}
]

:"GENDER",d:"性别"},{v:"EMPID",d:"员工编号"},{v:"DEPT",d:"部门"},{v:"NATIVE",d:"户籍城市"},{v:"DEGREE",d:"学历"}

[
	{
		name : "group1" 
		fields : [ 
			{ 
				name : "GENDER" 
				,title : "性别" 
				...
			}
			,{ 
				name : "EMPID" 
				,title : "员工编号" 
				...
			}
			,{ 
				name : "DEPT" 
				,title : "部门" 
				...
			}
			,{ 
				name : "NATIVE" 
				,title : "户籍城市" 
				...
			}
			,{ 
				name : "DEGREE" 
				,title : "学历" 
				...
			}
		]
	}
]


[
	{
		name : "group1" 
		fields : [ 
			{ 
				name : "GENDER" 
				,title : "性别" 
				,dataType : 1
				,referData : [
					{name:'男',v:1}
					,{name:'女',v:2}
				] 
			}
		]
	}
]


[
	{
		name : "group1" 
		fields : [ 
			{ 
				name : "DEGREE" 
				,title : "学历" 
				,dataType : 1
				,referData : [
					{name:'幼儿园',v:0}
					,{name:'小学',v:1}
					,{name:'初中',v:2}
					,{name:'高中',v:3}
					,{name:'大学本科',v:4}
					,{name:'硕士研究生',v:5}
					,{name:'博士研究生',v:6}
				] 
				,multi : 2
			}
		]
	}
]

[
	{
		name : "group1" 
		fields : [ 
			{ 
				name : "DEGREE" 
				,title : "学历" 
				,dataType : 1
				,referData : [
					{id:1, pId:0, name:'北京',v:'1'},
					{id:2, pId:0, name:'天津',v:'2'},
					{id:3, pId:0, name:'上海',v:'3'},
					{id:6, pId:0, name:'重庆',v:'4'},
					{id:4, pId:0, name:'河北省',v:'5'},
					{id:41, pId:4, name:'石家庄',v:'6'},
					{id:42, pId:4, name:'保定',v:'7'},
					{id:43, pId:4, name:'邯郸',v:'8'},
					{id:44, pId:4, name:'承德',v:'9'},
					{id:5, pId:0, name:'广东省',v:'10'},
					{id:51, pId:5, name:'广州',v:'11'},
					{id:52, pId:5, name:'深圳',v:'12'},
					{id:53, pId:5, name:'东莞',v:'13'},
					...
				] 
				,multi : 2
			}
		]
	}
]






{
	fields : [
		{
			name:"field1"
			,alias:"销售额合计"
			,dataType:1
			,comparison : ">="
			,value : [50000]
			,aggr : "sum"
		}
	]
	,currGroup : 'group1'
}




{
	fields : [
		{
			name:""
			,alias:"" //空表示不显示出来
			,dataType:1 //1数值  2字符串  3日期，格式yyyy-MM-dd
			,comparison : "" //=、>、<、like、区间
			,value : [] //一些比较符支持多个值，有aggr时，条件为having，否则为where
			,aggr : ""//sum,max,min,avg,count,countd去重计数,stddev标准差 有聚合字段存在，非聚合字段就是分组；无聚合字段时查明细；	
			
			,title : ''
		}
	]
	,currGroup : ''
}
*/

var md = null;
var rst = 
{
	fields : [
		/*
		{
			name:""
			,alias:"" //空表示不显示出来
			,dataType:1 //1整数  2实数  3字符串  4日期  5时间  6日期时间，格式yyyy-MM-dd
			,comparison : "" //=、>、<、like、区间
			,value : [] //一些比较符支持多个值，有aggr时，条件为having，否则为where
			,aggr : ""//sum,max,min,avg,count,countd 有聚合字段存在，非聚合字段就是分组；无聚合字段时查明细；	
		}
		*/
	]
}
var mdConf = {
	fields : [
		/*
		{
			name : ""
			,alias:""
			,dataType:""
			,comparison:''
			,value:''
			,aggr : ""

			,show:1 //1是 2否
			,title : ''
		}
		*/
		
	]
	,currGroup : ''
}

function getCommonQueryJSON() {
	var rst = JSON.parse(JSON.stringify(mdConf));
	for (var i=0; i<rst.fields.length; i++) {
		var fi = rst.fields[i];
		if (fi.show==2) fi.alias='';
		else fi.alias=fi.title;
		var fiv = fi.values;
		for (var j=0; j<fiv.length; j++) {
			if (typeof(fiv[j]) != 'String') fiv[j] = fiv[j]+"";
		}
		
		if (fiv.length == 1) {
			fi.values = fiv[0].replaceAll("，",",").split(",")
		}
		if (fi.values.length == 1) {
			if(fi.values[0]=="") fi.values = [];
		}
		
		var srcFObj = getArrayItem(md.allFields,"name",fi.name);
		//alert(srcFObj.dataType);
		if (srcFObj.dataType>=4) {
			fiv = fi.values;
			//if (v2(srcFObj.dateFormat)=="" && v2(srcFObj.timeFormat)=="") srcFObj.dateFormat = "yy-mm-dd";

			//alert(srcFObj.dateFormat);
			for (var j=0; j<fiv.length; j++) {
				var dj = $.datepicker.parseDate(srcFObj.dateFormat, fiv[j]);
				fiv[j] = dj.getTime()+"";
			}
		}
	}
	return JSON.stringify(rst);
}

function getParams() {
	return commQuery.params;
}

function setQueryFunc(func) {
	$("#queryBut").unbind("click").click(function(){
		eval(func);
	});
}

$(document).ready(function(){
	
	if (v2(commQuery.metadata)=='') {
		alert('元数据为空');
	}
	try {
		md = eval(commQuery.metadata);
	} catch(e) {
		alert(e);
	}
	md.allFields = [];
	for (var i=0; i<md.length; i++) {
		md.allFields = md.allFields.concat(md[i].fields);
		for (var j=0; j<md[i].fields.length; j++) {
			var fj = md[i].fields[j];
			if (v1(fj.dataType) == null) fj.dataType = 3;
			if (v2(fj.title) == '') fj.title = fj.name;
			if (v1(fj.referData) != null && typeof(fj.referData)=='string') fj.referData = eval(fj.referData);
			if (v1(fj.referValue) != null && typeof(fj.referValue)=='string') fj.referValue = eval(fj.referValue);
			if (v1(fj.referDisplay) != null && typeof(fj.referDisplay)=='string') fj.referDisplay = eval(fj.referDisplay);
		}
	}
	
	if (md.length>1) {
		var gs = $('<select id="groupSel" style="width:100%;"><option value="_all">全部</option></select>');
		$('#groups').append(gs);
		for (var i=0; i<md.length; i++) {
			gs.append("<option value="+md[i].name+">"+md[i].name+"</option>");
		}
		gs.change(function(){
			mdConf.currGroup = $(this).val();
			showFields($(this).val());
		});
		$('#fields').css('height','calc(100% - 27px)');
		//fieldsHeight = 30;
	} else $('#fields').css('height','calc(100% - 52px)');
	//alert(fieldsHeight);
	showFields('_all');
	
	$( "#tableDiv").droppable({
  	accept: "div[f]",
    //activeClass: "ui-state-hover",
    //hoverClass: "ui-state-active",
    drop: function( event, ui ) {
  		var f = ui.draggable.attr('f');
  		var title = ui.draggable.html();
  		for (var i=0; i<mdConf.fields.length; i++){
  			if (mdConf.fields[i].title == title) title = "_"+title;
  		}
  		mdConf.fields.push({
				name : f
				,alias:''
				,title:title
				,comparison:'='
				,values:[]
				,show:1 //1是 2否
				,aggr : ""
			});
  		refreshFields();
    }

  });
  
  $("#queryBut").click(function(){
  	console.log(getCommonQueryJSON());
  	parent.frames["commonReportFrame"].commonQuerySubmit();
  })
  
  if (commQuery.cqx!='') {
  	commQuery.cqx = commQuery.cqx.replaceAll("'",'"');
 		mdConf = JSON.parse(commQuery.cqx);
  }
	refreshFields();
	if (v2(mdConf.currGroup)=='') mdConf.currGroup = "_all";
	$('#groupSel').val(mdConf.currGroup);

});

function refreshFields() {
	var ft = $('#fieldTable');
	if (ft.length == 0) {
		ft = $('<table class="sortable" id="fieldTable" border=0 style="border:0;border-collapse:collapse;border:0px;margin:0px 0 10px 10px;float:left;" cellspacing=0 cellpadding=0></table>');
		$('#tableDiv').prepend(ft);

    $(".sortable").sortable({
        cursor: "move",
        items: "tr", //只是tr可以拖动 
        opacity: 0.6, //拖动时，透明度为0.6
        revert: true, //释放时，增加动画
        update: function(event, ui) { //更新排序之后
					var trs = $(".sortable").find('tr');
					var fObjs = [];
					for (var i=0; i<trs.length; i++) {
						fObjs[i] = getArrayItem(mdConf.fields,"title",$(trs[i]).attr("titl"));
					}
					mdConf.fields = fObjs;
        }
    });
	}
	var trs = ft.find('tr');
	//for (var i=trs.length-1; i>=0; i--) {
	//	trs[i].remove();
	//}
	//trs = ft.find('tr');
	var existTitle = [];
	for (var i=trs.length-1; i>=0; i--) {
		var tr = null;
		for (var j=0; j<mdConf.fields.length; j++) {
			if (mdConf.fields[j].title == $(trs[i]).attr('titl')) {
				tr = trs[i];
			}
		}
		if (tr == null) $(trs[i]).remove();
	}

	var addRow = function(rowTitle) {
		var fObj = getArrayItem(mdConf.fields,"title",rowTitle);
		var srcFObj = getArrayItem(md.allFields,"name",fObj.name);
		if (srcFObj == null) {
			alert("未找到字段 : " + fObj.name);
		}
		fObj.dataType = srcFObj.dataType;

		var tr = $(`<tr titl="${fObj.title}">
			<td style="width:30px;text-align:center;"></td>
			<td style="width:30px;text-align:center;"></td>
			<td style="width:40px;text-align:center;"></td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
			</tr>`);
		ft.append(tr);
		var tds = tr.find('td');
		var sp1 = $('<span>'+(fObj.show==1?"显示":"不显示")+'</span>');
		$(tds[2]).append(sp1);
		sp1.click(function(){
			var a = $(this).parent().parent().attr('titl');
			var fObj1 = getArrayItem(mdConf.fields,"title",a);
			fObj1.show = fObj.show==1?2:1;
			sp1.html(fObj1.show==1?"显示":"不显示");
		});
		var input1 = $('<input type="text" style="width:100px;">');
		$(tds[3]).append(input1);
		input1.change(function(){
			var newv = $.trim($(this).val());
			var oldv = $(this).parent().parent().attr('titl');
			var fObj1 = getArrayItem(mdConf.fields,"title",newv);
			if (newv==''||(fObj1!=null && fObj1!=fObj)) $(this).val(oldv);
			else {
				//var fObj = getArrayItem(mdConf.fields,"alias",oldv);
				fObj.title=newv;
				$(this).parent().parent().attr('titl',newv);
			}
		});

		/*
			,openValue : ""
			,closeValue : ""
			//日历
			,dateFormat : ""
			,timeFormat : ""
			//下拉列表&下拉树
			,referData : [{f1,f2...}] //下拉框和下拉树的数据
			,referValue : [f1] //真实值字段，下拉树时，分别代表1...n层
			,referDisplay : [f2]//显示值字段，下拉树时，分别代表1...n层
		*/
		//1整数  2实数  3字符串  4日期  5时间  6日期时间， 默认字符串
		var sp2 = null;
		if (srcFObj.dataType==1||srcFObj.dataType==2||fObj.aggr=='count'||fObj.aggr=='countd'||fObj.aggr=='avg'||fObj.aggr=='stddev') {
			sp2 = $('<select id="oper" style="float:left;"><option value="=">等于</option><option value=">=">大于等于</option><option value=">">大于</option><option value="<=">小于等于</option><option value="<">小于</option></select>');
			$(tds[5]).append(sp2).append('<span id="valueArea"></span>');
		} else if (srcFObj.dataType==3) {
			sp2 = $('<select id="oper" style="float:left;"><option value="=">等于</option><option value="like">包含</option><option value="unlike">不包含</option></select>');
			$(tds[5]).append(sp2).append('<span id="valueArea"></span>');
		} else {
			sp2 = $('<select id="oper" style="float:left;"><option value="=">等于</option><option value=">=">迟于等于</option><option value=">">迟于</option><option value="<=">早于等于</option><option value="<">早于</option></select>');
			$(tds[5]).append(sp2).append('<span id="valueArea"></span>');
		}
		sp2.css('margin-top','1px');
		var operChange = function(title){
			var tdss = ft.find('tr[titl="'+title+'"]').find('td');
			var fObj = getArrayItem(mdConf.fields,"title",title);
			var srcFObj = getArrayItem(md.allFields,"name",fObj.name);
			var sd = srcFObj.referData;
			//sd = [{id:1, pId:0, name:'北京',v:'1'},{id:2, pId:0, name:'天津',v:'2'},{id:3, pId:0, name:'上海',v:'3'},{id:6, pId:0, name:'重庆',v:'4'},{id:4, pId:0, name:'河北省',v:'5'},{id:41, pId:4, name:'石家庄',v:'6'},{id:42, pId:4, name:'保定',v:'7'},{id:43, pId:4, name:'邯郸',v:'8'},{id:44, pId:4, name:'承德',v:'9'},{id:5, pId:0, name:'广东省',v:'10'},{id:51, pId:5, name:'广州',v:'11'},{id:52, pId:5, name:'深圳',v:'12'},{id:53, pId:5, name:'东莞',v:'13'},{id:54, pId:5, name:'佛山',v:'14'},{id:6, pId:0, name:'福建省',v:'15'},{id:61, pId:6, name:'福州',v:'16'},{id:62, pId:6, name:'厦门',v:'17'},{id:63, pId:6, name:'泉州',v:'18'},{id:64, pId:6, name:'三明',v:'19'}]
			if (sd!=null) {
				for (var i=0; i<sd.length; i++) {
					if (sd[i].v==0) sd[i].v = "0";
					if (sd[i].d) sd[i].name=sd[i].d;	
					if (v2(sd[i].id)=='') sd[i].id=i+1;
					if (v2(sd[i].pId)=='') sd[i].pId=0;
				}
			}
			var oper = $(tdss[5]).find('#oper').val();
			fObj.comparison = oper;
			
			$(tdss[5]).find('#valueArea').html('');
			//if ($(tds[2]).find('#value1').length>0) return;
			
			if (sd!=null && sd.length==2) {
				var value1 = $('<select id="value1"><option value="">全部</option><option value="'+srcFObj.openValue+'">开</option><option value="'+srcFObj.closeValue+'">关</option></select>');
				var but = $('<input type="button">');
				var value1 = $('<input type="hidden" id="value1">');
				$(tdss[5]).find('#valueArea').append(but).append(value1);
				var v = fObj.values.length==0?"":fObj.values[0];
				value1.val(v);
				
				but.val(v===sd[0].v?sd[0].name:(v==sd[1].v?sd[1].name:"全部"));
				but.click(function(){
					if (value1.val()=='') {
						value1.val(sd[0].v);
						but.val(sd[0].name);
						fObj.values = [sd[0].v];
					} else if (value1.val()==sd[0].v) {
						value1.val(sd[1].v);
						but.val(sd[1].name);
						fObj.values = [sd[1].v];
					} else {
						value1.val("");
						but.val("全部");
						fObj.values = [""];
					}
				});
				
			} else if (sd!=null && sd.length<6) {
				
				var options5 = $('<div id="options5"></div>');
				$(tdss[5]).find('#valueArea').html('').css('float','left').append(options5);
				for (var i=0; i<sd.length; i++) {
					options5.append('<input v="'+sd[i].v+'" style="height:24px;vertical-align: 5px;" sel="1" type="button" value="'+sd[i].name+'">');
				}
				options5.find('input').click(function(){
					var vv = $(this).attr('v');
					if (v1(srcFObj.multi) == 1) {
						if (fObj.values.indexOf(vv)>=0) fObj.values.remove(vv);
						else fObj.values.push(vv);
					} else {
						if (fObj.values.indexOf(vv)>=0) fObj.values = [];
						else fObj.values = [vv];
					}
					initV();
				});
				
				var initV = function(){
					
					options5.find('input').each(function(){
						var vv = $(this).attr('v');
						if (fObj.values.indexOf(vv)>=0) $(this).attr('sel', 1).css('background-color','#888888');
						else $(this).attr('sel', 2).css('background-color','');
					});
				}
				
				initV();
				
							
			} else if (sd!=null && sd.length>0) {
				
				var datas = JSON.parse(JSON.stringify(sd));
				 
				 //[{id:1, pId:0, name:'北京',v:'1'},{id:2, pId:0, name:'天津',v:'2'},{id:3, pId:0, name:'上海',v:'3'},{id:6, pId:0, name:'重庆',v:'4'},{id:4, pId:0, name:'河北省',v:'5'},{id:41, pId:4, name:'石家庄',v:'6'},{id:42, pId:4, name:'保定',v:'7'},{id:43, pId:4, name:'邯郸',v:'8'},{id:44, pId:4, name:'承德',v:'9'},{id:5, pId:0, name:'广东省',v:'10'},{id:51, pId:5, name:'广州',v:'11'},{id:52, pId:5, name:'深圳',v:'12'},{id:53, pId:5, name:'东莞',v:'13'},{id:54, pId:5, name:'佛山',v:'14'},{id:6, pId:0, name:'福建省',v:'15'},{id:61, pId:6, name:'福州',v:'16'},{id:62, pId:6, name:'厦门',v:'17'},{id:63, pId:6, name:'泉州',v:'18'},{id:64, pId:6, name:'三明',v:'19'}]

				 
				for (var i=0; i<datas.length; i++) {
					datas[i].open = true;
				}
				 
				var disps = "";
				for (var i=0; i<fObj.values.length; i++) {
					var itemi = getArrayItem(datas,"v",fObj.values[i]);
					if (itemi!=null) {
						itemi.checked = true;
						disps += ","+itemi.name;
					}
				}
				if (disps.length>0) disps = disps.substring(1);
				
				var value1 = $('<input type="text" id="value1">');
				$(tdss[5]).find('#valueArea').append(value1);
				value1.val(disps);
				dropdownTree(value1, datas, fObj.title);
						


			} else if (srcFObj.dataType>3) {
				if (v2(srcFObj.dateFormat)=="" && v2(srcFObj.timeFormat)=="") srcFObj.dateFormat = "yy-mm-dd";
				var value2 = $('<input type="text">');
				value2.val(fObj.values[0]);
				//alert(title);
				$(tdss[5]).find('#valueArea').append(value2);
				var fmt = srcFObj.dateFormat;//'yy年mm月dd日'; //;
				value2.datepicker({
					changeMonth: true,
					changeYear: true,
					dateFormat : fmt
					,onClose:function(fmt,obj){
						//alert(value1.attr('a'));
						myblur();
						fObj.values = [value2.val()];
					}
				});
				$('#ui-datepicker-div').css('display','none');

			} else {
				var value1 = $('<input type="text" id="value1">');
				$(tdss[5]).find('#valueArea').append(value1);
				value1.val(fObj.values[0]);
				value1.change(function(){
					fObj.values = [$(this).val()];
				});
			}
		}
		sp2.val(fObj.comparison).css('width','80px').change(function(){
			operChange(rowTitle);
		});
		operChange(rowTitle);


		var aggrs = $(`<select style="width:100px;">
			<option value="">不汇总</option>
			<option value="sum">求和</option>
			<option value="count">计数</option>
			<option value="countd">去重计数</option>
			<option value="max">最大</option>
			<option value="min">最小</option>
			<option value="avg">平均</option>
			</select>`);
		//<option value="stddev">标准差</option>
		aggrs.val(fObj.aggr);
		$(tds[3]).append(aggrs);
		aggrs.change(function(){
			fObj.aggr = $(this).val();
			ft.remove();
			refreshFields();
		});
		
		var removeImg = $('<img style="vertical-align:-3px;cursor:pointer;margin:0px;" src="'+guideConf.guideDir+'/img/guide/13.png">');
		$(tds[0]).append(removeImg);
		removeImg.click(function(){
			mdConf.fields.remove(fObj);
			tr.remove();
		});

		var dragImg = $('<img style="vertical-align:-3px;margin:0px;cursor:move;" src="'+guideConf.guideDir+'/img/guide/12.png">');
		$(tds[1]).append(dragImg);

		
		tds.css('padding','4px');
		return tr;
	} 
	
	for (var i=0; i<mdConf.fields.length; i++) {
		var tr = null;
		for (var j=0; j<trs.length; j++) {
			if (mdConf.fields[i].title == $(trs[j]).attr('titl')) {
				tr = $(trs[j]);
			}
		}
		
		
		if (tr == null) {
			tr = addRow(mdConf.fields[i].title);
			//var sss = $(`<select id="OPNFUN2222_arg" name="OPNFUN2222_arg" class="easyui-combobox" style="text-align:left;vertical-align:middle;padding-left:0px;font-family:Dialog;font-size:12px;color:rgba(0,0,0,1.0);font-weight:normal;font-style:normal;text-decoration:none;background-color:transparent;white-space:nowrap;overflow:hidden;word-break:keep-all;;width:208px;height:23px" value="" canEmpty="1" el="全部" data-options="editable:false,valueField:'v',textField:'d',readonly:false,data:[{v:'',d:'全部'},{v:'0',d:'未开通'},{v:'1',d:'已开通'}]" onclick="_hideDropDown()"></select>`);
			//$(tds[5]).append(sss);
			
			
		//initJQEditor('CUSM_NATIONALITY_CODE_arg','multibox','form1_E4');
		//initJQEditor('CUSV_DD_MARITAL_STATUS_arg','combobox','form1_C6');

		} else {
			ft.append(tr);
		}
		
		var tds = tr.find('td');
		$(tds[3]).find('input').val(mdConf.fields[i].title);
		
    
    //$(".sortable").disableSelection();
		//initJQEditor('OPNFUN2222_arg','combobox','form1_E6');

	}
}

var fieldsHeight = 0;

function showFields(group) {
	var currGroup = $('#fields').find('div[v="'+group+'"]');
	if (currGroup.length==0) {
		var fs = [];
		
		var mdi = md[0];
		for (var i=0; i<md.length; i++) {
			if (md[i].name == group || group =="_all") fs = fs.concat(md[i].fields);
		}
		
		var currGroup = $('<div v="'+group+'" style="overflow-x:hidden"></div>');
		for (var i=0; i<fs.length; i++) {
			var title = fs[i].title;
			if (fs[i]==null||fs[i]=='') title = fs[i].name;
			var spani = $('<div style="margin:2px;padding:5px;" f="'+fs[i].name+'">'+title+'</div>');
			currGroup.append(spani);
			spani.hover(function(){
				$(this).css('background-color','#D0D0D0');
			},function(){
				$(this).css('background-color','');
			});
		}
		$('#fields').append(currGroup);
		currGroup.css('height','%100');//$('#sourceArea').height()-fieldsHeight+"px");
		
	
		currGroup.find('div[f]').css({"cursor":"move"}).draggable({
			//revert:true
			//items: '> tr',
			//forcePlaceholderSize: true,
			connectToSortable: "div[cType]",
			appendTo:'body',
			helper: function(e) {
				var div = $(this);
				/*
				var helper = $("<div style='margin:3px;padding:3px;background-color:#F8F8F8;'>"+str+"</div>");
				*/
				var helper = $(this).clone();
				helper.css("z-index",55555).css("opacity","0.8");
				
				return helper;
			}
			//,axis:"y"  
			,drag:function(e, ui){
				console.log(" drag begin ");
				$('#tableDiv').css("background-color","#FFFF88");
			}
			,stop : function(event, ui) {
				var iType = $(this).attr("iType");
				$('#tableDiv').css("background-color","");
				//confOverFunc(event, ui);
			}
			//,stop:confOverFunc
			
			//easyui
			//,proxy : 'clone'
			//,revert:true
			//,handle : "div[f]"
			
		});

	}
	$('#fields').find('div[v]').css('display','none');
	currGroup.css('display','block');
	
}

var getArrayItem = function(a, prop, value) {
	if (a == null) return null;
	for (var i=0; i<a.length; i++) {
		if (a[i][prop] == value) return a[i];
	}
}

var v1 = function(v) {
	if (v==undefined) return null;
	else return v;
}

var v2 = function(v) {
	if (v==undefined || v==null) return "";
	else return v;
}

function dropdownTree(inputObj, data, title) {
	var fObj = getArrayItem(mdConf.fields,"title",title);
	var srcFObj = getArrayItem(md.allFields,"name",fObj.name);

	$('body').append(`
		<div id="menuContent_${title}" class="menuContent" style="display:none; position: absolute;height: calc(100% - 75px);">
			<ul id="treeDemo_${title}" class="ztree" style="margin-top:0; width:180px; height: 100%;overflow:auto;background-color:#F0F0F0;"></ul>
		</div>
	`);

	var zztree = $.fn.zTree.init($("#treeDemo_"+title), {
		check: {
			enable: v2(srcFObj.multi)==1,
			chkStyle: "checkbox",
			chkboxType: {"Y":"ps", "N":"ps"}
		},
		view: {
			dblClickExpand: false
		},
		data: {
			simpleData: {
				enable: true
			}
		},
		callback: {
			beforeClick: function(treeId, treeNode) {
				var zTree = $.fn.zTree.getZTreeObj("treeDemo_"+title);
				zTree.checkNode(treeNode, !treeNode.checked, null, true);
				return v2(srcFObj.multi)!=1;
			},
			onCheck: function(e, treeId, treeNode) {
				fObj.values = [];
				var zTree = $.fn.zTree.getZTreeObj("treeDemo_"+title),
				nodes = zTree.getCheckedNodes(true),
				v = "";
				for (var i=0, l=nodes.length; i<l; i++) {
					v += nodes[i].name + ",";
					fObj.values.push(nodes[i].v);
				}
				if (v.length > 0 ) v = v.substring(0, v.length-1);
				inputObj.val(v);
			},
			onClick: function(e, treeId, treeNode) {
				if (v2(srcFObj.multi)!=1) {
					inputObj.val(treeNode.name);
					fObj.values = [treeNode.v];
					hideMenu();
				}
			}		
		}
	}, data);
	
	var hideMenu = function(){
		$("#menuContent_"+title).fadeOut("fast");
		$("body").unbind("mousedown", onBodyDown);
	}
	
	var onBodyDown = function(event) {
		if (!(event.target == inputObj[0] || event.target.id == "menuContent_"+title || $(event.target).parents("#menuContent_"+title).length>0)) {
			hideMenu();
		}
	}
	
	inputObj.click(function(){
		var inputObjOffset = inputObj.offset();
		$("#menuContent_"+title).css({left:inputObjOffset.left + "px", top:inputObjOffset.top + inputObj.outerHeight() + "px"}).slideDown("fast");

		$("body").bind("mousedown", onBodyDown);
	});
}



