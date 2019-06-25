package com.boc.androidclient.network;

import com.boc.client.api.ApiConfiguration;
import com.boc.client.model.Account;
import com.boc.client.model.Balance;
import com.boc.client.model.Statement;
import com.boc.client.service.AccountsService;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;

public class BocAccountsService {

    private AccountsService accService;

    public BocAccountsService(){
        accService = new AccountsService();
    }

    public Single<List<Account>> getAccounts(final String subId){
        return Single.fromCallable(new Callable<List<Account>>() {
            @Override
            public List<Account> call() throws Exception {
                String tppId = ApiConfiguration.TPP_ID;
                String journeyId = ApiConfiguration.JOURNEY_ID;
                String originUserId = ApiConfiguration.ORIGIN_USER_ID;

                List<Account> response = accService.getAccounts(subId,journeyId,originUserId,tppId,null,null,null,null,null,null,null,null,null);
                return response;
            }
        });
    }


    public Single<List<Account>> getAccountDetails(final String accId, final String subId){
        return Single.fromCallable(new Callable<List<Account>>() {
            @Override
            public List<Account> call() throws Exception {
                String tppId = ApiConfiguration.TPP_ID;
                String journeyId = ApiConfiguration.JOURNEY_ID;
                String originUserId = ApiConfiguration.ORIGIN_USER_ID;

                List<Account> response = accService.getAccountDetails(accId, subId, journeyId,originUserId,tppId,null,null,null,null,null,null,null,null);
                return response;
            }
        });
    }

    public Single<List<Balance>> getAvailableBalance(final String accId, final String subId){
        return Single.fromCallable(new Callable<List<Balance>>() {
            @Override
            public List<Balance> call() throws Exception {
                String tppId = ApiConfiguration.TPP_ID;
                String journeyId = ApiConfiguration.JOURNEY_ID;
                String originUserId = ApiConfiguration.ORIGIN_USER_ID;

                List<Balance> response = accService.getAvailableBalance(accId, subId, journeyId,originUserId,tppId,null,null,null,null,null,null,null);
                return response;
            }
        });
    }

    public Single<Statement> getAccountStatement(final String accId, final String subId){
        return Single.fromCallable(new Callable<Statement>() {
            @Override
            public Statement call() throws Exception {
                String tppId = ApiConfiguration.TPP_ID;
                String journeyId = ApiConfiguration.JOURNEY_ID;
                String originUserId = ApiConfiguration.ORIGIN_USER_ID;

                Statement response = accService.getAccountStatement(accId, subId, "","", new BigDecimal(1), journeyId,originUserId,tppId,null,null,null,null,null,null,null);
                return response;
            }
        });
    }

}
