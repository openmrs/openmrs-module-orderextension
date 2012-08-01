<%@ include file="/WEB-INF/view/module/orderextension/include/include.jsp"%>

<openmrs:htmlInclude file="/moduleResources/orderextension/fullcalendar/fullcalendar.js" />
<openmrs:htmlInclude file="/moduleResources/orderextension/orderextension.css" />
<openmrs:htmlInclude file="/moduleResources/orderextension/fullcalendar/fullcalendar.css" />
<openmrs:htmlInclude file="/moduleResources/orderextension/chosen/chosen.jquery.js" />
<openmrs:htmlInclude file="/moduleResources/orderextension/jquery.jqprint-0.3.js" />
<openmrs:htmlInclude file="/moduleResources/orderextension/chosen/chosen.css" />

<script type="text/javascript">
jQuery(document).ready(function() {
	 
	jQuery("#patientHeaderRegimen").hide();
	jQuery("#patientHeaderObsRegimen").html("Regimen: ${model.regimenHeading}");
	
	jQuery("#calendarContents").hide();
	jQuery("#printCalendar").hide();
	
	jQuery("#drugListView").addClass("selectedLink");
	
	jQuery('.error').hide();
	
	
	jQuery(".drugDetails").hide();
	
	jQuery("#drugCombo").chosen({allow_single_deselect: true});
	jQuery("#orderSet").chosen({allow_single_deselect: true});
		
	jQuery("#drugListView").click(function()
	{
		  jQuery("#calendarContents").hide();
		  jQuery("#regimenPortlet").show(); 
		  
		  jQuery("#drugListView").addClass("selectedLink");
		  jQuery("#calendarView").removeClass("selectedLink");
	});
	  
	jQuery("#calendarView").click(function()
	{
		  jQuery("#calendarContents").show();
		  jQuery("#regimenPortlet").hide(); 
		  
		  jQuery("#drugListView").removeClass("selectedLink");
		  jQuery("#calendarView").addClass("selectedLink");
		  
		  jQuery('#calendar').fullCalendar({
			  events: [
				<c:forEach items="${model.cycles}" var="cycle" varStatus="loop">
					<c:if test="${loop.index > 0}">,</c:if>	
						{
					    title  : '<spring:message code="orderextension.regimen.currentCycleNumber" /> <c:out value="${cycle.cycleNumber}"/> <spring:message code="general.of" /> <c:out value="${cycle.orderSet.name}"/>',				
					    start  : '<openmrs:formatDate date="${cycle.firstDrugOrderStartDate}"format="yyyy-MM-dd"/>',
					    end :'<openmrs:formatDate date="${cycle.lastDrugOrderEndDate}" format="yyyy-MM-dd"/>',
					    color : '#1AAC9B',
					    textColor: '#000000'
					 } 
				</c:forEach>
				<c:forEach items="${model.fixedLengthRegimen}" var="regimen" varStatus="loop">
					<c:if test="${loop.index > 0 || not empty model.cycles}">,</c:if>	
						{
					    title  : '<c:out value="${regimen.orderSet.name}"/>',				
					    start  : '<openmrs:formatDate date="${regimen.firstDrugOrderStartDate}"format="yyyy-MM-dd"/>',
					    end :'<openmrs:formatDate date="${regimen.lastDrugOrderEndDate}" format="yyyy-MM-dd"/>',
					    color : '#1AAC9B',
					    textColor: '#000000'
					 } 
				</c:forEach>
				<c:forEach items="${model.drugOrdersNonContinuous}" var="drugRegimen" varStatus="loop">
			           <c:if test="${loop.index > 0 || not empty model.cycles || not empty model.fixedLengthRegimen}">,</c:if>	
						{
			               title  : '<c:if test="${!empty drugRegimen.drug}"><c:out value="${drugRegimen.drug.name}"/></c:if><c:if test="${empty drugRegimen.drug}"><c:out value="${drugRegimen.concept.displayString}"/></c:if> <c:out value="${drugRegimen.dose}"/> <c:out value="${drugRegimen.units}"/> <c:if test="${!empty drugRegimen.route}"><c:out value="${drugRegimen.route.displayString}"/></c:if>',				
			               start  : '<openmrs:formatDate date="${drugRegimen.startDate}"format="yyyy-MM-dd"/>',
			               color :'#99CCFF',
			               textColor :'#000000',
			               end : <c:choose><c:when test="${!empty drugRegimen.discontinuedDate}">'<openmrs:formatDate date="${drugRegimen.discontinuedDate}" format="yyyy-MM-dd"/>'</c:when><c:otherwise><c:if test="${!empty drugRegimen.autoExpireDate}">'<openmrs:formatDate date="${drugRegimen.autoExpireDate}" format="yyyy-MM-dd"/>'</c:if></c:otherwise></c:choose>
			            } 
				</c:forEach>
			       ]
		    });
		  
		  	jQuery('#printCalendarContents').fullCalendar({ 
				height: 900,
				header: {
					    left:   'title',
					    center: '',
					    right:  ''
				},
				events: [
					<c:forEach items="${model.cycles}" var="cycle" varStatus="loop">
						<c:if test="${loop.index > 0}">,</c:if>	
							{
						    title  : '<spring:message code="orderextension.regimen.currentCycleNumber" /> <c:out value="${cycle.cycleNumber}"/> <spring:message code="general.of" /> <c:out value="${cycle.orderSet.name}"/>',				
						    start  : '<openmrs:formatDate date="${cycle.firstDrugOrderStartDate}"format="yyyy-MM-dd"/>',
						    end :'<openmrs:formatDate date="${cycle.lastDrugOrderEndDate}" format="yyyy-MM-dd"/>',
						    textColor: '#666666',
						    color: '#99CCFF'
						 } 
					</c:forEach>
					<c:forEach items="${model.fixedLengthRegimen}" var="regimen" varStatus="loop">
						<c:if test="${loop.index > 0 || not empty model.cycles}">,</c:if>	
							{
							title  : '<c:out value="${regimen.orderSet.name}"/>',		
						    start  : '<openmrs:formatDate date="${regimen.firstDrugOrderStartDate}"format="yyyy-MM-dd"/>',
						    end :'<openmrs:formatDate date="${regimen.lastDrugOrderEndDate}" format="yyyy-MM-dd"/>',
						    textColor: '#666666',
						    color: '#99CCFF'
						 } 
					</c:forEach>
					<c:forEach items="${model.drugOrdersNonContinuous}" var="drugRegimen" varStatus="loop">
				           <c:if test="${loop.index > 0 || not empty model.cycles || not empty model.fixedLengthRegimen}">,</c:if>	
							{
				               title  : '<c:if test="${!empty drugRegimen.drug}"><c:out value="${drugRegimen.drug.name}"/></c:if><c:if test="${empty drugRegimen.drug}"><c:out value="${drugRegimen.concept.displayString}"/></c:if> <c:out value="${drugRegimen.dose}"/> <c:out value="${drugRegimen.units}"/> <c:if test="${!empty drugRegimen.route}"><c:out value="${drugRegimen.route.displayString}"/></c:if>',				
				               start  : '<openmrs:formatDate date="${drugRegimen.startDate}"format="yyyy-MM-dd"/>',
				               textColor :'#000000',
				       		   color :'#CCCCCC',
				               end : <c:choose><c:when test="${!empty drugRegimen.discontinuedDate}">'<openmrs:formatDate date="${drugRegimen.discontinuedDate}" format="yyyy-MM-dd"/>'</c:when><c:otherwise><c:if test="${!empty drugRegimen.autoExpireDate}">'<openmrs:formatDate date="${drugRegimen.autoExpireDate}" format="yyyy-MM-dd"/>'</c:if></c:otherwise></c:choose>
				            } 
					</c:forEach>
				       ]
			    });
		  	jQuery('#printCalendarContents').hide();
	}); 
	
	jQuery(".monthPicker").datepicker({
        dateFormat: 'MM yy',
        changeMonth: true,
        changeYear: true,
        showButtonPanel: true,

        onClose: function(dateText, inst) {
            var month = jQuery("#ui-datepicker-div .ui-datepicker-month :selected").val();
            var year = jQuery("#ui-datepicker-div .ui-datepicker-year :selected").val();
            jQuery(this).val(jQuery.datepicker.formatDate('MM yy', new Date(year, month, 1)));
        }
    });

	jQuery(".monthPicker").focus(function () {
		jQuery(".ui-datepicker-calendar").hide();
		jQuery("#ui-datepicker-div").position({
            my: "center top",
            at: "center bottom",
            of: jQuery(this)
        });
    });
	
	jQuery('#printButton').click(function(){ 
		jQuery('.openmrs_error').hide();
		jQuery('#printDialog').dialog('open');
	});
	
	jQuery('#printDialog').dialog({
		position: 'middle',
		autoOpen: false,
		modal: true,
		title: '<spring:message code="orderextension.regimen.printCalendar" javaScriptEscape="true"/>',
		width: '80%',
		zIndex: 100,
		buttons: { '<spring:message code="orderextension.print"/>': function() { printRoadMap(); jQuery(this).dialog("close"); },
				   '<spring:message code="general.cancel"/>': function() { jQuery(this).dialog("close"); }
		}
	});
	
	
	jQuery('#addMedicationButton').click(function(){ 
		jQuery('#addNewRegimenDialog').dialog('open');
		jQuery(".newCycleNumber").hide();
		includeCycle();
		
		jQuery("#addOrderSetButton").attr("disabled", "disabled");
		jQuery("#addIndividualDrugButton").removeAttr("disabled");
		
		jQuery('#addOrderSet').show();
		jQuery('#addIndividualDrug').hide();
		jQuery('.openmrs_error').hide();
	});

	jQuery('#addNewRegimenDialog').dialog({
		position: 'middle',
		autoOpen: false,
		modal: true,
		title: '<spring:message code="orderextension.regimen.addMedication" javaScriptEscape="true"/>',
		height: 480,
		width: '100%',
		zIndex: 100,
		buttons: { '<spring:message code="general.add"/>': function() { handleAddMedication(); },
				   '<spring:message code="general.cancel"/>': function() { $j(this).dialog("close"); }
		}
	});
	
	jQuery('#addOrderSetButton').click(function(){ 
		jQuery('#addOrderSet').show();
		jQuery('#addIndividualDrug').hide();
		
		jQuery("#addOrderSetButton").attr("disabled", "disabled");
		jQuery("#addIndividualDrugButton").removeAttr("disabled");
		
		jQuery('.openmrs_error').hide();
	});
	
	jQuery('#addIndividualDrugButton').click(function(){ 
		jQuery('#addOrderSet').hide();
		jQuery('#addIndividualDrug').show();
		
		jQuery("#addIndividualDrugButton").attr("disabled", "disabled");
		jQuery("#addOrderSetButton").removeAttr("disabled");
		
		jQuery('.openmrs_error').hide();
		
		fetchDrugs();
	});
	
	jQuery("#orderSet").change(function(){ 
		includeCycle();
	});
});

function handleAddMedication() {
	
	if(jQuery('#addOrderSet').is(":visible") == true)
	{
		var error = "";
		
		var selectedIndex = jQuery("#orderSet").attr("selectedIndex");
		if(selectedIndex == 0)
		{
			error = " <spring:message code='orderextension.regimen.orderSetError' /> ";
			
		}
		else
		{
			var startDate = jQuery("#startDateSet").val();
			if(startDate == "")
			{
				error = error + " <spring:message code='orderextension.regimen.startDateError' /> ";
			}
		}
		
		if(error != "")
		{
			jQuery('.openmrs_error').show();
			jQuery('.openmrs_error').html(error);
		}
		else
		{
			jQuery('#addOrderSet').submit();
		}
	}
	if(jQuery('#addIndividualDrug').is(":visible") == true)
	{
		var error = validAddDrugPanel();
		
		if(error != "")
		{
			jQuery('.openmrs_error').show();
			jQuery('.openmrs_error').html(error);
		}
		else
		{
			jQuery('#addIndividualDrug').submit();
		}
	}
}

function includeCycle() {
	var selectedIndex = jQuery("#orderSet").attr("selectedIndex");
	
	var cycle = [];
	<c:forEach items="${orderSets}" var="orderSet">
		cycle.push("${orderSet.cyclical}");
	</c:forEach>
	
	if(selectedIndex > 0 && cycle[selectedIndex-1] == "true")
	{
		jQuery(".newCycleNumber").show();
	}
	if(selectedIndex == 0 || cycle[selectedIndex-1] == "false")
	{
		jQuery(".newCycleNumber").hide();
	}
}

var drugDetail;

function fetchDrugs() {
	var selected = jQuery('#drugCombo').val();
	
	if(jQuery("#drugCombo").attr("selectedIndex") > 0)
	{
		var url = "${pageContext.request.contextPath}/module/orderextension/getDrugsByName.form?name=" + selected
		jQuery.getJSON(url, function(result) 
		{ 
			drugDetail = result;
			
			var html = "<spring:message code='orderextension.regimen.form'/>*: <select id='drug' name='drug' onChange='updateDrugInfo()'";
			
			if(result.length == 1)
			{
				html = html +  "style='display:none'";
			}
			
			html = html + ">";
			
			var i=0;
			for (i=0;i< result.length;i++)
			{
				html = html + "<option value=" + result[i].id + ">" + result[i].name + " " + result[i].doseStrength + " " + result[i].units + "</option>";
			}
			
			html = html + "</select>";
			
			if(result.length == 1)
			{
				html = html + result[0].name + " " + result[0].doseStrength + " " + result[0].units;
			}
			
			jQuery("#drugName").html(html);
			
			updateDrugInfo();
		});
		
		jQuery(".drugDetails").show();
	}
	else
	{
		jQuery(".drugDetails").hide();
		jQuery("#drugName").html("");
	}
}

function updateDrugInfo() {
	
	var route = "";
	var units = "";
	
	var index = jQuery('#drug').attr("selectedIndex");
	if(index != null && index >=0)
	{
		if(drugDetail[index].route != "")
		{
			route =  "<spring:message code='orderextension.regimen.route'/>" + ": " + drugDetail[index].route;
		}
		units = " " + drugDetail[index].doseForm;
	}
	
	jQuery("#routeInfo").html(route);
	jQuery("#units").html(units);
}

function getIndicationClassifications() {

	var selected = jQuery('#indicationCombo').val();
	
	if(jQuery('#indicationCombo').attr("selectedIndex") > 0) {
		var url = "${pageContext.request.contextPath}/module/orderextension/getClassificationsByIndication.form?id=" + selected
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
			jQuery("#indClassification").html(html);
		});
	}
	else {
		jQuery("#indClassification").html("");
	}
}

function validAddDrugPanel() {
	
	var error = '';
	
	var selectedIndex = jQuery("#drugCombo").attr("selectedIndex");
	if(selectedIndex == 0)
	{
		error = " <spring:message code='orderextension.regimen.drugError' /> ";
	}
	else
	{
		var startDate = jQuery("#startDateDrug").val();

		if(startDate == "")
		{
			error = error + " <spring:message code='orderextension.regimen.startDateError' /> ";
		}
		
		var dose = jQuery("#dose").val();
		
		if(dose == "")
		{
			error = error + " <spring:message code='orderextension.regimen.doseError' /> ";

		}
	}
	
	return error;
}

function printRoadMap() {
	
	jQuery('#printCalendarContents').show();
	
	var startMonth = jQuery("#startMonth").val();
	var endMonth = jQuery("#endMonth").val();
	
	var error = "";
	
	if(startMonth == ""){
		error = error + " <spring:message code='orderextension.regimen.startMonthError' /> ";	
	}
	if(endMonth == "") {
		error = error + " <spring:message code='orderextension.regimen.endMonthError' /> ";	
	}
	
	if(error != ""){
		jQuery('.openmrs_error').show();
		jQuery('.openmrs_error').html(error);
	}
	else {
		
		var monthsToIncrement = monthDiff(startMonth, endMonth);
		
		var d1Parts = startMonth.split(" ");
		var d1String = d1Parts[0] + " 1, " + d1Parts[1];
	    var date = new Date(d1String);
	
		var index;
		
		jQuery("#printCalendar").html('<h1><spring:message code="orderextension.regimen.printCalendarTitle" /> - <c:out value="${patient.givenName}"/> <c:out value="${patient.familyName}"/></h1>');
		
		for (index = 0; index < monthsToIncrement; index++) {
	
			jQuery(jQuery("#printCalendarContents").fullCalendar('gotoDate',  date)).clone().appendTo("#printCalendar");
		
			date.setMonth(date.getMonth() + 1);
		}
		jQuery("#printCalendar").show();
		jQuery('#printCalendarContents').fullCalendar('render');
		jQuery('#printCalendar').jqprint();
		jQuery("#printCalendar").hide();
		jQuery('#printCalendarContents').hide();
	}
	
	function monthDiff(date1, date2) {
	    
		var d1Parts = date1.split(" ");
		var d1String = d1Parts[0] + " 1, " + d1Parts[1];
	    var d1 = new Date(d1String);
	    
	    var d2Parts = date2.split(" ");
	    var d2String = d2Parts[0] + " 1, " + d2Parts[1];
	    var d2 = new Date(d2String);
	   
	    return ((d2.getFullYear() * 12 + d2.getMonth()) - (d1.getFullYear() * 12 + d1.getMonth())) + 1;
	}
}
</script>

<openmrs:hasPrivilege privilege="Edit Regimen">
<div id="addMedicationLink"><input type="button" id="addMedicationButton" value="<spring:message code="orderextension.regimen.addMedication" />"></div>
</openmrs:hasPrivilege>
<div id="regimenViewLink"><a href="#" id="drugListView">Drug List View</a> <openmrs:hasPrivilege privilege="View Calendar Regimen">|<a href="#" id="calendarView">Calendar View</a></openmrs:hasPrivilege> </div>

<div id="regimenPortlet">
	<div class="regimenPortletCurrent">	
		<div class="boxHeader${model.patientVariation}"><spring:message code="orderextension.regimen.current" /></div>
		<div class="box${model.patientVariation}">
			<openmrs:portlet url="currentregimen" moduleId="orderextension" id="patientRegimenCurrent" patientId="${patient.patientId}" parameters="mode=current|redirect=${model.redirect}"/>	
		</div>			
	</div>
	<br />
	<div class="regimenPortletFuture">
		<div class="boxHeader${model.patientVariation}"><spring:message code="orderextension.regimen.future" /></div>
		<div class="box${model.patientVariation}">
			<openmrs:portlet url="futureregimen" moduleId="orderextension" id="patientRegimenCompleted" patientId="${patient.patientId}" parameters="mode=future|redirect=${model.redirect}"/>
		</div>
	</div>
	<br />
	<div class="regimenPortletCompleted">
		<div class="boxHeader${model.patientVariation}"><spring:message code="orderextension.regimen.completed" /></div>
		<div class="box${model.patientVariation}">
			<openmrs:portlet url="completedregimen" moduleId="orderextension" id="patientRegimenCompleted" patientId="${patient.patientId}" parameters="mode=completed|redirect=${model.redirect}"/>
		</div>
	</div>
</div>

<div id="calendarContents">
	<div id="printButton"><input type="button" id="printButton" value='<spring:message code="orderextension.regimen.printCalendar" />'></div>
	<div class="regimenShort">
	<div class="boxHeader${model.patientVariation}"><spring:message code="orderextension.regimen.ongoing" /></div>
		<div class="box${model.patientVariation}">
			<table class="regimenTableShort">
				<thead>
					<tr class="regimenCurrentHeaderRow">
						<th> <spring:message code="Order.item.ordered" /> </th>
						<th> <spring:message code="DrugOrder.dose"/>/<spring:message code="DrugOrder.units"/> </th>
						<th> <spring:message code="DrugOrder.frequency"/> </th>
						<th> <spring:message code="general.dateStart"/> </th>
					</tr>
				</thead>
				<c:forEach items="${model.drugOrdersContinuous}" var="regimen">
					<tr class="drugLine">
						<td class="regimenCurrentDrugOrdered"><orderextension:format object="${regimen}"/></td>
						<td class="regimenCurrentDrugDose"><c:out value="${regimen.dose}"/><c:out value="${regimen.drug.units}"/></td>
						<td class="regimenCurrentDrugFrequency"><c:out value="${regimen.frequency}"/></td>
						<td class="regimenCurrentDrugDateStart"><openmrs:formatDate date="${regimen.startDate}" type="medium" /></td>
						<td class="regimenCurrentDrugScheduledStopDate"><openmrs:formatDate date="${regimen.autoExpireDate}" type="medium" /></td>
				
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
		<br/>
	<div id="calendar">
	</div>
</div>


<div id="printCalendarContents"></div>
<div id="printCalendar">
</div>

<div id="printDialog">
	<div class="box">
		<div id="openmrs_error" class="openmrs_error"></div>
		<label for="month">Start Month: </label><input type="text" id="startMonth" name="startMonth" class="monthPicker" />
		<label for="month">End Month: </label><input type="text" id="endMonth" name="endMonth" class="monthPicker" />
	</div>
</div>

<div id="addNewRegimenDialog">

	<div id="buttonDiv">
		<input type="button" id="addOrderSetButton" value="<spring:message code="orderextension.regimen.chooseOrderSet" />">
		<input type="button" id="addIndividualDrugButton" value="<spring:message code="orderextension.regimen.addMedication" />">
	</div>
	
	<div class="box">
		<div id="openmrs_error" class="openmrs_error"></div>
		<form id="addOrderSet" name="addMedication" method="post" action="${pageContext.request.contextPath}/module/orderextension/addOrderSet.form">
			<input type="hidden" name="patientId" value="${patient.patientId}">
			<input type="hidden" name="returnPage" value="${model.redirect}?patientId=${patient.patientId}"/>
			<table>
				<tr>
					<td class="padding">
						<div id="orderSetLabel"><spring:message code="orderextension.regimen.chooseOrderSet" />*:</div></td>
						<td>
						<select name="orderSet" id="orderSet" data-placeholder="<spring:message code="orderextension.regimen.chooseOption" />" style="width:450px;">
							<option value="" selected="selected"></option>
							<c:forEach items="${orderSets}" var="orderSet">
								<option value="${orderSet.id}">
									<c:choose>
									 	<c:when test="${!empty orderSet.indication}">
									 		&nbsp; ${orderSet.indication.displayString} 
									 	</c:when>
									 	<c:otherwise>
											&nbsp; <spring:message code="orderextension.regimen.unclassified" />
										</c:otherwise>
									</c:choose>
								 - ${orderSet.name}</option>
							</c:forEach>
						</select>
					</td class="padding">
					<td class="padding">
						<spring:message code="orderextension.orderset.field.relativeStartDay" />*:  <openmrs_tag:dateField formFieldName="startDateSet" startValue=""/>
					</td>
					<td class="padding newCycleNumber">
						<spring:message code="orderextension.regimen.numberCycles" />:  <input type="text" name="numCycles" size="10"/>
					</td>
				</tr>
			</table>
		</form>
		<form id="addIndividualDrug" name="addIndividualDrug" method="post" action="${pageContext.request.contextPath}/module/orderextension/addDrugOrder.form">
			<input type="hidden" name="patientId" value="${patient.patientId}">
			<input type="hidden" name="returnPage" value="${model.redirect}?patientId=${patient.patientId}"/>	
			<table>
				<tr>
					<td class="padding"><spring:message code="orderextension.regimen.individualDrug" />*: </td>
					<td>	<select name="drugCombo" id="drugCombo" data-placeholder="<spring:message code="orderextension.regimen.chooseOption" />" style="width:350px;" onChange="fetchDrugs()">
							<option value="" selected="selected"></option>
							
							<c:forEach items="${model.drugs}" var="drug">
								<option value="${drug}">${drug}</option>
							</c:forEach>
						</select>
					</td>
					<td id="drugName" class="padding"></td>
					<td id="routeInfo" class="padding"></td>
				</tr>
			</table>
			<table>
				<tr class="drugDetails">
					<th class="padding"><spring:message code="orderextension.regimen.patientPrescription" />:</th>
				</tr>
				<tr class="drugDetails">
					<td class="padding"><spring:message code="DrugOrder.dose" />*:  <input type="text" name="dose" id="dose" size="10"/><span id="units"></span></td>
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
					<td class="padding"><spring:message code="orderextension.orderset.field.relativeStartDay" />*:  <openmrs_tag:dateField formFieldName="startDateDrug" startValue=""/></td>
					<td class="padding"><spring:message code="orderextension.regimen.stopDate" />:  <openmrs_tag:dateField formFieldName="stopDateDrug" startValue=""/></td>	
				</tr>
			</table>
		    
		    <openmrs:hasPrivilege privilege="Edit Current/Complete Regimen">
		    <table>
				<tr	class="drugDetails">
					<td class="padding"><spring:message code="orderextension.regimen.reasonForPrescription" />:
						<select name="indication" id="indicationCombo" onChange="getIndicationClassifications()">
							<option value="" selected="selected"></option>
							<c:forEach items="${model.indications}" var="indication">
								<option value="${indication.id}">${indication.displayString}</option>
							</c:forEach>
						</select>
					</td>
					<td id="indClassification" class="padding"></td>
				</tr>
			</table>
			</openmrs:hasPrivilege>
			<table>	
				<tr class="drugDetails">
					<td class="padding topAlignment"><spring:message code="orderextension.regimen.administrationInstructions"/>: <textarea rows="2" cols="40" name="adminInstructions" id="adminInstructions"></textarea></td>
					<td class="padding topAlignment"><spring:message code="orderextension.regimen.instructions" />: <textarea rows="2" cols="40" name="instructions" id="instructions"></textarea></td>
				</tr>							
			</table> 
		</form>
	</div>
</div>

