<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page session="false"%>
<html>
<body>
	<h1>Title : ${title}</h1>	
	<h1>Message : ${message}</h1>	
	<sec:authorize access="hasRole('ROLE_ADMIN')">
		<h1>This is ${pageContext.request.userPrincipal.name}</h1>
	</sec:authorize>
	
	<sec:authorize access="hasRole('ROLE_USER')">
		<h1>This is ${pageContext.request.userPrincipal.name}</h1>
	</sec:authorize>
</body>
</html>