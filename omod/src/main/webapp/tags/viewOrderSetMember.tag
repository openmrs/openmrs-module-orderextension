<%@ include file="/WEB-INF/view/module/orderextension/include/include.jsp"%>

<%@ attribute name="orderSet" required="true" type="org.openmrs.module.orderextension.OrderSet" %>
<%@ attribute name="member" required="true" type="org.openmrs.module.orderextension.OrderSetMember" %>
<%@ attribute name="nested" required="false" type="java.lang.Boolean" %>

<c:if test="${!empty member.title || !empty member.comment}">
	<tr>
		<td colspan="2" style="font-weight:bold; border-bottom:1px solid black;">
			${member.title}
			<c:if test="${!empty member.comment}">
				(${member.comment})
			</c:if>
		</td>
	</tr>
</c:if>
<tr>
	<td style="white-space:nowrap; vertical-align:top;">
		<c:if test="${nested == null || !nested}">
			&nbsp;
			<a href="orderSetMemberForm.form?orderSetId=${orderSet.id}&memberId=${member.id}"><img src='<c:url value="/images/edit.gif"/>' border="0"/></a>
			&nbsp;
			<a href="deleteOrderSetMember.form?id=${member.id}"><img src='<c:url value="/images/trash.gif"/>' border="0"/></a>
			&nbsp;
		</c:if>
	</td>
	<td style="width:100%; text-align:left; vertical-align:top; padding:5px;">
		<c:choose>
			<c:when test="${member['class'].name == 'org.openmrs.module.orderextension.TestOrderSetMember'}">
				<c:choose>
					<c:when test="${!empty member.concept}"><openmrs:format concept="${member.concept}"/></c:when>
					<c:otherwise><spring:message code="orderextension.orderset.labTestOrder"/></c:otherwise>
				</c:choose>
			</c:when>
			<c:when test="${member['class'].name == 'org.openmrs.module.orderextension.DrugOrderSetMember'}">
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
			</c:when>
			<c:when test="${member['class'].name == 'org.openmrs.module.orderextension.NestedOrderSetMember'}">
				<div style="border:1px solid black; padding:5px;">
					<b><a href="orderSet.form?id=${member.nestedOrderSet.id}&parentOrderSetId=${orderSet.id}">
						<c:choose>
							<c:when test="${!empty member.nestedOrderSet.name}">${member.nestedOrderSet.name}</c:when>
							<c:otherwise><spring:message code="orderextension.orderset.NestedOrderSetMember"/></c:otherwise>
						</c:choose>
						<br/>
					</a></b>
					<table>
						<c:forEach items="${member.nestedOrderSet.members}" var="nestedMember">
							<orderextensionTag:viewOrderSetMember orderSet="${member.nestedOrderSet}" member="${nestedMember}" nested="true"/>
						</c:forEach>
					</table>
				</div>
			</c:when>
		</c:choose>
	</td>
</tr>