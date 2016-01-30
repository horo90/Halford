<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</head>
<body>
	<div class="container-fluid" style="background-color:#F44336;color:#fff;height:100px;">
		<h3>Halford</h3>
	</div>
	
	<nav class="navbar navbar-inverse" data-spy="affix" data-offset-top="197">
		<ul class="nav navbar-nav">
			<li class="${param.page == 'boardPage' ? 'active' : '' }"><a href="./boardPage.do">Home</a></li>
			<li><a href="./logout.do">Logout</a></li>
		</ul>
	</nav>
</body>
</html>