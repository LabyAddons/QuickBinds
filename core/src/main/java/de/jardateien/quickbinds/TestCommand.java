package de.jardateien.quickbinds;

import net.labymod.api.client.chat.command.Command;
import java.util.UUID;

public class TestCommand extends Command {

  public TestCommand() {
    super("test123");
  }

  @Override
  public boolean execute(String prefix, String[] arguments) {
    if(arguments.length == 2) {
      switch (arguments[0]) {
        case "save": {
          QuickBindsAddon.referenceStorage().profileController().saveCurrentProfile(arguments[1]);
          this.displayMessage("Saved profile test123");
          break;
        }

        case "load": {
          QuickBindsAddon.referenceStorage().profileController().loadProfile(UUID.fromString(arguments[1]));
          this.displayMessage("Loaded profile test123");
          break;
        }
      }
    }
    return false;
  }
}
