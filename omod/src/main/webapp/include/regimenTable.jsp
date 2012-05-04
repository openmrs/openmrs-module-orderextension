

<table class="regimenTable">
	<%
			DrugGroup drugGroup = (DrugGroup)pageContext.getAttribute("drugGroup");
			
			Boolean ivDrugs = regimenHelper.hasIVDrugs(drugGroup);
	%>		
	<thead>
		<tr class="regimenCurrentHeaderRow">
			<th class="regimenCurrentDrugOrderedHeader"> <spring:message code="Order.item.ordered" /> </th>
			<th class="regimenCurrentDrugDoseHeader"> <spring:message code="DrugOrder.dose"/>/<spring:message code="DrugOrder.units"/> </th>
			<th class="regimenCurrentDrugFrequencyHeader"> <spring:message code="DrugOrder.frequency"/> </th>
			<th class="regimenCurrentDrugRouteHeader"> <spring:message code="orderextension.route"/> </th>
			<th class="regimenCurrentDrugInfusionHeader"><%if(ivDrugs)
			{%> <spring:message code="orderextension.infusion"/> <%
			}%></th>
			<th class="regimenCurrentDrugDateStartHeader"> <spring:message code="general.dateStart"/> </th>
			<th class="regimenCurrentDrugScheduledStopDateHeader"> <spring:message code="DrugOrder.scheduledStopDate"/> </th>
			<th class="regimenCurrentDrugInstructionsHeader"> <spring:message code="general.instructions" /> </th>
			<openmrs:hasPrivilege privilege="Edit Regimen">
				<th class="regimenCurrentEmptyHeader"> </th>
				<th class="regimenCurrentEmptyHeader"> </th>
			</openmrs:hasPrivilege>
		</tr>
	</thead>
	
	<%
			List<Concept> classifications = regimenHelper.getClassifications(drugGroup, classification);
			
			for(Concept classf: classifications)
		    {
		%>
		<c:if test="<%=classf != null && !classf.equals(classification)%>">
		<tr class="regimenClassificationHeading">
			<td><b><c:out value="<%=classf.getName() %>"/></b></td>
		</tr>
		</c:if>
		<%
		List<RegimenExtension> regimens = regimenHelper.getRegimensForClassification(drugGroup, classf);
		
		for(RegimenExtension regimen: regimens)
		{ 
			String drugName = "";
			%>
			<tr class="drugLine">
				<td class="regimenCurrentDrugOrdered"><c:if test="<%=regimen.getDrug() != null && !regimen.getDrug().getName().equals(drugName)%>">
						<c:out value="<%=regimen.getDrug().getName()%>"/>
						<%
						drugName = regimen.getDrug().getName();
						%>
					</c:if>
					<c:if test="<%=regimen.getDrug() == null && !regimen.getConcept().getName().getName().equals(drugName)%>">
						<c:out value="<%=regimen.getConcept().getName().getName()%>"/>
						<%
						drugName = regimen.getConcept().getName().getName();
						%>
					</c:if>
				</td>
				<td class="regimenCurrentDrugDose"><c:out value="<%=regimen.getDose() %>"/><c:out value="<%=regimen.getDrug().getUnits() %>"/></td>
				<td class="regimenCurrentDrugFrequency"><c:out value="<%=regimen.getFrequency() %>"/></td>
				<td class="regimenCurrentDrugRoute"><c:out value="<%=regimen.getDrug().getRoute() %>"/></td>
				<td class="regimenCurrentDrugInfusion"><%if(ivDrugs)
				{%><c:out value="<%=regimen.getInfusionInstructions() %>"/><%
				}%></td>
				<td class="regimenCurrentDrugDateStart"><openmrs:formatDate date="<%=regimen.getStartDate() %>" type="medium" /></td>
				<td class="regimenCurrentDrugScheduledStopDate"><openmrs:formatDate date="<%=regimen.getAutoExpireDate() %>" type="medium" /></td>
				<td class="regimenCurrentDrugInstructions"><c:out value="<%=regimen.getInstructions() %>"/></td>
				<openmrs:hasPrivilege privilege="Edit Regimen">
					<c:choose>
						<c:when test="<%=regimen.getStartDate().after(new Date()) || regimen.isCurrent()%>">
							<c:choose>
								<c:when test="<%=regimen.isCurrent()%>">
									<td class="regimenLinks"><button id="stopRegimenLink"><spring:message code="orderextension.stop"/></button></td>
								</c:when>
								<c:otherwise>
									<td class="regimenLinks"><button id="editRegimenLink"><spring:message code="general.edit"/></button></td>	
								</c:otherwise>
							</c:choose>	
						</c:when>
						<c:otherwise>
							<td></td>
						</c:otherwise>
					</c:choose>
					<td class="regimenLinks"><button id="deleteRegimenLink"><spring:message code="general.delete"/></button></td>
				</openmrs:hasPrivilege>
			</tr>
		<% }
			%>
	  <%}%>
</table>
	


