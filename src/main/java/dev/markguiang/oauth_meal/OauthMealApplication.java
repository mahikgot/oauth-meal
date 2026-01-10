/* (C)2026 */
package dev.markguiang.oauth_meal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class OauthMealApplication {

    public static void main(String[] args) {
        SpringApplication.run(OauthMealApplication.class, args);
    }
}
