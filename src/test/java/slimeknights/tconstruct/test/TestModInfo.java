package slimeknights.tconstruct.test;

import cpw.mods.jarhandling.SecureJar;
import cpw.mods.jarhandling.SecureJar.Status;
import lombok.Getter;
import net.minecraftforge.forgespi.language.IConfigurable;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.IModLanguageProvider;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.locating.ForgeFeature.Bound;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModProvider;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Mod info for running tests, a prime example of putting too many things in an interface */
public enum TestModInfo implements IModInfo {
  INSTANCE;

  @Getter
  private final ArtifactVersion version = new DefaultArtifactVersion(ModFileInfo.INSTANCE.versionString());

  @Override
  public IModFileInfo getOwningFile() {
    return ModFileInfo.INSTANCE;
  }

  @Override
  public String getModId() {
    return "test";
  }

  @Override
  public String getDisplayName() {
    return "Test";
  }

  @Override
  public String getDescription() {
    return "Test";
  }

  @Override
  public List<? extends ModVersion> getDependencies() {
    return List.of();
  }

  @Override
  public List<? extends Bound> getForgeFeatures() {
    return List.of();
  }

  @Override
  public String getNamespace() {
    return "test";
  }

  @Override
  public Map<String,Object> getModProperties() {
    return Map.of();
  }

  @Override
  public Optional<URL> getUpdateURL() {
    return Optional.empty();
  }

  @Override
  public Optional<URL> getModURL() {
    return Optional.empty();
  }

  @Override
  public Optional<String> getLogoFile() {
    return Optional.empty();
  }

  @Override
  public boolean getLogoBlur() {
    return false;
  }

  @Override
  public IConfigurable getConfig() {
    return Config.INSTANCE;
  }

  private enum Config implements IConfigurable {
    INSTANCE;

    @Override
    public <T> Optional<T> getConfigElement(String... key) {
      return Optional.empty();
    }

    @Override
    public List<? extends IConfigurable> getConfigList(String... key) {
      return List.of();
    }
  }

  private enum ModFileInfo implements IModFileInfo {
    INSTANCE;

    @Override
    public List<IModInfo> getMods() {
      return List.of();
    }

    @Override
    public List<LanguageSpec> requiredLanguageLoaders() {
      return List.of();
    }

    @Override
    public boolean showAsResourcePack() {
      return false;
    }

    @Override
    public Map<String,Object> getFileProperties() {
      return Map.of();
    }

    @Override
    public String getLicense() {
      return "MIT";
    }

    @Override
    public String moduleName() {
      return "test";
    }

    @Override
    public String versionString() {
      return "0.0.0";
    }

    @Override
    public List<String> usesServices() {
      return List.of();
    }

    @Override
    public IModFile getFile() {
      return ModFile.INSTANCE;
    }

    @Override
    public IConfigurable getConfig() {
      return Config.INSTANCE;
    }
  }

  private enum ModFile implements IModFile {
    INSTANCE;

    @Override
    public List<IModLanguageProvider> getLoaders() {
      return List.of();
    }

    @Override
    public Path findResource(String... pathName) {
      return Path.of(".");
    }

    @Override
    public Supplier<Map<String,Object>> getSubstitutionMap() {
      return Map::of;
    }

    @Override
    public Type getType() {
      return Type.MOD;
    }

    @Override
    public Path getFilePath() {
      return Path.of(".");
    }

    @Override
    public SecureJar getSecureJar() {
      // yeah, I gave up here, if we ever got this far in my fake mod info I'd be surprised
      return null;
    }

    @Override
    public void setSecurityStatus(Status status) {}

    @Override
    public List<IModInfo> getModInfos() {
      return List.of();
    }

    @Override
    public ModFileScanData getScanResult() {
      return new ModFileScanData();
    }

    @Override
    public String getFileName() {
      return "test";
    }

    @Override
    public IModProvider getProvider() {
      return ModProvider.INSTANCE;
    }

    @Override
    public IModFileInfo getModFileInfo() {
      return ModFileInfo.INSTANCE;
    }
  }

  private enum ModProvider implements IModProvider {
    INSTANCE;

    @Override
    public void scanFile(IModFile modFile, Consumer<Path> pathConsumer) {}

    @Override
    public void initArguments(Map<String,?> arguments) {}

    @Override
    public boolean isValid(IModFile modFile) {
      return false;
    }
  }
}
