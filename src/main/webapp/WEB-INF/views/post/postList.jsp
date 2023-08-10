<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>멋쟁이 사자처럼 SNS</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            margin: 0;
            padding: 0;
            background-color: #f2f2f2;
        }

        h1 {
            text-align: center;
            padding: 20px 0;
            background-color: #ff7f00;
            color: white;
            margin: 0;
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
        }

        #post-list {
            max-width: 800px;
            margin: 80px auto 20px;
            padding: 20px;
            background-color: #ffffff;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
            border-radius: 5px;
        }

        .post-item {
            background-color: #ffffff;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
            border-radius: 5px;
            padding: 20px;
        }

        .post-item h3 {
            margin: 0;
            font-size: 20px;
            color: #ff7f00;
        }

        .post-item p {
            margin: 10px 0;
        }

        .page-button {
            display: inline-block;
            padding: 8px 16px;
            margin: 4px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
            text-align: center;
            text-decoration: none;
            transition: background-color 0.3s;
        }

        .page-button:hover {
            background-color: #0056b3;
        }

        .create-post-button {
            position: fixed;
            right: 20px;
            bottom: 20px;
        }
    </style>
</head>
<body>

<%@ include file="../common/header.jsp" %>

<div id="post-list">
    <%-- 포스트 목록을 동적으로 추가합니다. --%>
    <c:forEach items="${postList}" var="post">
        <!-- 각각의 포스트 아이템 내용 생성 -->
        <div class="post-item">
            <p>NO.${post.id}</p>
            <a href="/post-detail?id=${post.id}" style="text-decoration: none; color: inherit;">
                <h3>${post.title}</h3>
            </a>
            <p>작성자: ${post.userName}</p>
            <p>작성 시간: ${post.createdAt}</p>
            <p>${post.body}</p>
        </div>
    </c:forEach>
</div>

<div style="text-align: center; margin-top: 20px;">
    <%-- 페이지 번호를 생성하여 페이지 버튼 추가 --%>
    <c:choose>
        <c:when test="${totalPages > 1}">
            <c:forEach begin="1" end="${totalPages}" var="pageNum">
                <c:url value="/post-list" var="pagingUrl">
                    <c:param name="page" value="${pageNum - 1}"/>
                </c:url>
                <c:choose>
                    <c:when test="${number + 1 == pageNum}">
                        <strong><a class="page-button" href="${pagingUrl}">${pageNum}</a></strong>
                    </c:when>
                    <c:otherwise>
                        <a class="page-button" href="${pagingUrl}">${pageNum}</a>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </c:when>
    </c:choose>
</div>

<div class="create-post-button">
    <a href="/createPost" class="page-button">글 작성하기</a>
</div>

</body>
</html>
