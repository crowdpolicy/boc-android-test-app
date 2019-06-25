package com.boc.androidclient.network;

import com.boc.client.api.ApiConfiguration;
import com.boc.client.model.Amount;
import com.boc.client.model.AuthorizePaymentRequest;
import com.boc.client.model.CreatePaymentResponse;
import com.boc.client.model.Creditor;
import com.boc.client.model.Debtor;
import com.boc.client.model.FundAvailabilityRequest;
import com.boc.client.model.Payment;
import com.boc.client.model.SignPaymentRequest;
import com.boc.client.model.SignPaymentResponse;
import com.boc.client.model.Status;
import com.boc.client.service.PaymentsService;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;

public class BocPaymentsService {

    private PaymentsService payService;

    public BocPaymentsService(){
        payService = new PaymentsService();
    }

    public Single<SignPaymentResponse> signPayment(final SignPaymentRequest requestBody){
        return Single.fromCallable(new Callable<SignPaymentResponse>() {
            @Override
            public SignPaymentResponse call() throws Exception {
                String tppId = ApiConfiguration.TPP_ID;
                SignPaymentResponse response = payService.signPayment(requestBody, tppId);
                return response;
            }
        });
    }

    public Single<CreatePaymentResponse> createPayment(final SignPaymentResponse signResponse, final String subId){
        return Single.fromCallable(new Callable<CreatePaymentResponse>() {
            @Override
            public CreatePaymentResponse call() throws Exception {
                String tppId = ApiConfiguration.TPP_ID;
                String journeyId = ApiConfiguration.JOURNEY_ID;
                String originUserId = ApiConfiguration.ORIGIN_USER_ID;

                CreatePaymentResponse response =  payService.createPayment(signResponse,subId,journeyId,originUserId,tppId,null,null,null,null,null,null,null);
                return response;
            }
        });
    }

    public Single<Status> authorizePayment(final String payId, final String subId){
        return Single.fromCallable(new Callable<Status>() {
            @Override
            public Status call() throws Exception {
                AuthorizePaymentRequest authorizeRequestBody = new AuthorizePaymentRequest();

                String tppId = ApiConfiguration.TPP_ID;
                String journeyId = ApiConfiguration.JOURNEY_ID;
                String originUserId = ApiConfiguration.ORIGIN_USER_ID;

                Status response = payService.authorizePayment(authorizeRequestBody, payId ,subId, journeyId, originUserId, tppId, null, null, null, null, null, null, null);
                return response;
            }
        });
    }

    public Single<Payment> getPaymentDetails(final String payId, final String subId){
        return Single.fromCallable(new Callable<Payment>() {
            @Override
            public Payment call() throws Exception {
                String tppId = ApiConfiguration.TPP_ID;
                String journeyId = ApiConfiguration.JOURNEY_ID;
                String originUserId = ApiConfiguration.ORIGIN_USER_ID;

                Payment response = payService.getPaymentDetails(payId,subId,journeyId,originUserId,tppId,null,null,null,null,null,null,null);
                return response;
            }
        });
    }

    public Single<Status> getPaymentStatus(final String payId, final String subId){
        return Single.fromCallable(new Callable<Status>() {
            @Override
            public Status call() throws Exception {
                String tppId = ApiConfiguration.TPP_ID;
                String journeyId = ApiConfiguration.JOURNEY_ID;
                String originUserId = ApiConfiguration.ORIGIN_USER_ID;

                Status response = payService.getPaymentStatus(payId,subId,journeyId,originUserId,tppId,null,null,null,null,null,null,null).getStatus();
                return response;
            }
        });
    }

    public Single<List<Payment>> getAccountPayments(final String accId, final String subId){
        return Single.fromCallable(new Callable<List<Payment>>() {
            @Override
            public List<Payment> call() throws Exception {
                String tppId = ApiConfiguration.TPP_ID;
                String journeyId = ApiConfiguration.JOURNEY_ID;
                String originUserId = ApiConfiguration.ORIGIN_USER_ID;

                List<Payment> response = payService.getAccountPayments(accId,subId,journeyId,originUserId,tppId,"","",1, null,null,null,null,null,null,null);
                return response;
            }
        });
    }

    public Single<Boolean> getAccountPayments(final FundAvailabilityRequest requestBody, final String subId){
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                String tppId = ApiConfiguration.TPP_ID;
                String journeyId = ApiConfiguration.JOURNEY_ID;
                String originUserId = ApiConfiguration.ORIGIN_USER_ID;

                Boolean response = payService.fundAvailability(requestBody, subId,journeyId,originUserId,tppId,null,null,null,null,null,null,null);
                return response;
            }
        });
    }


}
