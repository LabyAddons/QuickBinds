package de.jardateien.quickbinds.api;

import net.labymod.api.reference.annotation.Referenceable;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Referenceable
public interface ProfileController {

  void loadProfile(UUID id);
  void saveCurrentProfile(String name);
  void deleteProfile(UUID id);
  void updateProfile(Profile id);

  ProfileOptions loadOptions(UUID id) throws IOException;
  void saveOptions(UUID id, ProfileOptions options) throws IOException;

  List<Profile> profiles();

}
