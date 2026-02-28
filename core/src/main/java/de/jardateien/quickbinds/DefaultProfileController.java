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

  @Override
  public void loadProfile(UUID id) {
    Path profilePath = this.profilesDirectory().resolve(id.toString());
    if (Files.exists(profilePath)) {
      System.out.println("1");
      Path optionPath = profilePath.resolve("options.txt");
      if (Files.exists(optionPath)) {
        System.out.println("2");
        QuickBindsAddon.referenceStorage().settingController().save(optionPath);
      }
    }
  }

  @Override
  public void saveCurrentProfile(String name) {
    Profile profile = new Profile(name, UUID.randomUUID());
    Path directory = this.directory(profile.id());
    try {
      Files.createDirectories(directory);
      Files.copy(this.minecraftOptions, directory.resolve("options.txt"), StandardCopyOption.REPLACE_EXISTING);
      Path resolve = directory.resolve("info.json");
      resolve.toFile().createNewFile();
      Files.write(resolve, this.gson.toJson(profile).getBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Path profilesDirectory() {
    return Paths.get("labymod-neo/configs/quickbinds/profiles");
  }

  private Path directory(UUID id) {
    return this.profilesDirectory().resolve(id.toString());
  }

  @Override
  public void deleteProfile(UUID id) {

  }

  @Override
  public List<Profile> profiles() {
    if(this.profiles.isEmpty()) {

      try(DirectoryStream<Path> stream = Files.newDirectoryStream(this.profilesDirectory())) {
        for (Path path : stream) {
          if(Files.isDirectory(path)) {
            Path resolve = path.resolve("info.json");
            if(Files.exists(resolve)) {
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
}
