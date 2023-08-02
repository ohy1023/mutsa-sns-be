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

        .container {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
    </style>
</head>
<body>
<%@ include file="../common/header.jsp" %>
<h1>회원가입</h1>
<form id="registerForm" method="post">
    <label for="username">사용자명:</label>
    <input type="text" id="username" name="username" required><br>

    <label for="password">비밀번호:</label>
    <input type="password" id="password" name="password" required><br>

    <input type="submit" value="회원가입">
</form>
<%@ include file="../common/footer.jsp" %>
<script>
    // 회원가입 폼 제출 후 처리
    document.querySelector('#registerForm').addEventListener('submit', async (event) => {
        event.preventDefault();
        const formData = new FormData(event.target);

        const registerData = {
            userName: formData.get("username"),
            password: formData.get("password")
        };
        const requestBody = JSON.stringify(registerData);

        const response = await fetch('/api/v1/users/join', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: requestBody
        });

        if (response.ok) {
            // 회원가입 성공 처리
            alert('회원가입이 성공적으로 완료되었습니다.');
            // 회원가입 성공 후 이동할 페이지로 리다이렉트
            window.location.href = '/login'; // 회원가입 후 이동할 페이지 URL (로그인 페이지)
        } else {
            // 회원가입 실패 처리
            const errorData = await response.json();
            alert(errorData.result.message);
        }
    });
</script>
</body>
</html>
