package com.boc.androidclient.network;

import com.boc.client.model.AccessTokenResponse;
import com.boc.client.service.AuthorizationService;

import java.util.concurrent.Callable;

import io.reactivex.Single;

/**
 * Service wrapper class that utilizes the BOC SDK AuthorizationService
 * This class implements the Authorization API calls, wrapping them into RxJava Single objects that will be used for Android Async calls
 */
public class BocAuthorizationService {
    private AuthorizationService authService;

    /**
     * constructor
     */
    public BocAuthorizationService(){
        authService = new AuthorizationService();
    }

    /**
     * Wrap the getAccessToken2 (used toexchange authorization code to Patch a subscription) call into a RxJava Single object
     * Invokes the respective SDK function and when the Async call is finished returns the response
     * @param authcode
     * @return
     */
    public Single<AccessTokenResponse> getAccessToken2(final String authcode){
        return Single.fromCallable(new Callable<AccessTokenResponse>() {
            @Override
            public AccessTokenResponse call() throws Exception {
                AccessTokenResponse response = authService.getAccessToken2(authcode,null,null);
                return response;
            }
        });
    }


}
