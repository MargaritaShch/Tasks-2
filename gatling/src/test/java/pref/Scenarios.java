package pref;

import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static io.gatling.javaapi.core.CoreDsl.*;

public class Scenarios {

    // csv
    public static final FeederBuilder.Batchable<String> csvFeeder =
            csv("products.csv").circular();

    public static final ScenarioBuilder readById =
            scenario("TC01_Read_By_Id")
                    .feed(csvFeeder)  // id from csv
                    .exec(Chains.getProduct)
                    .pause(Duration.ofMillis(300), Duration.ofMillis(1200));

    public static final ScenarioBuilder updateProduct =
            scenario("TC03_Update_Product")
                    .feed(csvFeeder)  // id + new fields from CSV
                    .exec(Chains.updateProduct)
                    .pause(Duration.ofMillis(300), Duration.ofMillis(1200));

    public static final ScenarioBuilder createProduct =
            scenario("TC02_Create_Product")
                    .feed(csvFeeder)
                    .exec(session -> session
                            .set("uuid", UUID.randomUUID().toString())
                            .set("name", "Upd-" + UUID.randomUUID())
                    )
                    .exec(Chains.createProduct)
                    .pause(Duration.ofMillis(500), Duration.ofMillis(1500));

    // DELETE
    public static final ScenarioBuilder deleteProduct =
            scenario("TC04_Delete_Product")
                    .exec(session -> session
                            .set("uuid", UUID.randomUUID().toString())
                            .set("price", ThreadLocalRandom.current().nextDouble(0.0, 1000.0))
                            .set("stock", ThreadLocalRandom.current().nextInt(0, 500))
                    )
                    .exec(Chains.createProduct)                 // save id in session
                    .pause(Duration.ofMillis(200), Duration.ofMillis(800))
                    .exec(Chains.deleteProduct)
                    .pause(Duration.ofMillis(800), Duration.ofMillis(2000));
}
