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
            margin: 0;
            padding: 0;
            background-color: #f8f8f8;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
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

        .header-link {
            text-decoration: none;
            color: white;
            padding: 10px 20px;
        }

        .header-link:hover {
            background-color: #ff9900;
        }

        h1 {
            text-align: center;
            margin-top: 100px;
            font-size: 36pt;
            color: #333;
        }

        p {
            text-align: center;
            font-size: 18pt;
            color: #666;
            margin-bottom: 50px;
        }

        .cta-btn {
            display: block;
            width: 200px;
            margin: 0 auto;
            padding: 15px 20px;
            background-color: #ff7f00;
            color: white;
            font-size: 18pt;
            text-align: center;
            text-decoration: none;
            border-radius: 5px;
        }

        .cta-btn:hover {
            background-color: #ff9900;
        }
    </style>
</head>
<body>
<%@ include file="../common/header.jsp" %>

<div class="container">
    <h1>멋쟁이 사자처럼 SNS에 오신 것을 환영합니다!</h1>
    <p>로그인하고 다른 멋쟁이 사자처럼 SNS 회원들과 채팅을 즐겨보세요.</p>
    <a class="cta-btn" href="#">시작하기</a>
</div>

<%@ include file="../common/footer.jsp" %>

</body>
</html>
