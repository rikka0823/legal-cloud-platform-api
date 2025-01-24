package SmartLegalSearch.service.ifs;

import SmartLegalSearch.entity.LegalCase;
import SmartLegalSearch.vo.SearchReq;
import SmartLegalSearch.vo.SearchRes;

public interface CaseService {

	// 搜尋功能
    public SearchRes searchCriminalCase(SearchReq req);

    // 儲存判決書內容
    LegalCase saveJudgment(LegalCase res);
    
    // 取得判決書內容(字號、上文、下文、url)
    public SearchRes JudgmentidText(String groupId, String id, String court);

}
