package guru.sfg.beer.inventory.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;
import org.springframework.core.env.AbstractEnvironment;

@SpringBootApplication(exclude = ArtemisAutoConfiguration.class)
public class BeerInventoryApplication {

    public static void main(String[] args) {
        System.setProperty(AbstractEnvironment.DEFAULT_PROFILES_PROPERTY_NAME,"localmysql");
        SpringApplication.run(BeerInventoryApplication.class, args);
    }

}
