<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
    <title></title>

</head>
<body>
<div>
    <c:choose>
        <c:when test="${empty sessionScope.USER.account}">
            <a class="" href="javascript:void(0)" id="login">立即登录</a>
        </c:when>
        <c:otherwise>
            欢迎你，${sessionScope.USER.account}:
            <a class="" href="javascript:void(0)" id="logout">登出帐号</a>
        </c:otherwise>
    </c:choose>
</div>

<div><h3>对不期，来到这里是你的一个错误</h3></div>
<script type="text/javascript" src="${ctx}/static/jquery/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="${ctx}/extend/wmpassport2/pppppp.passport.js"></script>
<script type="text/javascript">
    var username = "${sessionScope.USER.account}";

    $(function () {
        initLoginLogout();

    });

    function initLoginLogout() {
        $('#login').click(function () {
            login();
        });
        $('#logout').click(function () {
            logout();
        });
    }
    function login() {
        pppppp.passport.islogin({session: 'USER', _true: function () {
        }, _false: function () {
            pppppp.passport.login({session: 'USER',hdid: "world2_login_status"})
        }});
    }
    function logout() {
        pppppp.passport.logout({opacity: 60, session: 'USER'});
    }
    var isEmpty = function (arg) {
        return arg == "undefined" || arg == null || arg == "" || arg == "null";
    };
    var isLogin = function () {
        if (isEmpty(username)) {
            $.simpleAlert({content: "请先登录。", ok: function () {
                login();
            }});
            return false;
        }

        return true;
    };
</script>
</body>
</html>
