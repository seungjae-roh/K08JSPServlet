<!-- page 지시어 선언 -->
<%@page import="common.MyClass"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<!-- 
선언부(Declaration) : 스크립트렛이나 표현식에서 사용할 메소드를 선언할 때 사용
   선언부에서 선언된 메소드는 해당 JSP가 서블릿(JAVA파일)로 변환시
   멤버메소드 형태로 선언
-->
<%!
public int add(int num1, int num2){
   return num1 + num2;
}
%>
<html>
<head>
<meta charset="UTF-8">
<title>스크립트 요소</title>
</head>
<body>
<!--
스크립트렛(Scriptlet) : 주로 JSP코드를 작성하고 실행할 때 사용
   스크립트렛은 body, head 어디서든 사용 가능
   <Script>/<style> 태그 내부에서도 사용 가능
   여기에 기술된 코드는 _jspService() 메소드 내에 포함되며,
   해당 영역에는 메소드를 선언 불가
   
표현식(Expression) : 변수를 웹브라우저상에 출력할 경우 사용
   JS의 document.write()와 동일한 역할
   표현식 ㅅ하용시 주의할 점은 문장 끝에 ;(세미콜론) 미사용
-->
<%
int result = add(10,20);
%>
덧셈 결과 1: <%= result %><br>
덧셈 결과 2: <%= add(30,40) %>

<h2>내가 만든 Java클래스의 메소드 호출하기</h2>
<%
	int sum = MyClass.myFunc(1, 100);
	out.println("1~100까지의 합: " + sum);
%>
</body>
</html>