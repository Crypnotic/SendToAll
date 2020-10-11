package tech.tagline.sendtoall;

import com.google.inject.Inject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import tech.tagline.sendtoall.command.SendCommand;
import tech.tagline.sendtoall.payload.CommandPayload;
import tech.tagline.trevor.api.TrevorAPI;
import tech.tagline.trevor.api.TrevorService;
import tech.tagline.trevor.api.network.event.NetworkIntercomEvent;
import tech.tagline.trevor.api.network.payload.NetworkPayload;

@Plugin(id = "sendtoall", name = "SendToAll", version = "@VERSION@",
        dependencies = { @Dependency(id = "trevor") })
public class SendToAll {

  public static final String CHANNEL = "sendtoall:dispatch";

  @Inject
  private Logger logger;
  @Inject
  private ProxyServer server;

  @Subscribe
  public void onProxyInitialization(ProxyInitializeEvent event) {
    TrevorAPI api = TrevorService.getAPI();

    api.getDatabase().getIntercom().add(CHANNEL);

    String instanceID = api.getPlatform().getInstanceConfiguration().getID();

    server.getCommandManager().register(
            server.getCommandManager().metaBuilder("sendtoall").build(),
            new SendCommand(instanceID, api.getDatabaseProxy()));
  }

  @Subscribe
  public void onNetworkMessage(NetworkIntercomEvent event) {
    if (event.payload() instanceof CommandPayload) {
      CommandPayload payload = (CommandPayload) event.payload();

      logger.info("Executing network command: " + payload.source() + " > " + payload.command());

      server.getCommandManager().executeAsync(server.getConsoleCommandSource(), payload.command());
    }
  }
}
