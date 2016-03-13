package tellit.com.tellit.model.user_creation;

import retrofit.RestAdapter;
import retrofit.android.AndroidLog;

/**
 * Created by probojnik on 04.09.15.
 */
public class LoginPasswAPIFacade {
    RestAdapter restAdapter;
    ILoginPasswAPI service;
    private static LoginPasswAPIFacade ourInstance = new LoginPasswAPIFacade();

    public static LoginPasswAPIFacade getInstance() {
        return ourInstance;
    }

    private LoginPasswAPIFacade() {
        restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://52.11.20.55:9001")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new AndroidLog("LOGIN_API"))
                .build();

        service = restAdapter.create(ILoginPasswAPI.class);
    }

    public ILoginPasswAPI getService() {
        return service;
    }
}
