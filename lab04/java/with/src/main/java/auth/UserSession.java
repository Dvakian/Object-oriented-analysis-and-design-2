package auth;

public class UserSession {

    private static String login;
    private static boolean pro;

    public static void setLogin(String userLogin) {
        login = userLogin;
    }

    public static String getLogin() {
        return login;
    }

    public static void setPro(boolean isPro) {
        pro = isPro;
    }

    public static boolean isPro() {
        return pro;
    }
}
