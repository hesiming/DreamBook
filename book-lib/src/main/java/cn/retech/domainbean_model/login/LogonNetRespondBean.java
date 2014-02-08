package cn.retech.domainbean_model.login;

import java.io.Serializable;

public final class LogonNetRespondBean implements Serializable {
  /**
	 * 
	 */
  private static final long serialVersionUID = 8580406908826318013L;
  private String username;
  private String password;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}
