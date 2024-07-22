<%--
  Created by IntelliJ IDEA.
  User: ohy
  Date: 2024-07-22
  Time: 오후 4:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>게시글 수정</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            margin: 0;
            padding: 0;
            background-color: #f2f2f2;
        }

        .edit-post {
            max-width: 800px;
            margin: 80px auto 20px;
            padding: 20px;
            background-color: #ffffff;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
            border-radius: 5px;
        }

        .edit-post h1 {
            font-size: 24px;
            color: #ff7f00;
            margin-bottom: 10px;
        }

        .edit-post input, .edit-post textarea {
            width: 100%;
            padding: 10px;
            margin: 10px 0;
            border: 1px solid #ddd;
            border-radius: 5px;
        }

        .edit-post button {
            background-color: #ff7f00;
            color: #ffffff;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
        }
    </style>
</head>
<body>

<%@ include file="../common/header.jsp" %>

<div class="edit-post">
    <h1>게시글 수정</h1>
    <input type="text" id="post-title" placeholder="제목을 입력하세요"/>
    <textarea id="post-body" rows="10" placeholder="내용을 입력하세요"></textarea>
    <button onclick="updatePost('<%=request.getParameter("id")%>')">수정 완료</button>
</div>

<%@ include file="../common/footer.jsp" %>

<script>
    function getAccessToken() {
        return "Bearer " + localStorage.getItem("accessToken");
    }

    function fetchPostDetail(postId) {
        fetch("/api/v1/posts/" + postId, {
            headers: {
                "Authorization": getAccessToken()
            }
        })
            .then(response => response.json())
            .then(data => {
                const post = data.result;
                document.getElementById("post-title").value = post.title;
                document.getElementById("post-body").value = post.body;
            })
            .catch(error => {
                console.error("게시글을 가져오는 중 오류 발생:", error);
            });
    }

    function updatePost(postId) {
        const postTitle = document.getElementById("post-title").value;
        const postBody = document.getElementById("post-body").value;

        fetch("/api/v1/posts/" + postId, {
            method: "PUT",
            headers: {
                "Authorization": getAccessToken(),
                "Content-Type": "application/json"
            },
            body: JSON.stringify({title: postTitle, body: postBody})
        })
            .then(response => response.json())
            .then(data => {
                if (data.resultCode === "SUCCESS") {
                    alert("게시글이 수정되었습니다.");
                    window.location.href = `/post-detail?id=`+postId;
                } else {
                    alert("게시글 수정에 실패했습니다.");
                }
            })
            .catch(error => {
                console.error("게시글 수정 중 오류 발생:", error);
            });
    }

    document.addEventListener("DOMContentLoaded", function () {
        const urlParams = new URLSearchParams(window.location.search);
        const postId = urlParams.get("id");
        fetchPostDetail(postId);
    });
</script>
</body>
</html>
