package SmartLegalSearch.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "highlighters")
@IdClass(HighlighterKey.class)
public class Highlighte {
	// 螢光筆與書籤

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

	@JsonProperty("highlighter_color")
	@Column(name = "highlighter_color")
	private String highlighterColor; // 螢光筆顏色

	@JsonProperty("Select_text")
	@Column(name = "Select_text") // 螢光筆文字
	private String selectText;

	@Id
	@JsonProperty("start_offset")
	@Column(name = "start_offset")
	private int startOffset; // 螢光筆 起始位置

	@JsonProperty("end_offset")
	@Column(name = "end_offset")
	private int endOffset; // 螢光筆 結束位置

	public Highlighte() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Highlighte(String email, String groupId, String id, String court, String highlighterColor, String selectText,
			int startOffset, int endOffset) {
		super();
		this.email = email;
		this.groupId = groupId;
		this.id = id;
		this.court = court;
		this.highlighterColor = highlighterColor;
		this.selectText = selectText;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
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

	public String getHighlighterColor() {
		return highlighterColor;
	}

	public String getSelectText() {
		return selectText;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public int getEndOffset() {
		return endOffset;
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

	public void setHighlighterColor(String highlighterColor) {
		this.highlighterColor = highlighterColor;
	}

	public void setSelectText(String selectText) {
		this.selectText = selectText;
	}

	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}

	public void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}

}
