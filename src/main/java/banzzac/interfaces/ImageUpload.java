package banzzac.interfaces;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import banzzac.dto.FileDataDTO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * ImageUpload 하는 Class는 ImageUpload를 구현하고,
 * 본인의 클래스 이름으로 주입받으세요.
 * public class ProfileUpload implements ImageUpload
 * 사용 시 ==>
 * ex) profile 이미지 업로드 처리
 * @Resource
 * ImageUpload profileUpload; 
 * */
public interface ImageUpload {

	public ResponseEntity<List<FileDataDTO>> imgSave(MultipartFile [] mfs, HttpServletRequest request);
}
