<%@ include file="/WEB-INF/view/module/orderextension/include/include.jsp"%>
<%@ include file="/WEB-INF/view/module/orderextension/include/localHeader.jsp"%>

<h2>Orders for ${patient.personName.fullName}</h2>

<div class="boxHeader">Orders</div>
<div class="box">
	<table id="orderSetTable" style="width:100%; padding:5px;)">
		<thead>
			<tr>
				<tr>
					<th>ID</th>
					<th>Java Type</a>
					<th>Concept</th>
					<th>Start Date</th>
					<th>Extended Order Data</th>
					<th>Drug Order Data</th>
					<th>Extended Drug Order Data</th>
				</tr>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${orders}" var="order">
				<c:set var="orderType" value="${order['class'].simpleName}"/>
				<tr>
					<td>${order.id}</td>
					<td>${orderType}</a>
					<td>6271</td>
					<td><openmrs:formatDate date="${order.startDate}"/></td>
					<td>
						<c:if test="${orderType == 'ExtendedOrder' || orderType == 'ExtendedDrugOrder'}">
							Group: ${order.group} | Indication: <openmrs:format concept="${order.indication}"/>
						</c:if>
					</td>
					<td>
						<c:if test="${orderType == 'DrugOrder' || orderType == 'ExtendedDrugOrder'}">
							Drug: ${!empty order.drug ? order.drug.name : ''} | Dose: ${order.dose} | Units: ${order.units}
						</c:if>
					</td>
					<td>
						<c:if test="${orderType == 'ExtendedDrugOrder'}">
							Route: <openmrs:format concept="${order.route}"/> | Additional Instructions: ${order.additionalInstructions}
						</c:if>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>