<%@ include file="/WEB-INF/view/module/orderextension/include/include.jsp"%>
<%@ include file="/WEB-INF/view/module/orderextension/include/localHeader.jsp"%>

<h2>Drug Orders for ${patient.personName.fullName}</h2>

<c:forEach items="${regimens}" var="regimen">
	<div class="boxHeader">
		<c:choose>
			<c:when test="${!empty regimen.orderSet}">
				<c:if test="${!empty regimen.cycleNumber}">Cycle ${regimen.cycleNumber} of</c:if>
				${regimen.orderSet.name}
			</c:when>
			<c:otherwise>Regimen</c:otherwise>
		</c:choose>
	</div>
	<div class="box">
		<table>
			<thead>
				<tr>
					<tr>
						<th>ID</th>
						<th>Type</th>
						<th>Concept</th>
						<th>Start Date</th>
						<th>Indication</th>
						<th>Drug</th>
						<th>Dose</th>
						<th>Units</th>
						<th>Route</th>
						<th>Frequency</th>
						<th>Additional Instructions</th>
					</tr>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${regimen.members}" var="eo">
					<tr>
						<td>${eo.id}</td>
						<td>${eo['class'].simpleName}</td>
						<td><openmrs:format concept="${eo.concept}"/></td>
						<td><openmrs:formatDate date="${eo.startDate}"/></td>
						<td><openmrs:format concept="${eo.indication}"/></td>
						<td>${eo.drug.name}</td>
						<td>${eo.dose}</td>
						<td>${eo.units}</td>
						<td><openmrs:format concept="${eo.route}"/></td>
						<td>${eo.frequency}</td>
						<td>${eo.administrationInstructions}</td>
					</tr>			
				</c:forEach>
			</tbody>
		</table>
	</div>
	<br/>
</c:forEach>
<br/>
<div class="boxHeader">
	Other Drug Orders
</div>
<div class="box">
	<table>
		<thead>
			<tr>
				<tr>
					<th>ID</th>
					<th>Type</th>
					<th>Concept</th>
					<th>Start Date</th>
					<th>Indication</th>
					<th>Group</th>
					<th>Drug</th>
					<th>Dose</th>
					<th>Units</th>
					<th>Route</th>
					<th>Frequency</th>
					<th>Additional Instructions</th>
				</tr>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${drugOrders}" var="eo">
				<tr>
					<td>${eo.id}</td>
					<td>${eo['class'].simpleName}</td>
					<td><openmrs:format concept="${eo.concept}"/></td>
					<td><openmrs:formatDate date="${eo.startDate}"/></td>
					<td>
						<c:if test="${eo['class'].simpleName == 'ExtendedDrugOrder'}">
							<openmrs:format concept="${eo.indication}"/>
						</c:if>
					</td>
					<td>${eo.drug.name}</td>
					<td>${eo.dose}</td>
					<td>${eo.units}</td>
					<td>
						<c:if test="${eo['class'].simpleName == 'ExtendedDrugOrder'}">
							<openmrs:format concept="${eo.route}"/>
						</c:if>
					</td>
					<td>${eo.frequency}</td>
					<td>
						<c:if test="${eo['class'].simpleName == 'ExtendedDrugOrder'}">
							${eo.administrationInstructions}
						</c:if>
					</td>
				</tr>			
			</c:forEach>
		</tbody>
	</table>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>