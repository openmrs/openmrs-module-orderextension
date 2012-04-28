<%@ include file="/WEB-INF/view/module/orderextension/include/include.jsp"%>
<%@ include file="/WEB-INF/view/module/orderextension/include/localHeader.jsp"%>

<openmrs:require privilege="Manage Order Sets" otherwise="/login.htm" redirect="/orderextension/orderSetForm.form" />

<style>
	#indication_id_selection { width: 400px; }
</style>

<h2>
<c:choose>
	<c:when test="${empty orderSet.id}"><spring:message code="orderextension.orderset.create.pageTitle"/></c:when>
	<c:otherwise><spring:message code="orderextension.orderset.edit.pageTitle"/></c:otherwise>
</c:choose>
</h2><br/>

<spring:hasBindErrors name="orderSet">
	<spring:message code="fix.error"/>
	<div class="error">
		<c:forEach items="${errors.allErrors}" var="error">
			<spring:message code="${error.code}" text="${error.code}"/><br/>
		</c:forEach>
	</div>
	<br />
</spring:hasBindErrors>

<form:form commandName="orderSet" method="post">
	<table>
		<tr>
			<td><spring:message code="orderextension.orderset.field.name"/></td>
			<td>
	        	<form:input path="name" size="50"/>
	            <form:errors path="name" cssClass="error"/>
			</td>
		</tr>
		<tr>
			<td><spring:message code="orderextension.orderset.field.title"/></td>
			<td>
	        	<form:input path="title" size="50"/>
	            <form:errors path="title" cssClass="error"/>
			</td>
		</tr>
		<tr>
			<td style="vertical-align:top;"><spring:message code="orderextension.orderset.field.comment"/></td>
			<td>
				<form:textarea path="comment" cols="70" rows="3"/>
				<form:errors path="comment" cssClass="error"/>
			</td>
		</tr>
		<tr>
			<td style="vertical-align:top;"><spring:message code="orderextension.orderset.field.operator"/></td>
			<td>
                <form:select path="operator" multiple="false">
                    <form:options items="${operators}"/>
                </form:select>
                <form:errors cssClass="error" path="operator"/>
			</td>
		</tr>
		<tr>
			<td style="vertical-align:top;"><spring:message code="orderextension.orderset.field.indication"/></td>
			<td>
				<spring:bind path="indication">
					<openmrs_tag:conceptField formFieldName="${status.expression}" initialValue="${status.value}"/>
				</spring:bind>
                <form:errors cssClass="error" path="indication"/>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<input type="submit" value="<spring:message code='general.save'/>" />
				&nbsp;&nbsp;
				<input type="button" value="<spring:message code='general.cancel'/>" onclick="document.location.href='orderSetList.list';" />
			</td>
		</tr>
	</table>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>