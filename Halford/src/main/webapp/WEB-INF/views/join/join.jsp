<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Join</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</head>
<body>
	<div class="container">
		<div class="container-fluid" style="background-color:#F44336;color:#fff;height:100px;">
			<h3>Halford</h3>
		</div>
		<div class="page-header">
			<span class="col-sm-4"></span>
			<h2>Join</h2>
		</div>
			
		<fieldset>
			<form:form modelAttribute="member" action="./join.do" method="post" role="form" class="form-horizontal">
				<div class="form-group">
					<form:label path="id" class="control-label col-sm-4">ID</form:label>
					<div class="col-sm-4">
						<form:input path="id" type="text" class="form-control"/>
					</div>
				</div>
				<div class="form-group">
					<form:label path="password" class="control-label col-sm-4">PW</form:label>
					<div class="col-sm-4">
						<form:input path="password" type="password" class="form-control"/>
					</div>
				</div>
				<div class="form-group">
					<span class="col-sm-4"></span>
					<div class="col-sm-4">
						<input type="submit" value="회원가입" class="btn btn-primary btn-block">
					</div>
				</div>
			</form:form>
		</fieldset>
	</div>
</body>
</html>