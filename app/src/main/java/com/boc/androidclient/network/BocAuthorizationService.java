package com.boc.androidclient.network;

import com.boc.client.model.AccessTokenResponse;
import com.boc.client.service.AuthorizationService;

import java.util.concurrent.Callable;

import io.reactivex.Single;

public class BocAuthorizationService {
    private AuthorizationService authService;

    public BocAuthorizationService(){
        authService = new AuthorizationService();
    }

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
