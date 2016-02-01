<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Board</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</head>
<body>
	<div class="container">
		<jsp:include page="../common/header.jsp" flush="false">
			<jsp:param value="boardPage" name="page"/>
		</jsp:include>
		
		<div class="page-header">
			<span class="col-sm-4"></span>
			<h2>Bulletin Board</h2>
		</div>
		
		<div class="table-responsive">
			<table class="table table-hover" id="board-table">
				<thead>
					<tr>
						<th>No.</th>
						<th>제목</th>
						<th>글쓴이</th>
						<th>날짜</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${empty postList }">
						<tr>
							<td colspan="4" style="text-align:center;">
								등록된 게시글이 없습니다.
							</td>
						</tr>
					</c:when>
					<c:otherwise>
						<c:forEach var="post" items="${postList }">
							<tr>
								<td>${post.postId }</td>
								<td><a href="./postPage.do?work=2&id=${post.postId }">${post.title }</a></td>
								<td>${post.memberId }</td>
								<td>${post.date }</td>
							</tr>
						</c:forEach>
					</c:otherwise>
					</c:choose>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="4">
							<ul class="pager">
								<c:if test="${pager.hasPre }">
									<li class="previous"><a href="./boardPage.do?current=${pager.currentPage-1 }"></a></li>
								</c:if>
								<c:forEach begin="${pager.startPage }" end="${pager.endPage }" var="i">
									<li>
										<c:choose>
											<c:when test="${pager.currentPage == i }">
												<a>${i }</a>
											</c:when>
											<c:otherwise>
												<a href="./boardPage.do?current=${i }">${i }</a>
											</c:otherwise>
										</c:choose>
									</li>
								</c:forEach>
								<c:if test="${pager.hasPost }">
									<li class="post"><a href="./boardPage.do?current=${pager.currentPage+1 }"></a></li>
								</c:if>
							</ul>
						</td>
					</tr>
					<tr>
						<td colspan="4" style="text-align:right;">
							<a href="./postPage.do?work=1" role="button" class="btn btn-primary">글쓰기</a>
						</td>
					</tr>
				</tfoot>
			</table>
		</div>
	</div>
</body>
</html>