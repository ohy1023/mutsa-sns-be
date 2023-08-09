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

        /* 추가한 스타일 */
        header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px 20px;
            background-color: #ff7f00;
        }

        a {
            text-decoration: none;
            color: white;
            padding: 5px 10px;
        }

        a:hover {
            background-color: #ff9900;
        }

        #chatContainer {
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            display: flex;
            flex-direction: column;
            background-color: white;
            border: 1px solid #ddd;
            border-radius: 5px;
            box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
            overflow-y: scroll; /* 스크롤이 생길 경우 자동으로 스크롤바가 생성됨 */
        }

        #chatHistory {
            flex: 1;
            overflow-y: auto; /* 스크롤이 생길 경우 자동으로 스크롤바가 생성됨 */
            margin-bottom: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            padding: 10px;
        }

        #sendMessageForm {
            display: flex;
            gap: 10px;
        }

        #content {
            flex: 1;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
        }

        #sendButton {
            padding: 10px 20px;
            background-color: #333;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }

        #sendButton:hover {
            background-color: #ff9900;
        }

        .messageContainer {
            display: flex;
            justify-content: flex-start;
            align-items: center;
            margin: 5px;
        }

        .messageBubble {
            background-color: #66a3d6;
            color: white;
            padding: 10px;
            border-radius: 10px;
            max-width: 70%;
            word-wrap: break-word;
        }

        .myMessageContainer {
            justify-content: flex-end;
        }

        .myMessageBubble {
            background-color: #67d66d;
        }
    </style>
</head>
<body>
<%@ include file="../common/header.jsp" %>
<h1>채팅 메시지 전송</h1>
<script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.5.1/sockjs.min.js"></script>
<div id="chatContainer">
    <div id="chatHistory"></div>
    <div id="sendMessageForm">
        <input type="text" id="content" name="content" required placeholder="메시지를 입력하세요">
        <button type="button" id="sendButton" onclick="sendMessage()">전송</button>
    </div>
</div>

<script>
    // WebSocket 연결을 위한 변수 선언
    let ws;

    // WebSocket 연결 함수
    function connectWebSocket() {
        // WebSocket 엔드포인트 주소
        const wsEndpoint = "http://localhost:8081/chat";

        // WebSocket 연결
        ws = new SockJS(wsEndpoint);

        // WebSocket 연결 이벤트 리스너
        ws.onopen = function () {
            console.log("WebSocket 연결 성공!");
        };

        // WebSocket 메시지 수신 이벤트 리스너
        ws.onmessage = function (event) {
            const data = JSON.parse(event.data);
            console.log("WebSocket 메시지 수신:", data);
        };

        // WebSocket 연결 종료 이벤트 리스너
        ws.onclose = function () {
            console.log("WebSocket 연결 종료!");
        };
    }

    // WebSocket 연결
    connectWebSocket();

    // 메시지 전송 함수
    function sendMessage() {

        const currentURL = new URL(window.location.href);

        // URLSearchParams 객체 생성
        const searchParams = new URLSearchParams(currentURL.search);

        // roomId 매개변수 값 가져오기
        const chatNo = searchParams.get('roomId');
        const content = document.getElementById("content").value;
        const currentTimeInMillis = new Date().getTime();

        // WebSocket을 통해 메시지 전송
        ws.send(JSON.stringify({
            chatNo: chatNo,
            content: content
        }));


        // HTTP POST 요청을 통해 콜백 엔드포인트 호출
        fetch("/chatroom/notification", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + localStorage.getItem("accessToken")
            },
            body: JSON.stringify({
                content: content,
                senderName: localStorage.getItem("userName"),
                chatNo: chatNo, // 채팅방 번호 추가
                sendTime: currentTimeInMillis.toString(), // 현재 시간을 사용하거나 적절한 방법으로 시간 정보를 추가
                readCount: 0
            })
        })
            .then(response => response.json())
            .then(data => {
                console.log("콜백 데이터 수신:", data)
                document.getElementById("content").value = "";
                chatHistoryDiv.innerHTML = "";
                fetchChatHistory(chatNo);
            })
            .catch(error => console.error("콜백 요청 실패:", error));
    }


    // 채팅 내역 표시 함수
    function showChatHistory(message, chatHistoryDiv) {
        const messageContainer = document.createElement("div");
        messageContainer.classList.add("messageContainer");

        const messageBubble = document.createElement("div");
        messageBubble.classList.add("messageBubble");

        const userName = localStorage.getItem("userName");

        if (message.senderName === userName) {
            messageContainer.classList.add("myMessageContainer");
            messageBubble.classList.add("myMessageBubble");
        }

        messageBubble.textContent = message.content;
        messageContainer.appendChild(messageBubble);
        chatHistoryDiv.appendChild(messageContainer);
    }

    const currentURL = new URL(window.location.href);
    const chatNo = currentURL.searchParams.get('roomId');
    const chatHistoryDiv = document.getElementById("chatHistory"); // chatHistoryDiv 가져오기
    fetchChatHistory(chatNo);

    // 채팅 내역 가져오는 함수
    function fetchChatHistory(chatNo) {
        fetch(`/chatroom/` + chatNo, {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + localStorage.getItem("accessToken")
            }
        })
            .then(response => response.json())
            .then(data => {
                console.log(data.result);
                const chatHistory = data.result.chatList;

                // 채팅 내역을 순회하며 화면에 표시
                chatHistory.forEach(message => {
                    showChatHistory(message, chatHistoryDiv);
                });
            })
            .catch(error => {
                console.error("Error fetching chat history:", error);
            });
    }
</script>
</body>
</html>