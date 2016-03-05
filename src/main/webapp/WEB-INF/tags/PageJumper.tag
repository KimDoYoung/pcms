<%@ tag language="java" pageEncoding="UTF-8"%>

<%@ attribute name="id" required="true" %>
<%@ attribute name="pageAttribute" type="kr.dcos.common.utils.PageAttribute"  required="true" %>
<%@ attribute name="functionName" required="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${empty functionName}" >
	<c:set var="functionName" value="BoardButton.goPageClick" />
</c:if>
<c:if test="${pageAttribute.totalPageCount > 1}">
	<div id="${id }" class="page_number">
		<%-- <span class="bt">
			<a href="javascript:emptyAction();"><img alt="처음" src='<c:url value="/common/img/button/btn_first.gif" />' /></a>
			<a href="javascript:emptyAction();" class="btn_last"><img alt="이전" src="img/button/btn_prev.gif" /></a>
		</span > --%>
		<c:if test ="${pageAttribute.isDisplayPrevPageNumberMark }" >
			<span class="bt">
				<a href="#" onclick="javascript:${functionName }(${pagedGroup.pageAttribute.startPageNumber } - 1)"><img alt="처음" src='<c:url value="/common/img/button/btn_prev.gif" />' /></a>
			</span>
		</c:if>
		<c:forEach var="i" begin="${pageAttribute.startPageNumber }" end="${pageAttribute.endPageNumber }" step="1">
		<c:choose>
			<c:when test="${i == pageAttribute.nowPageNumber }">
				[${i }]
			</c:when>
			<c:otherwise>
				<span><a href="#" onclick="javascript:${functionName }(${i })">${i }</a></span>
			</c:otherwise>
		</c:choose>
		</c:forEach>
		<c:if test ="${pageAttribute.isDisplayNextPageNumberMark }">
			
				<span class="bt"><a href="#" onclick="javascript:${functionName }(${pageAttribute.endPageNumber } + 1)"><img alt="처음" src='<c:url value="/common/img/button/btn_next.gif" />' /></a></span>
			
		</c:if>
		
	</div>
	<%-- <table id="${id }">
		<tr>
			<c:if test ="${pageAttribute.isDisplayPrevPageNumberMark }" >
			<td>
				<a href="#" onclick="javascript:${functionName }(${pagedGroup.pageAttribute.startPageNumber } - 1)">Prev</a>
			</td>
			</c:if>
			<c:forEach var="i" begin="${pageAttribute.startPageNumber }" end="${pageAttribute.endPageNumber }" step="1">
			<td>
			<c:choose>
				<c:when test="${i == pageAttribute.nowPageNumber }">
					[${i }]
				</c:when>
				<c:otherwise>
					<a href="#" onclick="javascript:${functionName }(${i })">${i }</a>
				</c:otherwise>
			</c:choose>
			</td>
			</c:forEach>
			<c:if test ="${pageAttribute.isDisplayNextPageNumberMark }">
			<td>
				<a href="#" onclick="javascript:${functionName }(${pageAttribute.endPageNumber } + 1)">Next</a>
			</td>
			</c:if>
		</tr>
	</table> --%>
</c:if>	