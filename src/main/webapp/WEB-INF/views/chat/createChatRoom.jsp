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
            background-color: #f7f7f7;
        }

        header {
            background-color: #333;
            color: white;
            padding: 10px;
            text-align: center;
        }

        h1 {
            margin-top: 20px;
            text-align: center;
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

        input[type="number"] {
            width: 100%;
            padding: 10px;
            margin-bottom: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
        }

        button[type="submit"] {
            width: 100%;
            padding: 10px;
            background-color: #333;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }

        button[type="submit"]:hover {
            background-color: #ff9900;
        }
    </style>
</head>
<body>
<%@ include file="../common/header.jsp" %>
<h1>채팅방 생성</h1>
<form action="/chatroom" method="post" onsubmit="createChatRoom(event)">
    <label for="joinUserId">참가자 ID:</label>
    <input type="number" id="joinUserId" name="joinUserId" required>
    <button type="submit">채팅방 생성</button>
</form>

<%@ include file="../common/footer.jsp" %>

<script>
    function getAccessToken() {
        // 로컬 스토리지에서 토큰을 가져오기
        return "Bearer " + localStorage.getItem("accessToken");
    }

    function createChatRoom(event) {
        event.preventDefault();
        const formData = new URLSearchParams();
        formData.append("joinUserId", document.getElementById("joinUserId").value);

        const xhr = new XMLHttpRequest();
        xhr.open("POST", "/chatroom");
        xhr.setRequestHeader("Authorization", getAccessToken());
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    const response = JSON.parse(xhr.responseText);
                    const chatNo = response.result.chatNo
                    const code = response.resultCode
                    alert("채팅방 " + chatNo + "번 생성 " + code);
                } else {
                    alert("채팅방 생성 실패");
                }
            }
        };
        xhr.send(formData);
    }
</script>
</body>
</html>