<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Post</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<style type="text/css">
	#post-table th {
		text-align:center;
	}
</style>
</head>
<body>
	<div class="container">
		<jsp:include page="../common/header.jsp" flush="false">
			<jsp:param value="postPage" name="page"/>
		</jsp:include>
		
		<div class="page-header">
			<span class="col-sm-1"></span>
			<h2>
				<c:choose>
					<c:when test="${work == 1 }">New Post</c:when>
					<c:when test="${work == 2 }">Post</c:when>
					<c:when test="${work == 3 }">Modify Post</c:when>
				</c:choose>
			</h2>
		</div>
		<c:choose>
			<c:when test="${work == 1 || work == 3}">
				<fieldset>
					<form:form role="form" class="form-horizontal" modelAttribute="post" action="./post.do" method="post">
						<c:if test="${work == 3 }">
							<form:hidden path="postId" value="${post.postId }"/>
							<form:hidden path="date" value="${post.date }"/>
						</c:if>
						<div class="form-group">
							<form:label path="title" class="control-label col-sm-3">제목</form:label>
							<div class="col-sm-6">
								<form:input path="title" type="text" class="form-control" value="${post.title }"/>
							</div>
						</div>
						<div class="form-group">
							<form:label path="memberId" class="control-label col-sm-3">글쓴이</form:label>
							<div class="col-sm-9">
								${post.memberId }
							</div>
						</div>
						<div class="form-group">
							<form:label path="date" class="control-label col-sm-3">작성일</form:label>
							<div class="col-sm-9">
								${post.date }
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-sm-3">내용</label>
							<div class="col-sm-6">
								<form:textarea path="contents" rows="10" class="form-control" value="${post.contents }"/>
							</div>
						</div>
						<div class="form-group">
							<span class="col-sm-8"></span>
							<div class="col-sm-2">
								<input type="submit" value="${work == 1 ? '등록' : '수정' }" class="btn btn-primary">
							</div>
						</div>
						<input type="hidden" name="work" value="${work }">
					</form:form>
				</fieldset>
			</c:when>
			<c:when test="${work == 2 }">
				<div class="table-responsive">
					<table class="table" id="post-table">
						<tbody>
							<tr>
								<th>제목</th>
								<td>${post.title }</td>
							</tr>
							<tr>
								<th>작성자</th>
								<td>${post.memberId }</td>
							</tr>
							<tr>
								<th>작성일</th>
								<td>${post.date }</td>
							</tr>
							<tr>
								<th colspan="2">내용</th>
							</tr>
							<tr>
								<td colspan="2" id="contents" style="padding:20px 150px 20px 150px;">
									${post.contents }
								</td>
							</tr>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="2" style="text-align:right;">
									<c:if test="${sessionScope.id == post.memberId }">
										<a href="./post.do?work=4&id=${post.postId }" role="button" class="btn btn-danger">삭제</a>
										<a href="./postPage.do?work=3&id=${post.postId }" role="button" class="btn btn-default">수정</a>
									</c:if>
								</td>
							</tr>
						</tfoot>
					</table>
				</div>
			</c:when>
		</c:choose>
		
	</div>
</body>
</html>