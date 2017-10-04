package com.mambu.ant.phraseapp.api;

import java.io.IOException;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import com.mambu.ant.helper.JSONHelper;
import com.mambu.ant.order.model.RequestCreateOrderModel;
import com.mambu.ant.order.model.ResponseCreateOrderModel;
import com.mambu.ant.phraseapp.PhraseApiSettings;

/**
 * @author aifrim.
 */
public class OrdersApi extends BaseApi{

    private static final String ORDERS_ENDPOINT = Constants.BASE_ENDPOINT + "/orders";

    private final String baseUrl;

    public OrdersApi(PhraseApiSettings settings) {

        super(settings);

        this.baseUrl = String.format(ORDERS_ENDPOINT, settings.getProjectId());
    }

    public ResponseCreateOrderModel crateOrder(RequestCreateOrderModel requestCreateOrderModel) {

       try {
           String bodyContent = JSONHelper.toJSON(requestCreateOrderModel);

           String callResult = invokeAsString(Request.Post(this.baseUrl).bodyString(bodyContent, ContentType.APPLICATION_JSON));

           ResponseCreateOrderModel responseCreateOrderModel = JSONHelper.fromJSON(callResult, ResponseCreateOrderModel.class);

           return responseCreateOrderModel;

       } catch (IOException e) {
           throw new RuntimeException(e);
       }
    }
}
