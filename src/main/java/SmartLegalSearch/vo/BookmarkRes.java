package SmartLegalSearch.vo;

import java.util.List;

import SmartLegalSearch.entity.Bookmark;

public class BookmarkRes extends BasicRes {
// 書籤
	private List<Bookmark> bookmarkList;

	public BookmarkRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BookmarkRes(int code, String message) {
		super(code, message);
		// TODO Auto-generated constructor stub
	}

	public BookmarkRes(int code, String message, List<Bookmark> bookmarkList) {
		super(code, message);
		this.bookmarkList = bookmarkList;
	}

	public List<Bookmark> getBookmarkList() {
		return bookmarkList;
	}

}
