<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<table width="100%"> 
    <tr>
        <td align="center">
        <!-- 로그인 여부에 따른 메뉴 변화 -->
        <% if (session.getAttribute("UserId") == null) { %>
            <a href="../homework/Hw/HwLoginForm.jsp">
            	<i class="bi bi-box-arrow-in-right font-size:1.5em">
            	</i>로그인</a>
            <a href="../homework/Hw/HwRegist.jsp">
            	<i class="bi bi-person-plus-fill font-size:1.5em" >
            	</i>회원가입</a>
        <% } else { %>
            <a href="../homework/Hw/HwLogout.jsp">
            	<i class="bi bi-box-arrow-right font-size:1.5em">
            	</i>로그아웃</a>
            <a href="../homework/Hw/HwUpdateInfo.jsp">
            	<i class="bi bi-person-lines-fill font-size:1.5em">
            	</i>회원정보수정</a>
        <% } %>
        </td>
    </tr>
</table>
