
package tv.mycujoo.example.ping;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.launcher.VertxCommandLauncher;
import io.vertx.core.impl.launcher.VertxLifecycleHooks;
import io.vertx.core.json.JsonObject;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;
import io.vertx.micrometer.backends.BackendRegistries;

public class Launcher extends VertxCommandLauncher implements VertxLifecycleHooks {


  public static void main(String[] args) {
    new Launcher().dispatch(args);
  }


  public static void executeCommand(String cmd, String... args) {
    new Launcher().execute(cmd, args);
  }

  public void afterConfigParsed(JsonObject config) {
  }

  public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {

  }

  @Override
  public void beforeStoppingVertx(Vertx vertx) {

  }

  @Override
  public void afterStoppingVertx() {

  }

  public void beforeStartingVertx(VertxOptions options) {
    options.setMetricsOptions(new MicrometerMetricsOptions()
            .setPrometheusOptions(new VertxPrometheusOptions().setEnabled(true))
            .setJvmMetricsEnabled(true)
            .setEnabled(true));
  }

  public void afterStartingVertx(Vertx vertx) {
    PrometheusMeterRegistry registry = (PrometheusMeterRegistry) BackendRegistries.getDefaultNow();
    registry.config().meterFilter(
            new MeterFilter() {
              @Override
              public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
                return DistributionStatisticConfig.builder()
                        .percentilesHistogram(true)
                        .build()
                        .merge(config);
              }
            });
  }

  public void handleDeployFailed(Vertx vertx, String mainVerticle, DeploymentOptions deploymentOptions, Throwable cause) {
    // Default behaviour is to close Vert.x if the deploy failed
    vertx.close();
  }
}
