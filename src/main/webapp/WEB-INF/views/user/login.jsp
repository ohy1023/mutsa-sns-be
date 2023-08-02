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
            background-color: #f5f5f5;
            margin: 0;
            padding: 0;
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
            max-width: 300px;
            margin: 0 auto;
            padding: 20px;
            background-color: white;
            border: 1px solid #ddd;
            border-radius: 5px;
            box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
        }

        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }

        input[type="text"],
        input[type="password"] {
            width: 94%;
            padding: 10px;
            margin-bottom: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
        }

        input[type="submit"] {
            width: 100%;
            padding: 10px;
            background-color: #ff7f00;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }

        input[type="submit"]:hover {
            background-color: #ff9900;
        }

    </style>
</head>
<body>
<%@ include file="../common/header.jsp" %>
<h1>로그인</h1>
<form id="loginForm" method="post">
    <label for="userName">사용자명:</label>
    <input type="text" id="userName" name="userName" required><br>

    <label for="password">비밀번호:</label>
    <input type="password" id="password" name="password" required><br>

    <input type="submit">
</form>

<%@ include file="../common/footer.jsp" %>
<script>
    // 로그인 폼 제출 후 처리
    document.querySelector('#loginForm').addEventListener('submit', async (event) => {
        event.preventDefault();
        const formData = new FormData(event.target);

        const loginData = {
            userName: formData.get("userName"),
            password: formData.get("password")
        };
        const requestBody = JSON.stringify(loginData);
        const response = await fetch('/api/v1/users/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: requestBody
        });

        if (response.ok) {
            const data = await response.json();
            const token = data.result.jwt;

            // 토큰을 localStorage에 저장
            localStorage.setItem('accessToken', token);

            // 로그인 성공 후 이동할 페이지로 리다이렉트
            window.location.href = '/'; // 로그인 후 이동할 페이지 URL
        } else {
            // 로그인 실패 처리
            const errorData = await response.json();
            alert(errorData.result.message);
        }
    });
</script>
</body>
</html>
