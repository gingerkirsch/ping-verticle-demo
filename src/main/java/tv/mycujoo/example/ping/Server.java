package tv.mycujoo.example.ping;

import graphql.GraphQL;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Launcher;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.graphql.GraphQLHandler;
import io.vertx.ext.web.handler.graphql.VertxDataFetcher;
import io.vertx.micrometer.backends.BackendRegistries;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

public class Server extends AbstractVerticle {

  public static void main(String[] args) {
    Launcher.executeCommand("run", Server.class.getName());
  }

  @Override
  public void start() {
    Router router = Router.router(vertx);
    router.route("/graphql").handler(GraphQLHandler.create(createGraphQL()));


    PrometheusMeterRegistry registry = (PrometheusMeterRegistry) BackendRegistries.getDefaultNow();

    vertx.setPeriodic(1000, timer -> {
        vertx.eventBus().send("metrics",
                new JsonObject().put("origin", "ping").put("metric", registry.scrape()));

    });


    vertx.createHttpServer()
            .requestHandler(router)
            .listen(8666);
  }

  private GraphQL createGraphQL() {
    String schema = vertx.fileSystem().readFileBlocking("ping.graphqls").toString();

    SchemaParser schemaParser = new SchemaParser();
    TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

    RuntimeWiring runtimeWiring = newRuntimeWiring()
            .type("Query", builder -> {
              VertxDataFetcher<String> ping = new VertxDataFetcher<>(this::doPing);
              return builder.dataFetcher("ping", ping);
            })
            .build();

    SchemaGenerator schemaGenerator = new SchemaGenerator();
    GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

    return GraphQL.newGraphQL(graphQLSchema)
            .build();
  }

  private void doPing(DataFetchingEnvironment env, Future<String> future) {
    vertx.eventBus().send("eventbus.ping", "hello moto", handler ->
            future.complete(handler.result().body().toString()));
  }

}
