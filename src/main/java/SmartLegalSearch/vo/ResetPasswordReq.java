package SmartLegalSearch.vo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ResetPasswordReq {

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Invalid email format.")
    private String email; // email 不得為空白字串、空字串、null，且須符合格式

    @NotBlank(message = "Password cannot be blank.")
    private String password; // password 不得為空白字串、空字串、null

    public ResetPasswordReq() {
    }

    public ResetPasswordReq(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
