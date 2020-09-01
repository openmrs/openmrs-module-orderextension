<%@ include file="/WEB-INF/view/module/orderextension/include/include.jsp"%>

<%
	DrugRegimen drugGroup = (DrugRegimen)pageContext.getAttribute("drugGroup");
	List<Integer> cycleDays = regimenHelper.getDrugStartDates(drugGroup);
	pageContext.setAttribute("cycleDays", cycleDays);
%>	

<table class="regimenTable">
	<thead>
		<tr class="regimenCurrentHeaderRow">
			<th class="regimenCurrentDrugOrderedHeader"> <spring:message code="Order.item.ordered" /> </th>
			<th class="regimenCurrentDrugDoseHeader"> <spring:message code="DrugOrder.dose"/>/<spring:message code="DrugOrder.units"/> </th>
			<th class="regimenCurrentDrugRouteHeader"> <spring:message code="orderextension.route"/> </th>
			<th class="regimenCurrentDrugFrequencyHeader"> <spring:message code="DrugOrder.frequency"/> </th>
			<th class="regimenCurrentDrugLengthHeader"> <spring:message code="orderextension.length"/> </th>
			<th class="regimenCurrentDrugInfusionHeader"><spring:message code="orderextension.regimen.administrationInstructions"/></th>
			<th class="regimenCurrentDrugDateStartHeader"> <spring:message code="general.dateStart"/> </th>
			<th class="regimenCurrentDrugScheduledStopDateHeader"><c:choose><c:when test="${completed eq 'true'}"> <spring:message code="DrugOrder.actualStopDate"/> </c:when><c:otherwise><spring:message code="DrugOrder.scheduledStopDate"/></c:otherwise></c:choose></th>
			<th class="regimenCurrentDrugInstructionsHeader"> <spring:message code="orderextension.regimen.instructions" /> </th>
			<c:if test="${completed eq 'true'}"><th class="regimenDiscontinuedReasonHeader"> <spring:message code="DrugOrder.discontinuedReason"/> </th></c:if>
			<c:if test="${model.readOnly != 'true'}">
			<c:choose>
				<c:when test="${empty drugGroup}">
				<openmrs:hasPrivilege privilege="Edit Regimen"> 
					<th class="regimenCurrentEmptyHeader"> </th>
				</openmrs:hasPrivilege> 
			    <openmrs:hasPrivilege privilege="Edit Current/Completed Regimen">
					<th class="regimenCurrentEmptyHeader"> </th>
				</openmrs:hasPrivilege>
			    <openmrs:hasPrivilege privilege="Edit Regimen"> 
					<th class="regimenCurrentEmptyHeader"> </th>
				</openmrs:hasPrivilege>
				
				</c:when>
				<c:otherwise>
				
					<openmrs:hasPrivilege privilege="Edit Current/Completed Regimen">
						<th class="regimenCurrentEmptyHeader"> </th>
						<th class="regimenCurrentEmptyHeader"> </th>
						<th class="regimenCurrentEmptyHeader"> </th>
					</openmrs:hasPrivilege>
				</c:otherwise>
			</c:choose>
			</c:if>
		</tr>
	</thead>

	<c:forEach items="${cycleDays}" var="cd" varStatus="count">
		<c:if test="${fn:length(cycleDays) > 1}">
			<tr class="cycleHeading">
				<td colspan="1"><b><spring:message code="orderextension.regimen.day" />: <c:out value="${cd}"/></b></td>

				<td colspan="8">
				 	<c:if test="${count.index > 0 }">
				 	<%
				 		Integer currDay = (Integer)pageContext.getAttribute("cd");
						Date currDate = regimenHelper.getCycleDate(drugGroup, currDay);
						pageContext.setAttribute("cycleDate", currDate);
				 	%>
				 		<input type="button" class="changeStartDateOfPartGroupButton" value="<spring:message code="orderextension.regimen.changeStartDateOfPartCycle"/>" id="${drugGroup.id},true,<openmrs:formatDate date="${cycleDate}"/>,${cd}">
					</c:if>
				</td>

				<c:if test="${completed eq 'true'}"><td></td></c:if>

				<c:if test="${model.readOnly != 'true'}">
			<c:choose>
				<c:when test="${empty drugGroup}">
				<openmrs:hasPrivilege privilege="Edit Regimen">
					<td></td>
				</openmrs:hasPrivilege>
			    <openmrs:hasPrivilege privilege="Edit Current/Completed Regimen">
					<td></td>
				</openmrs:hasPrivilege>
			    <openmrs:hasPrivilege privilege="Edit Regimen">
					<td></td>
				</openmrs:hasPrivilege>

				</c:when>
				<c:otherwise>

					<openmrs:hasPrivilege privilege="Edit Current/Completed Regimen">
						<td></td>
						<td></td>
						<td></td>
					</openmrs:hasPrivilege>
				</c:otherwise>
			</c:choose>
			</c:if>
			</tr>
		</c:if>

		<%
		Integer day = (Integer)pageContext.getAttribute("cd");
		List<Concept> classifications = regimenHelper.getClassifications(drugGroup, classification, day);
		pageContext.setAttribute("classifications", classifications);
		%>
		<c:forEach items="${classifications}" var="c">

			<c:if test="${c != classification}">
				<tr class="regimenClassificationHeading">
					<td><b><openmrs:format concept="${c}"/></b></td>
				</tr>
			</c:if>
			<%
				Concept c = (Concept)pageContext.getAttribute("c");
				List<DrugOrder> drugOrders = regimenHelper.getRegimensForClassification(drugGroup, c, day);
				pageContext.setAttribute("drugOrders", drugOrders);
			%>
			<c:forEach items="${drugOrders}" var="drugOrder">

				<orderextension:orderStatusCheck order="${drugOrder}" statusCheck="past" var="orderIsPast"/>
				<orderextension:orderStatusCheck order="${drugOrder}" statusCheck="current" var="orderIsCurrent"/>
				<orderextension:orderStatusCheck order="${drugOrder}" statusCheck="future" var="orderIsFuture"/>

				<c:choose>
					<c:when test="${!empty drugOrder.dateStopped && completed ne 'true'}">
						<tr class="drugLineRed">
					</c:when>
					<c:otherwise>
						<tr class="drugLine">
					</c:otherwise>
				</c:choose>

					<td class="regimenCurrentDrugOrdered"><orderextension:format object="${drugOrder}"/></td>
					<td class="regimenCurrentDrugDose"><orderextension:format object="${drugOrder.dose}"/> <orderextension:format object="${drugOrder.doseUnits}"/></td>
					<td class="regimenCurrentDrugRoute"><orderextension:format object="${drugOrder}" format="route"/></td>
					<td class="regimenCurrentDrugFrequency">${drugOrder.frequency} <c:if test="${drugOrder.asNeeded}"><spring:message code="orderextension.orderset.DrugOrderSetMember.asNeeded" /></c:if></td>
					<td class="regimenCurrentDrugLength"><orderextension:format object="${drugOrder}" format="length"/></td>
					<td class="regimenCurrentDrugInfusion"><orderextension:format object="${drugOrder}" format="administrationInstructions"/></td>
					<td class="regimenCurrentDrugDateStart"><openmrs:formatDate date="${drugOrder.effectiveStartDate}" type="medium" /></td>
					<td class="regimenCurrentDrugScheduledStopDate"><c:if test="${!empty drugOrder.effectiveStopDate }"><openmrs:formatDate date="${drugOrder.effectiveStopDate}" type="medium" /></c:if></td>
					<td class="regimenCurrentDrugInstructions">
					${drugOrder.instructions}
						<c:if test="${completed ne 'true'}">
							<orderextension:orderReason order="${drugOrder}" type="revision" var="revisionReason"/>
							<c:if test="${!empty revisionReason}">
								<spring:message code="orderextension.regimen.changeReason"/> ${revisionReason.displayString}
							</c:if>
						</c:if>
					</td>
					<c:if test="${completed eq 'true'}">
						<td class="regimenDiscontinuedReason">
							<orderextension:orderReason order="${drugOrder}" type="discontinue"/>
						</td>
					</c:if>

				<c:if test="${model.readOnly != 'true'}">
					<openmrs:hasPrivilege privilege="Edit Regimen">
					<c:choose>
						<c:when test="${empty drugGroup}">
							<c:choose>
								<c:when test="${orderIsCurrent}">
									<td class="regimenLinks"><input type="button" id="${drugOrder.id},false,<openmrs:formatDate date="${drugOrder.effectiveStartDate}" />" class="stopButton" value="<spring:message code="orderextension.regimen.stop" />"></td>
								</c:when>
								<c:otherwise>
									<td></td>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${orderIsFuture}">
									<td class="regimenLinks"><input type="button" id="${drugOrder.id}" class="editButton" value="<spring:message code="general.edit" />"></td>
								</c:when>
								<c:otherwise>
									 <openmrs:hasPrivilege privilege="Edit Current/Completed Regimen">
										<c:if test="${orderIsCurrent}">
											<td class="regimenLinks"><input type="button" id="${drugOrder.id}" class="editButton" value="<spring:message code="general.edit" />"></td>
										</c:if>
										<c:if test="${orderIsPast}">
											<td></td>
										</c:if>
									</openmrs:hasPrivilege>
								</c:otherwise>
							</c:choose>
							<td class="regimenLinks"><input type="button" id="${drugOrder.id}" class="deleteButton" value="<spring:message code="general.delete" />"></td>
						</c:when>
						<c:otherwise>
							<openmrs:hasPrivilege privilege="Edit Current/Completed Regimen">
								<c:choose>
									<c:when test="${orderIsCurrent}">
										<c:choose>
											<c:when test="${drugGroup.cyclical }">
												<td class="regimenLinks"><input type="button" id="${drugOrder.id},true,<openmrs:formatDate date="${drugOrder.effectiveStartDate}"/>" class="stopButton" value="<spring:message code="orderextension.regimen.stop" />"></td>
											</c:when>
											<c:otherwise>
												<td class="regimenLinks"><input type="button" id="${drugOrder.id},false,<openmrs:formatDate date="${drugOrder.effectiveStartDate}" />" class="stopButton" value="<spring:message code="orderextension.regimen.stop" />"></td>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<td class="regimenLinks"></td>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${orderIsFuture}">
										<c:choose>
											<c:when test="${drugGroup.cyclical }">
												<td class="regimenLinks"><input type="button" id="${drugOrder.id},true" class="editButton" value="<spring:message code="general.edit" />"></td>
											</c:when>
											<c:otherwise>
												<td class="regimenLinks"><input type="button" id="${drugOrder.id},false" class="editButton" value="<spring:message code="general.edit" />"></td>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<c:if test="${orderIsCurrent}">
											<c:choose>
												<c:when test="${drugGroup.cyclical }">
													<td class="regimenLinks"><input type="button" id="${drugOrder.id},true" class="editButton" value="<spring:message code="general.edit" />"></td>
												</c:when>
												<c:otherwise>
													<td class="regimenLinks"><input type="button" id="${drugOrder.id},false" class="editButton" value="<spring:message code="general.edit" />"></td>
												</c:otherwise>
											</c:choose>
										</c:if>
										<c:if test="${orderIsPast}">
											<td></td>
										</c:if>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${drugGroup.cyclical }">
										<td class="regimenLinks"><input type="button" id="${drugOrder.id},true" class="deleteButton" value="<spring:message code="general.delete" />"></td>
									</c:when>
									<c:otherwise>
										<td class="regimenLinks"><input type="button" id="${drugOrder.id},false" class="deleteButton" value="<spring:message code="general.delete" />"></td>
									</c:otherwise>
								</c:choose>
							</openmrs:hasPrivilege>
						</c:otherwise>
					</c:choose>
					</openmrs:hasPrivilege>
					</c:if>
				</tr>
			</c:forEach>
		</c:forEach>
	</c:forEach>
</table>

