package SmartLegalSearch.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteAccountReq {

	@JsonProperty(value = "email")
    private String email;

	@JsonProperty(value = "password")
    private String password;

    public DeleteAccountReq() {
		super();
	}

	public DeleteAccountReq(String email, String password) {
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
