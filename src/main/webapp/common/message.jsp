<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<head>
<meta charset="UTF-8">
<!-- Head -->
<jsp:include page="../common/header.jsp" flush="false" />
<title>mboard-index</title>
</head>
<body>
<jsp:include page="../common/navbar.jsp" flush="false" />
<div>
<h1>${message }</h1>
<!--          <div> -->
<%-- 			<c:if test="${errorManager.hasError }" > --%>
<!-- 			<ul> -->
<%-- 			<c:forEach var="err" items="${errorManager.list }"> --%>
<%-- 				<li> ${err } </li> --%>
<%-- 			</c:forEach> --%>
<!-- 			</ul> -->
<%-- 			</c:if>          --%>
<!--          </div> -->
</div>
</body>
<script>
$( document ).ready(function() {
    $('#btnSave').on('click', function(){
    	console.log('submit...');
    	$('#form1').submit();
    });
})
</script>
</html>
