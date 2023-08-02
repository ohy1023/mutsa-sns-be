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
            font-size: 30pt;
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
    </style>
</head>
<body>
<header>
    <%-- 스크립트 코드: accessToken 체크하여 로그인/회원가입 또는 로그아웃 링크 보이기 --%>
    <script>
        function getAccessToken() {
            // 로컬 스토리지에서 토큰을 가져오기
            return localStorage.getItem("accessToken");
        }

        // 로그아웃 함수: 로그아웃 버튼을 누르면 호출되는 함수
        function logout() {
            // 로컬 스토리지에서 accessToken 삭제하고 페이지 새로고침
            localStorage.removeItem('accessToken');
            window.location.reload();
        }

        document.addEventListener("DOMContentLoaded", function () {
            const logoutLink = document.querySelector(".logout-link");
            const loginSignupLinks = document.querySelectorAll(".login-signup-link");

            if (getAccessToken()) {
                // 로그아웃 상태: 로그아웃 링크 보이기, 로그인/회원가입 링크 숨기기
                logoutLink.style.display = "block";
                for (const link of loginSignupLinks) {
                    link.style.display = "none";
                }
            } else {
                // 로그인/회원가입 상태: 로그인/회원가입 링크 보이기, 로그아웃 링크 숨기기
                logoutLink.style.display = "none";
                for (const link of loginSignupLinks) {
                    link.style.display = "block";
                }
            }
        });
    </script>

    <%-- 헤더 링크 추가 및 수정 --%>
    <div class="header-left">
        <a class="header-link" href="/hello">home</a>
    </div>

    <div class="header-right">
        <%-- 로그인 상태일 때 로그아웃 링크 보이기 --%>
        <div class="header-link logout-link" onclick="logout()">로그아웃</div>

        <%-- 로그아웃 상태일 때 로그인/회원가입 링크 보이기 --%>
        <a class="header-link login-signup-link" href="/login">로그인</a>
        <a class="header-link login-signup-link" href="/register">회원가입</a>
    </div>
</header>
</body>
</html>
