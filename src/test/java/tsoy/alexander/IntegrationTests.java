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
import tsoy.alexander.model.Transfer;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
public class IntegrationTests {

    @BeforeEach
    void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
        vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
    }

    @Test
    @DisplayName("Should start a Web Server on port 8080")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void start_http_server(Vertx vertx, VertxTestContext testContext) {
        vertx.createHttpClient().getNow(8080, "localhost", "/", response -> testContext.verify(() -> {
            assertEquals(200, response.statusCode());
            response.handler(body -> {
                assertTrue(body.toString().contains("Money transfer test!"));
                testContext.completeNow();
            });
        }));
    }

    @Test
    @DisplayName("Should create an account and get it")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void create_account(Vertx vertx, VertxTestContext testContext) {
        WebClient client = WebClient.create(vertx);
        client.post(8080, "localhost", "/accounts").as(BodyCodec.json(Account.class))
                .sendJsonObject(new JsonObject().put("username", "Curtis")
                        .put("balance", "3501.25"), response -> testContext.verify(() -> {
                    assertEquals(201, response.result().statusCode());
                    Account account = response.result().body();
                    assertEquals(2L, (long) account.getId());
                    assertEquals("Curtis", account.getUsername());
                    assertEquals(new BigDecimal("3501.25"), account.getBalance());
                    client.get(8080, "localhost", "/accounts/2").as(BodyCodec.json(Account.class))
                            .send(response1 -> testContext.verify(() -> {
                                assertEquals(200, response1.result().statusCode());
                                Account account1 = response1.result().body();
                                assertEquals(2L, (long) account1.getId());
                                assertEquals("Curtis", account1.getUsername());
                                assertEquals(new BigDecimal("3501.25"), account1.getBalance());
                                testContext.completeNow();
                            }));
                }));
    }

    @Test
    @DisplayName("Should fail to create an account with negative balance")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void create_account_fail(Vertx vertx, VertxTestContext testContext) {
        WebClient client = WebClient.create(vertx);
        client.post(8080, "localhost", "/accounts").as(BodyCodec.jsonObject())
                .sendJsonObject(new JsonObject().put("username", "Curtis")
                        .put("balance", "-3501.25"), response -> testContext.verify(() -> {
                    assertEquals(400, response.result().statusCode());
                    JsonObject responseObject = response.result().body();
                    assertEquals("ERROR", responseObject.getString("status"));
                    assertEquals("Can't create an account with negative balance!", responseObject.getString("description"));
                    testContext.completeNow();
                }));
    }

    @Test
    @DisplayName("Should get an account")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void get_account(Vertx vertx, VertxTestContext testContext) {
        WebClient client = WebClient.create(vertx);
        client.get(8080, "localhost", "/accounts/0").as(BodyCodec.json(Account.class))
                .send(response -> testContext.verify(() -> {
                    assertEquals(200, response.result().statusCode());
                    Account account = response.result().body();
                    assertEquals(0L, (long) account.getId());
                    assertEquals("John", account.getUsername());
                    assertEquals(new BigDecimal("100.00"), account.getBalance());
                    testContext.completeNow();
                }));
    }

    @Test
    @DisplayName("Should fail to get an account")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void get_account_fail(Vertx vertx, VertxTestContext testContext) {
        WebClient client = WebClient.create(vertx);
        client.get(8080, "localhost", "/accounts/100").as(BodyCodec.jsonObject())
                .send(response -> testContext.verify(() -> {
                    assertEquals(404, response.result().statusCode());
                    JsonObject responseObject = response.result().body();
                    assertEquals("ERROR", responseObject.getString("status"));
                    assertEquals("Failed to find account with id 100", responseObject.getString("description"));
                    testContext.completeNow();
                }));
    }

    @Test
    @DisplayName("Should get all accounts")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void get_all_accounts(Vertx vertx, VertxTestContext testContext) {
        WebClient client = WebClient.create(vertx);
        client.get(8080, "localhost", "/accounts").as(BodyCodec.jsonArray())
                .send(response -> testContext.verify(() -> {
                    assertEquals(2, response.result().body().size());
                    testContext.completeNow();
                }));
    }

    @Test
    @DisplayName("Should create a transfer and verify results")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void create_transfer(Vertx vertx, VertxTestContext testContext) {
        WebClient client = WebClient.create(vertx);
        client.post(8080, "localhost", "/transfers").as(BodyCodec.json(Transfer.class))
                .sendJsonObject(new JsonObject().put("sourceAccountId", 0).put("destinationAccountId", 1)
                        .put("amount", "10.00"), createResponse -> testContext.verify(() -> {
                    assertEquals(201, createResponse.result().statusCode());
                    Transfer transfer = createResponse.result().body();
                    assertEquals(0L, (long) transfer.getId());
                    assertEquals(0L, (long) transfer.getSourceAccountId());
                    assertEquals(1L, (long) transfer.getDestinationAccountId());
                    client.get(8080, "localhost", "/accounts/0").as(BodyCodec.json(Account.class))
                            .send(firstAccountResponse -> testContext.verify(() -> {
                                assertEquals(200, firstAccountResponse.result().statusCode());
                                Account account = firstAccountResponse.result().body();
                                assertEquals(0L, (long) account.getId());
                                assertEquals("John", account.getUsername());
                                assertEquals(new BigDecimal("90.00"), account.getBalance());
                                client.get(8080, "localhost", "/accounts/1").as(BodyCodec.json(Account.class))
                                        .send(secondAccountResponse -> testContext.verify(() -> {
                                            assertEquals(200, secondAccountResponse.result().statusCode());
                                            Account account1 = secondAccountResponse.result().body();
                                            assertEquals(1L, (long) account1.getId());
                                            assertEquals("Susan", account1.getUsername());
                                            assertEquals(new BigDecimal("210.00"), account1.getBalance());
                                            testContext.completeNow();
                                        }));
                            }));
                }));
    }

    @Test
    @DisplayName("Should fail to create a transfer with negative amount")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void create_transfer_fail_negative(Vertx vertx, VertxTestContext testContext) {
        WebClient client = WebClient.create(vertx);
        client.post(8080, "localhost", "/transfers").as(BodyCodec.jsonObject())
                .sendJsonObject(new JsonObject().put("sourceAccountId", 0).put("destinationAccountId", 1)
                        .put("amount", "-10.00"), response -> testContext.verify(() -> {
                    assertEquals(400, response.result().statusCode());
                    JsonObject responseObject = response.result().body();
                    assertEquals("ERROR", responseObject.getString("status"));
                    assertEquals("Can't transfer negative sum!", responseObject.getString("description"));
                    testContext.completeNow();
                }));
    }

    @Test
    @DisplayName("Should create a transfer and get list of all transfers")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void create_transfer_and_get_all(Vertx vertx, VertxTestContext testContext) {
        WebClient client = WebClient.create(vertx);
        client.post(8080, "localhost", "/transfers").as(BodyCodec.json(Transfer.class))
                .sendJsonObject(new JsonObject().put("sourceAccountId", 0).put("destinationAccountId", 1)
                        .put("amount", "10.00"), createResponse -> testContext.verify(() -> {
                    assertEquals(201, createResponse.result().statusCode());
                    Transfer transfer = createResponse.result().body();
                    assertEquals(0L, (long) transfer.getId());
                    assertEquals(0L, (long) transfer.getSourceAccountId());
                    assertEquals(1L, (long) transfer.getDestinationAccountId());
                    assertEquals(Transfer.TransferStatus.SUCCESSFUL, transfer.getResult());
                    client.get(8080, "localhost", "/transfers").as(BodyCodec.jsonArray())
                            .send(getAllResponse -> testContext.verify(() -> {
                                assertEquals(1, getAllResponse.result().body().size());
                                testContext.completeNow();
                            }));
                }));
    }

    @Test
    @DisplayName("Should create a failed transfer when balance is less than the amount being transferred " +
            "and verify that the balance did not change")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void create_transfer_fail(Vertx vertx, VertxTestContext testContext) {
        WebClient client = WebClient.create(vertx);
        client.post(8080, "localhost", "/transfers").as(BodyCodec.json(Transfer.class))
                .sendJsonObject(new JsonObject().put("sourceAccountId", 0).put("destinationAccountId", 1)
                        .put("amount", "1000.00"), createResponse -> testContext.verify(() -> {
                    assertEquals(201, createResponse.result().statusCode());
                    Transfer transfer = createResponse.result().body();
                    assertEquals(0L, (long) transfer.getId());
                    assertEquals(0L, (long) transfer.getSourceAccountId());
                    assertEquals(1L, (long) transfer.getDestinationAccountId());
                    assertEquals(Transfer.TransferStatus.FAILED, transfer.getResult());
                    client.get(8080, "localhost", "/accounts/0").as(BodyCodec.json(Account.class))
                            .send(firstAccountResponse -> testContext.verify(() -> {
                                assertEquals(200, firstAccountResponse.result().statusCode());
                                Account account = firstAccountResponse.result().body();
                                assertEquals(0L, (long) account.getId());
                                assertEquals("John", account.getUsername());
                                assertEquals(new BigDecimal("100.00"), account.getBalance());
                                client.get(8080, "localhost", "/accounts/1").as(BodyCodec.json(Account.class))
                                        .send(secondAccountResponse -> testContext.verify(() -> {
                                            assertEquals(200, secondAccountResponse.result().statusCode());
                                            Account account1 = secondAccountResponse.result().body();
                                            assertEquals(1L, (long) account1.getId());
                                            assertEquals("Susan", account1.getUsername());
                                            assertEquals(new BigDecimal("200.00"), account1.getBalance());
                                            testContext.completeNow();
                                        }));
                            }));
                }));
    }
}
