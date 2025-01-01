package slimeknights.tconstruct.library.client.data;

import com.google.common.hash.Hashing;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/** Data generator to create png image files */
public abstract class GenericTextureGenerator extends GenericDataProvider {
  @Nullable
  private final ExistingFileHelper existingFileHelper;
  @Nullable
  private final ExistingFileHelper.ResourceType resourceType;

  /** Constructor which marks files as existing */
  public GenericTextureGenerator(PackOutput packOutput, @Nullable ExistingFileHelper existingFileHelper, String folder) {
    super(packOutput, Target.RESOURCE_PACK, folder);
    this.existingFileHelper = existingFileHelper;
    if (existingFileHelper != null) {
      this.resourceType = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".png", folder);
    } else {
      this.resourceType = null;
    }
  }

  /** Constructor which does not mark files as existing */
  public GenericTextureGenerator(PackOutput packOutput, String folder) {
    this(packOutput, null, folder);
  }

  /** Saves the given image to the given location */
  protected CompletableFuture<?> saveImage(CachedOutput cache, ResourceLocation location, NativeImage image) {
    if (existingFileHelper != null && resourceType != null) {
      existingFileHelper.trackGenerated(location, resourceType);
    }
    return CompletableFuture.runAsync(() -> {
      try {
        Path path = this.pathProvider.file(location, "png");
        byte[] bytes = image.asByteArray();
        cache.writeIfNeeded(path, bytes, Hashing.sha1().hashBytes(bytes));
      } catch (IOException e) {
        TConstruct.LOG.error("Couldn't write image for {}", location, e);
        throw new CompletionException(e);
      }
    }, Util.backgroundExecutor());
  }

  /** Saves metadata for the given image */
  protected CompletableFuture<?> saveMetadata(CachedOutput cache, ResourceLocation location, JsonObject metadata) {
    return DataProvider.saveStable(cache, metadata, this.pathProvider.file(location, "png.mcmeta"));
  }
}
