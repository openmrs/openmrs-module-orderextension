<%@page import="org.openmrs.api.context.Context"%>
<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ page import="org.openmrs.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.openmrs.module.orderextension.*" %>

<c:set var="completed" value="true"/>
<c:set var="classifications" value="${model.classifications}"/>
<%
DrugClassificationHelper helper = (DrugClassificationHelper)pageContext.getAttribute("classifications");
%>

<c:forEach items="${classifications.classificationsForRegimenList}" var="classification">

 	<c:choose>
 		<c:when test="${!empty classification}"><div class="regimenClassificationTitle"><c:out value="${classification.displayString}" /></div></c:when>
 		<c:otherwise><c:if test="${status.count > 0}"><div class="regimenClassificationTitle"><spring:message code="orderextension.regimen.otherMedicationTitle" /></div></c:if></c:otherwise>
 	</c:choose>
	
	<%
	Concept classification = (Concept)pageContext.getAttribute("classification");
	RegimenHelper regimenHelper = helper.getRegimenHelperForClassification(classification);
	%>
	
	<c:set var="regimenHelper" value="<%=regimenHelper %>"/>
	
	<c:if test="${regimenHelper.hasCycles}">
		<div class="boxHeader">
			<spring:message code="orderextension.regimen.completedCycleTitle" />
		</div>
		<div class="layer1">
		<div class="box">
		<c:forEach items="${regimenHelper.cycleList}" var="drugGroup">
			<div class="cycleTitleCollapsable">
				<div class="cycleHeader">
					<spring:message code="orderextension.regimen.currentCycleNumber" /> <c:out value="${drugGroup.cycleNumber}"/>
					<spring:message code="general.of" /> <c:out value="${drugGroup.orderSet.name}"/>
				
					<div class="cycleStartdate">
						<spring:message code="general.dateStart"/>: <openmrs:formatDate date="${drugGroup.firstDrugOrderStartDate}" type="medium" />
						<img class="desc" src="/openmrs/moduleResources/orderextension/sort_desc.png"/>
						<img class="asc" src="/openmrs/moduleResources/orderextension/sort_asc.png"/>
					</div>
				</div>	
			</div>
			<div class="content">
				
				<%@ include file="../include/regimenTable.jsp"%> 
				<div class="cycleLinks">
				<openmrs:hasPrivilege privilege="Edit Regimen">
					<input type="button" class="addDrugToGroupButton" value="<spring:message code="orderextension.regimen.addDrugCycle"/>" id="${drugGroup.id},true">
					<input type="button" class="changeStartDateOfGroupButton" value="<spring:message code="orderextension.regimen.changeStartDateOfCycle"/>" id="${drugGroup.id},true,<openmrs:formatDate date="${drugGroup.firstDrugOrderStartDate}"/>">
					<input type="button" class="deleteAllDrugsInGroupButton" value="<spring:message code="orderextension.regimen.deleteDrugCycle"/>" id="${drugGroup.id},true">
				</openmrs:hasPrivilege>
				</div>
			</div>
		</c:forEach>
		</div>
	</div>
	</c:if>
	<c:if test="${regimenHelper.hasDrugGroups}">
		<div class="boxHeader">
			<spring:message code="orderextension.regimen.currentDrugGroupTitle" />
		</div>
		
		<div class="box">
		<c:forEach items="${regimenHelper.drugGroupList}" var="drugGroup">
			<div class="drugGroupTitleCompleted">
				<div class="drugGroupHeader">
					<c:out value="${drugGroup.orderSet.name}"/>
				
					<div class="drugGroupStartdate">
						<spring:message code="general.dateStart"/>: <openmrs:formatDate date="${drugGroup.firstDrugOrderStartDate}" type="medium" />
					</div>
				</div>	
			</div>
			<%@ include file="../include/regimenTable.jsp"%>
			<div class="drugLinks">
				<openmrs:hasPrivilege privilege="Edit Current/Completed Regimen">
					<input type="button" class="addDrugToGroupButton" value="<spring:message code="orderextension.regimen.addDrugGroup"/>" id="${drugGroup.id},false">
					<input type="button" class="changeStartDateOfGroupButton" value="<spring:message code="orderextension.regimen.changeStartDateOfCycle"/>" id="${drugGroup.id},false,<openmrs:formatDate date="${drugGroup.firstDrugOrderStartDate}"/>">
				</openmrs:hasPrivilege>
				<openmrs:hasPrivilege privilege="Edit Regimen">
				<input type="button" class="deleteAllDrugsInGroupButton" value="<spring:message code="orderextension.regimen.deleteAllDrugGroup"/>" id="${drugGroup.id},false">
				</openmrs:hasPrivilege>
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
			<%@ include file="../include/regimenTable.jsp"%>
		</div>
	</c:if>
</c:forEach>



