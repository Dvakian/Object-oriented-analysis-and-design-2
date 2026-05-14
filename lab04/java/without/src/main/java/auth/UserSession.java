package auth;

public class UserSession {

    private static String login;
    private static int accessLevel;

    public static void setLogin(String userLogin) {
        login = userLogin;
    }

    public static String getLogin() {
        return login;
    }

    public static void setAccessLevel(int level) {
        accessLevel = level;
    }

    public static int getAccessLevel() {
        return accessLevel;
    }
}
