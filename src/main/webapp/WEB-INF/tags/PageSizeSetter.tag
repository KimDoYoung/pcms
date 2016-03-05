<%@ tag language="java" pageEncoding="UTF-8"%>

<%@ attribute name="id" required="true" %>
<%@ attribute name="pageAttr" type="kr.dcos.common.utils.PageAttribute"  required="true" %>
<%@ attribute name="functionName" type="java.lang.String"  required="false" %>
<%@ attribute name="boardType" type="java.lang.String"  required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${empty functionName}" >
	<c:set var="functionName" value="BoardButton.setPageSizeClick" />
</c:if>
<c:choose> 
  <c:when test="${boardType == 'Board'}" > 
    <c:set var="list" value="<%=new int[]{5,10,15,20,30} %>" />
  </c:when> 
  <c:when test="${boardType == 'Photo'}" > 
    <c:set var="list" value="<%=new int[]{8,16,24,32} %>" />
  </c:when> 
  <c:otherwise> <!-- 잘못된 게시판종류임 -->
   <h1>cms:PageSizeSetter error  boardType is 'Board' or 'Photo'</h1>
   <c:set var="list" value="<%=new int[]{5,10,15,20,30} %>" />
  </c:otherwise> 
</c:choose> 

<div id="div${id }">
한페이지당 갯수:
<select id="${id }PageSize"  style="vertical-align:middle;">
	<c:forEach var="item" items="${list}">
	 	<option value="${item}" ${item == pageAttr.pageSize ? 'selected' : ''}>${item}</option>
	</c:forEach>
</select>
<a href="#none" class="btn_type01"  onClick="javascript:${functionName}('${id}')"><span><em class="type_submit">설정</em></span></a>
<%-- <input type="button" value="확인" onClick="javascript:${functionName}('${id}')" /> --%>
</div>
