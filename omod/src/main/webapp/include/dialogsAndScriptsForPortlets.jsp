
<openmrs:htmlInclude file="/moduleResources/orderextension/orderextension.css" />
<openmrs:htmlInclude file="/moduleResources/orderextension/chosen/chosen.jquery.js" />
<openmrs:htmlInclude file="/moduleResources/orderextension/chosen/chosen.css" />

<script type="text/javascript">
jQuery(document).ready(function() {
	 
	jQuery('.drugLine').mouseover(function() { jQuery(this).addClass('zebraHover'); }); 
	jQuery('.drugLine').mouseout(function() { jQuery(this).removeClass('zebraHover'); });
	
	jQuery(".content").hide();
	jQuery(".asc").hide();
	 
	//toggle the componenet with class msg_body
	jQuery(".cycleTitleCollapsable").click(function()
	{
		jQuery(this).next(".content").toggle();
		jQuery("img", this).toggle();
	});
	  
	jQuery(".drugDetails").hide();
	
	jQuery('.openmrs_error').hide();
	
	jQuery("#drugComboTwo").chosen({allow_single_deselect: true});
	
	
	jQuery('.addDrugToGroupButton').click(function(){ 
		jQuery('#addNewDrugToGroupDialog').dialog('open');
		
		var val = this.id;
		var values = val.split(",");
		jQuery("#groupId").val(values[0]);
		jQuery("#cycle").val(values[1]);
		jQuery('.openmrs_error').hide();
		if(values[1] == "true")
		{
			jQuery('.repeatCycleDiv').show();
		}
		else
		{
			jQuery('.repeatCycleDiv').hide();
		}
	});

	jQuery('#addNewDrugToGroupDialog').dialog({
		position: 'middle',
		autoOpen: false,
		modal: true,
		title: '<spring:message code="orderextension.regimen.addDrugGroup" javaScriptEscape="true"/>',
		height: 480,
		width: '100%',
		zIndex: 100,
		buttons: { '<spring:message code="general.add"/>': function() { handleAddMedicationToGroup(); },
				   '<spring:message code="general.cancel"/>': function() { $j(this).dialog("close"); }
		}
	});	
	
	jQuery('.changeStartDateOfGroupButton').click(function(){ 
		jQuery('#changeStartDateOfGroupDialog').dialog('open');
		
		var val = this.id;
		var values = val.split(",");
		jQuery("#changeStartGroupId").val(values[0]);
		jQuery("#changeDate").val(values[2]);
		jQuery('.openmrs_error').hide();
		
		if(values[1] == "true")
		{
			jQuery('.repeatCycleDiv').show();
		}
		else
		{
			jQuery('.repeatCycleDiv').hide();
		}
	});

	jQuery('#changeStartDateOfGroupDialog').dialog({
		position: 'middle',
		autoOpen: false,
		modal: true,
		title: '<spring:message code="orderextension.regimen.changeStartDate" javaScriptEscape="true"/>',
		height: 480,
		width: '80%',
		zIndex: 100,
		buttons: { '<spring:message code="general.change"/>': function() { handleChangeStartDateOfGroup(); },
				   '<spring:message code="general.cancel"/>': function() { $j(this).dialog("close"); }
		}
	});	
	
	jQuery('.editButton').click(function(){ 
		var val = this.id;
		var values = val.split(",");
		
		jQuery('#editDrugDialog').dialog('open');
		jQuery("#orderId").val(values[0]);
		jQuery('.openmrs_error').hide();
		
		if(values[1] == "true")
		{
			jQuery('.repeatCycleDiv').show();
		}
		else
		{
			jQuery('.repeatCycleDiv').hide();
		}
		
		updateEditDrugDialog();
	});
	
	jQuery('#editDrugDialog').dialog({
		position: 'middle',
		autoOpen: false,
		modal: true,
		title: '<spring:message code="orderextension.regimen.editDialog" javaScriptEscape="true"/>',
		height: 480,
		width: '100%',
		zIndex: 100,
		buttons: { '<spring:message code="general.edit"/>': function() { handleEditDrugOrder(); },
				   '<spring:message code="general.cancel"/>': function() { $j(this).dialog("close"); }
		}
	});	
	
	jQuery('.stopButton').click(function(){ 
		
		var val = this.id;
		var values = val.split(",");
		
		jQuery('#stopDrugDialog').dialog('open');
		jQuery("#stopOrderId").val(values[0]);
		jQuery("#startDateInd").val(values[2]);
		
		jQuery('.openmrs_error').hide();
		
		if(values[1] == "true")
		{
			jQuery('.repeatCycleDiv').show();
		}
		else
		{
			jQuery('.repeatCycleDiv').hide();
		}
	});
	
	jQuery('#stopDrugDialog').dialog({
		position: 'middle',
		autoOpen: false,
		modal: true,
		title: '<spring:message code="orderextension.regimen.stopDialog" javaScriptEscape="true"/>',
		width: '80%',
		zIndex: 100,
		buttons: { '<spring:message code="orderextension.regimen.stop"/>': function() { handleStopDrugOrder(); },
				   '<spring:message code="general.cancel"/>': function() { $j(this).dialog("close"); }
		}
	});
	
	jQuery('.deleteButton').click(function(){ 
		
		var val = this.id;
		var values = val.split(",");
		
		jQuery('#deleteDrugDialog').dialog('open');
		jQuery("#deleteOrderId").val(values[0]);
		jQuery('.openmrs_error').hide();
		
		if(values[1] == "true")
		{
			jQuery('.repeatCycleDiv').show();
		}
		else
		{
			jQuery('.repeatCycleDiv').hide();
		}
	});
	
	jQuery('#deleteDrugDialog').dialog({
		position: 'middle',
		autoOpen: false,
		modal: true,
		title: '<spring:message code="orderextension.regimen.deleteDialog" javaScriptEscape="true"/>',
		width: '80%',
		zIndex: 100,
		buttons: { '<spring:message code="general.delete"/>': function() { handleDeleteDrugOrder(); },
				   '<spring:message code="general.cancel"/>': function() { jQuery(this).dialog("close"); }
		}
	});
	
	jQuery('.stopAllDrugsInGroupButton').click(function(){ 
		
		var val = this.id;
		var values = val.split(",");
		
		jQuery('#stopAllDrugDialog').dialog('open');
		jQuery("#stopAllOrderId").val(values[0]);
		jQuery("#startDate").val(values[2]);
		jQuery('.openmrs_error').hide();
		
		if(values[1] == "true")
		{
			jQuery('.repeatCycleDiv').show();
		}
		else
		{
			jQuery('.repeatCycleDiv').hide();
		}
	});
	
	jQuery('#stopAllDrugDialog').dialog({
		position: 'middle',
		autoOpen: false,
		modal: true,
		title: '<spring:message code="orderextension.regimen.stopAllDialog" javaScriptEscape="true"/>',
		width: '80%',
		zIndex: 100,
		buttons: { '<spring:message code="orderextension.regimen.stop"/>': function() { handleStopAllDrugOrder(); },
				   '<spring:message code="general.cancel"/>': function() { $j(this).dialog("close"); }
		}
	});
	
	jQuery('.deleteAllDrugsInGroupButton').click(function(){ 
		
		var val = this.id;
		var values = val.split(",");
		
		jQuery('#deleteAllDrugDialog').dialog('open');
		jQuery("#deleteAllOrderId").val(values[0]);
		jQuery('.openmrs_error').hide();
		
		if(values[1] == "true")
		{
			jQuery('.repeatCycleDiv').show();
		}
		else
		{
			jQuery('.repeatCycleDiv').hide();
		}
	});
	
	jQuery('#deleteAllDrugDialog').dialog({
		position: 'middle',
		autoOpen: false,
		modal: true,
		title: '<spring:message code="orderextension.regimen.deleteAllDialog" javaScriptEscape="true"/>',
		width: '80%',
		zIndex: 100,
		buttons: { '<spring:message code="general.delete"/>': function() { handleDeleteAllDrugOrder(); },
				   '<spring:message code="general.cancel"/>': function() { jQuery(this).dialog("close"); }
		}
	});
});

var drugDetailTwo;

function fetchDrugsTwo() {
	
	var selected = jQuery('#drugComboTwo').val();
	
	if(jQuery("#drugComboTwo").attr("selectedIndex") > 0)
	{
		var url = "${pageContext.request.contextPath}/module/orderextension/getDrugsByName.form?name=" + selected;
		jQuery.getJSON(url, function(result) 
		{ 
			drugDetailTwo = result;
			
			var html = "<spring:message code='orderextension.regimen.form'/>*: <select id='drugTwo' name='drug' onChange='updateDrugInfoTwo()'";
			
			if(result.length == 1)
			{
				html = html +  "style='display:none'";
			}
			
			html = html + ">";
			
			var i=0;
			for (i=0;i< result.length;i++)
			{
				html = html + "<option value=" + result[i].id + ">" + result[i].name + "</option>";
			}
			
			html = html + "</select>";
			
			if(result.length == 1)
			{
				html = html + result[0].name;
			}
			
			jQuery("#drugNameTwo").html(html);
			
			updateDrugInfoTwo();
		});
		
		jQuery(".drugDetails").show();
	}
	else
	{
		jQuery(".drugDetails").hide();
		jQuery("#drugNameTwo").html("");
	}
}

var drugDetailThree;

function fetchDrugsThree() {
	
	var selected = jQuery('#drugComboThree').val();
	
	if(jQuery("#drugComboThree").attr("selectedIndex") > 0)
	{
		var url = "${pageContext.request.contextPath}/module/orderextension/getDrugsByName.form?name=" + selected;
		jQuery.getJSON(url, function(result) 
		{ 
			drugDetailThree = result;
			
			var html = "<spring:message code='orderextension.regimen.form'/>*: <select id='drugThree' name='drug' onChange='updateDrugInfoThree()'";
			
			if(result.length == 1)
			{
				html = html +  "style='display:none'";
			}
			
			html = html + ">";
			
			var i=0;
			for (i=0;i< result.length;i++)
			{
				html = html + "<option value=" + result[i].id + ">" + result[i].name  + "</option>";
			}
			
			html = html + "</select>";
			
			if(result.length == 1)
			{
				html = html + result[0].name;
			}
			
			jQuery("#drugNameThree").html(html);
			
			updateDrugInfoThree();
		});
		
		jQuery(".drugDetails").show();
	}
	else
	{
		jQuery(".drugDetails").hide();
		jQuery("#drugNameThree").html("");
	}
	
}

function updateDrugInfoTwo() {
	
	var route = "";
	var units = "";
	
	var index = jQuery('#drugTwo').attr("selectedIndex");
	
	if(index != null && index >=0)
	{
		if(drugDetailTwo[index].route != "")
		{
			route =  "<spring:message code='orderextension.regimen.route'/>" + ": " + drugDetailTwo[index].route;
		}
		units = " " + drugDetailTwo[index].doseForm;
	}
	
	jQuery("#routeInfoTwo").html(route);
	jQuery("#unitsTwo").html(units);
}

function getIndicationClassificationsTwo() {

	var selected = jQuery('#indicationComboTwo').val();
	
	if(jQuery('#indicationComboTwo').attr("selectedIndex") > 0) {
		
		var url = "${pageContext.request.contextPath}/module/orderextension/getClassificationsByIndication.form?id=" + selected;
		jQuery.getJSON(url, function(result) 
		{
			var html = "";
			
			if(result.length > 0)
			{
				html = html +  "<spring:message code='orderextension.regimen.classification'/>: <select id='classification' name='classification'><option value='' selected='selected'></option>";
			
				var i=0;
				for (i=0;i< result.length;i++)
				{
					html = html + "<option value=" + result[i].id + ">" + result[i].name + "</option>";
				}
				
				html = html + "</select>";
			}
			jQuery("#indClassificationTwo").html(html);
		});
	}
	else {
		jQuery("#indClassificationTwo").html("");
	}
}

function updateDrugInfoThree() {
	
	var route = "";
	var units = "";
	
	var index = jQuery('#drugThree').attr("selectedIndex");
	
	if(index != null && index >=0)
	{
		if(drugDetailThree[index].route != "")
		{
			route =  "<spring:message code='orderextension.regimen.route'/>" + ": " + drugDetailThree[index].route;
		}
		units = " " + drugDetailThree[index].units;
	}
	
	jQuery("#routeInfoThree").html(route);
	jQuery("#unitsThree").html(units);
}

function getIndicationClassificationsThree() {

	var selected = jQuery('#indicationComboThree').val();
	
	if(jQuery('#indicationComboThree').attr("selectedIndex") > 0) {
		var url = "${pageContext.request.contextPath}/module/orderextension/getClassificationsByIndication.form?id=" + selected;
		jQuery.getJSON(url, function(result) 
		{
			var html = "";
			
			if(result.length > 0)
			{
				html = html +  "<spring:message code='orderextension.regimen.classification'/>: <select id='classificationThree' name='classification'><option value='' selected='selected'></option>";
			
				var i=0;
				for (i=0;i< result.length;i++)
				{
					html = html + "<option value=" + result[i].id + ">" + result[i].name + "</option>";
				}
				
				html = html + "</select>";
			}
			jQuery("#indClassificationThree").html(html);
		});
	}
	else {
		jQuery("#indClassificationThree").html("");	
	}
}

function handleAddMedicationToGroup()
{	
	var error = validAddDrugPanelTwo();
	
	if(error != "")
	{
		jQuery('.openmrs_error').show();
		jQuery('.openmrs_error').html(error);
	}
	else
	{
		jQuery('#addDrugToGroup').submit();
	}
}

function handleChangeStartDateOfGroup()
{	
	var changeDate = jQuery("#changeDate").val();

	if(changeDate == "")
	{
		jQuery('.openmrs_error').show();
		jQuery('.openmrs_error').html("<spring:message code='orderextension.regimen.changeDateError' /> ");
	}
	else
	{
		jQuery('#changeStartDateOfGroup').submit();
	}
}

function validAddDrugPanelTwo() {
	
	var error = '';
	
	var selectedIndex = jQuery("#drugComboTwo").attr("selectedIndex");
	if(selectedIndex == 0)
	{
		error = " <spring:message code='orderextension.regimen.drugError' /> ";
	}
	else
	{
		var startDate = jQuery("#addCycleStartDate").val();

		if(startDate == "")
		{
			error = error + " <spring:message code='orderextension.regimen.startDateError' /> ";
		}
		
		var dose = jQuery("#dosage").val();
		
		if(dose == "")
		{
			error = error + " <spring:message code='orderextension.regimen.doseError' /> ";

		}
	}
	
	return error;
}

var editDrugDetail;
var classification;

function updateEditDrugDialog() {
	
	var drugOrderId = jQuery("#orderId").val();
	
	var url = "${pageContext.request.contextPath}/module/orderextension/getDrugOrder.form?id=" + drugOrderId;
	jQuery.getJSON(url, function(result) 
	{
		 var comboHtml = "<select name='drugComboThree' id='drugComboThree' data-placeholder='<spring:message code='orderextension.regimen.chooseOption' />' style='width:350px;'>";
			
		 comboHtml = comboHtml + "<option value=''></option>";
		 <c:forEach items="${model.drugs}" var="drug">
			comboHtml = comboHtml + "<option value='${drug}'>${drug}</option>";
		</c:forEach>
		comboHtml = comboHtml + "</select>";
				
		jQuery("#drugSelector").html(comboHtml);	
		
		jQuery("#drugComboThree").val(result.concept);
		
		jQuery("#drugComboThree").chosen({allow_single_deselect: true});
		
		var drugUrl = "${pageContext.request.contextPath}/module/orderextension/getDrugsByName.form?name=" + result.concept;
		var drugId = result.drugId;
		jQuery.getJSON(drugUrl, function(result) 
		{ 
			drugDetailThree = result;
			
			var html = "<spring:message code='orderextension.regimen.form'/>*: <select id='drugThree' name='drug' onChange='updateDrugInfoThree()'";
			
			if(result.length == 1)
			{
				html = html +  "style='display:none'";
			}
			
			html = html + ">";
			
			var i=0;
			for (i=0;i< result.length;i++)
			{
				html = html + "<option value=" + result[i].id + ">" + result[i].name + "</option>";
			}
			
			html = html + "</select>";
			
			if(result.length == 1)
			{
				html = html + result[0].name;
			}
			
			jQuery("#drugNameThree").html(html);
			jQuery("#drugThree").val(drugId);
			
		});
		
		jQuery("#drugComboThree").attr("onChange", "fetchDrugsThree()"); 
		
		jQuery("#editStartDate").val(result.startDate);
		
		jQuery("#editStopDate").val(result.endDate);
		
		jQuery("#dosageThree").val(result.dose);
		
		jQuery("#frequencyDayThree").val(result.freqDay);
		
		jQuery("#frequencyWeekThree").val(result.freqWeek);
		
		if(result.asNeeded == "true")
		{
			jQuery("#asNeededThree").attr('checked', 'checked');
		}
		else
		{
			jQuery("#asNeededThree").removeAttr('checked');
		}
		
		var indicationHtml = "<spring:message code='orderextension.regimen.reasonForPrescription' />: <select name='indicationCombo' id='indicationComboThree' onChange='getIndicationClassificationsThree()'><option value='' ></option>";
				
		<c:forEach items="${model.indications}" var="indication">
			indicationHtml = indicationHtml + "<option value='${indication.id}'>${indication.displayString}</option>";
		</c:forEach>
			indicationHtml = indicationHtml + "</select>";
		
		jQuery("#indicationSelector").html(indicationHtml);	
		
		classification = result.classification;
		
		jQuery("#indicationComboThree").val(result.indication);
		
		if(result.indication != ""){
			
			if(result.classification != ""){
				
				var classUrl = "${pageContext.request.contextPath}/module/orderextension/getClassificationsByIndication.form?id=" + result.indication;
				jQuery.getJSON(classUrl, function(result) 
				{
					var html = "";
					
					if(result.length > 0)
					{
						html = html +  "<spring:message code='orderextension.regimen.classification'/>: <select id='classificationThree' name='classification'><option value='' selected='selected'></option>";
					
						var i=0;
						for (i=0;i< result.length;i++)
						{
							html = html + "<option value=" + result[i].id + ">" + result[i].name + "</option>";
						}
						
						html = html + "</select>";
					}
					jQuery("#indClassificationThree").html(html);	
					jQuery("#classificationThree").val(classification);
				});	
			}
			else {
				jQuery("#classificationThree").val(classification);
			}
		}
		else {
			getIndicationClassificationsThree();
		}
		
		jQuery("#adminInstructionsThree").val(result.adminInstructions);
		
		jQuery("#instructionsThree").val(result.instructions);
			
		jQuery(".drugDetails").show();
	});
}

function handleEditDrugOrder()
{	
	var error = validAddDrugPanelThree();
	
	if(error != "")
	{
		jQuery('.openmrs_error').show();
		jQuery('.openmrs_error').html(error);
	}
	else
	{
		jQuery('#editDrug').submit();
	}
}

function validAddDrugPanelThree() {
	
	var error = '';
	
	var selectedIndex = jQuery("#drugComboThree").attr("selectedIndex");
	if(selectedIndex == 0)
	{
		error = " <spring:message code='orderextension.regimen.drugError' /> ";
	}
	else
	{
		var startDate = jQuery("#editStartDate").val();

		if(startDate == "")
		{
			error = error + " <spring:message code='orderextension.regimen.startDateError' /> ";
		}
		
		var dose = jQuery("#dosageThree").val();
		
		if(dose == "")
		{
			error = error + " <spring:message code='orderextension.regimen.doseError' /> ";

		}
	}
	
	return error;
}

function handleStopDrugOrder()
{	
	var error = '';
	
	var selectedIndex = jQuery("#drugStopReason").attr("selectedIndex");
	if(selectedIndex == 0)
	{
		error = " <spring:message code='orderextension.regimen.stopReasonError' /> ";
	}
	else
	{
		var stopDate = jQuery("#drugStopDate").val();

		if(stopDate == "")
		{
			error = error + " <spring:message code='orderextension.regimen.stopDateError' /> ";
		}
		else {
			var datePattern = '<openmrs:datePattern />';
			
			var startYears = datePattern.indexOf("yyyy");
			
			var startMonths =  datePattern.indexOf("mm");
			
			var startDays = datePattern.indexOf("dd");
			
			var convertDateStop = stopDate.substring(startYears, startYears + 4) + "/" + stopDate.substring(startMonths, startMonths + 2) + "/" + stopDate.substring(startDays, startDays + 2);
			var dateStop = new Date(convertDateStop);
			
			
			var startDate = jQuery("#startDateInd").val();
			var convertDateStart = startDate.substring(startYears, startYears + 4) + "/" + startDate.substring(startMonths, startMonths + 2) + "/" + startDate.substring(startDays, startDays + 2);
			var dateStart = new Date(convertDateStart);
			
			
			if(dateStop < dateStart)
			{
				error = error + " <spring:message code='orderextension.regimen.stopDateLessStartError' /> ";
			}
		}
	}
	
	if(error != "")
	{
		jQuery('.openmrs_error').show();
		jQuery('.openmrs_error').html(error);
	}
	else
	{
		jQuery('#stopDrug').submit();
	}
}

function handleDeleteDrugOrder()
{	
	var error = '';

	if(jQuery("#deleteReason").val() == null || jQuery("#deleteReason").val() == "")
	{
		error = "<spring:message code='orderextension.regimen.deleteReasonError' /> ";
		jQuery('.openmrs_error').show();
		jQuery('.openmrs_error').html(error);
	}
	else
	{
		jQuery('#deleteDrug').submit();
	}
}

function handleStopAllDrugOrder()
{	
	var error = '';
	
	var selectedIndex = jQuery("#drugStopAllReason").attr("selectedIndex");
	if(selectedIndex == 0)
	{
		error = " <spring:message code='orderextension.regimen.stopReasonError' /> ";
	}
	else
	{
		var stopDate = jQuery("#drugStopAllDate").val();

		if(stopDate == "")
		{
			error = error + " <spring:message code='orderextension.regimen.stopDateError' /> ";
		}
		else {
			var dateStop = new Date(stopDate);
			var dateStart = new Date(jQuery("#startDate").val());
			
			if(dateStop < dateStart)
			{
				error = error + " <spring:message code='orderextension.regimen.stopDateLessStartError' /> ";
			}
		}
	}
	
	if(error != "")
	{
		jQuery('.openmrs_error').show();
		jQuery('.openmrs_error').html(error);
	}
	else
	{
		jQuery('#stopAllDrug').submit();
	}
}

function handleDeleteAllDrugOrder()
{	
	var error = '';

	if(jQuery("#deleteAllReason").val() == null || jQuery("#deleteAllReason").val() == "")
	{
		error = "<spring:message code='orderextension.regimen.deleteReasonError' /> ";
		jQuery('.openmrs_error').show();
		jQuery('.openmrs_error').html(error);
	}
	else
	{
		jQuery('#deleteAllDrug').submit();
	}
}
</script>

<div id="addNewDrugToGroupDialog">	
	<div class="box">
	<div id="openmrs_error" class="openmrs_error"></div>
		<form id="addDrugToGroup" name="addDrugToGroup" method="post" action="${pageContext.request.contextPath}/module/orderextension/addDrugOrderToGroup.form">
			<input type="hidden" name="patientId" value="${model.patient.patientId}">
			<input type="hidden" name="returnPage" value="${model.redirect}&patientId=${model.patient.patientId}"/>	
			<input type="hidden" name="groupId" id="groupId"/>	
			<input type="hidden" name="cycle" id="cycle"/>	
			<table>
				<tr>
					<td class="padding"><spring:message code="orderextension.regimen.individualDrug" />*: </td>
					<td>	<select name="drugComboTwo" id="drugComboTwo" data-placeholder="<spring:message code="orderextension.regimen.chooseOption" />" style="width:350px;" onChange="fetchDrugsTwo()">
						<option value="" selected="selected"></option>
						
						<c:forEach items="${model.drugs}" var="drug">
							<option value="${drug}">${drug}</option>
								</c:forEach>
							</select>
						</td>
						<td id="drugNameTwo" class="padding"></td>
						<td id="routeInfoTwo" class="padding"></td>
					</tr>
				</table>
				<table>
					<tr class="drugDetails">
						<th class="padding"><spring:message code="orderextension.regimen.patientPrescription" />:</th>
					</tr>
					<tr class="drugDetails">
						<td class="padding"><spring:message code="DrugOrder.dose" />*:  <input type="text" name="dose" id="dosage" size="10"/></td><td id="unitsTwo"></td>
						<td class="padding"><spring:message code="DrugOrder.frequency"/>:			
							<select name="frequencyDay" id="frequencyDay">
								<% for ( int i = 1; i <= 10; i++ ) { %>
								<option value="<%= i %>/<spring:message code="DrugOrder.frequency.day" />"><%= i %>/<spring:message code="DrugOrder.frequency.day" /></option>
								<% } %>
								<option value="<spring:message code="orderextension.regimen.onceOnlyDose" />"><spring:message code="orderextension.regimen.onceOnlyDose" /></option>
							</select>
							<span> x </span>
							<select name="frequencyWeek" id="frequencyWeek">
								<openmrs:globalProperty var="drugFrequencies" key="dashboard.regimen.displayFrequencies" listSeparator="," />
								<c:if test="${empty drugFrequencies}">
									<option disabled>&nbsp; <spring:message code="DrugOrder.add.error.missingFrequency.interactions" arguments="dashboard.regimen.displayFrequencies"/></option>
								</c:if>
								<c:if test="${not empty drugFrequencies}">
										<option value=""></option>
									<c:forEach var="drugFrequency" items="${drugFrequencies}">
										<option value="${drugFrequency}">${drugFrequency}</option>
									</c:forEach>
								</c:if>											
							</select>
							</td>
							<td class="padding"><input type="checkbox" name="asNeeded" id="asNeeded" value="asNeeded"><spring:message code='orderextension.orderset.DrugOrderSetMember.asNeeded'/></td>
						</tr>
					</table>
					<table>
						<tr class="drugDetails">
							<td class="padding"><spring:message code="orderextension.orderset.field.relativeStartDay" />*:  <openmrs_tag:dateField formFieldName="addCycleStartDate" startValue=""/></td>
							<td class="padding"><spring:message code="orderextension.regimen.stopDate" />:  <openmrs_tag:dateField formFieldName="stopDate" startValue=""/></td>
					</tr>
				</table>
				<table>
					<tr	class="drugDetails">
						<td class="padding"><spring:message code="orderextension.regimen.reasonForPrescription" />:
							<select name="indicationCombo" id="indicationComboTwo" onChange="getIndicationClassificationsTwo()">
								<option value="" selected="selected"></option>
								<c:forEach items="${model.indications}" var="indication">
							<option value="${indication.id}">${indication.displayString}</option>
								</c:forEach>
							</select>
						</td>
						<td id="indClassificationTwo" class="padding"></td>
					</tr>
				</table>
				<table>	
					<tr class="drugDetails">
						<td class="padding topAlignment"><spring:message code="orderextension.regimen.administrationInstructions"/>: <textarea rows="2" cols="40" name="adminInstructions" id="adminInstructions"></textarea></td>
						<td class="padding topAlignment"><spring:message code="orderextension.regimen.instructions" />: <textarea rows="2" cols="40" name="instructions" id="instructions"></textarea></td>
					</tr>							
				</table> 
				<div class="repeatCycleDiv">
					<table>
						<tr>
							<td class="padding"><input type="checkbox" name="repeatCycles" id="repeatCycles" value="repeatCycles"><spring:message code='orderextension.regimen.addAllFutureCycles'/></td>
						</tr>
					</table>
				</div>  
		</form>
	</div>
</div>

<div id="editDrugDialog">	
	<div class="box">
	<div id="openmrs_error" class="openmrs_error"></div>
		<form id="editDrug" name="editDrug" method="post" action="${pageContext.request.contextPath}/module/orderextension/editDrug.form">
			<input type="hidden" name="orderId" id="orderId">
			<input type="hidden" name="patientId" value="${model.patient.patientId}">
			<input type="hidden" name="returnPage" value="${model.redirect}&patientId=${model.patient.patientId}"/>	
			<table>
				<tr>
					<td class="padding"><spring:message code="orderextension.regimen.individualDrug" />*: </td>
					<td id="drugSelector"></td>
					<td id="drugNameThree" class="padding"></td>
					<td id="routeInfoThree" class="padding"></td>
					</tr>
				</table>
				<table>
					<tr class="drugDetails">
						<th class="padding"><spring:message code="orderextension.regimen.patientPrescription" />:</th>
					</tr>
					<tr class="drugDetails">
						<td class="padding"><spring:message code="DrugOrder.dose" />*:  <input type="text" name="dose" id="dosageThree" size="10"/><span id="unitsThree"></span></td>
						<td class="padding"><spring:message code="DrugOrder.frequency"/>:			
							<select name="frequencyDay" id="frequencyDayThree">
								<% for ( int i = 1; i <= 10; i++ ) { %>
								<option value="<%= i %>/<spring:message code="DrugOrder.frequency.day" />"><%= i %>/<spring:message code="DrugOrder.frequency.day" /></option>
							<% } %>
							<option value="<spring:message code="orderextension.regimen.onceOnlyDose" />"><spring:message code="orderextension.regimen.onceOnlyDose" /></option>
							</select>
					<span> x </span>
					<select name="frequencyWeek" id="frequencyWeekThree">
						<openmrs:globalProperty var="drugFrequencies" key="dashboard.regimen.displayFrequencies" listSeparator="," />
						<c:if test="${empty drugFrequencies}">
							<option disabled>&nbsp; <spring:message code="DrugOrder.add.error.missingFrequency.interactions" arguments="dashboard.regimen.displayFrequencies"/></option>
						</c:if>
						<c:if test="${not empty drugFrequencies}">
								<option value=""></option>
							<c:forEach var="drugFrequency" items="${drugFrequencies}">
								<option value="${drugFrequency}">${drugFrequency}</option>
							</c:forEach>
						</c:if>											
					</select></td>
					<td class="padding"><input type="checkbox" name="asNeeded" id="asNeededThree" value="asNeeded"><spring:message code='orderextension.orderset.DrugOrderSetMember.asNeeded'/></td>
					</tr>
				</table>
				<table>
					<tr class="drugDetails">
						<td class="padding"><spring:message code="orderextension.orderset.field.relativeStartDay" />*:  <openmrs_tag:dateField formFieldName="editStartDate" startValue=""/></td>
						<td class="padding"><spring:message code="orderextension.regimen.stopDate" />:  <openmrs_tag:dateField formFieldName="editStopDate" startValue=""/></td>
					</tr>	
				</table>
			 	<table>
					<tr	class="drugDetails">
						<td class="padding" id="indicationSelector"></td>
						<td id="indClassificationThree" class="padding"></td>
					</tr>
				</table>
				<table>	
					<tr class="drugDetails">
						<td class="padding topAlignment"><spring:message code="orderextension.regimen.administrationInstructions"/>: <textarea rows="2" cols="40" name="adminInstructions" id="adminInstructionsThree"></textarea></td>
						<td class="padding topAlignment"><spring:message code="orderextension.regimen.instructions" />: <textarea rows="2" cols="40" name="instructions" id="instructionsThree"></textarea></td>
					</tr>							
				</table>
				<div class="repeatCycleDiv">
					<table>
						<tr>
							<td class="padding"><input type="checkbox" name="repeatCycles" id="repeatCycles" value="repeatCycles"><spring:message code='orderextension.regimen.editAllFutureCycles'/></td>
						</tr>
					</table>
				</div> 
		</form>
	</div>
</div>

<div id="stopDrugDialog">	
	<div class="box">
	<div id="openmrs_error" class="openmrs_error"></div>
		<form id="stopDrug" name="stopDrug" method="post" action="${pageContext.request.contextPath}/module/orderextension/stopDrug.form">
			<input type="hidden" name="orderId" id="stopOrderId">
			<input type="hidden" name="startDate" id="startDateInd">
			<input type="hidden" name="patientId" value="${model.patient.patientId}">
			<input type="hidden" name="returnPage" value="${model.redirect}&patientId=${model.patient.patientId}"/>	
			<table>
				<tr>
					<td class="padding"><spring:message code="orderextension.regimen.stopDate"/>: <openmrs_tag:dateField formFieldName="drugStopDate" startValue=""/></td>
					<td class="padding"><spring:message code="orderextension.regimen.stopReason"/>:<openmrs:fieldGen type="org.openmrs.DrugOrder.discontinuedReason" formFieldName="drugStopReason" val="" parameters="optionHeader=[blank]|globalProp=concept.reasonOrderStopped" /></td>
				</tr>
				<tr class="repeatCycleDiv">
					<td class="padding"><input type="checkbox" name="repeatCycles" id="repeatCycles" value="repeatCycles"><spring:message code='orderextension.regimen.deleteInAllFutureCycles'/></td>
				</tr>
			</table>
		</form>
	</div>
</div>

<div id="changeStartDateOfGroupDialog">	
	<div class="box">
	<div id="openmrs_error" class="openmrs_error"></div>
		<form id="changeStartDateOfGroup" name="changeStartDateOfGroup" method="post" action="${pageContext.request.contextPath}/module/orderextension/changeStartDateOfGroup.form">
			<input type="hidden" name="groupId" id="changeStartGroupId">
			<input type="hidden" name="patientId" value="${model.patient.patientId}">
			<input type="hidden" name="returnPage" value="${model.redirect}&patientId=${model.patient.patientId}"/>	
			<table>
				<tr>
					<td class="padding"><spring:message code="orderextension.regimen.changeStartDate"/>: <openmrs_tag:dateField formFieldName="changeDate" startValue=""/></td>
				</tr>
				<tr class="repeatCycleDiv">
					<td class="padding"><input type="checkbox" name="repeatCycles" id="repeatCycles" value="repeatCycles"><spring:message code='orderextension.regimen.changeAllFutureCycles'/></td>
				</tr>
			</table>
		</form>
	</div>
</div>

<div id="deleteDrugDialog">	
	<div class="box">
	<div id="openmrs_error" class="openmrs_error"></div>
		<form id="deleteDrug" name="deleteDrug" method="post" action="${pageContext.request.contextPath}/module/orderextension/deleteDrug.form">
			<input type="hidden" name="orderId" id="deleteOrderId">
			<input type="hidden" name="patientId" value="${model.patient.patientId}">
			<input type="hidden" name="returnPage" value="${model.redirect}&patientId=${model.patient.patientId}"/>
			<table>
				<tr>
					<td class="padding"><spring:message code="orderextension.regimen.deleteReason"/>: <input type="text" name="deleteReason" id="deleteReason" size="100"/></td>
				</tr>
				<tr class="repeatCycleDiv">
					<td class="padding"><input type="checkbox" name="repeatCycles" id="repeatCycles" value="repeatCycles"><spring:message code='orderextension.regimen.deleteInAllFutureCycles'/></td>
				</tr>
			</table>
		</form>
	</div>
</div>

<div id="stopAllDrugDialog">	
	<div class="box">
	<div id="openmrs_error" class="openmrs_error"></div>
		<form id="stopAllDrug" name="stopAllDrug" method="post" action="${pageContext.request.contextPath}/module/orderextension/stopAllDrugsInGroup.form">
			<input type="hidden" name="groupId" id="stopAllOrderId">
			<input type="hidden" name="patientId" value="${model.patient.patientId}">
			<input type="hidden" name="startDate" id="startDate">
			<input type="hidden" name="returnPage" value="${model.redirect}&patientId=${model.patient.patientId}"/>	
			<table>
				<tr>
					<td class="padding"><spring:message code="orderextension.regimen.stopDate"/>: <openmrs_tag:dateField formFieldName="drugStopAllDate" startValue=""/></td>
					<td class="padding"><spring:message code="orderextension.regimen.stopReason"/>:<openmrs:fieldGen type="org.openmrs.DrugOrder.discontinuedReason" formFieldName="drugStopAllReason" val="" parameters="optionHeader=[blank]|globalProp=concept.reasonOrderStopped" /></td>
				</tr>
				<tr class="repeatCycleDiv">
					<td class="padding"><input type="checkbox" name="repeatCycles" id="repeatCycles" value="repeatCycles"><spring:message code='orderextension.regimen.deleteAllFutureCycles'/></td>
				</tr>
			</table>
		</form>
	</div>
</div>

<div id="deleteAllDrugDialog">	
	<div class="box">
	<div id="openmrs_error" class="openmrs_error"></div>
		<form id="deleteAllDrug" name="deleteAllDrug" method="post" action="${pageContext.request.contextPath}/module/orderextension/deleteAllDrugsInGroup.form">
			<input type="hidden" name="groupId" id="deleteAllOrderId">
			<input type="hidden" name="patientId" value="${model.patient.patientId}">
			<input type="hidden" name="returnPage" value="${model.redirect}&patientId=${model.patient.patientId}"/>	
			<table>
				<tr>
					<td class="padding"><spring:message code="orderextension.regimen.deleteReason"/>: <input type="text" name="deleteReason" id="deleteAllReason" size="100"/></td>
				</tr>
				<tr class="repeatCycleDiv">
					<td class="padding"><input type="checkbox" name="repeatCycles" id="repeatCycles" value="repeatCycles"><spring:message code='orderextension.regimen.deleteAllFutureCycles'/></td>
				</tr>
			</table>
		</form>
	</div>
</div>
