<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${param.message != null}">
	<div class="panel panel-danger" id="message-box">
		<div class="panel-heading">
			${param.message }
		</div>
	</div>
</c:if>