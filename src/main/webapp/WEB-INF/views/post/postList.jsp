<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
    </style>
</head>
<body>

<%@ include file="../common/header.jsp" %>

<h1>피드 목록</h1>
<div id="post-list">
    <!-- 포스트 목록이 여기에 동적으로 추가됩니다. -->
</div>


<%@ include file="../common/footer.jsp" %>
<script>
    function getAccessToken() {
        return "Bearer " + localStorage.getItem("accessToken");
    }

    function createPostLink(post) {
        const postLink = document.createElement("a");
        postLink.href = "/post-detail?id=" + post.id; // 상세보기 페이지 URL로 이동
        postLink.style.textDecoration = "none";
        postLink.style.color = "inherit";

        const postTitle = document.createElement("h3");
        postTitle.textContent = post.title;
        postLink.appendChild(postTitle);

        return postLink;
    }

    function renderPostList(posts) {
        const postListContainer = document.getElementById("post-list");
        postListContainer.innerHTML = "";

        posts.forEach(post => {
            const postItem = document.createElement("div");
            postItem.classList.add("post-item");

            const postNumber = document.createElement("p");
            postNumber.textContent = "포스트 번호: " + post.id;

            // 포스트 상세보기 페이지로 넘어가는 링크 추가
            const postLink = createPostLink(post);

            const postAuthor = document.createElement("p");
            postAuthor.textContent = "작성자: " + post.userName;

            const postCreatedAt = document.createElement("p");
            postCreatedAt.textContent = "작성 시간: " + post.createdAt;

            const postBody = document.createElement("p");
            postBody.textContent = post.body;

            postItem.appendChild(postNumber);
            postItem.appendChild(postLink);
            postItem.appendChild(postAuthor);
            postItem.appendChild(postCreatedAt);
            postItem.appendChild(postBody);
            postListContainer.appendChild(postItem);
        });
    }


    function fetchPosts() {
        fetch("/api/v1/posts", {
            headers: {
                "Authorization": getAccessToken()
            }
        })
            .then(response => response.json())
            .then(data => {
                renderPostList(data.result.content);
            })
            .catch(error => {
                console.error("Error fetching posts:", error);
            });
    }

    document.addEventListener("DOMContentLoaded", function () {
        // 페이지 로딩 시 포스트 목록을 불러와서 렌더링합니다.
        fetchPosts();
    });
</script>
</body>
</html>