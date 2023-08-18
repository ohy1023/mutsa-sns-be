<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create Post</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            background-color: #f7f7f7;
        }

        h1 {
            margin-top: 20px;
            text-align: center;
            color: #ff7f00; /* 주황색 */
        }

        form {
            max-width: 400px;
            margin: 0 auto;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
            box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
            background-color: #fff;
        }

        label {
            display: block;
            margin-bottom: 5px;
        }

        input[type="text"],
        textarea {
            width: 94%;
            padding: 10px;
            margin-bottom: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
        }

        textarea {
            resize: vertical;
        }

        input[type="submit"] {
            width: 100%;
            padding: 10px;
            background-color: #ff7f00; /* 주황색 */
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }

        input[type="submit"]:hover {
            background-color: #ff9900; /* 더 진한 주황색 */
        }
    </style>
</head>
<body>
<%@ include file="../common/header.jsp" %>
<h1>Create New Post</h1>

<form method="post" action="${pageContext.request.contextPath}/createPost">
    <label for="postTitle">Title:</label>
    <input type="text" name="postTitle" id="postTitle" required>
    <br>
    <label for="postBody">Body:</label>
    <textarea name="postBody" id="postBody" rows="5" required></textarea>
    <br>
    <input type="submit" value="Submit">
</form>

<%@ include file="../common/footer.jsp" %>
<script>
    function getAccessToken() {
        // 로컬 스토리지에서 토큰을 가져오기
        return "Bearer " + localStorage.getItem("accessToken");
    }

    async function createPost(event) {
        event.preventDefault();

        const createPostData = {
            title: document.getElementById("postTitle").value,
            body: document.getElementById("postBody").value,
        }

        const requestBody = JSON.stringify(createPostData);

        try {
            const response = await fetch("/api/v1/posts", {
                method: "POST",
                headers: {
                    "Authorization": getAccessToken(),
                    "Content-Type": "application/json"
                },
                body: requestBody
            });

            if (response.ok) {
                const data = await response.json();
                const postId = data.result.postId;
                const resultCode = data.resultCode;
                alert("포스트 " + postId + " 등록 " + resultCode);
                window.location.href = "/post-detail?id=" + postId;

            } else {
                alert("포스트 등록 실패");
            }
        } catch (error) {
            console.error("포스트 등록 오류:", error);
            alert("포스트 등록 중 오류가 발생했습니다.");
        }
    }

    // 이벤트 리스너를 등록하여 폼 제출 시 createPost() 함수가 호출되도록 합니다.
    document.querySelector("form").addEventListener("submit", createPost);
</script>
</body>
</html>
