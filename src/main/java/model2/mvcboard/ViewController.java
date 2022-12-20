package model2.mvcboard;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
어노테이션을 통한 매핑은 요청명을 해당 컨트롤러 상단에 기술
web.xml에 기술하는 것과 동일하게 입력이 잘못되는 경우 톰캣이 실행되지
않을 수 있으므로 주의가 요함
 */
@WebServlet("/mvcboard/view.do")
public class ViewController extends HttpServlet{
   /*
    service()는 서블릿의 수명주기 메소드 중 사용자의 요청을 전송방식에
    상관없이 먼저 받아 doGet() 혹은 doPost()로 요청을 전달하는 역할
    전송방식에 상관없이 요청 처리가 가능
    */
   @Override
   protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      //DAO객체 생성을 통해 커넥션풀 객체 획득
      MVCBoardDAO dao = new MVCBoardDAO();
      //일련번호 획득
      String idx = req.getParameter("idx");
      //조회수 증가
      dao.updateVisitCount(idx);
      //게시물을 인출
      MVCBoardDTO dto = dao.selectView(idx);
      //커넥션풀 자원 반납
      dao.close();
      
      //내용에 대해서는 줄바꿈 처리를 위해 <br>태그로 변경
      dto.setContent(dto.getContent().replace("\r\n", "<br/>"));
      
      //첨부파일이 이미지인 경우 화면에 출력하기 위한 로직 추가
      String ext = null, fileName=dto.getSfile();
      //서버에 저장된 파일이 있는 경우라면 확장자를 획득
      if(fileName!=null) {
         ext = fileName.substring(fileName.lastIndexOf(".")+1);
      }
      //이미지의 대표적인 확장자를 String배열로 선언
      String[] mineStr = {"png", "jpg", "gif"};
      //String 배열을 List컬렉션으로 변환
      List<String> mineList = Arrays.asList(mineStr);
      boolean isImage = false;
      //변환된 List 컬렉션에 우리가 첨부한 이미지의 확장자 존재여부 확인
      if(mineList.contains(ext)) {
         //존재시 true로 변경
         isImage=true;
      }
      //request영역에 저장
      req.setAttribute("isImage", isImage);
      
      //request영역에 DTO객체를 저장한 후 View로 포워드
      req.setAttribute("dto", dto);
      req.getRequestDispatcher("/14MVCBoard/View.jsp").forward(req, resp);
   }
}