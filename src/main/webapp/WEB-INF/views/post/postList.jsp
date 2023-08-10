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
            margin: 0; /* 헤더의 기본 마진 제거 */
            position: fixed; /* 헤더를 고정 위치로 설정 */
            top: 0;
            left: 0;
            right: 0;
        }

        #post-list {
            max-width: 800px;
            margin: 80px auto 20px; /* 헤더의 높이와 동일한 마진을 주어 포스트 목록과 헤더가 붙어있도록 설정 */
            padding: 20px;
            background-color: #ffffff;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
            border-radius: 5px;
        }

        .post-item {
            margin-bottom: 20px;
            padding: 10px;
            background-color: #ffffff;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
            border-radius: 5px;
        }

        .post-item h3 {
            margin: 0;
            font-size: 20px;
            color: #ff7f00;
        }

        .post-item p {
            margin: 10px 0;
        }

        .create-post-button {
            display: inline-block;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
            text-align: center;
            text-decoration: none;
            transition: background-color 0.3s;
        }

        .create-post-button:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>

<%@ include file="../common/header.jsp" %>

<h1>피드 목록</h1>
<div id="post-list">
    <%-- 포스트 목록을 동적으로 추가합니다. --%>
    <c:forEach items="${postList}" var="post">
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

<div style="position: fixed; right: 400px; bottom: 750px;">
    <a class="create-post-button" href="/createPost">글 작성하기</a>
</div>

<%@ include file="../common/footer.jsp" %>

</body>
</html>
