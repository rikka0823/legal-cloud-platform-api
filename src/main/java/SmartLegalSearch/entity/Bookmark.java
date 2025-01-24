package SmartLegalSearch.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "bookmarks")
@IdClass(Bookmark.class)
public class Bookmark {
	@Id
	@Column(name = "email")
	private String email; // 信箱

	@Id
	@JsonProperty("group_id")
	@Column(name = "group_id")
	private String groupId; // 歷屆字號

	@Id
	@Column(name = "id")
	private String id; // 字號

	@Id
	@Column(name = "court")
	private String court; // 法院代號

	@JsonProperty("verdict_date")
	@Column(name = "verdict_date")
	private LocalDate verdictDate; // 判決日期

	@Column(name = "charge")
	private String charge; // 案由

	@JsonProperty("defendant_name")
	@Column(name = "defendant_name")
	private String defendantName; // 被告姓名
	
	@JsonProperty("judge_name")
	@Column(name = "judge_name")
	private String judgeName; // 法官姓名
	
	@JsonProperty("case_type")
	@Column(name = "case_type")
	private String caseType; // 案件類型，如刑事、民事、行政
	
	@JsonProperty("doc_type")
	@Column(name = "doc_type")
	private String docType; // 文件類型，裁定或判決或釋字等

	public Bookmark() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Bookmark(String email, String groupId, String id, String court, LocalDate verdictDate, String charge,
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

	public void setEmail(String email) {
		this.email = email;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCourt(String court) {
		this.court = court;
	}

	public void setVerdictDate(LocalDate verdictDate) {
		this.verdictDate = verdictDate;
	}

	public void setCharge(String charge) {
		this.charge = charge;
	}

	public void setDefendantName(String defendantName) {
		this.defendantName = defendantName;
	}

	public void setJudgeName(String judgeName) {
		this.judgeName = judgeName;
	}

	public void setCaseType(String caseType) {
		this.caseType = caseType;
	}

	public void setDocType(String docType) {
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
