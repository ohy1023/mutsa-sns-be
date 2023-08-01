<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>채팅 메시지 전송</title>
</head>
<body>
<h1>채팅 메시지 전송</h1>
<form id="sendMessageForm">
    <input type="hidden" id="chatNo" name="chatNo" value="1"> <!-- 채팅방 번호를 여기에 입력 -->
    <label for="content">메시지 내용:</label>
    <input type="text" id="content" name="content" required>
    <button type="button" onclick="sendMessage()">메시지 전송</button>
</form>

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