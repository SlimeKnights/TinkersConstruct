package slimeknights.tconstruct.common.data;

import com.google.common.hash.Hashing;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerUpper;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Map.Entry;

/**
 * Data provider to update structures to a newer data fixer upper version
 * Based on https://github.com/BluSunrize/ImmersiveEngineering/blob/1.19.2/src/datagen/java/blusunrize/immersiveengineering/data/StructureUpdater.java
 */
public class StructureUpdater implements DataProvider {
  private final String basePath;
  private final String modId;
  private final DataGenerator gen;
  private final MultiPackResourceManager resources;

  public StructureUpdater(DataGenerator gen, ExistingFileHelper helper, String modId, PackType packType, String basePath) {
    this.gen = gen;
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
  public void run(CachedOutput cache) throws IOException {
    for(Entry<ResourceLocation,Resource> entry : resources.listResources(basePath, file -> file.getNamespace().equals(modId) && file.getPath().endsWith(".nbt")).entrySet()) {
      process(entry.getKey(), entry.getValue(), cache);
    }
  }

  private void process(ResourceLocation location, Resource resource, CachedOutput cache) throws IOException {
    CompoundTag inputNBT = NbtIo.readCompressed(resource.open());
    CompoundTag converted = updateNBT(inputNBT);
    if (!converted.equals(inputNBT)) {
      Class<? extends DataFixer> fixerClass = DataFixers.getDataFixer().getClass();
      if (!fixerClass.equals(DataFixerUpper.class)) {
        throw new RuntimeException("Structures are not up to date, but unknown data fixer is in use: " + fixerClass.getName());
      }
      writeNBTTo(location, converted, cache);
    }
  }

  private void writeNBTTo(ResourceLocation loc, CompoundTag data, CachedOutput cache) throws IOException {
    ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
    NbtIo.writeCompressed(data, bytearrayoutputstream);
    byte[] bytes = bytearrayoutputstream.toByteArray();
    Path outputPath = gen.getOutputFolder().resolve("data/"+loc.getNamespace()+"/"+loc.getPath());
    cache.writeIfNeeded(outputPath, bytes, Hashing.sha1().hashBytes(bytes));
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
