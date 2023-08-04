<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>WebSocket 사용 예제</title>
</head>
<body>
<h1>WebSocket 사용 예제</h1>
<!-- WebSocket 클라이언트 라이브러리 불러오기 -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.js"
        integrity="sha512-tL4PIUsPy+Rks1go4kQG8M8/ItpRMvKnbBjQm4d2DQnFwgcBYRRN00QdyQnWSCwNMsoY/MfJY8nHp2CzlNdtZA=="
        crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.5.1/sockjs.min.js"></script>

<script>
    // WebSocket 연결을 위한 변수 선언
    let stompClient;
    let isConnected = false;

    // JWT 토큰 설정 함수
    function getAccessToken() {
        // 로컬 스토리지에서 토큰을 가져오기
        return "Bearer " + localStorage.getItem("accessToken");
    }

    // WebSocket 연결 함수
    function connectWebSocket() {
        const wsEndpoint = 'http://localhost:8081/chat'; // WebSocket 연결 엔드포인트 주소
        const socket = new SockJS(wsEndpoint);
        stompClient = Stomp.over(socket);

        // STOMP 연결 완료 시 처리
        stompClient.connect({'Authorization': getAccessToken(), 'chatRoomNo': 13}, function (frame) {
            console.log('WebSocket 연결 성공!');
            isConnected = true;

            subscribeToTopic('/subscribe/13');
        });
    }

    // 구독 설정 함수
    function subscribeToTopic(topic) {
        stompClient.subscribe(topic, function (message) {
            const receivedMessage = JSON.parse(message.body);
            console.log('새로운 메시지 수신:', receivedMessage);
            // 원하는 방식으로 메시지 처리를 수행합니다.
        });
    }

    // WebSocket 연결
    connectWebSocket();

    // 연결 상태 확인 함수
    function checkConnection() {
        if (isConnected) {
            console.log('WebSocket 연결이 완료되었습니다.');
        } else {
            console.log('WebSocket 연결이 아직 완료되지 않았습니다.');
        }
    }

    // 1초마다 연결 상태 확인
    setInterval(checkConnection, 1000);

    // 메시지 전송 함수
    function sendMessage() {
        if (!isConnected) {
            console.log('WebSocket 연결이 완료되지 않았습니다.');
            return;
        }

        const message = {
            content: "hello",
            senderName: "test",
            chatNo: 13, // 채팅방 번호 추가
            sendTime: 0, // 현재 시간을 사용하여 메시지 시간 정보 추가
            readCount: 0
        };

        // WebSocket을 통해 메시지 전송
        stompClient.send("/message", {'Authorization': getAccessToken()}, JSON.stringify(message));

        // 콜백 API 호출
        callCallbackAPI(message);
    }

    // 콜백 API 호출 함수
    function callCallbackAPI(message) {
        const accessToken = getAccessToken();

        fetch("/chatroom/notification", {
            method: "POST",
            headers: {
                "Authorization": accessToken,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(message)
        })
            .then(response => {
                if (!response.ok) {
                    console.error("콜백 API 호출에 실패하였습니다.");
                }
                return response.json();
            })
            .then(data => {
                console.log("콜백 API 응답:", data);
            })
            .catch(error => {
                console.error("콜백 API 호출 중 오류 발생:", error);
            });
    }
</script>

<!-- 메시지 전송 버튼 -->
<button onclick="sendMessage()">메시지 보내기</button>

</body>
</html>