<%@ include file="/WEB-INF/view/module/orderextension/include/include.jsp"%>
<%@ include file="/WEB-INF/view/module/orderextension/include/localHeader.jsp"%>

<h2>Drug Orders for ${patient.personName.fullName}</h2>

<c:forEach items="${ordersByGroup}" var="entry">
	<div class="boxHeader">
		<c:choose>
			<c:when test="${empty entry.key}">Ungrouped</c:when>
			<c:when test="${!empty entry.key.orderSet}">
				<c:if test="${!empty entry.key.cycleNumber}">Cycle ${entry.key.cycleNumber} of</c:if>
				${entry.key.orderSet.name}
			</c:when>
			<c:otherwise>Order Group</c:otherwise>
		</c:choose>
	</div>
	<div class="box">
		<table>
			<thead>
				<tr>
					<tr>
						<th>ID</th>
						<th>Order ID</th>
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
				<c:forEach items="${entry.value}" var="eo">
					<tr>
						<td>${eo.id}</td>
						<td>${eo.order.id}</td>
						<td>${eo.order['class'].simpleName}</td>
						<td><openmrs:format concept="${eo.order.concept}"/></td>
						<td><openmrs:formatDate date="${eo.order.startDate}"/></td>
						<td><openmrs:format concept="${eo.indication}"/></td>
						<td>${eo.group.id}</td>
						<td>${eo.order.drug.name}</td>
						<td>${eo.order.dose}</td>
						<td>${eo.order.units}</td>
						<td><openmrs:format concept="${eo.route}"/></td>
						<td>${eo.order.frequency}</td>
						<td>${eo.additionalInstructions}</td>
					</tr>			
				</c:forEach>
			</tbody>
		</table>
	</div>
	<br/>
</c:forEach>

<%@ include file="/WEB-INF/template/footer.jsp"%>