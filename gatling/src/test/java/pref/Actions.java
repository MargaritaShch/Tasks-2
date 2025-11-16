package pref;

import io.gatling.javaapi.http.HttpRequestActionBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class Actions {

    public static HttpRequestActionBuilder getById() {
        return http("TC01_GET_product_by_id")
                .get("/api/products/#{id}")
                .check(status().is(200));
    }

    public static HttpRequestActionBuilder create() {
        return http("TC02_POST_create_product")
                .post("/api/products")
                .header("Content-Type", "application/json")
                .body(StringBody("{\"name\":\"Item-#{uuid}\",\"category\":\"LoadTest\",\"price\": #{price},\"stock\": #{stock}}"))
                .check(status().in(200, 201))
                //.check(jsonPath("$.id").saveAs("id")); // сохраняем id созданного
                .check(jsonPath("$.id").saveAs("newId"));
    }

    public static HttpRequestActionBuilder update() {
        return http("TC03_PUT_update_product")
                .put("/api/products/#{id}")
                .header("Content-Type", "application/json")
                .body(StringBody("{\"name\":\"#{name}\",\"category\":\"LoadTest\",\"price\": #{price},\"stock\": #{stock}}"))
                .check(status().is(200));
    }

    public static HttpRequestActionBuilder deleteById() {
        return http("TC04_DELETE_product")
                .delete("/api/products/#{newId}")
                .check(status().in(200, 204));
    }
}
