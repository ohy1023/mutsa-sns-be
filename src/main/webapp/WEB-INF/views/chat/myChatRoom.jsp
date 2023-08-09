<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>My Chat Rooms</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
        }

        #chatRoomList {
            width: 80%;
            margin: 0 auto;
            background-color: #ffffff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);
        }

        ul {
            list-style: none;
            padding: 0;
        }

        li {
            padding: 10px;
            border-bottom: 1px solid #e0e0e0;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        a {
            text-decoration: none;
            color: #007bff;
            font-weight: bold;
        }

        .chat-room {
            background-color: #f9f9f9;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            padding: 10px;
            margin: 10px 0;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .room-link {
            color: #007bff;
            text-decoration: none;
        }

        .room-actions {
            text-align: right;
        }
    </style>
</head>
<body>
<%@ include file="../common/header.jsp" %>
<h1 style="text-align: center;">My Chat Rooms</h1>

<div id="chatRoomList">
    <!-- Chat room list will be dynamically populated here -->
</div>

<%@ include file="../common/footer.jsp" %>
<script>
    // Fetch chat room list from API
    fetch("/my-chatroom", {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("accessToken")
        }
    })
        .then(response => response.json())
        .then(data => {
            const chatRoomListDiv = document.getElementById("chatRoomList");
            if (data.resultCode === "SUCCESS") {
                const chatRooms = data.result;
                const ul = document.createElement("ul");

                chatRooms.forEach(chatRoom => {
                    const li = document.createElement("li");
                    li.className = "chat-room";

                    const roomDetails = document.createElement("span");
                    roomDetails.textContent = 'NO.' + chatRoom.chatRoomId + ' - 참가자: ' + chatRoom.joinUserName;

                    const roomLink = document.createElement("a");
                    roomLink.className = "room-link";
                    roomLink.href = "/sendTest?roomId=" + chatRoom.chatRoomId;
                    roomLink.textContent = "입장하기";

                    const roomActions = document.createElement("div");
                    roomActions.className = "room-actions";
                    roomActions.appendChild(roomLink);

                    li.appendChild(roomDetails);
                    li.appendChild(roomActions);

                    ul.appendChild(li);
                });

                chatRoomListDiv.appendChild(ul);
            } else {
                chatRoomListDiv.innerHTML = "<p>No chat rooms available.</p>";
            }
        })
        .catch(error => {
            console.error("Error fetching chat room list:", error);
        });
</script>
</body>
</html>
