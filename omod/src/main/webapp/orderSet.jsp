<%@ include file="/WEB-INF/view/module/orderextension/include/include.jsp"%>
<%@ include file="/WEB-INF/view/module/orderextension/include/localHeader.jsp"%>

<openmrs:require privilege="View Order Sets" otherwise="/login.htm" redirect="/orderextension/orderSet.form" />

<h2>${orderSet.name}</h2>
<br/>

<table>
	<tr>
		<td><spring:message code="orderextension.orderset.field.name"/></td>
		<td>${orderSet.name}</td>
	</tr>
	<tr>
		<td><spring:message code="orderextension.orderset.field.title"/></td>
		<td>${orderSet.title}</td>
	</tr>
	<tr>
		<td style="vertical-align:top;"><spring:message code="orderextension.orderset.field.comment"/></td>
		<td>${orderSet.comment}</td>
	</tr>
	<tr>
		<td style="vertical-align:top;"><spring:message code="orderextension.orderset.field.operator"/></td>
		<td>${orderSet.operator}</td>
	</tr>
	<tr>
		<td style="vertical-align:top;"><spring:message code="orderextension.orderset.field.indication"/></td>
		<td><openmrs:format concept="${orderSet.indication}"/></td>
	</tr>
	<tr>
		<td colspan="2"><button onclick="document.location.href='orderSetForm.form?id=${orderSet.id}';"><spring:message code="general.edit"/></button></td>
	</tr>
</table>

<%@ include file="/WEB-INF/template/footer.jsp"%>