<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="id" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- 로그인되어 있지 않다면  -->
<c:if test="${empty sessionInfo}" >
	<a  class="btn type_user" href="LoginController.login.cms">Login</a>
</c:if>
<!-- 로그인된 상태라면  -->
<c:if test="${not empty sessionInfo}">
	<li>
		<a href="#none" class="btn type_user"><span><em>${sessionInfo.name }(${sessionInfo.id })</em></span></a>
	</li>
	<li>
		<div class="util_write">
		<c:if test="${sessionInfo.level == 'A' }">
			<span><a href="configure.setup.cms"><img src="<c:url value='/common/img/icon/btn_setting.jpg'/>" /></a></span>
		</c:if>
			<span><a href="LoginController.logout.cms"><img src="<c:url value='/common/img/icon/btn_logOut.jpg'/>" /></a></span>
		</div>
	</li>
</c:if>
