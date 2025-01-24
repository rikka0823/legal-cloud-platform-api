package SmartLegalSearch.vo;

import java.util.List;

import SmartLegalSearch.entity.Highlighte;

public class HighlighteRes extends BasicRes {

	private List<Highlighte> highlighteList;

	public HighlighteRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public HighlighteRes(int code, String message) {
		super(code, message);
		// TODO Auto-generated constructor stub
	}

	public HighlighteRes(int code, String message ,List<Highlighte> highlighteList) {
		super(code, message);
		this.highlighteList = highlighteList;
	}

	public List<Highlighte> getHighlighteList() {
		return highlighteList;
	}

}
