package pref;

import io.gatling.javaapi.core.OpenInjectionStep;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class ProductSimulation extends Simulation {

    private static final String BASE_URL = System.getProperty("baseUrl", "http://localhost:8080");

    private static final HttpProtocolBuilder httpConf = http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    private static final Duration STEP = Duration.ofMinutes(10); // time of one step
    // запуск теста jdbc 12:57 09.11
    // запуск теста jpa 14:19 09.11

    private static final double[] MULT = {0.5, 1.00, 1.5, 2.00, 2.5, 3.00, 3.5};

    //debug
    //private static final double[] MULT = {2.5, 3.00};

    private static final double BASE_READ_RPS   = 50.0;
    private static final double BASE_UPDATE_RPS =  10.0;
    private static final double BASE_CREATE_RPS =  5.0;
    private static final double BASE_DELETE_RPS =  2.5;

    private static double[] scale(double base) {
        double[] out = new double[MULT.length];
        for (int i = 0; i < MULT.length; i++) out[i] = base * MULT[i];
        return out;
    }

    private static OpenInjectionStep[] steps(double[] levels, Duration step) {
        OpenInjectionStep[] arr = new OpenInjectionStep[levels.length];
        for (int i = 0; i < levels.length; i++) {
            arr[i] = constantUsersPerSec(levels[i]).during(step);
        }
        return arr;
    }

    private static final double[] READ_STEPS   = scale(BASE_READ_RPS);
    private static final double[] UPDATE_STEPS = scale(BASE_UPDATE_RPS);
    private static final double[] CREATE_STEPS = scale(BASE_CREATE_RPS);
    private static final double[] DELETE_STEPS = scale(BASE_DELETE_RPS);

    {
        setUp(
                Scenarios.readById.injectOpen(steps(READ_STEPS, STEP)),
                Scenarios.updateProduct.injectOpen(steps(UPDATE_STEPS, STEP)),
                Scenarios.createProduct.injectOpen(steps(CREATE_STEPS, STEP)),
                Scenarios.deleteProduct.injectOpen(steps(DELETE_STEPS, STEP))
        )
                .protocols(httpConf)
                .maxDuration(Duration.ofMinutes(MULT.length * STEP.toMinutes() + 1))
                .assertions(
                        global().failedRequests().percent().lt(1.0),
                        global().responseTime().percentile(95).lt(2000),
                        global().responseTime().percentile(99).lt(5000)
                );
    }
}
