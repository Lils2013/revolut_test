package tsoy.alexander;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tsoy.alexander.model.Account;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

    @BeforeEach
    void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
        vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
    }

    @Test
    @DisplayName("Should start a Web Server on port 8080")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void start_http_server(Vertx vertx, VertxTestContext testContext) throws Throwable {
        vertx.createHttpClient().getNow(8080, "localhost", "/", response -> testContext.verify(() -> {
            assertEquals(200, response.statusCode());
            response.handler(body -> {
                assertTrue(body.toString().contains("Money transfer test!"));
                testContext.completeNow();
            });
        }));
    }

    @Test
    @DisplayName("Should create an account")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void create_account(Vertx vertx, VertxTestContext testContext) throws Throwable {
        WebClient client = WebClient.create(vertx);
        client.post(8080, "localhost", "/accounts").as(BodyCodec.json(Account.class)).sendJsonObject(new JsonObject()
                .put("username", "Curtis")
                .put("balance", "3501.25"), response -> testContext.verify(() -> {
            if (response.succeeded()) {
                assertEquals(201, response.result().statusCode());
                Account account = response.result().body();
                assertEquals(2L, (long) account.getId());
                assertEquals("Curtis", account.getUsername());
                assertEquals(new BigDecimal("3501.25"), account.getBalance());
            } else {
                fail();
            }
            testContext.completeNow();
        }));
    }
}
