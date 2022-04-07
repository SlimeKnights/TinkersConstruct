package slimeknights.tconstruct.common.data;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerUpper;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Data provider to update structures to a newer data fixer upper version
 * Based on https://github.com/BluSunrize/ImmersiveEngineering/blob/1.18.1/src/datagen/java/blusunrize/immersiveengineering/data/StructureUpdater.java
 */
public class StructureUpdater implements DataProvider {

  private final PackType packType;
  private final String basePath;
  private final String modid;
  private final DataGenerator gen;
  private final MultiPackResourceManager resources;

  public StructureUpdater(DataGenerator gen, ExistingFileHelper helper, String modid, PackType packType, String basePath) {
    this.gen = gen;
    this.modid = modid;
    this.packType = packType;
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
  public void run(HashCache cache) throws IOException {
    for(ResourceLocation loc : resources.listResources(basePath, file -> file.endsWith(".nbt"))) {
      if (loc.getNamespace().equals(modid)) {
        process(loc, cache);
      }
    }
  }

  private void process(ResourceLocation loc, HashCache cache) throws IOException {
    CompoundTag inputNBT = NbtIo.readCompressed(resources.getResource(loc).getInputStream());
    CompoundTag converted = updateNBT(inputNBT);
    if (!converted.equals(inputNBT)) {
      Class<? extends DataFixer> fixerClass = DataFixers.getDataFixer().getClass();
      if (!fixerClass.equals(DataFixerUpper.class)) {
        throw new RuntimeException("Structures are not up to date, but unknown data fixer is in use: " + fixerClass.getName());
      }
      writeNBTTo(loc, converted, cache);
    }
  }

  private void writeNBTTo(ResourceLocation loc, CompoundTag data, HashCache cache) throws IOException {
    ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
    NbtIo.writeCompressed(data, bytearrayoutputstream);
    byte[] bytes = bytearrayoutputstream.toByteArray();
    String hashString = SHA1.hashBytes(bytes).toString();
    Path outputPath = gen.getOutputFolder().resolve(packType.getDirectory() + "/" + loc.getNamespace() + "/" + loc.getPath());

    if(!Objects.equals(cache.getHash(outputPath), hashString) || !Files.exists(outputPath)) {
      Files.createDirectories(outputPath.getParent());
      try(OutputStream outputstream = Files.newOutputStream(outputPath)) {
        outputstream.write(bytes);
      }
    }
    cache.putNew(outputPath, hashString);
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
