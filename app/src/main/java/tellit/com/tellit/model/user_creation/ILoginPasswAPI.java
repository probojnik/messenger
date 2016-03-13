package tellit.com.tellit.model.user_creation;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by probojnik on 04.09.15.
 */
public interface ILoginPasswAPI {

    @GET("/request/{phone}")
    void getOTP(
            @Path("phone") String phone,
            Callback<String> cb
    );

    @GET("/request/{secretKey}/validate/{otp}")
    void getPassword(
            @Path("secretKey") String secretKey,
            @Path("otp") String otp,
            Callback<UserPassw> cb
    );

    @POST("/request/validate/phones/")
    PhoneNormaliseResp getNormalizPhones(
            @Body PhonesNormaliseReq req
            //, Callback<PhoneNormaliseResp> cb
    );
}
