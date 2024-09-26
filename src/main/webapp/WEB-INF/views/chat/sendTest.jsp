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
            overflow-y: scroll;
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

        #noticeContainer {
            padding: 10px;
            background-color: #fffdb3;
            border: 1px solid #ff004d;
            border-radius: 5px;
            margin-bottom: 10px;
            text-align: center; /* 텍스트 가운데 정렬 추가 */
            position: relative; /* 포지션 속성 추가 */
        }

        #noticeBox {
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 10px;
            background-color: #fffdb3;
            border-radius: 5px;
            margin-bottom: 10px;
        }

        #noticeText {
            font-weight: bold;
            color: #0a0a07;
            margin-top: -55px; /* '알림' 텍스트 위로 이동 */
            margin-left: -15px; /* '알림' 텍스트 왼쪽으로 이동 */
        }

        #noticeContent {
            flex: 1;
            text-align: center;
        }
    </style>
</head>
<body>
<%@ include file="../common/header.jsp" %>
<h1>채팅 메시지 전송</h1>
<script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.5.1/sockjs.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
<div id="chatContainer">
    <div id="noticeContainer">
        <div id="noticeBox">
            <span id="noticeText">알림</span>
            <div id="noticeContent">
                <span id="noticeTextContent"></span>
            </div>
        </div>
    </div>
    <div id="chatHistory"></div>
    <div id="sendMessageForm">
        <input type="text" id="content" name="content" required placeholder="메시지를 입력하세요">
        <button type="button" id="sendButton" onclick="sendMessage()">전송</button>
        <button type="button" id="exitButton" onclick="exitChatRoom()">채팅방 나가기</button>
    </div>
</div>

<script>
    let noticeHideTimer;

    const currentURL = new URL(window.location.href);
    const chatNo = currentURL.searchParams.get('roomId');

    // WebSocket 연결을 위한 변수 선언
    let stompClient;

    const headers = {
        "Authorization": "Bearer " + localStorage.getItem("accessToken"),
        "chatRoomNo": chatNo
    };

    // WebSocket 연결 함수
    function connectWebSocket() {
        // WebSocket 엔드포인트 주소
        const wsEndpoint = "http://localhost:8081/chat";

        // WebSocket 연결
        const ws = new SockJS(wsEndpoint);
        stompClient = Stomp.over(ws);


        stompClient.connect(headers, function (frame) {
            console.log("WebSocket 연결 성공!");
            // 구독 로직 추가
            stompClient.subscribe("/subscribe/" + chatNo, function (message) {
                // 구독한 토픽에서 메시지를 받았을 때 실행되는 콜백 함수
                const data = JSON.parse(message.body);
                console.log("WebSocket 메시지 수신:", data);
                // 수신된 메시지를 화면에 표시하는 코드 추가

                const chatHistoryDiv = document.getElementById("chatHistory");

                // 공지 메시지 처리
                if (data.senderName === "notice") {
                    showNotice(data.content);
                } else {
                    showChatHistory(data, chatHistoryDiv);

                    const notificationPayload = {
                        content: data.content,
                        senderName: data.senderName,
                        chatNo: data.chatNo,
                        sendTime: data.sendTime,
                        readCount: data.readCount
                    };

                    // HTTP POST 요청을 통해 콜백 엔드포인트 호출
                    fetch("api/v1/chatroom/message-alarm-record", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                            "Authorization": "Bearer " + localStorage.getItem("accessToken")
                            },
                        body: JSON.stringify(notificationPayload)
                    })
                }
            }, headers);
        });
    }

    // WebSocket 연결
    connectWebSocket();

    // 공지 메시지 표시 함수
    function showNotice(content) {
        const noticeDiv = document.createElement("div");
        noticeDiv.classList.add("notice");

        const noticeBubble = document.createElement("div");
        noticeBubble.classList.add("noticeBubble");
        noticeBubble.textContent = content;

        noticeDiv.appendChild(noticeBubble);
        const noticeContainer = document.getElementById("noticeContainer");
        noticeContainer.appendChild(noticeDiv);

        // 5초 후에 공지 메시지 숨기기
        noticeHideTimer = setTimeout(() => {
            noticeDiv.remove();
        }, 5000);
    }


    // 메시지 전송 함수
    function sendMessage() {

        const currentURL = new URL(window.location.href);

        // URLSearchParams 객체 생성
        const searchParams = new URLSearchParams(currentURL.search);

        // roomId 매개변수 값 가져오기
        const chatNo = searchParams.get('roomId');
        const content = document.getElementById("content").value;

        // WebSocket을 통해 메시지 전송
        stompClient.send("/publish/message", headers, JSON.stringify({
            chatNo: chatNo,
            senderName: null,
            sendTime: null,
            content: content,
            readCount: null
        }));

        document.getElementById("content").value = "";

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


    const chatHistoryDiv = document.getElementById("chatHistory"); // chatHistoryDiv 가져오기
    fetchChatHistory(chatNo);

    // 채팅 내역 가져오는 함수
    function fetchChatHistory(chatNo) {
        fetch(`api/v1/chatroom/` + chatNo, {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + localStorage.getItem("accessToken")
            }
        })
            .then(response => response.json())
            .then(data => {
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


    function exitChatRoom() {

        // Authorization 헤더와 함께 STOMP 메시지를 서버로 전송
        stompClient.send("/publish/chatroom/leave", {
            "Authorization": "Bearer " + localStorage.getItem("accessToken") // 헤더에 JWT 토큰을 추가
        }, JSON.stringify({
            chatNo: chatNo,  // 채팅방 번호
            userName: localStorage.getItem("userName") // 로컬 스토리지에서 가져온 사용자 이름
        }));

        stompClient.disconnect(function() {
            console.log("Disconnected from the server");
        });

    }

</script>
</body>
</html>