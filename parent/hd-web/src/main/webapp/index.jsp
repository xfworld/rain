<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>welcome</title>
</head>
<body>
<%
    String url = request.getRequestURL().toString();
    response.sendRedirect("http://www.baidu.com");
%>
</body>
</html>
