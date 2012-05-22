<%@ include file="/WEB-INF/view/module/orderextension/include/include.jsp"%>

<openmrs:htmlInclude file="/moduleResources/orderextension/fullcalendar/fullcalendar.js" />
<openmrs:htmlInclude file="/moduleResources/orderextension/orderextension.css" />
<openmrs:htmlInclude file="/moduleResources/orderextension/fullcalendar/fullcalendar.css" />

<script type="text/javascript">
jQuery(document).ready(function() {
	 
	jQuery("#calendarContents").hide();
	jQuery("#drugListView").addClass("selectedLink");
	
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
					    color : '#1AAC9B'
					 } 
				</c:forEach>
				<c:forEach items="${model.drugOrdersNonContinuous}" var="drugRegimen" varStatus="loop">
			           <c:if test="${loop.index > 0 || not empty model.cycles}">,</c:if>	
						{
			               title  : '<c:if test="${!empty drugRegimen.drug}"><c:out value="${drugRegimen.drug.name}"/></c:if><c:if test="${empty drugRegimen.drug}"><c:out value="${!drugRegimen.concept.name.name}"/></c:if> <c:out value="${drugRegimen.dose}"/> <c:out value="${drugRegimen.units}"/>',				
			               start  : '<openmrs:formatDate date="${drugRegimen.startDate}"format="yyyy-MM-dd"/>',
			               end : <c:if test="${!empty drugRegimen.discontinuedDate}">'<openmrs:formatDate date="${drugRegimen.discontinuedDate}" format="yyyy-MM-dd"/>'</c:if><c:if test="${!empty drugRegimen.autoExpireDate}">'<openmrs:formatDate date="${drugRegimen.autoExpireDate}" format="yyyy-MM-dd"/>'</c:if>
			            } 
				</c:forEach>
			       ]
		    })
	});  
});
</script>

<div id="regimenViewLink"><a href="#" id="drugListView">Drug List View</a>|<a href="#" id="calendarView">Calendar View</a></div>

<div id="regimenPortlet">
	<div class="regimenPortletCurrent">	
		<div class="boxHeader${model.patientVariation}"><spring:message code="orderextension.regimen.current" /></div>
		<div class="box${model.patientVariation}">
		
			<openmrs:portlet url="patientRegimenCurrent" id="patientRegimenCurrent" patientId="${patient.patientId}" parameters="mode=current"/>	
		</div>			
	</div>
	<br />
	<div class="regimenPortletFuture">
		<div class="boxHeader${model.patientVariation}"><spring:message code="orderextension.regimen.future" /></div>
		<div class="box${model.patientVariation}">
			<openmrs:portlet url="patientRegimenFuture" id="patientRegimenCompleted" patientId="${patient.patientId}" parameters="mode=future"/>
		</div>
	</div>
	<br />
	<div class="regimenPortletCompleted">
		<div class="boxHeader${model.patientVariation}"><spring:message code="orderextension.regimen.completed" /></div>
		<div class="box${model.patientVariation}">
			<openmrs:portlet url="patientRegimenCompleted" id="patientRegimenCompleted" patientId="${patient.patientId}" parameters="mode=completed"/>
		</div>
	</div>
</div>

<div id="calendarContents">
	<div class="boxHeader${model.patientVariation}"><spring:message code="orderextension.regimen.ongoing" /></div>
		<div class="box${model.patientVariation}">
			<table class="regimenTableShort">
				<thead>
					<tr class="regimenCurrentHeaderRow">
						<th class="regimenCurrentDrugOrderedHeader"> <spring:message code="Order.item.ordered" /> </th>
						<th class="regimenCurrentDrugDoseHeader"> <spring:message code="DrugOrder.dose"/>/<spring:message code="DrugOrder.units"/> </th>
						<th class="regimenCurrentDrugFrequencyHeader"> <spring:message code="DrugOrder.frequency"/> </th>
						<th class="regimenCurrentDrugDateStartHeader"> <spring:message code="general.dateStart"/> </th>
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
		<br/>
	<div id="calendar">
	</div>
</div>


