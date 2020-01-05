package org.gassman.order.job;

import org.springframework.context.annotation.Configuration;

@Configuration
public class JobConfiguration {
    public static void startJobs(){
        OrderNonPaidJob.getInstance().start();
        OrderProductDeliveryJob.getInstance().start();
    }
}
