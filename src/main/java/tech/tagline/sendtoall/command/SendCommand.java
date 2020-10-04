package tech.tagline.sendtoall.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import tech.tagline.sendtoall.SendToAll;
import tech.tagline.sendtoall.payload.CommandPayload;
import tech.tagline.trevor.api.database.DatabaseProxy;

import javax.inject.Named;

public class SendCommand {

  public static BrigadierCommand build(String instanceID, DatabaseProxy proxy) {
    return new BrigadierCommand(LiteralArgumentBuilder
            .<CommandSource>literal("sendtoall")
            .executes(context -> {
              CommandSource source = context.getSource();
              if (!source.hasPermission("sendtoall.dispatch")) {
                source.sendMessage(Component.text("Permission denied.").color(NamedTextColor.RED));
              }

              String name = source instanceof ConsoleCommandSource ? "Console" :
                      ((Player) source).getUniqueId().toString();
              String sender = "[" + instanceID + "]" + name;

              CommandPayload payload = new CommandPayload(sender, context.getInput());

              proxy.post(SendToAll.CHANNEL, payload);

              source.sendMessage(
                      Component.text("Command dispatched.").color(NamedTextColor.LIGHT_PURPLE));

              return 1;
            }).build());
  }
}
