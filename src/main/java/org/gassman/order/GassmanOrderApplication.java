package org.gassman.order;

import org.gassman.order.binding.MQBinding;
import org.gassman.order.job.JobConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(MQBinding.class)
@EnableEurekaClient
@SpringBootApplication
public class GassmanOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(GassmanOrderApplication.class, args);
        JobConfiguration.startJobs();
    }
}
