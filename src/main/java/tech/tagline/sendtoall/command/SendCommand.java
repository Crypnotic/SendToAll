package tech.tagline.sendtoall.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import tech.tagline.sendtoall.SendToAll;
import tech.tagline.sendtoall.payload.CommandPayload;
import tech.tagline.trevor.api.database.DatabaseProxy;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class SendCommand {

  public static BrigadierCommand build(String instanceID, DatabaseProxy proxy) {
    return new BrigadierCommand(LiteralArgumentBuilder.<CommandSource>literal("sendtoall")
            .requires(source -> source.hasPermission("sendtoall.dispatch"))
            .then(
                    RequiredArgumentBuilder.<CommandSource, String>argument("command", string())
                            .executes(context -> handle(context, instanceID, proxy))
            )
    );
  }

  private static int handle(CommandContext<CommandSource> context, String instanceID,
                            DatabaseProxy proxy) {
    CommandSource source = context.getSource();
    String command = getString(context, "command");
    String name = source instanceof ConsoleCommandSource ? "Console" :
            ((Player) source).getUniqueId().toString();
    String sender = "[" + instanceID + "]" + name;

    CommandPayload payload = new CommandPayload(sender, command);

    proxy.post(SendToAll.CHANNEL, payload);

    source.sendMessage(
            Component.text("Command dispatched.").color(NamedTextColor.LIGHT_PURPLE));

    return 1;
  }
}
