package banzzac.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDataDTO {
	private String fileName;
	private String uuid;
	private String path;
}
