<%@ tag language="java" pageEncoding="UTF-8"%>

<%@ attribute name="id" required="true" %>
<%@ attribute name="pageAttr" type="kr.dcos.common.utils.PageAttribute"  required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<table id="${id }">
		<tr>
		<td><strong>Page : </strong> &nbsp;</td>
		<td>
			<c:out value="${pageAttr.nowPageNumber }" />/<c:out value="${pageAttr.totalPageCount }" />
		</td>
		<td>&nbsp;</td>
		<td><strong>Total : </strong> &nbsp;</td>
		<td>
			<c:out value="${pageAttr.totalItemCount }" />
		</td>
<!-- 		<td>&nbsp;</td> -->
<!-- 		<td>PageSize:</td> -->
<!-- 		<td> -->
<%-- 			<c:out value="${pageAttr.pageSize }" /> --%>
<!-- 		</td> -->
		</tr>
</table>