package de.jardateien.quickbinds;

import com.google.gson.Gson;
import de.jardateien.quickbinds.api.Profile;
import de.jardateien.quickbinds.api.ProfileController;
import net.labymod.api.models.Implements;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Singleton
@Implements(ProfileController.class)
public class DefaultProfileController implements ProfileController {

  private final List<Profile> profiles = new ArrayList<>();
  private final Gson gson = new Gson();
  private final Path minecraftOptions = Paths.get("options.txt");
  private final Path profilesPath = Paths.get("labymod-neo/configs/quickbinds/profiles");

  @Override
  public void loadProfile(UUID id) {
    Path profilePath = this.getProfilePath(id);
    if (Files.exists(profilePath)) {
      Path optionPath = profilePath.resolve("options.txt");
      if (Files.exists(optionPath)) {
        QuickBindsAddon.referenceStorage().settingController().save(optionPath);
      }
    }
  }

  @Override
  public void saveCurrentProfile(String name) {
    Profile profile = new Profile(name, UUID.randomUUID());
    Path directory = this.getProfilePath(profile.id());
    try {
      Files.createDirectories(directory);
      Files.copy(this.minecraftOptions, directory.resolve("options.txt"), StandardCopyOption.REPLACE_EXISTING);
      Path infoJson = directory.resolve("info.json");
      boolean fileCreated = infoJson.toFile().createNewFile();
      if (fileCreated)
        Files.write(infoJson, this.gson.toJson(profile).getBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteProfile(UUID id) {
    try {
      Files.delete(this.getProfilePath(id));
      this.profiles.removeIf(profile -> profile.id().equals(id));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<Profile> profiles() {
    if (this.profiles.isEmpty()) {
      try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.profilesPath)) {
        for (Path path : stream) {
          if (Files.isDirectory(path)) {
            Path resolve = path.resolve("info.json");
            if (Files.exists(resolve)) {
              Profile profile = this.gson.fromJson(Files.newBufferedReader(resolve), Profile.class);
              this.profiles.add(profile);
            }
          }
        }

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return this.profiles;
  }

  private Path getProfilePath(UUID id) {
    return this.profilesPath.resolve(id.toString());
  }

}
