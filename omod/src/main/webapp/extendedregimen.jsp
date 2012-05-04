<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js" />
<openmrs:htmlInclude file="/moduleResources/orderextension/fullcalendar/fullcalendar.js" />
<openmrs:htmlInclude file="/moduleResources/orderextension/orderextension.css" />
<openmrs:htmlInclude file="/moduleResources/orderextension/fullcalendar/fullcalendar.css" />

<script type="text/javascript">
jQuery(document).ready(function() {
	 
	jQuery("#calendar").hide();
	jQuery("#drugListView").addClass("selectedLink");
	
	jQuery("#drugListView").click(function()
	{
		  jQuery("#calendar").hide();
		  jQuery("#regimenPortlet").show(); 
		  
		  jQuery("#drugListView").addClass("selectedLink");
		  jQuery("#calendarView").removeClass("selectedLink");
	});
	  
	jQuery("#calendarView").click(function()
	{
		  jQuery("#calendar").show();
		  jQuery("#regimenPortlet").hide(); 
		  
		  jQuery("#drugListView").removeClass("selectedLink");
		  jQuery("#calendarView").addClass("selectedLink");
		  
		  jQuery('#calendar').fullCalendar({
			  events: [
				<c:forEach items="${model.allDrugOrders}" var="drugRegimen" varStatus="loop">
			           <c:if test="${loop.index > 0}">,</c:if>	
						{
			               title  : '<c:if test="${!empty drugRegimen.drug}"><c:out value="${drugRegimen.drug.name}"/></c:if><c:if test="${empty drugRegimen.drug}"><c:out value="${!drugRegimen.concept.name.name}"/></c:if> <c:out value="${drugRegimen.dose}"/> <c:out value="${drugRegimen.units}"/>',				
			               start  : '<openmrs:formatDate date="${drugRegimen.startDate}"format="yyyy-MM-dd"/>',
			               end : <c:choose><c:when test="${!empty drugRegimen.discontinuedDate || !empty drugRegimen.autoExpireDate}"><c:if test="${!empty drugRegimen.discontinuedDate}">'<openmrs:formatDate date="${drugRegimen.discontinuedDate}" format="yyyy-MM-dd"/>'</c:if>
			               	     <c:if test="${!empty drugRegimen.autoExpireDate}">'<openmrs:formatDate date="${drugRegimen.autoExpireDate}" format="yyyy-MM-dd"/>'</c:if>
			               	 </c:when>
			               	 <c:otherwise>
			               		'<openmrs:formatDate date="${model.futureDate}" format="yyyy-MM-dd"/>'
			               	</c:otherwise>
			               </c:choose>
			              
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

<div id="calendar">
</div>

