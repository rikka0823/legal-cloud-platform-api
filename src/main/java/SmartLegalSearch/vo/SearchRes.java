package SmartLegalSearch.vo;

import java.util.List;

import SmartLegalSearch.entity.LegalCase;

public class SearchRes extends BasicRes {

	private List<LegalCase> CaseList;

	public SearchRes() {
		super();
	}

	public SearchRes(int code, String message) {
		super(code, message);
	}

	public SearchRes(int code, String message, List<LegalCase> caseList) {
		super(code, message);
		CaseList = caseList;
	}

	public List<LegalCase> getCaseList() {
		return CaseList;
	}

}
