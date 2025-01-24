package SmartLegalSearch.constants;

public enum ResMessage {
	// 格式: 全大寫英文(code, message);
	SUCCESS(200, "Success!!"),
	NOT_FOUND(404, "Not found!!"),
	DATE_ERROR(400, "Date error!!"),
	EMAIL_DUPLICATED(400, "Email duplicated!!"),
	EMAIL_SEND_FAILED(400, "Email send failed!!"),
	PASSWORD_ERROR(400, "Password error!"),
	EMAIL_ERROR(400, "Email error!"),
	LINK_HAS_EXPIRED(400, "Link has expired!!"),
	FILE_TOO_LARGE(400, "File too large!!"),
	FILE_UPLOAD_FAILED(400, "File upload failed!!"),
	INCORRECTLY_FILLED_IN(400, "Incorrectly filled-in!!" ),
	ALREADY_EXISTS(200,"already exists"),
	INVALID_FILE_FORMAT(400, "Invalid file format!!");
	
	private int code;

	private String message;

	// 需要有建構方法和 get 方法， set 就不用
	private ResMessage(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
