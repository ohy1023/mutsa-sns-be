<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>포스트 상세보기</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            margin: 0;
            padding: 0;
            background-color: #f2f2f2;
        }

        .post-detail {
            max-width: 800px;
            margin: 80px auto 20px;
            padding: 20px;
            background-color: #ffffff;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
            border-radius: 5px;
        }

        .post-detail h1 {
            font-size: 24px;
            color: #ff7f00;
            margin-bottom: 10px;
        }

        .post-detail p {
            margin: 5px 0;
        }

        .comment-input {
            display: flex;
            margin-top: 20px;
        }

        .comment-input input {
            flex: 1;
            padding: 5px;
            margin-right: 10px;
        }

        .comment-list {
            margin-top: 20px;
        }

        .comment-item {
            padding: 10px;
            background-color: #ffffff;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
            border-radius: 5px;
            margin-bottom: 10px;
        }

        .comment-item p {
            margin: 5px 0;
        }
    </style>
</head>
<body>

<%@ include file="../common/header.jsp" %>

<div class="post-detail">
    <h1 id="post-title"></h1>
    <p id="post-author"></p>
    <p id="post-created-at"></p>
    <p id="post-body"></p>
    <p id="post-like-count"></p>
    <button id="like-button" data-is-liked="false">좋아요</button>
    <p id="post-comment-count"></p>

    <div class="comment-input">
        <input type="text" id="comment-input" placeholder="댓글을 입력하세요">
        <button onclick="addComment('<%=request.getParameter("id")%>')">등록</button>
    </div>

    <div class="comment-list" id="comment-list">
        <!-- 댓글 목록이 여기에 동적으로 추가됩니다. -->
    </div>
</div>

<%@ include file="../common/footer.jsp" %>

<script>
    function getAccessToken() {
        return "Bearer " + localStorage.getItem("accessToken");
    }

    function renderPostDetail(post) {
        const postTitle = document.getElementById("post-title");
        const postAuthor = document.getElementById("post-author");
        const postCreatedAt = document.getElementById("post-created-at");
        const postBody = document.getElementById("post-body");
        const postLikeCount = document.getElementById("post-like-count");
        const postCommentCount = document.getElementById("post-comment-count");


        postTitle.textContent = post.title;
        postAuthor.textContent = "작성자: " + post.userName;
        postCreatedAt.textContent = "작성 시간: " + post.createdAt;
        postBody.textContent = post.body;
        postLikeCount.textContent = "좋아요 개수: " + post.likeCnt;
        postCommentCount.textContent = "총 댓글 개수: " + post.commentCnt;
    }

    function renderComment(comments) {
        const commentList = document.getElementById("comment-list");

        // 댓글 목록을 렌더링합니다.
        commentList.innerHTML = "";
        comments.forEach(comment => {
            const commentItem = document.createElement("div");
            commentItem.classList.add("comment-item");

            const commentAuthor = document.createElement("p");
            commentAuthor.textContent = "작성자: " + comment.userName;

            const commentCreatedAt = document.createElement("p");
            commentCreatedAt.textContent = "작성 시간: " + comment.createdAt;

            const commentText = document.createElement("p");
            commentText.textContent = comment.comment;

            commentItem.appendChild(commentAuthor);
            commentItem.appendChild(commentCreatedAt);
            commentItem.appendChild(commentText);
            commentList.appendChild(commentItem);
        });
    }

    function fetchPostDetail(postId) {
        fetch("/api/v1/posts/" + postId, {
            headers: {
                "Authorization": getAccessToken()
            }
        })
            .then(response => response.json())
            .then(data => {
                renderPostDetail(data.result);
            })
            .catch(error => {
                console.error("Error fetching post detail:", error);
            });
    }

    function fetchComment(postId) {
        fetch("/api/v1/posts/" + postId + "/comments", {
            headers: {
                "Authorization": getAccessToken()
            }
        })
            .then(response => response.json())
            .then(data => {
                renderComment(data.result.content);
            })
            .catch(error => {
                console.error("Error fetching comment:", error);
            });
    }

    function addComment(postId) {
        const commentInput = document.getElementById("comment-input");
        const commentText = commentInput.value;

        // 서버에 댓글 추가 요청을 보내고, 성공하면 댓글 목록을 다시 불러옵니다.
        // (댓글 추가 요청을 보내는 방식은 서버와의 API에 따라 달라질 수 있습니다.)
        fetch("/api/v1/posts/" + postId + "/comments", {
            method: "POST",
            headers: {
                "Authorization": getAccessToken(),
                "Content-Type": "application/json"
            },
            body: JSON.stringify({comment: commentText})
        })
            .then(response => response.json())
            .then(data => {
                // 댓글 추가 성공시 댓글 목록을 다시 불러옵니다.
                fetchComment(postId);
            })
            .catch(error => {
                console.error("Error adding comment:", error);
            });

        // 댓글 입력창 비우기
        commentInput.value = "";
    }

    function handleLike(postId) {
        const likeButton = document.getElementById("like-button");
        const isLiked = likeButton.dataset.isLiked === "true";

        const method = isLiked ? "DELETE" : "POST";

        fetch("/api/v1/posts/" + postId + "/likes", {
            method: method,
            headers: {
                "Authorization": getAccessToken(),
                "Content-Type": "application/json"
            }
        })
            .then(response => response.json())
            .then(data => {
                // 요청에 성공했을 때 처리할 내용
                if (isLiked) {
                    likeButton.dataset.isLiked = "false";
                    likeButton.textContent = "좋아요";
                } else {
                    likeButton.dataset.isLiked = "true";
                    likeButton.textContent = "좋아요 취소";
                }
            })
            .catch(error => {
                console.error("Error handling like:", error);
            });
    }

    document.addEventListener("DOMContentLoaded", function () {
        const urlParams = new URLSearchParams(window.location.search);
        const postId = urlParams.get("id");

        fetchPostDetail(postId);
        fetchComment(postId);

        // 좋아요 버튼에 클릭 이벤트 추가
        const likeButton = document.getElementById("like-button");
        likeButton.addEventListener("click", function () {
            handleLike(postId);
        });
    });
</script>
</body>
</html>