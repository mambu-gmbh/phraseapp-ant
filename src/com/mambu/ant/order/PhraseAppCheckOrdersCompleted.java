package com.mambu.ant.order;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;

import com.mambu.ant.BaseTask;
import com.mambu.ant.order.model.ResponseCreateOrderModel;

/**
 * Ant target for checking if orders are completed in PhraseApp
 *
 * @author aifrim.
 */
public class PhraseAppCheckOrdersCompleted extends BaseTask {

    private static final String ORDER_ID_SEPARATOR = ",";
    private static final String COMPLETED_STATE = "completed";

    private String orderIds;

    public static void main(String[] args) {

        PhraseAppCheckOrdersCompleted check = new PhraseAppCheckOrdersCompleted();

        check.execute();
    }

    @Override
    public void execute() throws BuildException {

        if (StringUtils.isEmpty(orderIds)) {

            log("No orders were requested for confirmation.");

            return;
        }

        initAPI();

        String[] orders = StringUtils.split(orderIds, ORDER_ID_SEPARATOR);

        boolean allOrdersCompleted = true;

        for (String orderId : orders) {

            ResponseCreateOrderModel response = phraseApi.orders().getOrder(orderId);

            log(String.format("The order for %s is %s and has %s percentage completed", response.getTarget_locales().get(0).getName(), response.getState(), response.getProgress_percent()));

            allOrdersCompleted &= COMPLETED_STATE.equalsIgnoreCase(response.getState());
        }

        if(!allOrdersCompleted) {
            throw new RuntimeException("Not all orders are completed");
        } else {
            log("All orders are completed");
        }
    }

    public void setOrderIds(String orderIds) {
        this.orderIds = orderIds;
    }
}
