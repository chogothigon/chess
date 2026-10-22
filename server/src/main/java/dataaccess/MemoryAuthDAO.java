package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO{

    private final Map<String, AuthData> auths = new HashMap<>();

    public void createAuth(AuthData auth) {
        auths.put(auth.authToken(), auth);
    }

    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }

    public void clear() {
        auths.clear();
    };
}
