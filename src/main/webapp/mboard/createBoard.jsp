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
     <div class="col-md-8">
         <header><h3>게시판 메타데이터</h3></header>
         <div class="row">
             <form id='form1' action="/MBoard.createBoard.cms" method="POST">
             	 <input type='hidden' id='mode' name='mode' value='${mode }' />
                 <textarea name="metadata" id="metadata" cols="70" rows="25">${metadata }</textarea>
             </form>
         </div>
         <footer>
         <div>
			<c:if test="${errorManager.hasError }" >
			<ul>
			<c:forEach var="err" items="${errorManager.list }">
				<li> ${err } </li>
			</c:forEach>
			</ul>
			</c:if>         
         </div>
         <button type="button" class="btn btn-default" aria-label="Center Align" id="btnSave">생성</button>
         </footer>
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
