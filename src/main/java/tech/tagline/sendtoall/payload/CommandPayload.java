package tech.tagline.sendtoall.payload;

import tech.tagline.trevor.api.network.event.EventProcessor;
import tech.tagline.trevor.api.network.event.NetworkEvent;
import tech.tagline.trevor.api.network.payload.NetworkPayload;

import java.util.UUID;

public class CommandPayload extends NetworkPayload<String> {

  private final String command;

  public CommandPayload(String sender, String command) {
    super(sender);

    this.command = command;
  }

  public String command() {
    return command;
  }

  @Override
  public EventProcessor.EventAction<? extends NetworkEvent> process(EventProcessor processor) {
    return processor.onMessage(this);
  }
}
