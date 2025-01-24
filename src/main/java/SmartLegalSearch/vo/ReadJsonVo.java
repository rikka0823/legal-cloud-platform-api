package SmartLegalSearch.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReadJsonVo {

	@JsonProperty("JID")
	private String id;

	@JsonProperty("JYEAR")
	private String year;

	@JsonProperty("JCASE")
	private String jCase;

	@JsonProperty("JNO")
	private String no;

	@JsonProperty("JDATE")
	private String date;

	@JsonProperty("JTITLE")
	private String title;

	@JsonProperty("JFULL")
	private String full;

	@JsonProperty("JPDF")
	private String pdf;

	public ReadJsonVo() {
		super();
	}

	public String getId() {
		return id;
	}

	public String getYear() {
		return year;
	}

	public String getJCase() {
		return jCase;
	}

	public String getNo() {
		return no;
	}

	public String getDate() {
		return date;
	}

	public String getTitle() {
		return title;
	}

	public String getFull() {
		return full;
	}

	public String getPdf() {
		return pdf;
	}

}
