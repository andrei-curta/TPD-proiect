package server;

import java.util.Calendar;
import java.util.Date;

public class AuthData {
    String username;
    Date validUntil;

    public AuthData() {
    }

    public AuthData(String username, Date validUntil) {
        this.username = username;
        this.validUntil = validUntil;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Date validUntil) {
        this.validUntil = validUntil;
    }

    public boolean isValid() {
        Date currentDate = new Date();
        return currentDate.before(validUntil);
    }
}
