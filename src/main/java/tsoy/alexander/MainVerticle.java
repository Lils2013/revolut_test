package tsoy.alexander;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Launcher;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import tsoy.alexander.dao.AccountDao;
import tsoy.alexander.dao.Dao;
import tsoy.alexander.dao.TransferDao;
import tsoy.alexander.model.Account;
import tsoy.alexander.model.Transfer;

import java.math.BigDecimal;

public class MainVerticle extends AbstractVerticle {

    private Dao<Account> accountDao = new AccountDao();
    private Dao<Transfer> transferDao = new TransferDao();

    public static void main(final String[] args) {
        Launcher.executeCommand("run", MainVerticle.class.getName());
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.route("/").handler(req -> {
            req.response()
                    .putHeader("content-type", "text/plain")
                    .end("Money transfer test!");
        });

        router.get("/accounts").handler(this::getAllAccounts);
        router.get("/accounts/:id").handler(this::getAccount);
        router.post("/accounts").handler(this::createAccount);

        router.get("/transfers").handler(this::getAllTransfers);
        router.post("/transfers").handler(this::createTransfer);

        vertx.createHttpServer().requestHandler(router::accept).listen(8080, http -> {
            if (http.succeeded()) {
                startFuture.complete();
                System.out.println("HTTP server started on http://localhost:8080");
            } else {
                startFuture.fail(http.cause());
            }
        });
    }

    private void getAllAccounts(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(accountDao.getAll()));
    }

    private void getAccount(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        try {
            final long idAsLong = Long.parseLong(id);
            if (!accountDao.get(idAsLong).isPresent()) {
                respondWithError(routingContext, 404, "Failed to find account with id " + id);
            } else {
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(accountDao.get(idAsLong).get()));
            }
        } catch (NumberFormatException e) {
            respondWithError(routingContext, 400, "Invalid parameter " + id);
        }
    }

    private void createAccount(RoutingContext routingContext) {
        try {
            JsonObject jsonObject = routingContext.getBodyAsJson();
            final Account account = new Account(jsonObject.getString("username"),
                    new BigDecimal(jsonObject.getString("balance")));
            if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                respondWithError(routingContext, 400, "Can't create an account with negative balance!");
            } else {
                accountDao.save(account);
                routingContext.response().setStatusCode(201)
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(account));
            }
        } catch (Exception e) {
            respondWithError(routingContext, 400, "Can't parse the body of the request!");
        }
    }

    private void getAllTransfers(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(transferDao.getAll()));
    }

    private void createTransfer(RoutingContext routingContext) {
        try {
            JsonObject jsonObject = routingContext.getBodyAsJson();
            final Transfer transfer = new Transfer(jsonObject.getLong("sourceAccountId"),
                    jsonObject.getLong("destinationAccountId"), new BigDecimal(jsonObject.getString("amount")));
            if (transfer.getDestinationAccountId().equals(transfer.getSourceAccountId())) {
                respondWithError(routingContext, 400, "Can't transfer money to the same account!");
            } else if (transfer.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                respondWithError(routingContext, 400, "Can't transfer negative sum!");
            } else if (!accountDao.get(transfer.getSourceAccountId()).isPresent() || !accountDao.get(transfer.getDestinationAccountId()).isPresent()) {
                respondWithError(routingContext, 400, "Failed to find accounts with given ids");
            } else {
                try {
                    transfer(accountDao.get(transfer.getSourceAccountId()).get(), accountDao.get(transfer.getDestinationAccountId()).get(),
                            transfer.getAmount());
                    transfer.setResult(Transfer.TransferStatus.SUCCESSFUL);
                } catch (Exception e) {
                    transfer.setResult(Transfer.TransferStatus.FAILED);
                } finally {
                    transferDao.save(transfer);
                    routingContext.response()
                            .setStatusCode(201)
                            .putHeader("content-type", "application/json; charset=utf-8")
                            .end(Json.encodePrettily(transfer));
                }
            }
        } catch (Exception e) {
            respondWithError(routingContext, 400, "Can't parse the body of the request!");
        }
    }

    private void respondWithError(RoutingContext routingContext, int i, String s) {
        routingContext.response().setStatusCode(i).putHeader("content-type", "application/json; charset=utf-8")
                .end(new JsonObject()
                        .put("status", "ERROR")
                        .put("description", s).encodePrettily());
    }

    private void transfer(Account acc1, Account acc2, BigDecimal value) {
        if (acc1.getBalance().compareTo(value) >= 0) {
            acc1.withdraw(value);
            acc2.deposit(value);
        } else {
            throw new IllegalStateException();
        }
    }
}