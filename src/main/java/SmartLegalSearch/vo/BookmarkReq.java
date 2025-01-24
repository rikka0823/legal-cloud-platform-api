package SmartLegalSearch.vo;

import java.time.LocalDate;



public class BookmarkReq {
// 書籤
	private String email; // 信箱

	private String groupId; // 歷屆字號

	private String id; // 字號

	private String court; // 法院代號

	private LocalDate verdictDate; // 判決日期

	private String charge; // 案由

	private String defendantName; // 被告姓名

	private String judgeName; // 法官姓名

	private String caseType; // 案件類型，如刑事、民事、行政

	private String docType; // 文件類型，裁定或判決或釋字等

	public BookmarkReq() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BookmarkReq(String email, String groupId, String id, String court, LocalDate verdictDate, String charge,
			String defendantName, String judgeName, String caseType, String docType) {
		super();
		this.email = email;
		this.groupId = groupId;
		this.id = id;
		this.court = court;
		this.verdictDate = verdictDate;
		this.charge = charge;
		this.defendantName = defendantName;
		this.judgeName = judgeName;
		this.caseType = caseType;
		this.docType = docType;
	}

	public String getEmail() {
		return email;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getId() {
		return id;
	}

	public String getCourt() {
		return court;
	}

	public LocalDate getVerdictDate() {
		return verdictDate;
	}

	public String getCharge() {
		return charge;
	}

	public String getDefendantName() {
		return defendantName;
	}

	public String getJudgeName() {
		return judgeName;
	}

	public String getCaseType() {
		return caseType;
	}

	public String getDocType() {
		return docType;
	}

}
