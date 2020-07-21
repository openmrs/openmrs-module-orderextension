<%@ include file="/WEB-INF/view/module/orderextension/include/include.jsp"%>
<%@ include file="/WEB-INF/view/module/orderextension/include/localHeader.jsp"%>

<openmrs:require privilege="View Order Sets" otherwise="/login.htm" redirect="/orderextension/orderSet.form" />

<style>
	a.memberLink:link { color:lightyellow; text-decoration:none; }
</style>

<h2><spring:message code="orderextension.orderset.view"/>: ${orderSet.name}</h2>
<br/>

<b class="boxHeader" style="font-weight:bold; text-align:right;">
	<span style="float:left;"><spring:message code="orderextension.orderset.details"/></span>
	<a style="color:lightyellow;" href="orderSetForm.form?id=${orderSet.orderSetId}"><spring:message code="general.edit"/></a>
</b>
<div class="box">
	<table>
		<tr>
			<th><spring:message code="orderextension.orderset.field.name"/>:</th>
			<td>${orderSet.name}</td>
		</tr>
		<tr>
			<th><spring:message code="orderextension.orderset.field.description"/>:</th>
			<td>${orderSet.description}</td>
		</tr>
		<tr>
			<th style="vertical-align:top;"><spring:message code="orderextension.orderset.field.operator"/>:</th>
			<td>${orderSet.operator}</td>
		</tr>
		<tr>
			<th style="vertical-align:top;"><spring:message code="orderextension.orderset.field.indication"/>:</th>
			<td><openmrs:format concept="${orderSet.indication}"/></td>
		</tr>
		<tr>
			<th style="vertical-align:top;"><spring:message code="orderextension.orderset.field.cyclical"/>:</th>
			<td>
				<spring:message code="general.${orderSet.cyclical}"/>
				<c:if test="${!empty orderSet.cycleLengthInDays}">
					(<spring:message code="${orderSet.cycleLengthInDays}"/> <spring:message code="orderextension.orderset.field.length.days"/>)
				</c:if>
			</td>
		</tr>
	</table>
</div>
<br/>
<b class="boxHeader" style="font-weight:bold; text-align:right;">
	<span style="float:left;"><spring:message code="orderextension.orderset.members"/></span>
	<span style="padding-left:5px; padding-right:5px;">|</span>
	<a class="memberLink" href="orderSetMemberForm.form?orderSetId=${orderSet.orderSetId}">
		<spring:message code="orderextension.orderset.addMember"/>
	</a>
</b>
<div class="box" style="padding-top:10px;padding-bottom:10px;">

	<style>
		.memberTable td {vertical-align: top;}
	</style>

	<table class="memberTable">
		<c:forEach items="${members}" var="member">
			<orderextensionTag:viewOrderSetMember orderSet="${orderSet}" member="${member}"/>
		</c:forEach>
	</table>
	<c:if test="${empty members}">
		<span><spring:message code="general.none"/></span>
	</c:if>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
