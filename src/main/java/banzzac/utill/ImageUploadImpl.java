package banzzac.utill;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import banzzac.dto.FileDataDTO;
import banzzac.interfaces.ImageUpload;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class ImageUploadImpl implements ImageUpload {

	private final String PATH = "C:\\woong\\springworkspace\\banzzac-server\\src\\main\\webapp\\imgs\\";
	/**
	 * 
	 * 파일 업로드에 실패하면 NULL 값 반환, 성공적으로 끝날 경우 FileName Return
	 * */
	@Override
	public ResponseEntity<List<FileDataDTO>> imgSave(MultipartFile [] mfs, HttpServletRequest request) {
		List<FileDataDTO> res = new ArrayList<>();
		
		for(MultipartFile mf : mfs) {
			//파일 크기가 0이거나 없을 경우 체크
			if(mf.isEmpty()) {
				System.out.println("파일 없음");
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			
			
			//파일 중복 체크에 필요한 설정 값들
			String fullName = mf.getOriginalFilename();
			String exetention = fullName.substring(fullName.lastIndexOf(".")+1).toLowerCase();
			//고유 번호 생성
			String uuid = UUID.randomUUID().toString();
			//이미지 유효성 검사
			if(!exetention.matches("^(jpg|jpeg|png|gif|svg|bmp)$")) {
				System.out.println("이미지 확장자 아님!");
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			
			//증복 체크			  경로 + 도메인 네임+_+오늘 날짜 + uuid+ 확장자
			File file = new File(PATH+"Banzzac"+File.separator + new SimpleDateFormat("yyyyMMdd").format(new Date())+uuid+"."+exetention);
			int cnt = 1;

			//파일 쓰기
			try(FileOutputStream fos = new FileOutputStream(PATH+file.getName())) {
				
				fos.write(mf.getBytes());
				res.add(new FileDataDTO(file.getName(), uuid , PATH));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		return new ResponseEntity<>(res,HttpStatus.OK);
			
	}
	
	
}
