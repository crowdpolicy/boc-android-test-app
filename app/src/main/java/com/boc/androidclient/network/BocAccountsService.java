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

/**
 * Service wrapper class that utilizes the BOC SDK AccountsService
 * This class implements the Accounts API calls, wrapping them into RxJava Single objects that will be used for Android Async calls
 */
public class BocAccountsService {

    private AccountsService accService;

    /**
     * constructor
     */
    public BocAccountsService(){
        accService = new AccountsService();
    }

    /**
     * Wrap the getAccounts call into a RxJava Single object
     * Invokes the respective SDK function and when the Async call is finished returns the response
     * @param subId
     * @return
     */
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


    /**
     * Wrap the getAccountDetails call into a RxJava Single object
     * Invokes the respective SDK function and when the Async call is finished returns the response
     * @param accId
     * @param subId
     * @return
     */
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

    /**
     * Wrap the getAvailableBalance call into a RxJava Single object
     * Invokes the respective SDK function and when the Async call is finished returns the response
     * @param accId
     * @param subId
     * @return
     */
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

    /**
     * Wrap the getAccountStatement call into a RxJava Single object
     * Invokes the respective SDK function and when the Async call is finished returns the response
     * @param accId
     * @param subId
     * @return
     */
    public Single<Statement> getAccountStatement(final String accId, final String subId, final String startDate, final String endDate, final int maxCount){
        return Single.fromCallable(new Callable<Statement>() {
            @Override
            public Statement call() throws Exception {
                String tppId = ApiConfiguration.TPP_ID;
                String journeyId = ApiConfiguration.JOURNEY_ID;
                String originUserId = ApiConfiguration.ORIGIN_USER_ID;

                Statement response = accService.getAccountStatement(accId, subId, startDate, endDate, new BigDecimal(maxCount), journeyId,originUserId,tppId,null,null,null,null,null,null,null);
                return response;
            }
        });
    }

}
