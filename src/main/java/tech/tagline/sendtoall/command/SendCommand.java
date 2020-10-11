package tech.tagline.sendtoall.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import tech.tagline.sendtoall.SendToAll;
import tech.tagline.sendtoall.payload.CommandPayload;
import tech.tagline.trevor.api.database.DatabaseProxy;

public class SendCommand implements SimpleCommand {

  private final String instanceID;
  private final DatabaseProxy proxy;

  public SendCommand(String instanceID, DatabaseProxy proxy) {
    this.instanceID = instanceID;
    this.proxy = proxy;
  }

  @Override
  public void execute(Invocation invocation) {
    CommandSource source = invocation.source();
    if (!source.hasPermission("sendtoall.dispatch")) {
      source.sendMessage(Component.text("Permission denied.").color(NamedTextColor.RED));
      return;
    }

    String command = String.join(" ", invocation.arguments());
    String name = source instanceof ConsoleCommandSource ? "Console" :
            ((Player) source).getUniqueId().toString();
    String sender = "[" + instanceID + "]" + name;

    CommandPayload payload = new CommandPayload(sender, command);

    proxy.post(SendToAll.CHANNEL, payload);

    source.sendMessage(
            Component.text("Command dispatched.").color(NamedTextColor.LIGHT_PURPLE));
  }
}
