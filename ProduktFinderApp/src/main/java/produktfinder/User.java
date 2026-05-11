package produktfinder;

public class User {
    private String username;
    private String password;

    public boolean checkCredentials(String u, String p) {
        return username.equals(u) && password.equals(p);
    }
}
