package slimeknights.tconstruct.library.data;

import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/** Data generator to create NBT files */
@RequiredArgsConstructor
public abstract class GenericNBTProvider implements DataProvider {
  protected final PackOutput.PathProvider pathProvider;
  private final String folder;

  public GenericNBTProvider(PackOutput output, Target type, String folder) {
    this(output.createPathProvider(type, folder), folder);
  }

  public GenericNBTProvider(DataGenerator generator, Target type, String folder) {
    this(generator.getPackOutput(), type, folder);
  }

  /** Localizes the given resource to the folder */
  public ResourceLocation localize(ResourceLocation name) {
    return JsonHelper.localize(name, folder, ".nbt");
  }

  /** Saves the given image to the given location */
  protected CompletableFuture<?> saveNBT(CachedOutput cache, ResourceLocation location, CompoundTag data) {
    return CompletableFuture.runAsync(() -> {
      try {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        NbtIo.writeCompressed(data, outputStream);
        byte[] bytes = outputStream.toByteArray();
        Path outputPath = this.pathProvider.file(location, "nbt");
        cache.writeIfNeeded(outputPath, bytes, Hashing.sha1().hashBytes(bytes));
      } catch (IOException e) {
        TConstruct.LOG.error("Couldn't write NBT for {}", location, e);
        throw new CompletionException(e);
      }
    }, Util.backgroundExecutor());
  }
}
