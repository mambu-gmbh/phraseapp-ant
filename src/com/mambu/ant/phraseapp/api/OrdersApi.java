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

    private static final String CREATE_ORDERS_ENDPOINT = Constants.BASE_ENDPOINT + "/orders";

    private static final String CONFIRM_ORDERS_ENDPOINT = CREATE_ORDERS_ENDPOINT + "/%s/confirm";

    private final String baseUrl;
    private final String confirmOrderUrl;

    public OrdersApi(PhraseApiSettings settings) {

        super(settings);

        this.baseUrl = String.format(CREATE_ORDERS_ENDPOINT, settings.getProjectId());
        this.confirmOrderUrl = String.format(CONFIRM_ORDERS_ENDPOINT, settings.getProjectId(), "%s");
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

    public ResponseCreateOrderModel confirmOrder(String orderId) {

        try {

            String confirmURL = String.format(confirmOrderUrl, orderId);

            String callResult = invokeAsString(Request.Patch(confirmURL));

            return JSONHelper.fromJSON(callResult, ResponseCreateOrderModel.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public ResponseCreateOrderModel getOrder(String orderId) {

        try {
            String getOrderURL = baseUrl + "/" + orderId;


            String callResult = invokeAsString(Request.Get(getOrderURL));

            return JSONHelper.fromJSON(callResult, ResponseCreateOrderModel.class);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
