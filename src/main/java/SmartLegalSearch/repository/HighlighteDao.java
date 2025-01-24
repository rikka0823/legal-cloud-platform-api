package SmartLegalSearch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import SmartLegalSearch.entity.Highlighte;
import SmartLegalSearch.entity.HighlighterKey;
import jakarta.transaction.Transactional;

// 螢光筆dao
@Repository
public interface HighlighteDao extends JpaRepository<Highlighte, HighlighterKey> {

	// 查詢是否已經存在該筆螢光筆
	Optional<Highlighte> findByEmailAndGroupIdAndIdAndCourtAndStartOffset(String email, String groupId, String id,
			String court, int startOffset);

//	// 查詢是否已經存在該筆螢光筆
//	Optional<Highlighte> findByEmailAndGroupIdAndIdAndCourtAndStartOffset2(String email, String groupId, String id,
//			String court);

	// 會員中心 書籤頁面 顯示所有擁有的螢光筆
	@Query(value = "select email, group_id, id, court, highlighter_color, select_text, start_offset, end_offset "
			+ " from highlighters where email = ?1 ;", nativeQuery = true)
	public List<Highlighte> getEmailHighlighte(String email);

	@Query(value = "select email, group_id, id, court, highlighter_color, select_text, start_offset, end_offset "
			+ " from highlighters where email = ?1 and group_id = ?2 and id = ?3 and court = ?4 ;", nativeQuery = true)
	public List<Highlighte> getHighlighteAlreadyExists(String email, String groupId, String id, String court);

	// 刪除單筆螢光筆-全文頁面點擊
	@Transactional
	@Modifying
	void deleteByEmailAndGroupIdAndIdAndCourt(String email, String groupId, String id, String court);

}
