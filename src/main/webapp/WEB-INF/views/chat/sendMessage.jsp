<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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

        input[type="text"] {
            width: 100%;
            padding: 10px;
            margin-bottom: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
        }

        button[type="button"] {
            width: 100%;
            padding: 10px;
            background-color: #333;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }

        button[type="button"]:hover {
            background-color: #ff9900;
        }
    </style>
</head>
<body>
<%@ include file="../common/header.jsp" %>
<h1>채팅 메시지 전송</h1>
<form id="sendMessageForm">
    <input type="hidden" id="chatNo" name="chatNo" value="1"> <!-- 채팅방 번호를 여기에 입력 -->
    <label for="content">메시지 내용:</label>
    <input type="text" id="content" name="content" required>
    <button type="button" onclick="sendMessage()">메시지 전송</button>
</form>
<%@ include file="../common/footer.jsp" %>
<script>
    function getAccessToken() {
        // 로컬 스토리지에서 토큰을 가져오기
        return "Bearer " + localStorage.getItem("accessToken");
    }

    function sendMessage() {
        const formData = new FormData(document.getElementById("sendMessageForm"));
        const xhr = new XMLHttpRequest();
        xhr.open("POST", "/message");
        xhr.setRequestHeader("Authorization", getAccessToken()); // 로컬 스토리지에서 토큰을 가져와서 설정
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    const response = JSON.parse(xhr.responseText);
                    alert("메시지 전송 성공: " + response.data);
                } else {
                    alert("메시지 전송 실패");
                }
            }
        };
        xhr.send(formData);
    }
</script>
</body>
</html>