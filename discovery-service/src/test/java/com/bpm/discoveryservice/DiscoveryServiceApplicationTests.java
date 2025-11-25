package com.bpm.discoveryservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnableAutoConfiguration(exclude = {
        org.springframework.cloud.netflix.eureka.server.EurekaServerAutoConfiguration.class
})
class DiscoveryServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
