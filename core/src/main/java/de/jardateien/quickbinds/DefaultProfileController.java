package de.jardateien.quickbinds;

import com.google.gson.Gson;
import de.jardateien.quickbinds.api.Profile;
import de.jardateien.quickbinds.api.ProfileController;
import de.jardateien.quickbinds.ui.activity.ConfirmActivity;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
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
  private final String version = Laby.labyAPI().minecraft().getVersion();
  private final int protocol = Laby.labyAPI().minecraft().getProtocolVersion();

  @Override
  public void loadProfile(UUID id) {
    Path profilePath = this.profilePath(id);
    if (!Files.exists(profilePath))
      return;

    Path optionPath = profilePath.resolve("options.txt");
    if (!Files.exists(optionPath))
      return;

    Profile profile = this.profile(id);
    assert profile != null;

    if(profile.protocol() != this.protocol) {
      ConfirmActivity.confirm(Component.translatable("quickbinds.ui.confirm.title"), Component.translatable("quickbinds.ui.confirm.description"), result -> {
        if(result == true) {
          QuickBindsAddon.referenceStorage().settingController().save(optionPath);
        }
      });

      return;
    }

    QuickBindsAddon.referenceStorage().settingController().save(optionPath);
  }

  @Override
  public void saveCurrentProfile(String name) {
    Profile profile = new Profile(name, this.version, this.protocol, UUID.randomUUID());
    Path directory = this.profilePath(profile.id());
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
      Files.walk(this.profilePath(id))
          .sorted(Comparator.reverseOrder())
          .forEach(path -> {
            try {
              Files.delete(path);
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          });
      this.profiles.removeIf(profile -> profile.id().equals(id));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void renameProfile(UUID id, String name) {
    Path infoJsonPath = this.profilePath(id).resolve("info.json");
    if (!Files.exists(infoJsonPath))
      return;

    Profile profile = this.profile(id);
    if(profile == null)
      return;

    profile.setName(name);
    try {
      Files.writeString(infoJsonPath, this.gson.toJson(profile), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<Profile> profiles() {
    if (this.profiles.isEmpty()) {
      try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.profilesPath())) {
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

  private Path profilesPath() {
    Path path;

    if (Laby.labyAPI().labyModLoader().isAddonDevelopmentEnvironment()) {
      path = Paths.get("labymod-neo/configs/quickbinds/profiles");
    } else {
      path = QuickBindsAddon.instance.addonInfo().getDirectoryPath();
    }

    if(!Files.exists(path)) {
      try {
        Files.createDirectories(path);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return path;
  }

  private Path profilePath(UUID id) {
    return this.profilesPath().resolve(id.toString());
  }

  private Profile profile(UUID id) {
    for (Profile profile : this.profiles) {
      if (profile.id().equals(id))
        return profile;
    }
    return null;
  }

}
