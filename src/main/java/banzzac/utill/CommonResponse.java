package banzzac.utill;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse<T> {
	
	private boolean success;
	private  T data;
	private ErrorInfo error;
	
	public static <T> ResponseEntity<CommonResponse<T>> success(T data) {
        return ResponseEntity.ok(new CommonResponse<T>(true, data, null));
    }

    public static <T> ResponseEntity<CommonResponse<T>> error(HttpStatus status, String code, String message) {
        ErrorInfo errorInfo = new ErrorInfo(code, message);
        return ResponseEntity.status(status).body(new CommonResponse<T>(false, null, errorInfo));
    }
}
