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
            width: 94%;
            padding: 10px;
            margin-bottom: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
        }

        button[type="submit"] {
            width: 100%;
            padding: 10px;
            background-color: #ff7f00;
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

    async function createChatRoom(event) {
        event.preventDefault();


        const createChatData = {
            joinUserId: document.getElementById("joinUserId").value,
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