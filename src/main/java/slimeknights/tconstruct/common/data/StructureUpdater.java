package slimeknights.tconstruct.common.data;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerUpper;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.data.GenericNBTProvider;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map.Entry;

/**
 * Data provider to update structures to a newer data fixer upper version
 * Based on https://github.com/BluSunrize/ImmersiveEngineering/blob/1.19.2/src/datagen/java/blusunrize/immersiveengineering/data/StructureUpdater.java
 */
public class StructureUpdater extends GenericNBTProvider {
  private final String basePath;
  private final String modId;
  private final MultiPackResourceManager resources;

  public StructureUpdater(DataGenerator generator, ExistingFileHelper helper, String modId, PackType packType, String basePath) {
    super(generator, packType, basePath);
    this.modId = modId;
    this.basePath = basePath;
    try {
      Field resourceManager = ExistingFileHelper.class.getDeclaredField(packType == PackType.SERVER_DATA ? "serverData" : "clientResources");
      resourceManager.setAccessible(true);
      resources = (MultiPackResourceManager)resourceManager.get(helper);
    } catch (NoSuchFieldException|IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void run(CachedOutput cache) {
    for(Entry<ResourceLocation,Resource> entry : resources.listResources(basePath, file -> file.getNamespace().equals(modId) && file.getPath().endsWith(".nbt")).entrySet()) {
      process(localize(entry.getKey()), entry.getValue(), cache);
    }
  }

  /** Updates the given structure */
  private void process(ResourceLocation location, Resource resource, CachedOutput cache) {
    try {
      CompoundTag inputNBT = NbtIo.readCompressed(resource.open());
      CompoundTag converted = updateNBT(inputNBT);
      if (!converted.equals(inputNBT)) {
        Class<? extends DataFixer> fixerClass = DataFixers.getDataFixer().getClass();
        if (!fixerClass.equals(DataFixerUpper.class)) {
          throw new RuntimeException("Structures are not up to date, but unknown data fixer is in use: " + fixerClass.getName());
        }
        saveNBT(cache, location, converted);
      }
    } catch (IOException e) {
      TConstruct.LOG.error("Couldn't read NBT for {}", location, e);
    }
  }

  private static CompoundTag updateNBT(CompoundTag nbt) {
    final CompoundTag updatedNBT = NbtUtils.update(DataFixers.getDataFixer(), DataFixTypes.STRUCTURE, nbt, nbt.getInt("DataVersion"));
    StructureTemplate template = new StructureTemplate();
    template.load(updatedNBT);
    return template.save(new CompoundTag());
  }

  @Override
  public String getName() {
    return "Update structure files in " + basePath;
  }
}
