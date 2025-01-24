package SmartLegalSearch.entity;


import java.io.Serial;
import java.io.Serializable;

public class CaseId implements Serializable {

    @Serial
    private static final long serialVersionUID = -7790140973026611129L;

    private String groupId; // 案件群組識別碼

    private String id; // 案件的唯一識別碼

    private String court;

    public CaseId() {
    }

    public CaseId(String groupId, String id, String court) {
        this.groupId = groupId;
        this.id = id;
        this.court = court;
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
}
