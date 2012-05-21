<%@page import="org.openmrs.api.context.Context"%>
<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ page import="org.openmrs.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.openmrs.module.orderextension.*" %>
<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js" />
<openmrs:htmlInclude file="/moduleResources/orderextension/orderextension.css" />

<c:set var="classifications" value="${model.classifications}"/>

<script>
jQuery(document).ready(function() {
	jQuery('.drugLine').mouseover(function() { jQuery(this).addClass('zebraHover'); }); 
	jQuery('.drugLine').mouseout(function() { jQuery(this).removeClass('zebraHover'); });
	});
</script>
<%
DrugClassificationHelper helper = (DrugClassificationHelper)pageContext.getAttribute("classifications");
%>

<c:forEach items="${classifications.classificationsForRegimenList}" var="classification">

 	<c:if test="${!empty classification}"><div class="regimenClassificationTitle"><c:out value="${classification.name.name}" /></div></c:if>
	
	<%
	Concept classification = (Concept)pageContext.getAttribute("classification");
	RegimenHelper regimenHelper = helper.getRegimenHelperForClassification(classification);
	%>
	
	<c:set var="regimenHelper" value="<%=regimenHelper %>"/>
	
	<c:if test="${regimenHelper.hasCycles}">
		<div class="boxHeader">
			<spring:message code="orderextension.regimen.currentCycleTitle" />
		</div>
		
		<div class="box">
		<c:forEach items="${regimenHelper.cycleList}" var="drugGroup">
			<div class="cycleTitle">
				<div class="cycleHeader">
					<spring:message code="orderextension.regimen.currentCycleNumber" /> <c:out value="${drugGroup.cycleNumber}"/>
					<spring:message code="general.of" /> <c:out value="${drugGroup.orderSet.name}"/>
					<div class="cycleStartdate">
						<spring:message code="general.dateStart"/>: <openmrs:formatDate date="${drugGroup.firstDrugOrderStartDate}" type="medium" />
					</div>
				</div>
			</div>
			<%@ include file="include/regimenTable.jsp"%> 
			<div class="cycleLinks">
				<button id="addDrugGroupLink"><spring:message code="orderextension.regimen.addDrugCycle"/></button>
				<button id="stopDrugGroupLink"><spring:message code="orderextension.regimen.stopAllDrugCycle"/></button>
			</div>
		</c:forEach>
		</div>
	</c:if>
	<c:if test="${regimenHelper.hasDrugGroups}">
		<div class="boxHeader">
			<spring:message code="orderextension.regimen.currentDrugGroupTitle" />
		</div>
		
		<div class="box">
		<c:forEach items="${regimenHelper.drugGroupList}" var="drugGroup">
			<div class="drugGroupTitle">
				<div class="drugGroupHeader">
					<c:out value="${drugGroup.orderSet.name}"/>
					<div class="drugGroupStartdate">
						<spring:message code="general.dateStart"/>: <openmrs:formatDate date="${drugGroup.firstDrugOrderStartDate}" type="medium" />
					</div>
				</div>
			</div>
			<%@ include file="include/regimenTable.jsp"%>
			<div class="drugLinks">
				<button id="addDrugLink"><spring:message code="orderextension.regimen.addDrugGroup"/></button>
				<button id="stopDrugLink"><spring:message code="orderextension.regimen.stopAllDrugGroup"/></button>
			</div>
		</c:forEach>
		</div>
	</c:if>
	<c:if test="${regimenHelper.hasOtherMedications}">
		<c:if test="${regimenHelper.hasCycles || regimenHelper.hasDrugGroups}">
			<div class="boxHeader">
				<spring:message code="orderextension.regimen.otherMedicationTitle" />
			</div>
		</c:if>
		<div class="box">
		<c:set var="drugGroup" value="${null}"/>
			<%@ include file="include/regimenTable.jsp"%>
		</div>
	</c:if>
</c:forEach>



