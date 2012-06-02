<%@ include file="/WEB-INF/view/module/orderextension/include/include.jsp"%>

<%
	DrugRegimen drugGroup = (DrugRegimen)pageContext.getAttribute("drugGroup");
	List<Concept> classifications = regimenHelper.getClassifications(drugGroup, classification);
	pageContext.setAttribute("classifications", classifications);
%>	

<table class="regimenTable">
	<thead>
		<tr class="regimenCurrentHeaderRow">
			<th class="regimenCurrentDrugOrderedHeader"> <spring:message code="Order.item.ordered" /> </th>
			<th class="regimenCurrentDrugDoseHeader"> <spring:message code="DrugOrder.dose"/>/<spring:message code="DrugOrder.units"/> </th>
			<th class="regimenCurrentDrugFrequencyHeader"> <spring:message code="DrugOrder.frequency"/> </th>
			<th class="regimenCurrentDrugRouteHeader"> <spring:message code="orderextension.route"/> </th>
			<th class="regimenCurrentDrugInfusionHeader"><spring:message code="orderextension.regimen.administrationInstructions"/></th>
			<th class="regimenCurrentDrugDateStartHeader"> <spring:message code="general.dateStart"/> </th>
			<th class="regimenCurrentDrugScheduledStopDateHeader"> <spring:message code="DrugOrder.scheduledStopDate"/> </th>
			<th class="regimenCurrentDrugInstructionsHeader"> <spring:message code="orderextension.regimen.instructions" /> </th>
			<openmrs:hasPrivilege privilege="Edit Regimen">
				<th class="regimenCurrentEmptyHeader"> </th>
			<openmrs:hasPrivilege privilege="Edit Current Regimen">
				<th class="regimenCurrentEmptyHeader"> </th>
			</openmrs:hasPrivilege>
				<th class="regimenCurrentEmptyHeader"> </th>
			</openmrs:hasPrivilege>
		</tr>
	</thead>
	<c:forEach items="${classifications}" var="c">

		<c:if test="${c != classification}">
			<tr class="regimenClassificationHeading">
				<td><b><openmrs:format concept="${c}"/></b></td>
			</tr>
		</c:if>
		<%
			Concept c = (Concept)pageContext.getAttribute("c");
			List<DrugOrder> drugOrders = regimenHelper.getRegimensForClassification(drugGroup, c);
			pageContext.setAttribute("drugOrders", drugOrders); 
		%>
		<c:forEach items="${drugOrders}" var="drugOrder">
			<tr class="drugLine">
				<td class="regimenCurrentDrugOrdered"><orderextension:format object="${drugOrder}"/></td>
				<td class="regimenCurrentDrugDose">${drugOrder.dose} ${drugOrder.units}</td>
				<td class="regimenCurrentDrugFrequency">${drugOrder.frequency}</td>
				<td class="regimenCurrentDrugRoute"><orderextension:format object="${drugOrder}" format="route"/></td>
				<td class="regimenCurrentDrugInfusion"><orderextension:format object="${drugOrder}" format="administrationInstructions"/></td>
				<td class="regimenCurrentDrugDateStart"><openmrs:formatDate date="${drugOrder.startDate}" type="medium" /></td>
				<td class="regimenCurrentDrugScheduledStopDate"><openmrs:formatDate date="${drugOrder.autoExpireDate}" type="medium" /></td>
				<td class="regimenCurrentDrugInstructions">${drugOrder.instructions}</td>
				<openmrs:hasPrivilege privilege="Edit Regimen">
					<c:choose>
						<c:when test="${drugOrder.current}">
							<td class="regimenLinks"><input type="button" id="${drugOrder.id}" class="stopButton" value="<spring:message code="orderextension.regimen.stop" />"></td>
						</c:when>
						<c:otherwise>
							<td></td>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${drugOrder.future}">
							<td class="regimenLinks"><input type="button" id="${drugOrder.id}" class="editButton" value="<spring:message code="general.edit" />"></td>
						</c:when>
						<c:otherwise>
							<openmrs:hasPrivilege privilege="Edit Current/Completed Regimen">
								<c:if test="${empty drugOrder.discontinuedDate}">
									<td class="regimenLinks"><input type="button" id="${drugOrder.id}" class="editButton" value="<spring:message code="general.edit" />"></td>	
								</c:if>
								<c:if test="${!empty drugOrder.discontinuedDate}">
									<td></td>
								</c:if>
							</openmrs:hasPrivilege>
						</c:otherwise>
					</c:choose>
					<td class="regimenLinks"><input type="button" id="${drugOrder.id}" class="deleteButton" value="<spring:message code="general.delete" />"></td>
				</openmrs:hasPrivilege>
			</tr>
		</c:forEach>
	</c:forEach>
</table>
