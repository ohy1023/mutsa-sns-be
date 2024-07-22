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

    <div id="action-buttons"></div>

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

        // 작성자가 localstorage에 있는 userName과 같다면 수정하기, 삭제하기 버튼 추가
        const actionButtons = document.getElementById("action-buttons");
        const loggedInUserName = localStorage.getItem("userName");

        if (post.userName === loggedInUserName) {
            const editButton = document.createElement("button");
            editButton.textContent = "수정하기";
            editButton.onclick = function () {
                goEditPost(post.id);
            };

            const deleteButton = document.createElement("button");
            deleteButton.textContent = "삭제하기";
            deleteButton.onclick = function () {
                // 삭제하기 버튼 클릭 시 동작
                if (confirm("정말 삭제하시겠습니까?")) {
                    deletePost(post.id);
                }
            };

            actionButtons.appendChild(editButton);
            actionButtons.appendChild(deleteButton);
        } else {
            const chatButton = document.createElement("button");
            chatButton.textContent = "채팅하기";
            chatButton.onclick = function () {
                // 채팅하기 버튼 클릭 시 동작
                createChatRoom(post.userName);
            };

            actionButtons.appendChild(chatButton);
        }
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
                fetchComment(postId);
            })
            .catch(error => {
                console.error("Error adding comment:", error);
            });

        commentInput.value = "";
    }

    function goEditPost(postId) {
        window.location.href = '/post-update?id=' + postId;
    }

    function deletePost(postId) {
        fetch("/api/v1/posts/" + postId, {
            method: "DELETE",
            headers: {
                "Authorization": getAccessToken(),
                "Content-Type": "application/json"
            }
        })
            .then(response => response.json())
            .then(data => {
                if (data.resultCode === "SUCCESS") {
                    alert("게시글이 삭제되었습니다.");
                    window.location.href = "/post-list";
                } else {
                    alert("게시글 삭제에 실패했습니다.");
                }
            })
            .catch(error => {
                console.error("Error deleting post:", error);
            });
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

        const likeButton = document.getElementById("like-button");
        likeButton.addEventListener("click", function () {
            handleLike(postId);
        });
    });


    function getAccessToken() {
        // 로컬 스토리지에서 토큰을 가져오기
        return "Bearer " + localStorage.getItem("accessToken");
    }

    async function createChatRoom(userName) {

        const createChatData = {
            joinUserName: userName,
        }

        const requestBody = JSON.stringify(createChatData);

        try {
            const response = await fetch("/chatroom", {
                method: "POST",
                headers: {
                    "Authorization": getAccessToken(),
                    "Content-Type": "application/json"
                },
                body: requestBody
            });

            if (response.ok) {
                const data = await response.json();
                const chatNo = data.result.chatNo;
                const code = data.resultCode;
                alert("채팅방 " + chatNo + "번 생성 " + code);
                window.location.href = `/sendTest?roomId=` + chatNo;

            } else {
                alert("채팅방 생성 실패");
            }
        } catch (error) {
            console.error("채팅방 생성 오류:", error);
            alert("채팅방 생성 중 오류가 발생했습니다.");
        }
    }

</script>
</body>
</html>
