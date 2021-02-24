<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<body>
<c:if test="${userId eq null}">
    <a href="https://kauth.kakao.com/oauth/authorize?client_id=856ec0be1a62b01007353103f2cbc64d&redirect_uri=http://localhost:8080/login&response_type=code">
        button
    </a>
</c:if>
<c:if test="${userId ne null}">
    <h1>안녕하세요</h1>
</c:if>
</body>

</html>