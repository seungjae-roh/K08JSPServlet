package fileupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;

public class FileUtil {

	//파일 업로드 처리
	public static MultipartRequest uploadFile(HttpServletRequest req, 
			String saveDirectory, int maxPostSize) {
		try {
			/*
			MultipartRequest(request내장객체, 디렉토리의 물리적 경로,
				업로드 제한용량, 인코딩 방식);
			위와 같은 형태로 객체를 생성함과 동시에 파일은 업로드 된다.
			업로드에 성공하면 객체의 참조값을 반환한다.
			만약 실패했다면 디렉토리 경로나 파일용량을 확인해본다.
			 */
			return new MultipartRequest(req, saveDirectory, maxPostSize, "UTF-8");
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void download(HttpServletRequest req, HttpServletResponse resp,
			String directory, String sfileName, String ofileName) {
		
		//파일이 저장된 디렉토리의 물리적경로를 얻어온다.
		String sDirectory = req.getServletContext().getRealPath(directory);
		
		try {
		    //물리적경로와 파일명을 통해 File객체 생성
		    File file = new File(sDirectory, sfileName);
		    //파일의 내용을 읽어오기 위한 입력스트림 생성
		    InputStream inStream = new FileInputStream(file);
		    
		    //사용자의 웹브라우저 종류를 알아내기 위해 요청헤더를 얻어온다.
		    String client = req.getHeader("User-Agent");
		    if (client.indexOf("WOW64") == -1) {
		    	//인터넷 익스플로러가 아닌경우 한글파일명 인코딩
		        ofileName = 
		        	new String(ofileName.getBytes("UTF-8"), "ISO-8859-1");
		    }
		    else {
		    	//인터넷 익스플로러 일때 한글파일명 인코딩
		        ofileName = 
		        	new String(ofileName.getBytes("KSC5601"), "ISO-8859-1");
		    }
		    /*
	    	위 처리를 통해 원본파일명이 한글인 경우 깨짐을 방지할수 있다. 
	    	getBytes() : String객체를 byte형의 배열로 반환해준다. 
		    */
		   
		    /*
		    파일 다운로드를 위한 응답 헤더 설정
		    1. 응답헤더 초기화
		    2. 웹브라우저가 인식하지 못하는 컨텐츠 타입을 설정하여 다운로드 창을
		    	강제로 띄우게 함.
		    3. 파일명을 변경하기 위한 응답헤더를 설정하고 파일사이즈도 설정한다. 
		    */
		    
		    resp.reset();
		    resp.setContentType("application/octet-stream");
		    resp.setHeader("Content-Disposition", 
		                       "attachment; filename=\"" + ofileName + "\"");
		    resp.setHeader("Content-Length", "" + file.length() );
		    
		    //새로운 출력스트림을 생성하기 위해 초기화
		    //out.clear(); 출력 스트림 초기화
		    
		    //새로운 출력스트림을 생성해서 파일을 내보낸다.
		    OutputStream outStream = resp.getOutputStream();  
		    byte b[] = new byte[(int)file.length()];
		    int readBuffer = 0;    
		    while ( (readBuffer = inStream.read(b)) > 0 ) {
		        outStream.write(b, 0, readBuffer);
		    }
			//입출력 스트림을 닫아준다.(자원해제)
		    inStream.close(); 
		    outStream.close();
		}
		catch (FileNotFoundException e) {
		    System.out.println("파일을 찾을 수 없습니다.");
		    e.printStackTrace();
		}
		catch (Exception e) {
			System.out.println("예외가 발생하였습니다.");
			e.printStackTrace();
		}
	}
	
	//파일 삭제를 위한 메소드
	public static void deleteFile(HttpServletRequest req,
			String directory, String filename) {
		//물리적 경로와 파일명을 통해 File객체를 생성한다.
		String sDirectory = req.getServletContext().getRealPath(directory);
		File file = new File(sDirectory + File.separator + filename);
		//해당 경로에 파일이 존재하면..
		if (file.exists()) {
			//파일을 삭제한다.
			file.delete();
		}
	}
}
