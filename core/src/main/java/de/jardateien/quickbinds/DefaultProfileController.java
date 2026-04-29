package de.jardateien.quickbinds;

import com.google.gson.Gson;
import de.jardateien.quickbinds.api.Profile;
import de.jardateien.quickbinds.api.ProfileController;
import net.labymod.api.Laby;
import net.labymod.api.models.Implements;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Singleton
@Implements(ProfileController.class)
public class DefaultProfileController implements ProfileController {

  private final List<Profile> profiles = new ArrayList<>();
  private final Gson gson = new Gson();
  private final Path minecraftOptions = Paths.get("options.txt");

  @Override
  public void loadProfile(UUID id) {
    Path profilePath = this.getProfilePath(id);
    if (!Files.exists(profilePath))
      return;
    Path optionPath = profilePath.resolve("options.txt");
    if (!Files.exists(optionPath))
      return;
    QuickBindsAddon.referenceStorage().settingController().save(optionPath);
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

      this.profiles.add(profile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteProfile(UUID id) {
    try {
      Files.walk(this.getProfilePath(id))
          .sorted(Comparator.reverseOrder())
          .forEach(path -> {
            try { Files.delete(path); }
            catch (IOException e) { throw new RuntimeException(e); }
          });
      this.profiles.removeIf(profile -> profile.id().equals(id));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void renameProfile(UUID id, String name) {
    Path infoJsonPath = this.getProfilePath(id).resolve("info.json");

    if (!Files.exists(infoJsonPath))
      return;

    try {
      Profile renamed = new Profile(name, id);
      Files.writeString(infoJsonPath, this.gson.toJson(renamed), StandardCharsets.UTF_8);

      for (int i = 0; i < this.profiles.size(); i++) {
        Profile profile = this.profiles.get(i);
        if (profile.id().equals(id)) {
          this.profiles.set(i, renamed);
          break;
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<Profile> profiles() {
    if (this.profiles.isEmpty()) {
      try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.getProfilesPath())) {
        for (Path path : stream) {
          if (!Files.isDirectory(path))
            continue;
          Path infoPath = path.resolve("info.json");
          if (!Files.exists(infoPath))
            continue;
          Profile profile = this.gson.fromJson(Files.newBufferedReader(infoPath), Profile.class);
          if(this.profiles.stream().anyMatch(p -> p.id().equals(profile.id())))
            continue;
          this.profiles.add(profile);
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return this.profiles;
  }

  /*
  @Override
  public List<Profile> profiles() {
    if (this.profiles.isEmpty()) {
      try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.getProfilesPath())) {
        for (Path path : stream) {
          if (Files.isDirectory(path)) {
            Path infoPath = path.resolve("info.json");
            if (Files.exists(infoPath)) {
              Profile profile = this.gson.fromJson(Files.newBufferedReader(infoPath), Profile.class);
              if(this.profiles.stream().noneMatch(p -> p.id().equals(profile.id()))) {
                this.profiles.add(profile);
              }
            }
          }
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return this.profiles;
  }
   */

  private Path getProfilesPath() {
    if (Laby.labyAPI().labyModLoader().isAddonDevelopmentEnvironment()) {
      return Paths.get("labymod-neo/configs/quickbinds/profiles");
    } else {
      return QuickBindsAddon.instance.addonInfo().getDirectoryPath();
    }
  }

  private Path getProfilePath(UUID id) {
    return this.getProfilesPath().resolve(id.toString());
  }

}
