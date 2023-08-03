<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<% request.setCharacterEncoding("UTF-8"); %>
<% response.setContentType("text/html; charset=UTF-8"); %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>멋쟁이 사자처럼 SNS</title>
    <style>
        body {
            margin: 0; /* 본문과 푸터 사이에 기본적으로 존재하는 마진을 제거 */
        }
        footer {
            position: fixed; /* 화면에 고정 */
            bottom: 0; /* 화면 아래에 위치 */
            left: 0; /* 좌측에 정렬 */
            width: 100%; /* 가로 폭을 100%로 설정하여 화면 크기에 맞게 폭을 늘림 */
            background-color: #ff7f00;
            font-weight: bold;
            font-size: 20pt;
            color: #666;
            height: 40px;
            display: flex;
            justify-content: center;
            align-items: center;
        }
    </style>
</head>
<body>
<footer>
    저작권 © 2023 멋쟁이 사자처럼 SNS. All rights reserved.
</footer>
</body>
</html>