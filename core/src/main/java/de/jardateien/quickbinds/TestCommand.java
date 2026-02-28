package de.jardateien.quickbinds;

import net.labymod.api.client.chat.command.Command;
import java.util.UUID;

public class TestCommand extends Command {

  public TestCommand() {
    super("test123");
  }

  @Override
  public boolean execute(String prefix, String[] arguments) {
    if(arguments.length == 1) {
      switch (arguments[0]) {
        case "save": {
          QuickBindsAddon.referenceStorage().profileController().saveCurrentProfile("test123");
          this.displayMessage("Saved profile test123");
        }

        case "load": {
          QuickBindsAddon.referenceStorage().profileController().loadProfile(UUID.fromString("a6eb9e63-3683-4eba-8e2d-eb1823b6b1b9"));
          this.displayMessage("Loaded profile test123");
        }
      }
    }
    return false;
  }
}
