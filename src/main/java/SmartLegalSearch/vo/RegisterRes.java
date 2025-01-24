package SmartLegalSearch.vo;

public class RegisterRes extends BasicRes {

    public enum RegisterStatus {
        EMAIL_VERIFICATION_PENDING // email 未認證狀態
    }

    private String email;

    private RegisterStatus status; // pending、success、error

    public RegisterRes() {
    }

    public RegisterRes(int code, String message) {
        super(code, message);
    }

    public RegisterRes(int code, String message, String email, RegisterStatus status) {
        super(code, message);
        this.email = email;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public RegisterStatus getStatus() {
        return status;
    }
}
