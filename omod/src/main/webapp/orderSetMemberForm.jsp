<%@ include file="/WEB-INF/view/module/orderextension/include/include.jsp"%>
<%@ include file="/WEB-INF/view/module/orderextension/include/localHeader.jsp"%>

<openmrs:require privilege="Get Order Sets" otherwise="/login.htm" redirect="/orderextension/orderSet.form" />

<script type="text/javascript">

</script>

<h2>
	${orderSet.name}:
	<c:choose>
		<c:when test="${empty orderSetMember.member}"><spring:message code="general.add"/></c:when>
		<c:otherwise><spring:message code="general.edit"/></c:otherwise>
	</c:choose>
	<spring:message code="orderextension.orderset.DrugOrderSetMember"/>
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
					</table>
				</fieldSet>
			</td>
			<td>
				<fieldSet style="height:100px;">
					<legend><spring:message code="orderextension.orderset.memberLengthSection"/></legend>
					<table style="padding:5px; width:100%;">
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
							<th><spring:message code="orderextension.orderset.DrugOrderSetMember.instructions"/></th>
							<td>
								<form:textarea path="instructions" cols="70" rows="3"/>
								<form:errors path="instructions" cssClass="error"/>
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
								<spring:bind path="doseUnits">
									<select name="${status.expression}" id="doseUnits">
										<option value=""></option>
										<c:forEach var="doseUnit" items="${dosingUnits}">
											<option value="${doseUnit.conceptId}" <c:if test="${doseUnit.conceptId == status.value}">selected</c:if>>
												${doseUnit.displayString}
											</option>
										</c:forEach>
									</select>
								</spring:bind>
								<c:if test="${!empty orderSetMember.units}"> ( ${orderSetMember.units} )</c:if>
								<form:errors path="doseUnits" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<th><spring:message code="orderextension.orderset.DrugOrderSetMember.route"/></th>
							<td>
								<spring:bind path="route">
									<select name="${status.expression}" id="route">
										<option value=""></option>
										<c:forEach var="route" items="${routes}">
											<option value="${route.conceptId}" <c:if test="${route.conceptId == status.value}">selected</c:if>>
													${route.displayString}
											</option>
										</c:forEach>
									</select>
								</spring:bind>
								<form:errors cssClass="error" path="route"/>
							</td>
						</tr>
						<tr>
							<th><spring:message code="orderextension.orderset.DrugOrderSetMember.frequency"/></th>
							<td>
								<spring:bind path="orderFrequency">
									<select name="${status.expression}" id="frequency">
										<option value=""></option>
										<c:forEach var="frequency" items="${frequencies}">
											<option value="${frequency.id}" <c:if test="${frequency.id == status.value}">selected</c:if>>
													${frequency}
											</option>
										</c:forEach>
									</select>
								</spring:bind>
								<c:if test="${!empty orderSetMember.frequency}"> ( ${orderSetMember.frequency} )</c:if>
								<form:errors path="orderFrequency" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<th><spring:message code="orderextension.orderset.DrugOrderSetMember.asNeeded"/></th>
							<td><form:checkbox path="asNeeded"/></td>
						</tr>
						<tr>
							<th><spring:message code="orderextension.orderset.DrugOrderSetMember.administrationInstructions"/></th>
							<td>
								<form:textarea path="administrationInstructions" cols="70" rows="3"/>
								<form:errors path="administrationInstructions" cssClass="error"/>
							</td>
						</tr>
					</table>
				</fieldSet>
			</td>
		</tr>
	</table>
	<input type="submit" value="<spring:message code='general.save'/>" />
	&nbsp;&nbsp;
	<input type="button" value="<spring:message code='general.cancel'/>" onclick="document.location.href='orderSet.list?id=${orderSet.orderSetId};'" />
</form:form>

</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
