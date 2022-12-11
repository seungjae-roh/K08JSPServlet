<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet"
        integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p"
        crossorigin="anonymous"></script>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.1/font/bootstrap-icons.css">    
</head>
<body>
<div class="container">
    <div class="row">
        <!-- 상단 네비게이션 인클루드  -->
        <%@ include file = "./inc/top.jsp" %>
    </div>
    <div class="row">
        <div class="col-3">
            <div style="height: 100px; line-height: 100px; margin:10px 0; text-align: center; 
				color:#ffffff; background-color:rgb(133, 133, 133); border-radius:10px; font-size: 1.5em;">
                웹사이트제작
            </div>
            <!-- 좌측 인클루드 -->
           	<%@ include file = "./inc/left.jsp" %>
        </div>
        <div class="col-9 pt-3">
            <h3>게시판 목록 - <small>자유게시판</small></h3>

            <div class="row ">
                <!-- 검색부분 -->
                <form>
                    <div class="input-group ms-auto" style="width: 400px;">
                        <select name="keyField" class="form-control">
                            <option value="">제목</option>
                            <option value="">작성자</option>
                            <option value="">내용</option>
                        </select>
                        <input type="text" name="keyString" class="form-control" style="width: 150px;"/>
                        <div class="input-group-btn">
                            <button type="submit" class="btn btn-secondary">
                                <i class="bi bi-search" style='font-size:20px'></i>
                            </button>
                        </div>
                    </div>
                </form>
            </div>
            <div class="row mt-3 mx-1">
                <!-- 게시판리스트부분 -->
                <table class="table table-bordered table-hover table-striped">
                    <colgroup>
                        <col width="60px" />
                        <col width="*" />
                        <col width="120px" />
                        <col width="120px" />
                        <col width="80px" />
                        <col width="60px" />
                    </colgroup>
                    <thead>
                        <tr style="background-color: rgb(133, 133, 133); " class="text-center text-white">
                            <th>번호</th>
                            <th>제목</th>
                            <th>작성자</th>
                            <th>작성일</th>
                            <th>조회수</th>
                            <th>첨부</th>
                        </tr>
                    </thead>
                    <tbody>
                        <h2>TJEOUN LOGIN</h2>
						<!-- 
						삼항연산자를 통해 request영역에 저장된 속성값이 있는 경우에만
						에러메세지를 화면에 출력한다.
						해당 속성값은 로그인 처리 페이지에서 회원정보를 DB에서 찾지 못한
						경우 request영역에 속성값을 저장하게 된다.
						 -->
						<span style="color: red; font-size: 1.2em;">
							<%= request.getAttribute("LoginErrMsg") == null ?
								"" : request.getAttribute("LoginErrMsg")%>
						</span>
						
						<%
						/* 세션영역에 UserId라는 속성값이 없는 경우 즉, 로그인 처리가 되지
						않은 상태에서는 로그인 폼과 검증을 위한 JS를 웹브라우저에 출력한다.*/
						if (session.getAttribute("UserId") == null) { //로그인 상태 확인
							//로그아웃 상태
						%>
						<!-- 로그인 폼의 입력값을 검증하기 위한 함수로 빈값인지를 확인한다. -->
						<script>
						function validateForm(form) {
							/* <form> 태그 하위의 각 input 태그에 입력값이 있는지 확인하여 
							만약 빈값이라면 경고창, 포커스 이동, 폼값전송취소 처리를 한다. */
							if (!form.user_id.value) {
								alert("아이디를 입력하세요.");
								form.user_id.focus();
								return false;
							}
							if (form.user_pw.value == "") {
								alert("패스워드를 입력하세요.");
								form.user_pw.focus();
								return false;
							}
						}
						</script>
						<!-- 
						폼값 전송을 위한 <form>태그로 전송할 URL, 전송방식, 폼의 이름,
						submit 이벤트 리스너로 구성한다. 특히 폼값검증을 위한 
						validateForm() 함수 호출 시 <form>태그의 DOM을 인수로 전달한다.
						 -->
						<form action="HwLoginProcess.jsp" method="post" name="loginFrm"
							onsubmit="return validateForm(this);">
							아이디: <input type="text" name="user_id"><br>
							패스워드: <input type="password" name="user_pw"><br>
							<input type="submit" value="로그인하기">
						</form>
						<%
						} else { //로그인된 상태
							/* session영역에 저장된 속성이 있다면 로그인 된 상태이므로
							회원정보 및 로그아웃 버튼을 화면에 출력한다.*/
						%>
							<%= session.getAttribute("UserName") %> 회원님, 로그인하셨습니다. <br>
							<a href="HwLogout.jsp">[로그아웃]</a>
						<% 
						}
						%>	
                    </tbody>
                </table>
            </div>
            <div class="row mt-3">
                <div class="col">
                    <!-- 페이지번호 부분 -->
                    <ul class="pagination justify-content-center">
                        <li class="page-item">
                            <a href="#" class="page-link"><i class='bi bi-skip-backward-fill'></i></a>
                        </li>
                        <li class="page-item">
                            <a href="#" class="page-link"><i class='bi bi-skip-start-fill'></i></a>
                        </li>
                        <li class="page-item"><a href="#" class="page-link">1</a></li>
                        <li class="page-item active"><a href="#" class="page-link">2</a></li>
                        <li class="page-item"><a href="#" class="page-link">3</a></li>
                        <li class="page-item"><a href="#" class="page-link">4</a></li>
                        <li class="page-item"><a href="#" class="page-link">5</a></li>
                        <li class="page-item">
                            <a href="#" class="page-link"><i class='bi bi-skip-end-fill'></i></a>
                        </li>
                        <li class="page-item">
                            <a href="#" class="page-link"><i class='bi bi-skip-forward-fill'></i></a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
    <div class="row border border-dark border-bottom-0 border-right-0 border-left-0"></div>
    <div class="row mb-5 mt-3">
		<%@ include file = "./inc/bottom.jsp" %>
    </div>
</div>
</body>
</html>