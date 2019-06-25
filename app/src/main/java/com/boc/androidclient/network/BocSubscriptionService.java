package com.boc.androidclient.network;

import com.boc.client.api.ApiConfiguration;
import com.boc.client.model.AccountView;
import com.boc.client.model.CreateSubscriptionRequest;
import com.boc.client.model.CreateSubscriptionResponse;
import com.boc.client.model.PaymentView;
import com.boc.client.model.SubscriptionView;
import com.boc.client.model.UpdateSubscriptionRequest;
import com.boc.client.model.UpdateSubscriptionResponse;
import com.boc.client.service.SubscriptionService;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;


/**
 * Service wrapper class that utilizes the BOC SDK SubscriptionService
 * This class implements the Subscription API calls, wrapping them into RxJava Single objects that will be used for Android Async calls
 */
public class BocSubscriptionService {
    private SubscriptionService subService;
    private BocAuthorizationService authService;

    public BocSubscriptionService(){
        subService = new SubscriptionService();
        authService = new BocAuthorizationService();
    }

    /**
     * Wrap the createSubscription call into a RxJava Single object
     * Invokes the respective SDK function and when the Async call is finished returns the response
     * @return
     */
    public Single<CreateSubscriptionResponse> createSubscription(){
        return Single.fromCallable(new Callable<CreateSubscriptionResponse>() {
            @Override
            public CreateSubscriptionResponse call() throws Exception {
                String journeyId = ApiConfiguration.JOURNEY_ID;
                String appName = ApiConfiguration.APP_NAME;
                String originUserId = ApiConfiguration.ORIGIN_USER_ID;
                String tppId = ApiConfiguration.TPP_ID;

                // TODO: Mock data, provide option for user input
                PaymentView paymentView = new PaymentView();
                paymentView.setAmount(new BigDecimal(ApiConfiguration.AMOUNT));
                paymentView.setCurrency(ApiConfiguration.CURRENCY);
                paymentView.setLimit(new BigDecimal(ApiConfiguration.LIMIT));

                AccountView accountView = new AccountView();
                accountView.setBalance(ApiConfiguration.BALANCE);
                accountView.setTransactionHistory(ApiConfiguration.TRANASACTION_HISTORY);
                accountView.setDetails(ApiConfiguration.DETAILS);
                accountView.setCheckFundsAvailability(ApiConfiguration.CHECK_FUNDS_AVAILABILITY);

                CreateSubscriptionRequest request = new CreateSubscriptionRequest();
                request.setAccounts(accountView);
                request.setPayments(paymentView);

                CreateSubscriptionResponse response = subService.createSubscription(
                        request,
                        journeyId,
                        appName,
                        originUserId,
                        tppId,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);

                return response;
            }
        });
    }


    /**
     * Wrap the getSubscriptionIdInfo call into a RxJava Single object
     * Invokes the respective SDK function and when the Async call is finished returns the response
     * @param subId
     * @return
     */
    public Single<List<SubscriptionView>> getSubscriptionIdInfo(final String subId){
        return Single.fromCallable(new Callable<List<SubscriptionView>>() {
            @Override
            public List<SubscriptionView> call() throws Exception {
                String journeyId = ApiConfiguration.JOURNEY_ID;
                String originUserId = ApiConfiguration.ORIGIN_USER_ID;
                String tppId = ApiConfiguration.TPP_ID;
                List<SubscriptionView> response = subService.getSubscriptionIdInfo(subId,journeyId,originUserId,tppId,originUserId,null,null,null,null,null,null);

                return response;
            }
        });
    }


    /**
     * Wrap the patchSubscription call into a RxJava Single object
     * Invokes the respective SDK function and when the Async call is finished returns the response
     * @param subscriptionInfo
     * @param subId
     * @param accesstoken2
     * @return
     */
    public Single<UpdateSubscriptionResponse> patchSubscriptionIdInfo(final List<SubscriptionView> subscriptionInfo,final String subId, final String accesstoken2){
        return Single.fromCallable(new Callable<UpdateSubscriptionResponse>() {
            @Override
            public UpdateSubscriptionResponse call() throws Exception {
                String journeyId = ApiConfiguration.JOURNEY_ID;
                String appName = ApiConfiguration.APP_NAME;
                String originUserId = ApiConfiguration.ORIGIN_USER_ID;
                String tppId = ApiConfiguration.TPP_ID;

                SubscriptionView subView = subscriptionInfo.get(0);
                UpdateSubscriptionRequest requestBody = new UpdateSubscriptionRequest();
                requestBody.setPayments(subView.getPayments());
                requestBody.setAccounts(subView.getAccounts());
                requestBody.setSelectedAccounts(subView.getSelectedAccounts());

                UpdateSubscriptionResponse response = subService.patchSubscription(requestBody, subId, accesstoken2,journeyId,originUserId,tppId,appName,null,null,null,null,null,null,null);

                return response;
            }
        });
    }
}
