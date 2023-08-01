<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>채팅방 생성</title>
</head>
<body>
<h1>채팅방 생성</h1>
<form action="/chatroom" method="post" onsubmit="createChatRoom(event)">
    <label for="joinUserId">참가자 ID:</label>
    <input type="number" id="joinUserId" name="joinUserId" required>
    <button type="submit">채팅방 생성</button>
</form>

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