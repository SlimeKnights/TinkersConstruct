package slimeknights.tconstruct.common.data;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerUpper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.data.GenericNBTProvider;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Data provider to update structures to a newer data fixer upper version
 * Based on https://github.com/BluSunrize/ImmersiveEngineering/blob/1.19.2/src/datagen/java/blusunrize/immersiveengineering/data/StructureUpdater.java
 */
@SuppressWarnings("unused")  // we use it once each update then disable as its task need not be repeated
public class StructureUpdater extends GenericNBTProvider {
  private final String basePath;
  private final String modId;
  private final MultiPackResourceManager resources;

  public StructureUpdater(PackOutput output, ExistingFileHelper helper, String modId, Target packType, String basePath) {
    super(output, packType, basePath);
    this.modId = modId;
    this.basePath = basePath;
    try {
      Field resourceManager = ExistingFileHelper.class.getDeclaredField(packType == Target.DATA_PACK ? "serverData" : "clientResources");
      resourceManager.setAccessible(true);
      resources = (MultiPackResourceManager)resourceManager.get(helper);
    } catch (NoSuchFieldException|IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    return GenericDataProvider.allOf(
      resources.listResources(basePath, file -> file.getNamespace().equals(modId) && file.getPath().endsWith(".nbt"))
               .entrySet().stream()
               .<CompletableFuture<?>>map(entry -> process(localize(entry.getKey()), entry.getValue(), cache))
               .filter(Objects::nonNull));
  }

  /** Updates the given structure */
  @Nullable
  private CompletableFuture<?> process(ResourceLocation location, Resource resource, CachedOutput cache) {
    try {
      CompoundTag inputNBT = NbtIo.readCompressed(resource.open());
      CompoundTag converted = updateNBT(inputNBT);
      if (!converted.equals(inputNBT)) {
        Class<? extends DataFixer> fixerClass = DataFixers.getDataFixer().getClass();
        if (!fixerClass.equals(DataFixerUpper.class)) {
          throw new RuntimeException("Structures are not up to date, but unknown data fixer is in use: " + fixerClass.getName());
        }
        return saveNBT(cache, location, converted);
      }
      return null;
    } catch (IOException e) {
      TConstruct.LOG.error("Couldn't read NBT for {}", location, e);
      return CompletableFuture.failedFuture(e);
    }
  }

  @SuppressWarnings("deprecation")  // I'd like to see Forge try this
  private static CompoundTag updateNBT(CompoundTag nbt) {
    final CompoundTag updatedNBT = DataFixTypes.STRUCTURE.updateToCurrentVersion(DataFixers.getDataFixer(), nbt, nbt.getInt("DataVersion"));
    StructureTemplate template = new StructureTemplate();
    template.load(BuiltInRegistries.BLOCK.asLookup(), updatedNBT);
    return template.save(new CompoundTag());
  }

  @Override
  public String getName() {
    return "Update structure files in " + basePath;
  }
}
