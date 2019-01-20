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
import tsoy.alexander.model.Account;
import tsoy.alexander.model.Transfer;

public class MainVerticle extends AbstractVerticle {

    private Dao<Account> accountDao = new AccountDao();

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
                    .end("Hello from Vert.x!");
        });

        router.get("/accounts").handler(this::getAllAccounts);
        router.get("/accounts/:id").handler(this::getAccount);
        router.post("/accounts").handler(this::createAccount);
        router.delete("/accounts/:id").handler(this::deleteAccount);

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
        if (id == null) {
            routingContext.response().setStatusCode(400).putHeader("content-type", "application/json; charset=utf-8")
                    .end(new JsonObject()
                    .put("status", "ERROR")
                    .put("description", "Failed to read id").encodePrettily());
        } else {
            final long idAsLong = Long.parseLong(id);
            if (!accountDao.get(idAsLong).isPresent()) {
                routingContext.response().setStatusCode(404)
                        .putHeader("content-type", "application/json; charset=utf-8").end(new JsonObject()
                        .put("status", "ERROR")
                        .put("description", "Failed to find account with id " + id)
                        .encodePrettily());
            } else {
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(accountDao.get(idAsLong).get()));
            }
        }
    }

    private void createAccount(RoutingContext routingContext) {
        try {
            final Account account = Json.decodeValue(routingContext.getBodyAsString(),
                    Account.class);
            accountDao.save(account);
            routingContext.response()
                    .setStatusCode(201)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(account));
        } catch (Exception e) {
            routingContext.response().setStatusCode(400).end();
        }
    }

    private void deleteAccount(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).putHeader("content-type", "application/json; charset=utf-8")
                    .end(new JsonObject()
                            .put("status", "ERROR")
                            .put("description", "Failed to read id").encodePrettily());
        } else {
            final long idAsLong = Long.parseLong(id);
            if (!accountDao.get(idAsLong).isPresent()) {
                routingContext.response().setStatusCode(404)
                        .putHeader("content-type", "application/json; charset=utf-8").end(new JsonObject()
                        .put("status", "ERROR")
                        .put("description", "Failed to find account with id " + id)
                        .encodePrettily());
            } else {
                accountDao.delete(accountDao.get(idAsLong).get());
                routingContext.response().setStatusCode(204).end();
            }
        }
    }

    private void createTransfer(RoutingContext routingContext) {
        try {
            final Transfer transfer = Json.decodeValue(routingContext.getBodyAsString(),
                    Transfer.class);
//            transfers.put(transfer.getId(), transfer);
            routingContext.response()
                    .setStatusCode(201)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(transfer));
        } catch (Exception e) {
            routingContext.response().setStatusCode(400).end();
        }
    }
}