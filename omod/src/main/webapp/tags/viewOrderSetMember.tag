<%@ include file="/WEB-INF/view/module/orderextension/include/include.jsp"%>

<%@ attribute name="orderSet" required="true" type="org.openmrs.module.orderextension.ExtendedOrderSet" %>
<%@ attribute name="member" required="true" type="org.openmrs.module.orderextension.ExtendedOrderSetMember" %>

<c:if test="${!empty member.title}">
	<tr>
		<td colspan="2" style="font-weight:bold; border-bottom:1px solid black;">
			${member.title}
		</td>
	</tr>
</c:if>
<tr>
	<td style="white-space:nowrap; vertical-align:top;">
		<a href="orderSetMemberForm.form?orderSetId=${orderSet.orderSetId}&uuid=${member.uuid}"><img src='<c:url value="/images/edit.gif"/>' border="0"/></a>
		&nbsp;
		<a href="deleteOrderSetMember.form?uuid=${member.uuid}"><img src='<c:url value="/images/trash.gif"/>' border="0"/></a>
	</td>
	<td style="width:100%; text-align:left; vertical-align:top; padding:5px;">
		<c:choose>
			<c:when test="${!empty member.drug}">${member.drug.name}</c:when>
			<c:when test="${!empty member.concept}">
				<openmrs:format concept="${member.concept}"/>
			</c:when>
			<c:otherwise><spring:message code="orderextension.orderset.drugOrder"/></c:otherwise>
		</c:choose>
		<c:if test="${!empty member.dose}">${member.dose}</c:if>
		<c:if test="${!empty member.units}">${member.units}</c:if>
		<c:if test="${!empty member.route}"><openmrs:format concept="${member.route}"/></c:if>
		<c:if test="${!empty member.frequency}">${member.frequency}</c:if>
		<c:if test="${member.asNeeded}"><spring:message code="orderextension.orderset.DrugOrderSetMember.asNeeded"/></c:if>
		<c:if test="${!empty member.administrationInstructions}">${member.administrationInstructions}</c:if>
	</td>
</tr>
