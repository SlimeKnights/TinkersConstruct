package slimeknights.tconstruct.library.data;

import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Data generator to create NBT files */
@RequiredArgsConstructor
public abstract class GenericNBTProvider implements DataProvider {
  protected final DataGenerator generator;
  protected final PackType packType;
  protected final String folder;

  /** Localizes the given resource to the folder */
  public ResourceLocation localize(ResourceLocation name) {
    return JsonHelper.localize(name, folder, ".nbt");
  }

  /** Saves the given image to the given location */
  @SuppressWarnings("UnstableApiUsage")
  protected void saveNBT(CachedOutput cache, ResourceLocation location, CompoundTag data) {
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      NbtIo.writeCompressed(data, outputStream);
      byte[] bytes = outputStream.toByteArray();
      Path outputPath = generator.getOutputFolder().resolve(Paths.get(packType.getDirectory(), location.getNamespace(), folder, location.getPath() + ".nbt"));
      cache.writeIfNeeded(outputPath, bytes, Hashing.sha1().hashBytes(bytes));
    } catch (IOException e) {
      TConstruct.LOG.error("Couldn't write NBT for {}", location, e);
    }
  }
}
