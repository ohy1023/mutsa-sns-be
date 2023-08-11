<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<% request.setCharacterEncoding("UTF-8"); %>
<% response.setContentType("text/html; charset=UTF-8"); %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>멋쟁이 사자처럼 SNS</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            margin: 0;
            padding: 0;
        }

        header {
            background-color: #ff7f00;
            font-weight: bold;
            font-size: 20pt;
            height: 50px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0 20px;
        }

        .header-left {
            display: flex;
            align-items: center;
        }

        .header-center {
            display: flex;
            align-items: center;
        }

        .header-center a {
            margin: 0 10px;
        }

        .header-right {
            display: flex;
            align-items: center;
        }

        .header-link {
            text-decoration: none;
            color: white;
            padding: 10px 20px;
        }

        .header-link:hover {
            background-color: #ff9900;
        }

        .header-link:not(:last-child) {
            margin-right: 10px;
        }

        .notification-icon {
            cursor: pointer;
            font-size: 20pt;
            position: relative;
        }

        .notification-popup {
            display: none;
            position: absolute;
            top: 40px;
            left: 1680px;
            background-color: #ffffff;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
            padding: 10px;
            border-radius: 5px;
            z-index: 999;
        }

        .notification-popup h3 {
            margin: 0;
            padding-bottom: 10px;
            border-bottom: 1px solid #ccc;
        }

        .notification-list {
            list-style-type: none;
            padding: 0;
            margin: 0;
        }

        .notification-list li {
            padding: 5px 0;
        }
    </style>
</head>
<body>
<header>
    <script>
        function getAccessToken() {
            return "Bearer " + localStorage.getItem("accessToken");
        }

        function logout() {
            localStorage.removeItem('accessToken');
            localStorage.removeItem('userName');
            window.location.reload();
        }

        function togglePopup() {
            const notificationPopup = document.querySelector(".notification-popup");

            if (notificationPopup.style.display === "none") {
                // API 요청 보내기
                fetch("/api/v1/users/alarm", {
                    headers: {
                        "Authorization": getAccessToken()
                    }
                })
                    .then(response => response.json())
                    .then(data => {
                        renderNotifications(data.result.content); // 팝업창에 알림 내역 띄우기
                        notificationPopup.style.display = "block"; // 팝업창 보이기
                    })
                    .catch(error => {
                        console.error("Error fetching notifications:", error);
                    });
            } else {
                notificationPopup.style.display = "none";
            }
        }

        function renderNotifications(notifications) {
            const notificationList = document.getElementById("notification-list");
            notificationList.innerHTML = "";

            notifications.forEach(notification => {
                const li = document.createElement("li");
                li.textContent = notification.alarmType;
                notificationList.appendChild(li);
            });
        }

        function updateHeader() {
            const logoutLink = document.querySelector(".logout-link");
            const loginSignupLinks = document.querySelectorAll(".login-signup-link");
            const notificationIcon = document.querySelector(".notification-icon");
            const postLink = document.querySelector(".post-link");
            const chatLink = document.querySelector(".chat-link");
            if (getAccessToken() !== "Bearer null") {
                logoutLink.style.display = "block";
                notificationIcon.style.display = "block";
                postLink.style.display = "block";
                chatLink.style.display = "block";
                for (const link of loginSignupLinks) {
                    link.style.display = "none";
                }
            } else {
                logoutLink.style.display = "none";
                notificationIcon.style.display = "none";
                postLink.style.display = "none";
                chatLink.style.display = "none";
                for (const link of loginSignupLinks) {
                    link.style.display = "block";
                }
            }
        }

        document.addEventListener("DOMContentLoaded", updateHeader);
    </script>

    <div class="header-left">
        <a class="header-link" href="/">home</a>
    </div>

    <div class="header-center">
        <%-- 포스트 링크 (로그인 상태일 때만 보이기) --%>
        <a class="header-link post-link" href="/post-list">포스트</a>
        <%-- 채팅 링크 (로그인 상태일 때만 보이기) --%>
        <a class="header-link chat-link" href="/myChat">채팅</a>
    </div>

    <div class="header-right">
        <div class="notification-icon" onclick="togglePopup()">🔔</div>

        <div class="header-link logout-link" onclick="logout()">로그아웃</div>

        <a class="header-link login-signup-link" href="/login">로그인</a>
        <a class="header-link login-signup-link" href="/register">회원가입</a>
    </div>
</header>

<!-- 알림 팝업 (내용 추가) -->
<div class="notification-popup">
    <h3>알림 내역</h3>
    <ul id="notification-list" class="notification-list">
    </ul>
</div>
</body>
</html>
