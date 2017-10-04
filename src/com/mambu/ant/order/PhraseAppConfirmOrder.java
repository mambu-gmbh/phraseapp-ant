package com.mambu.ant.order;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;

import com.mambu.ant.BaseTask;
import com.mambu.ant.order.model.ResponseCreateOrderModel;

/**
 * Ant target for confirming translations orders to PhraseApp
 * @author aifrim.
 */
public class PhraseAppConfirmOrder extends BaseTask {

    private static final String ORDER_ID_SEPARATOR = ",";

    private String orderIds;

    /**
     * Create a PhraseApp session and for the given list of order ids, orders will confirmed
     */
    @Override
    public void execute() throws BuildException {

        if (StringUtils.isEmpty(orderIds) ){

            log("No orders were requested for confirmation.");

            return;
        }

        initAPI();

        String[] orders = StringUtils.split(orderIds, ORDER_ID_SEPARATOR);

        for(String orderId: orders) {

            ResponseCreateOrderModel response = phraseApi.orders().confirmOrder(orderId);

            log(String.format("Order %s for language % was confirmed", response.getId(), response.getTarget_locales().get(0).getName()));
        }
    }

    public void setOrderIds(String orderIds) {
        this.orderIds = orderIds;
    }
}
