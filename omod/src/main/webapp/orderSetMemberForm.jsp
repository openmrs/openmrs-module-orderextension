<%@ include file="/WEB-INF/view/module/orderextension/include/include.jsp"%>
<%@ include file="/WEB-INF/view/module/orderextension/include/localHeader.jsp"%>

<openmrs:require privilege="View Order Sets" otherwise="/login.htm" redirect="/orderextension/orderSet.form" />

<script type="text/javascript">

</script>

<h2>
	${orderSet.name}: 
	<c:choose>
		<c:when test="${empty orderSetMember.id}"><spring:message code="general.add"/></c:when>
		<c:otherwise><spring:message code="general.edit"/></c:otherwise>
	</c:choose>
	<spring:message code="orderextension.orderset.${orderSetMember['class'].simpleName}"/>
</h2>
<br/>

<style>
	#memberTable th {text-align:left; vertical-align:top; font-weight:normal; }
	#memberTable td {text-align:left; vertical-align:top;}
	legend {font-weight:bold;}
	#concept_id_selection { width: 400px; }
	#indication_id_selection { width: 400px; }
</style>

<spring:hasBindErrors name="orderSetMember">
	<spring:message code="fix.error"/>
	<div class="error">
		<c:forEach items="${errors.allErrors}" var="error">
			<spring:message code="${error.code}" text="${error.code}"/><br/>
		</c:forEach>
	</div>
	<br />
</spring:hasBindErrors>

<form:form commandName="orderSetMember" method="post">

	<table id="memberTable" style="padding:5px; width:100%;">
		<tr>
			<td>
				<fieldSet style="height:100px;">
					<legend><spring:message code="orderextension.orderset.memberTitleSection"/></legend>
					<table style="padding:5px; width:100%;">
						<tr>
							<th><spring:message code="orderextension.orderset.field.title"/></th>
							<td>
								<form:input path="title" size="50"/>
								<form:errors path="title" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<th><spring:message code="orderextension.orderset.field.comment"/></th>
							<td>
								<form:textarea path="comment" cols="50" rows="2"/>
								<form:errors path="comment" cssClass="error"/>
							</td>
						</tr>
					</table>
				</fieldSet>
			</td>
			<td>
				<fieldSet style="height:100px;">
					<legend><spring:message code="orderextension.orderset.memberLengthSection"/></legend>
					<table style="padding:5px; width:100%;">
						<tr>
							<th><spring:message code="orderextension.orderset.field.selected"/></th>
							<td>
								<form:checkbox path="selected"/>
								<form:errors path="comment" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<th><spring:message code="orderextension.orderset.field.relativeStartDay"/></th>
							<td>
								<form:input path="relativeStartDay" size="10"/>
								<spring:message code="orderextension.orderset.field.relativeStartDay.suffix"/>
								<form:errors path="relativeStartDay" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<th><spring:message code="orderextension.orderset.field.length"/></th>
							<td>
								<form:input path="lengthInDays" size="10"/>
								<spring:message code="orderextension.orderset.field.length.days"/>
								<form:errors path="lengthInDays" cssClass="error"/>
							</td>
						</tr>
					</table>
				</fieldSet>
			</td>
		</tr>
		<c:choose>
			<c:when test="${orderSetMember['class'].name == 'org.openmrs.module.orderextension.TestOrderSetMember'}">
				<tr>
					<td colspan="2">
						<fieldSet style="width:100%;">
							<legend><spring:message code="orderextension.orderset.memberTestSection"/></legend>
							<table style="padding:5px;">
								<tr>
									<th><spring:message code="orderextension.orderset.TestOrderSetMember.concept"/></th>
									<td>
										<spring:bind path="concept">
											<openmrs_tag:conceptField formFieldName="${status.expression}" initialValue="${status.value}"/>
										</spring:bind>
						                <form:errors cssClass="error" path="indication"/>
									</td>
								</tr>
								<tr>
									<th><spring:message code="orderextension.orderset.TestOrderSetMember.indication"/></th>
									<td>
										<spring:bind path="indication">
											<openmrs_tag:conceptField formFieldName="${status.expression}" initialValue="${status.value}"/>
										</spring:bind>
						                <form:errors cssClass="error" path="indication"/>
									</td>
								</tr>
							</table>
						</fieldSet>
					</td>
				</tr>
			</c:when>
			<c:when test="${orderSetMember['class'].name == 'org.openmrs.module.orderextension.DrugOrderSetMember'}">
				<tr>
					<td colspan="2">
						<fieldSet style="width:100%;">
							<legend><spring:message code="orderextension.orderset.memberDrugSection"/></legend>
							<table style="padding:5px;">
								<tr>
									<th><spring:message code="orderextension.orderset.DrugOrderSetMember.concept"/></th>
									<td>
										<spring:bind path="concept">
											<openmrs_tag:conceptField formFieldName="${status.expression}" initialValue="${status.value}"/>
										</spring:bind>
						                <form:errors cssClass="error" path="indication"/>
									</td>
								</tr>
								<tr>
									<th><spring:message code="orderextension.orderset.DrugOrderSetMember.drug"/></th>
									<td>
										<spring:bind path="drug">
											<openmrs_tag:drugField formFieldName="${status.expression}" drugs="${drugList}" initialValue="${status.value}"/>
										</spring:bind>
						                <form:errors cssClass="error" path="drug"/>
									</td>
								</tr>
								<tr>
									<th><spring:message code="orderextension.orderset.DrugOrderSetMember.indication"/></th>
									<td>
										<spring:bind path="indication">
											<openmrs_tag:conceptField formFieldName="${status.expression}" initialValue="${status.value}"/>
										</spring:bind>
						                <form:errors cssClass="error" path="indication"/>
									</td>
								</tr>
								<tr>
									<th><spring:message code="orderextension.orderset.DrugOrderSetMember.dose"/></th>
									<td>
										<form:input path="dose" size="10"/>
										<form:errors path="dose" cssClass="error"/>
									</td>
								</tr>
								<tr>
									<th><spring:message code="orderextension.orderset.DrugOrderSetMember.units"/></th>
									<td>
										<form:input path="units" size="10"/>
										<form:errors path="units" cssClass="error"/>
									</td>
								</tr>
								<tr>
									<th><spring:message code="orderextension.orderset.DrugOrderSetMember.route"/></th>
									<td>
										<spring:bind path="route">
											<openmrs_tag:conceptField formFieldName="${status.expression}" initialValue="${status.value}"/>
										</spring:bind>
						                <form:errors cssClass="error" path="route"/>
									</td>
								</tr>
								<tr>
									<th><spring:message code="orderextension.orderset.DrugOrderSetMember.frequency"/></th>
									<td>
										<form:input path="frequency" size="50"/>
										<form:errors path="frequency" cssClass="error"/>
									</td>
								</tr>
								<tr>
									<th><spring:message code="orderextension.orderset.DrugOrderSetMember.asNeeded"/></th>
									<td><form:checkbox path="asNeeded"/></td>
								</tr>
								<tr>
									<th><spring:message code="orderextension.orderset.DrugOrderSetMember.additionalInstructions"/></th>
									<td>
										<form:textarea path="additionalInstructions" cols="70" rows="3"/>
										<form:errors path="additionalInstructions" cssClass="error"/>
									</td>
								</tr>
							</table>
						</fieldSet>
					</td>			
				</tr>			
			</c:when>
			<c:when test="${orderSetMember['class'].name == 'org.openmrs.module.orderextension.NestedOrderSetMember'}">
				<tr>
					<td colspan="2">
						<fieldSet style="width:100%">
							<legend><spring:message code="orderextension.orderset.memberOrderSetSection"/></legend>
							<table style="padding:5px;">
								<tr>
									<td>
										<form:select path="nestedOrderSet">
											<form:option value=""><spring:message code="orderextension.orderset.NestedOrderSetMember.nestedOrderSet.new"/></form:option>
											<c:forEach items="${existingOrderSets}" var="existingOrderSet">
												<form:option value="${existingOrderSet}" >
													<c:choose>
														<c:when test="${!empty existingOrderSet.name}">${existingOrderSet.name}</c:when>
														<c:otherwise><spring:message code="orderextension.orderset.unnamedOrderSet"/></c:otherwise>
													</c:choose>
												</form:option>
											</c:forEach>
										</form:select>
						                <form:errors cssClass="error" path="nestedOrderSet"/>
									</td>
								</tr>
							</table>
						</fieldSet>
					</td>
				</tr>			
			</c:when>
			<c:otherwise>
				<!-- TODO: Add extension point here -->
			</c:otherwise>
		</c:choose>
	</table>
	<input type="submit" value="<spring:message code='general.save'/>" />
	&nbsp;&nbsp;
	<input type="button" value="<spring:message code='general.cancel'/>" onclick="document.location.href='orderSet.list?id=${orderSet.id};'" />
</form:form>
	
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>