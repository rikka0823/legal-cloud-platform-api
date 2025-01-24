package SmartLegalSearch.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
public class HighlighterKey implements Serializable {

	private String email;

	@JsonProperty("group_id")
	private String groupId;

	private String id;

	private String court;

	@JsonProperty("start_offset")
	private int startOffset;

	public HighlighterKey() {
		super();
		// TODO Auto-generated constructor stub
	}

	public HighlighterKey(String email, String groupId, String id, String court, int startOffset) {
		super();
		this.email = email;
		this.groupId = groupId;
		this.id = id;
		this.court = court;
		this.startOffset = startOffset;
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

	public int getStartOffset() {
		return startOffset;
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

	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}

}
