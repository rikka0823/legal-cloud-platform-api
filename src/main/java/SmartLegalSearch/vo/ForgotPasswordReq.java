package SmartLegalSearch.vo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordReq {

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Invalid email format.")
    private String email; // email 不得為空白字串、空字串、null，且須符合格式

    public ForgotPasswordReq() {
    }

    public ForgotPasswordReq(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
