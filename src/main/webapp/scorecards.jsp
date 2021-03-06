<%@page import="
java.util.Date,
java.util.Calendar
"%>

<%@include file="header.jsp"%>

<style>

.edit-link{
	font-family: Overpass;
	/*
	font-size: smaller;
	*/
	font-size: 6pt;
	vertical-align: super;
}

.btn{
  padding: 2px 12px !important;
}
.btn-small{
	height: 25px;
	
}
</style>
<script>

var idFieldName="userId";

function edit2(id){
  document.getElementById("edit-ok").innerHTML="Update";
  var xhr = new XMLHttpRequest();
  var ctx = "${pageContext.request.contextPath}";
  xhr.open("GET", ctx+"/api/scorecard/"+id, true);
  xhr.send();
  xhr.onloadend = function () {
    var json=JSON.parse(xhr.responseText);
    var form=document.getElementById("myform");
    
    $("#editFieldsDiv").empty();
    for (var propertyName in json) {
    	if (json.hasOwnProperty(propertyName)) {
    	  if (propertyName === idFieldName || propertyName === "displayName") continue;
	    	var fieldName=propertyName;
	    	var fieldValue=json[propertyName];
	    	var displayName=propertyName;
	      $("#editFieldsDiv").append('<div class="form-group"><label for="'+fieldName+'" class="control-label">'+displayName+':</label><input id="'+fieldName+'" name="'+fieldName+'" type="text" value="'+fieldValue+'" class="form-control"></div>');
      }    
    }
    
    for (var i = 0, ii = form.length; i < ii; ++i) {
      if (typeof json[form[i].name] == "undefined"){
        form[i].value="";
      }else{
        form[i].value=json[form[i].name];
      }
    }
  }
}
function deleteItem(id){
  post("/analytics/delete/"+id);
}
function reset(){
    document.getElementById("edit-ok").innerHTML="Create";
    
    var form=document.getElementById("myform");
    for (var i = 0, ii = form.length; i < ii; ++i) {
      var input = form[i];
      input.value="";
    }
    document.getElementById(idFieldName).value="NEW";
}

function update(){
  var data = {};
  var op="";
  var form=document.getElementById("myform");
  for (var i = 0, ii = form.length; i < ii; ++i) {
    var input = form[i];
    if (input.name==idFieldName) op=input.value;
    
    if (input.name) {
      data[input.name] = input.value;
    }
  }
  if (op=="") alert("ERROR: OP is empty!");
  post("/scorecard/"+op, data);
  reset();
}

function post(uri, data){
  var xhr = new XMLHttpRequest();
  var ctx = "${pageContext.request.contextPath}";
  var url=ctx+"/api"+uri;
  xhr.open("POST", url, true);
  if (data != undefined){
    xhr.send(JSON.stringify(data));
  }else{
    xhr.send();
  }
  xhr.onloadend = function () {
    //$('#example').dataTable().fnReloadAjax();
    
    $('#example').DataTable().destroy();
		loadDataTable();
    
  };
}

function loadDataTable(){
	$('#example').DataTable( {
				bSort: false,
        "ajax": {
            "url": '${pageContext.request.contextPath}/api/scorecards/',
            "success": function(json){
            		//console.log("json="+JSON.stringify(json));
	            	var tableHeaders="";
	            	var tableColumns=[];
	            	$.each(json.columns, function(i, val){
	              	tableHeaders += "<th>" + val.title + "</th>";
	              	if (val.data=="level"){
	              		tableColumns.push({data: val.data, render: function(data,type,row){return "<span style='width:25px;height:25px;background-color:"+row['level'].toLowerCase()+"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;"+row['level'].toLowerCase();}});
	              	}else{
	              		tableColumns.push({data: val.data});
	              	}
	              });
	              $("#tableDiv").empty();
                $("#tableDiv").append('<table id="example" class="display" cellspacing="0" width="100%"><thead><tr>' + tableHeaders + '</tr></thead></table>');
                
                //console.log("tableColumns="+JSON.stringify(tableColumns));
                //console.log("json.data="+JSON.stringify(json.data));
                
                $('#example').DataTable({
                		"data": json.data,
                		"columns": tableColumns,
						        "scrollCollapse": true,
						        "paging":         true,
						        "lengthMenu": [[10, 25, 50, 100, 200, -1], [10, 25, 50, 100, 200, "All"]], // page entry options
						        "pageLength" : 25, // default page entries
						        "searching" : true,
						        "order" : [[1,"desc"]],
						        "columnDefs": [
						        	{ "targets": 0,  "render": function (data,type,row){
						        		return "<a href='events.jsp?id="+row['id']+"&name="+row['name']+"'>"+row["name"]+"</a> <span class='edit-link'>(<a href='#' onclick='edit2(\""+row["id"]+"\");' data-toggle='modal' data-target='#exampleModal'>edit</a>)</span>";
						        	}}
                		]
              	});
                // position:relative;height:25px;width:75px;left:-18px;top:-3px;
                // tag the export to the left of the search button
                var btnExport=`
                <div style="left:-20px;float:left;" class="dropdown export">
                   <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                     Export
                   </button>
                   <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                     <a class="dropdown-item" href="api/scorecards/export/csv">... as CSV</a><br/>
                     <a class="dropdown-item" href="api/scorecards/export/xls">... as XLS</a><br/>
                     <a class="dropdown-item" href="api/scorecards/export/json">... as JSON</a>
                   </div>
                 </div>
                 `;
                 
	               // Insert Export button next to Search box
	               var searchBoxDiv=document.querySelector("#example_filter");
	               var wrapper=searchBoxDiv.parentNode;
	               var newNode = document.createElement("span");
	               newNode.innerHTML=btnExport+"&nbsp;";
	               searchBoxDiv.appendChild(newNode);
                
            },
            "dataType": "json"
        },
        "scrollCollapse": true,
        "paging":         false,
        "lengthMenu": [[10, 25, 50, 100, 200, -1], [10, 25, 50, 100, 200, "All"]], // page entry options
        "pageLength" : 25, // default page entries
        //"columnDefs": [
        //	{"targets": 1, "render": function (data,type,row){
        //		return "XXXXXXXX";
        //	}}
        //]
    } );
}

$(document).ready(function() {
  loadDataTable();
});


</script>
	
	<style>
		.export div .dropdown-item{
			padding-left: 10px;
		}
		.link{
			cursor: pointer;
			font-weight: bold;
			color: grey;
		}
		
		.link:hover{
		  font-weight: bold;
		  color: #333333;
		}
	</style>

    <%@include file="nav.jsp"%>
    
	<div class="navbar-connector"></div>
    <div class="navbar-title">
    	<h2><span class="navbar-title-text">Scorecards</span></h2>
    </div>
		
    <div id="solutions">
		    <div id="solutions-buttonbar">
		    <!--
		        <button style="position:relative;height:30px;width:75px;left:0px;top:0px;"   class="btn btn-primary" name="New"    onclick="reset();" type="button" data-toggle="modal" data-target="#exampleModal" data-whatever="@new" disabled>New</button>
		        <button style="position:relative;height:30px;width:75px;left:0px;top:0px;"   class="btn btn-primary" name="Export" onclick="window.location.href='<%=request.getContextPath()%>/api/analytics/export/xls';" disabled>Export</button>
		    <button style="position:relative;height:30px;width:75px;left:0px;top:0px;"   class="btn btn-primary" name="Export" onclick="window.location.href='<%=request.getContextPath()%>/api/scorecards/export/xls';">Export</button>
		    -->
		    </div>
		    <div id="tableDiv">
			    <table id="example" class="display" cellspacing="0" width="100%">
			        <thead>
			            <tr>
			                <th align="left">User ID</th>
			                <th align="left">Name</th>
			                <th align="left">Total Points</th>
			                <th align="left">A dynamic bunch of points fields go here</th>
			                <th align="left"></th>
			            </tr>
			        </thead>
			    </table>
			  </div>
    </div>

<div class="modal fade" id="exampleModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel">
  <div class="modal-dialog" role="document"> <!-- make wider by adding " modal-lg" to class -->
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="exampleModalLabel">User Scorecard</h4>
      </div>
      <div class="modal-body">
        <form id="myform">
          <div id="form-id" class="form-group">
            <label for="userId" class="control-label">User ID:</label>
            <input id="userId" disabled name="userId" type="text" class="form-control"/>
          </div>
          <div class="form-group">
            <label for="displayName" class="control-label">Display Name:</label>
            <input id="displayName" name="displayName" type="text" class="form-control">
          </div>
          
          <div id="editFieldsDiv">
          </div>
          
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button id="edit-ok" type="button" class="btn btn-primary" data-dismiss="modal" onclick="update(); return false;">Create</button>
      </div>
    </div>
  </div>
</div>
