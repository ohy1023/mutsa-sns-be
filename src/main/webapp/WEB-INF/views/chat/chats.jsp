<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>채팅</title>
    <style>
        /* 스타일 설정 */
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            background-color: #f7f7f7;
            display: flex;
        }

        /* 채팅 목록 스타일 */
        #chatList {
            flex: 1;
            max-width: 200px;
            border: 1px solid #ddd;
            border-radius: 5px;
            padding: 10px;
            margin: 10px;
        }

        /* 채팅 목록 목록 스타일 */
        #chatList ul {
            list-style: none;
            padding: 0;
        }

        /* 채팅 목록 아이템 스타일 */
        #chatList li {
            cursor: pointer;
            padding: 5px;
            border-radius: 3px;
            margin-bottom: 5px;
        }

        /* 채팅 박스 스타일 */
        #chatBox {
            flex: 3;
            max-width: 400px;
            border: 1px solid #ddd;
            border-radius: 5px;
            padding: 10px;
            margin: 10px;
        }

        /* 채팅 메시지 목록 스타일 */
        #chatBox .chatMessages {
            max-height: 300px;
            overflow-y: auto;
        }

        /* 채팅 메시지 스타일 */
        #chatBox .chatMessage {
            margin-bottom: 5px;
        }

        /* 내 메시지 스타일 */
        #chatBox .myMessage {
            text-align: right;
            color: blue;
        }

        /* 상대방 메시지 스타일 */
        #chatBox .otherMessage {
            text-align: left;
            color: green;
        }

        /* 채팅 입력 스타일 */
        #chatBox .chatInput {
            width: 100%;
            padding: 5px;
            border: 1px solid #ddd;
            border-radius: 3px;
            margin-top: 10px;
        }

    </style>

</head>
<body>
<!-- 왼쪽 박스: 채팅 창 목록 -->
<div id="chatList">
    <h3>채팅 창 목록</h3>
    <ul>
        <li onclick="showChatBox(1)">채팅방 1</li>
        <li onclick="showChatBox(2)">채팅방 2</li>
        <li onclick="showChatBox(3)">채팅방 3</li>
        <!-- 채팅방 목록을 서버에서 동적으로 받아와 생성할 수도 있습니다. -->
    </ul>
</div>

<!-- 오른쪽 박스: 채팅 내용 및 입력 창 -->
<div id="chatBox">
    <div class="chatMessages" id="chatMessages">
        <!-- 채팅 내용이 표시되는 영역 -->
    </div>
    <input type="text" class="chatInput" id="chatInput" placeholder="채팅을 입력하세요.">
    <button onclick="sendMessage()">전송</button>
</div>

<!-- JavaScript 코드 -->
<script>
    // 채팅창 목록에서 채팅방 클릭시 해당 채팅방으로 전환하는 함수
    function showChatBox(chatNo) {
        // 채팅창 목록에서 클릭한 채팅방에 대한 처리 로직 추가
        // 서버에서 해당 채팅방의 이전 채팅 내용을 받아와서 chatMessages 영역에 표시하는 로직을 추가해야 합니다.
        // 아래는 예시로 임시로 채팅 내용을 추가하는 방법입니다.
        document.getElementById('chatMessages').innerHTML = '<div class="chatMessage myMessage">안녕하세요!</div><div class="chatMessage otherMessage">반갑습니다.</div>';
    }

    // 메시지 전송 함수
    function sendMessage() {
        const chatNo = 1; // 현재 선택한 채팅방 번호
        const message = document.getElementById('chatInput').value;
        // 메시지 전송 로직 추가 (WebSocket 등을 활용하여 서버로 메시지를 전송하는 코드)
        // 서버로 메시지를 전송한 후, 아래와 같이 채팅 메시지를 chatMessages 영역에 표시해주어야 합니다.
        document.getElementById('chatMessages').innerHTML += '<div class="chatMessage myMessage">' + message + '</div>';
        // 입력창 비우기
        document.getElementById('chatInput').value = '';
    }
</script>
</body>
</html>
