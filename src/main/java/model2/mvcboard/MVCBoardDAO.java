package model2.mvcboard;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import common.DBConnPool;
import model1.board.BoardDTO;


public class MVCBoardDAO extends DBConnPool{
	//생성자에서 DBCP(커넥션풀)를 통해 오라클에 연결한다.
	public MVCBoardDAO() {
		super();
	}
	
	//게시물의 갯수를 카운트한다.
	public int selectCount(Map<String, Object> map) {
		int totalCount = 0;
		
		//만약 검색어가 있다면 조건에 맞는 게시물을 카운트해야하므로
		//조건부(where)로 쿼리문을 추가한다.
		String query = "SELECT COUNT(*) FROM mvcboard";
		if (map.get("searchWord") != null) {
			query += " WHERE " + map.get("searchField") + " "
					+ " LIKE '%" + map.get("searchWord") + "%'";
		}
		try {
			//인파라미터가 없는 정적쿼리문 실행을 위한 Statement객체 생성
			stmt = con.createStatement();
			//쿼리문 실행 후 결과는 ResultSet으로 반환
			rs = stmt.executeQuery(query);
			//커서를 첫번째 행으로 이동하여 레코드를 읽는다.
			rs.next();
			//첫번째 컬럼의 값을 가져와서 변수에 저장한다.
			totalCount = rs.getInt(1);
		}
		catch (Exception e) {
			System.out.println("게시물 카운트 중 예외 발생");
			e.printStackTrace();
		}
		
		return totalCount;
	}
	
	
	/*
	모델1 방식에서는 board테이블 및 boardDTO클래스를 사용했지만
	모델2 방식에서는 mvcboard테이블 및 MVCBoardDTO를 사용하므로
	해당 코드만 수정하면 된다.
	 */
	//조건에 맞는 게시물을 목록에 출력하기 위한 쿼리문을 실행한다.
	public List<MVCBoardDTO> selectListPage(Map<String, Object> map) {
		
		List<MVCBoardDTO> board = new Vector<MVCBoardDTO>();
		
		/*
		검색조건에 일치하는 게시물을 얻어온 후 각 페이지에 출력할
		구간까지 설정한 서브쿼리문 작성 (JDBC실습 참고)
		 */
		String query = " SELECT * FROM ( "
					+ " 	SELECT Tb.*, ROWNUM rNum FROM ( "
					+ "			SELECT * FROM mvcboard ";
		if (map.get("searchWord") != null) {
			query += " WHERE " + map.get("searchField")
					+ " LIKE '%" + map.get("searchWord") + "%' ";
		}
		query += "		ORDER BY idx DESC "
				+ "		) Tb "
				+ " ) "
				+ " WHERE rNum BETWEEN ? AND ?";
		try {
			//쿼리실행
			psmt = con.prepareStatement(query);
			/*
			ListController에서 Map컬렉션에 저장한 값들을 가져온다.
			저장타입이 Object형이므로 String으로 형변환해서 인파라미터를
			채워준다.
			 */
			psmt.setString(1, map.get("start").toString());
			psmt.setString(2, map.get("end").toString());
			//결과값 반환
			rs = psmt.executeQuery();
			
			/*
			레코드가 2개 이상 반환될 수 있으므로 while문으로 작성한다.
			 */
			while (rs.next()) {
				//하나의 레코드를 저장할 수 있는 DTO객체 생성
				MVCBoardDTO dto = new MVCBoardDTO();
				
				//setter()를 이용해서 각 컬럼의 값을 저장
				dto.setIdx(rs.getString(1));
				dto.setName(rs.getString(2));
				dto.setTitle(rs.getString(3));
				dto.setContent(rs.getString(4));
				dto.setPostdate(rs.getDate(5));
				dto.setOfile(rs.getString(6));
				dto.setSfile(rs.getString(7));
				dto.setDowncount(rs.getInt(8));
				dto.setPass(rs.getString(9));
				dto.setVisitcount(rs.getInt(10));
				
				//List컬렉션에 DTO객체를 추가한다.
				board.add(dto);
			}
		}
		catch (Exception e) {
			System.out.println("게시물 조회 중 예외 발생");
			e.printStackTrace();
		}
		return board;
	}
	
	//글쓰기 처리시 첨부파일까지 함께 입력한다.
	public int insertWrite(MVCBoardDTO dto) {
		int result = 0;
		
		try {
			/*
			ofile : 원본파일명
			sfile : 서버에 저장된 파일명
			pass : 비회원제 게시판이므로 수정, 삭제를 위한 인증에
				사용되는 비밀번호
			 */
			String query = "INSERT INTO mvcboard ( "
						+ " idx, name, title, content, ofile, sfile, pass) "
						+ " VALUES ( "
						+ " seq_board_num.NEXTVAL,?,?,?,?,?,?)";
			
			//동적쿼리문 -> prepared객체
			psmt = con.prepareStatement(query);
			psmt.setString(1, dto.getName());
			psmt.setString(2, dto.getTitle());
			psmt.setString(3, dto.getContent());
			psmt.setString(4, dto.getOfile());
			psmt.setString(5, dto.getSfile());
			psmt.setString(6, dto.getPass());
			//insert를 실행하여 입력된 행의 갯수를 반환받는다.
			result = psmt.executeUpdate();
		}
		catch (Exception e) {
			System.out.println("게시물 입력 중 예외 발생");
			e.printStackTrace();
		}
		return result;
	}
	
	//내용보기를 위해 일련번호를 인수로 받아 게시물 하나를 인출한다.
	public MVCBoardDTO selectView(String idx) {
		//레코드 저장을 위해 DTO객체를 생성한다.
		MVCBoardDTO dto = new MVCBoardDTO();
		//쿼리문 작성 후 인파라미터를 설정하고 실행한다.
		String query = "SELECT * FROM mvcboard WHERE idx=?";
					
		try {
			psmt = con.prepareStatement(query);
			psmt.setString(1, idx);
			rs = psmt.executeQuery();
			//일련번호는 중복되지 않으므로 반환된 게시물은 하나다. 
			//하나의 게시물이므로 if문을 통해 next()함수를 실행한다.
			if (rs.next()) {
				//인출한 게시물이 있다면 DTO객체에 저장한다.
				dto.setIdx(rs.getString(1));
				dto.setName(rs.getString(2));
				dto.setTitle(rs.getString(3));
				dto.setContent(rs.getString(4));
				dto.setPostdate(rs.getDate(5));
				dto.setOfile(rs.getString(6));
				dto.setSfile(rs.getString(7));
				dto.setDowncount(rs.getInt(8));
				dto.setPass(rs.getString(9));
				dto.setVisitcount(rs.getInt(10));
			}
		}
		catch (Exception e) {
			System.out.println("게시물 상세보기 중 예외 발생");
			e.printStackTrace();
		}
		return dto;
	}
	
	//게시물의 조회수를 1증가시킨다.
	public void updateVisitCount(String idx) {
		
		String query = "UPDATE mvcboard SET "
					+ " visitcount=visitcount+1 "
					+ " WHERE idx=?";
		try {
			psmt = con.prepareStatement(query);
			psmt.setString(1, idx);
			psmt.executeQuery();
		}
		catch (Exception e) {
			System.out.println("게시물 조회수 증가 중 예외 발생");
			e.printStackTrace();
		}
	}
	
	//첨부파일을 다운받은 횟수
	//조회수 증가 메소드와 동일한 로직
	public void downCountPlus(String idx) {
		String sql = "UPDATE mvcboard SET "
				+ " downcount=downcount+1 "
				+ "WHERE idx=? ";
		try {
			psmt = con.prepareStatement(sql);
			psmt.setString(1, idx);
			psmt.executeUpdate();
		}
		catch (Exception e) {}
	}
	
	//패스워드 검증을 위한 메소드로 조건에 맞는 게시물을 카운트한다.
	public boolean confirmPassword(String pass, String idx) {
		boolean isCorr = true;
		try {
			//일련번호와 패스워드가 일치하는 게시물이 있는지 확인한다.
			String sql = "SELECT COUNT(*) FROM mvcboard WHERE pass=? AND idx=?";
			psmt = con.prepareStatement(sql);
			psmt.setString(1, pass);
			psmt.setString(2, idx);
			rs = psmt.executeQuery();
			/*
			count()함수의 경우 조건에 맞는 레코드가 없으면 0을 반환하므로
			어떤 경우에도 결과값이 있다. 따라서 next()를 단독으로 실행한다.
			 */
			rs.next();
			if (rs.getInt(1) == 0) {
				isCorr = false;
			}
		}
		catch (Exception e) {
			isCorr = false;
			e.printStackTrace();
		}
		return isCorr;
	}
	

	public int deletePost(String idx) {
		int result = 0;
		try {
			String query = "DELETE FROM mvcboard WHERE idx=?";
			psmt = con.prepareStatement(query);
			psmt.setString(1, idx);
			result = psmt.executeUpdate();
		}
		catch (Exception e) {
			System.out.println("게시물 삭제 중 예외 발생");
			e.printStackTrace();
		}
		return result;
	}
	
	//게시글 데이터를 받아 DB에 저장되어 있던 내용을 갱신합니다.
	//(파일 업로드 지원)
	public int updatePost(MVCBoardDTO dto) {
		int result = 0;
		
		try {
			//쿼리문 템플릿 준비
			//일련번호와 패스워드까지 where절에 추가하여 둘다 일치할 때만
			//수정 처리 된다.
			String query = "UPDATE mvcboard "
						+ " SET title=?, name=?, content=?, ofile=?, sfile=? "
						+ " WHERE idx=? and pass=?";
			
			//쿼리문 준비
			psmt = con.prepareStatement(query);
			psmt.setString(1, dto.getTitle());
			psmt.setString(2, dto.getName());
			psmt.setString(3, dto.getContent());
			psmt.setString(4, dto.getOfile());
			psmt.setString(5, dto.getSfile());
			psmt.setString(6, dto.getIdx());
			psmt.setString(7, dto.getPass());
			
			result = psmt.executeUpdate();
		}
		catch (Exception e) {
			System.out.println("게시물 수정 중 예외 발생");
			e.printStackTrace();
		}
		return result;
	}
}
