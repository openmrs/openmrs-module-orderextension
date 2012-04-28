<%@ include file="/WEB-INF/view/module/orderextension/include/include.jsp"%>
<%@ include file="/WEB-INF/view/module/orderextension/include/localHeader.jsp"%>

<openmrs:require privilege="View Order Sets" otherwise="/login.htm" redirect="/module/orderextension/orderSetList.list" />

<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables_jui.css"/>
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js"/>
<script type="text/javascript">
    jQuery(document).ready(function() {
    	jQuery('#orderSetTable').dataTable({
            "bPaginate": true,
            "bLengthChange": false,
            "bFilter": false,
            "bSort": true,
            "bInfo": false,
            "bAutoWidth": false,
            "bSortable": true,
            "aoColumns": [{ "iDataSort": 1 }, { "sType": "html" }, null, null]
        });
    });
</script>

<h3><spring:message code="orderextension.orderset.manage.linkTitle"/></h3>

<button onclick="document.location.href='orderSetForm.form';">
	<spring:message code="orderextension.orderset.addButton"/>
</button>
<hr/>
<table id="orderSetTable" style="width:100%; padding:5px;)">
	<thead>
		<tr>
			<th></th>
			<th><spring:message code="orderextension.orderset.field.name"/></th>
			<th><spring:message code="orderextension.orderset.field.title"/></th>
			<th><spring:message code="orderextension.orderset.field.indication"/></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${orderSets}" var="orderSet">
			<tr>
				<td><a href="deleteOrderSet.form?id=${orderSet.id}"><img src="<c:url value="/images/trash.gif"/>"/></a>
				<td nowrap><a href="orderSet.form?id=${orderSet.id}">${orderSet.name}</a></td>
				<td>${orderSet.title}</td>
				<td nowrap><openmrs:format concept="${orderSet.indication}"/></td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<%@ include file="/WEB-INF/template/footer.jsp"%>