<%@ include file="/WEB-INF/view/module/orderextension/include/include.jsp"%>
<%@ include file="/WEB-INF/view/module/orderextension/include/localHeader.jsp"%>

<h2>Drug Orders for ${patient.personName.fullName}</h2>

<c:forEach items="${regimens}" var="regimen">
	<b class="boxHeader">
		<c:choose>
			<c:when test="${!empty regimen.orderSet}">
				<c:if test="${!empty regimen.cycleNumber}">Cycle ${regimen.cycleNumber} of</c:if>
				${regimen.orderSet.name}
			</c:when>
			<c:otherwise>Unnamed Drug Regimen</c:otherwise>
		</c:choose>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<openmrs:formatDate date="${regimen.firstDrugOrderStartDate}"/> - <openmrs:formatDate date="${regimen.lastDrugOrderEndDate}"/>
	</b>
	<div class="box">
		<table>
			<thead>
				<tr>
					<tr>
						<th>Concept</th>
						<th>Start Date</th>
						<th>End Date</th>
						<th>Indication</th>
						<th>Drug</th>
						<th>Dose</th>
						<th>Units</th>
						<th>Route</th>
						<th>Administration Instructions</th>
						<th>Frequency</th>
					</tr>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${regimen.members}" var="eo">
					<tr>
						<td><openmrs:format concept="${eo.concept}"/></td>
						<td><openmrs:formatDate date="${eo.effectiveStartDate}"/></td>
						<td><c:if test="${!empty eo.effectiveStopDate}"><openmrs:formatDate date="${eo.effectiveStopDate}"/></c:if></td>
						<td><openmrs:format concept="${eo.orderReason}"/></td>
						<td>${eo.drug.name}</td>
						<td>${eo.dose}</td>
						<td>${eo.doseUnits}</td>
						<td><openmrs:format concept="${eo.route}"/></td>
						<td>${eo.dosingInstructions}</td>
						<td>${eo.frequency}</td>
					</tr>			
				</c:forEach>
			</tbody>
		</table>
	</div>
	<br/>
</c:forEach>
<br/>
<b class="boxHeader">
	Other Drug Orders
</b>
<div class="box">
	<table>
		<thead>
			<tr>
				<tr>
					<th>Concept</th>
					<th>Start Date</th>
					<th>End Date</th>
					<th>Indication</th>
					<th>Drug</th>
					<th>Dose</th>
					<th>Units</th>
					<th>Route</th>
					<th>Administration Instructions</th>
					<th>Frequency</th>
				</tr>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${drugOrders}" var="eo">
				<tr>
					<td><openmrs:format concept="${eo.concept}"/></td>
					<td><openmrs:formatDate date="${eo.effectiveStartDate}"/></td>
					<td><c:if test="${!empty eo.effectiveStopDate}"><openmrs:formatDate date="${eo.effectiveStopDate}"/></c:if></td>
					<td><openmrs:format concept="${eo.orderReason}"/></td>
					<td>${eo.drug.name}</td>
					<td>${eo.dose}</td>
					<td>${eo.doseUnits}</td>
					<td><openmrs:format concept="${eo.route}"/></td>
					<td>${eo.dosingInstructions}</td>
					<td>${eo.frequency}</td>
				</tr>			
			</c:forEach>
		</tbody>
	</table>
</div>
<br/><br/>
<b class="boxHeader">
	Add new Drug Order
</b>
<div class="box">
	<form action="addOrdersFromSet.form">
		<input type="hidden" name="patientId" value="${patient.patientId}"/>
		<input type="hidden" name="returnPage" value="/module/orderextension/orderList.list?patientId=${patient.patientId}"/>
		<table>
			<tr>
				<td>
					<select name="orderSet">
						<option value="">Choose a pre-defined Order Set...</option>
						<c:forEach items="${orderSets}" var="orderSet">
							<option value="${orderSet.orderSetId}">${orderSet.name}</option>
						</c:forEach>
					</select>
				</td>
				<td>
					Start Date:  <openmrs_tag:dateField formFieldName="startDateSet" startValue=""/>
				</td>
				<td>
					Number of Cycles:  <input type="text" name="numCycles" size="10"/>
				</td>
				<td><input type="submit"/></td>
			</tr>
		</table>
	</form>
</div>


<%@ include file="/WEB-INF/template/footer.jsp"%>
