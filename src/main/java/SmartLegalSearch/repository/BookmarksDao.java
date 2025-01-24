package SmartLegalSearch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import SmartLegalSearch.entity.Bookmark;
import SmartLegalSearch.entity.HighlighterKey;
import jakarta.transaction.Transactional;

// 書籤dao
@Repository
public interface BookmarksDao extends JpaRepository<Bookmark, HighlighterKey> {
	// 存入書籤檢查
	// 查詢是否已經存在該筆書籤
	Optional<Bookmark> findByEmailAndGroupIdAndIdAndCourt(String email, String groupId, String id, String court);

	// 搜尋所有該email擁有的書籤
	@Query(value = "select email, group_id, id, court, verdict_date, charge, judge_name, defendant_name, case_type, doc_type "
			+ " from bookmarks where email = ?1 ;", nativeQuery = true)
	public  List<Bookmark> getEmailBookmark(String email);
	
	// 刪除單筆書籤-全文頁面點擊
	@Transactional
    @Modifying
    void deleteByEmailAndGroupIdAndIdAndCourt(String email, String groupId, String id, String court);
}
