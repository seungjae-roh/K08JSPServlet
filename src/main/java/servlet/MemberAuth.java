package servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import membership.MemberDAO;
import membership.MemberDTO;

//MVC패턴으로 회원인증을 처리하기 위한 서블릿 정의
public class MemberAuth extends HttpServlet {

	//서블릿에서 멤버변수 선언
	MemberDAO dao;

	/*
	클라이언트가 최초로 요청했을 때 서블릿객체가 생성되는데 이때
	최초 1번만 호출되는 init()메소드에서 DB연결 처리를 한다.
	 */
	@Override
	public void init() throws ServletException {
		
		/*
		서블릿 내에서 application 내장객체를 받아온다. 모델2 방식에서는
		서블릿이 먼저 요청을 받기 때문에 모델1 방식과 같이 JSP에서
		매개변수로 내장객체를 전달할 수 없다. 따라서 각 내장객체를
		얻어올 수 있는 메소드가 존재한다.
		 */
		ServletContext application = this.getServletContext();
		
		//web.xml에 저장된 컨텍스트 초기화 파라미터를 얻어온다.
		String driver = application.getInitParameter("OracleDriver");
		String connectUrl = application.getInitParameter("OracleURL");
		String oId = application.getInitParameter("OracleId");
		String oPass = application.getInitParameter("OraclePwd");
		
		//DAO객체 생성을 통해 DB연결
		dao = new MemberDAO(driver, connectUrl, oId, oPass);
	}
	
	/*
	service()메소드는 get방식, post방식 둘다 요청을 받을 수 있으므로
	해당 메소드 내에서 요청을 처리할 수 있다.
	 */
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		서블릿 매핑시 <init-param>에 등록한 서블릿 초기화 파라미터를
		얻어온다. 해당 서블릿내에서만 사용할 수 있다.
		 */
		String admin_id = this.getInitParameter("admin_id");
		
		//쿼리스트링으로 전달된 파라미터를 얻어온다.
		String id = req.getParameter("id");
		String pass = req.getParameter("pass");
		
		/*
		DAO의 회원인증을 위한 메소드를 호출한다.
		아이디, 패스워드를 통해 회원인증을 진행한 후 일치하는 정보가
		있다면 회원레코드를 DTO에 저장한 후 반환한다. 만약 정보가 일치하지
		않는다면 내용이 없는 DTO를 반환하게 될 것이다.
		 */
		MemberDTO memberDTO = dao.getMemberDTO(id, pass);
		
		//회원이름을 통해 로그인 성공 여부를 판단한다. 판단 후 출력할
		//메세지를 request 영역에 저장한다.
		String memberName = memberDTO.getName();
		if (memberName != null) {
			//회원인증에 성공한 경우
			req.setAttribute("authMessage", memberName + " 회원님 환영합니다.");
		}
		else {
			/*
			회원인증에 실패한 경우에는 서블릿 초기화 파라미터와 비교해서
			최고관리자인지 재확인한다. 만약 최고관리자도 아니라면
			비회원이라 판단할 수 있다.
			 */
			if (admin_id.equals(id)) 
				req.setAttribute("authMessage", admin_id + "는 최고관리자입니다."); 
			else 
				req.setAttribute("authMessage", "귀하는 회원이 아닙니다.");
			
		}
			//처리한 메세지를 request영역에 저장한 후 JSP로 포워드한다.
			req.getRequestDispatcher("/13Servlet/MemberAuth.jsp").forward(req, resp);
	}
	//서블릿 종료시 DAO객체도 같이 자원해제한다.
	@Override
	public void destroy() {
		dao.close();
	}
}
