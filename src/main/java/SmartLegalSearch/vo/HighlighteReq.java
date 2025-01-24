package SmartLegalSearch.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import SmartLegalSearch.entity.HighlighterKey;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

public class HighlighteReq {
	// 螢光筆

	private String email; // 信箱

	private String groupId; // 歷屆字號

	private String id; // 字號

	private String court; // 法院代號

//	@JsonProperty("highlighter_color")
//	private String highlighterColor; // 螢光筆顏色
//
//	@JsonProperty("select_text")
//	private String selectText; // 螢光筆文字
//
//	@JsonProperty("start_offset")
//	private int startOffset; // 螢光筆 起始位置
//
//	@JsonProperty("end_offset")
//	private int endOffset; // 螢光筆 結束位置

	@JsonProperty("highlights")
	private List<Highlight> highlights; // 高亮列表

	public HighlighteReq() {
	}

	public HighlighteReq(String email, String groupId, String id, String court, List<Highlight> highlights) {
		this.email = email;
		this.groupId = groupId;
		this.id = id;
		this.court = court;
		this.highlights = highlights;
	}

	// Getter methods

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

	public List<Highlight> getHighlights() {
		return highlights;
	}

	// 設置高亮內部類
	public static class Highlight {
		private String highlighterColor; // 螢光筆顏色

		private String selectText; // 螢光筆文字

		private int startOffset; // 螢光筆 起始位置

		private int endOffset; // 螢光筆 結束位置

		public Highlight() {
		}

		public Highlight(String highlighterColor, String selectText, int startOffset, int endOffset) {
			this.highlighterColor = highlighterColor;
			this.selectText = selectText;
			this.startOffset = startOffset;
			this.endOffset = endOffset;
		}

		// Getter methods

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
	}
}