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
	<a style="color:lightyellow;" href="orderSetForm.form?id=${orderSet.id}"><spring:message code="general.edit"/></a>
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
			<td><spring:message code="general.${orderSet.cyclical}"/></td>
		</tr>
	</table>
</div>
<br/>
<b class="boxHeader" style="font-weight:bold; text-align:right;">
	<span style="float:left;"><spring:message code="orderextension.orderset.members"/></span>
	<spring:message code="orderextension.general.add"/>:
	<c:forEach items="${memberTypes}" var="memberType">
		<span style="padding-left:5px; padding-right:5px;">|</span>
		<a class="memberLink" href="orderSetMemberForm.form?orderSetId=${orderSet.id}&memberType=${memberType.name}">
			<spring:message code="orderextension.orderset.${memberType.simpleName}"/>
		</a>
	</c:forEach>
</b>
<div class="box" style="padding-top:10px;padding-bottom:10px;">

	<style>
		.memberTable td {vertical-align: top;}
	</style>

	<table class="memberTable">
		<c:forEach items="${orderSet.members}" var="member">
			<orderextensionTag:viewOrderSetMember orderSet="${orderSet}" member="${member}"/>
		</c:forEach>
	</table>
	<c:if test="${empty orderSet.members}">
		<span><spring:message code="general.none"/></span>
	</c:if>
</div>

<c:if test="${!empty parentOrderSets}">
	<br/>
	<b class="boxHeader">
		<span><spring:message code="orderextension.orderset.parents"/></span>
	</b>
	<div class="box">
		<c:forEach items="${parentOrderSets}" var="parentOrderSet" varStatus="parentStatus">
			<button onclick="document.location.href='orderSet.form?id=${parentOrderSet.id}';">
				<img src='<c:url value="/images/leftArrow.gif"/>' border="0"/>
				<c:choose>
					<c:when test="${!empty parentOrderSet.name}">${parentOrderSet.name}</c:when>
					<c:otherwise><spring:message code="orderextension.orderset.unnamedOrderSet"/></c:otherwise>
				</c:choose>
			</button>
			<c:if test="${!parentStatus.last}"><br/></c:if>
		</c:forEach>
	</div>
</c:if>

<%@ include file="/WEB-INF/template/footer.jsp"%>