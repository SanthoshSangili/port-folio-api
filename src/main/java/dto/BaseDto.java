package dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class BaseDto implements Serializable {

	private static final long serialVersionUID = 4432264169829377737L;

	private int statusCode;
	private String message;
	private String errorDescription;
	private Object responseContent;
}
