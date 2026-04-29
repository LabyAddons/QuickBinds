package de.jardateien.quickbinds.api;

import net.labymod.api.reference.annotation.Referenceable;
import java.util.List;
import java.util.UUID;

@Referenceable
public interface ProfileController {

  void loadProfile(UUID id);
  void saveCurrentProfile(String name);
  void deleteProfile(UUID id);
  void renameProfile(UUID id, String name);

  List<Profile> profiles();

}
