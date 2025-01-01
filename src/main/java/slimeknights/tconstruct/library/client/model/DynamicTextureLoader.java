package slimeknights.tconstruct.library.client.model;

import lombok.extern.log4j.Log4j2;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import slimeknights.mantle.data.listener.ResourceValidator;
import slimeknights.tconstruct.common.config.Config;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Logic to handle dynamic texture scans. Really just logging missing textures at this point.
 */
@Log4j2
public class DynamicTextureLoader extends ResourceValidator {
  /** Instance to register with the loader */
  private static final DynamicTextureLoader INSTANCE = new DynamicTextureLoader();

  private DynamicTextureLoader() {
    super("textures/item", "textures", ".png");
  }

  @Override
  public void onReloadSafe(ResourceManager manager) {
    // if we are logging missing textures we can use the vanilla validator instead of needing our own
    if (!Config.CLIENT.logMissingModifierTextures.get()) {
      super.onReloadSafe(manager);
    }
  }

  @Override
  public CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
    return super.reload(stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor).thenRunAsync(this::clear);
  }

  /** Registers this manager */
  public static void init(RegisterClientReloadListenersEvent event) {
    event.registerReloadListener(INSTANCE);
  }

  /**
   * Gets a consumer to add textures to the given collection
   *
   * @param spriteGetter        Function mapping material names to sprites
   * @param logMissingTextures  If true, log textures that were not found
   * @return  Texture consumer
   */
  public static Predicate<Material> getTextureValidator(Function<Material,TextureAtlasSprite> spriteGetter, boolean logMissingTextures) {
    if (logMissingTextures || INSTANCE.resources.isEmpty()) {
      // this logs due to the vanilla sprite getter logging
      return mat -> !MissingTextureAtlasSprite.getLocation().equals(spriteGetter.apply(mat).contents().name());
    } else {
      return mat -> {
        // to suppress logging, need to load from our own list. We just load it for `textures/item` on the block atlas
        if (InventoryMenu.BLOCK_ATLAS.equals(mat.atlasLocation())) {
          ResourceLocation texture = mat.texture();
          if (texture.getPath().startsWith("item/")) {
            return INSTANCE.test(mat.texture());
          }
        }
        // failed preconditions? can't stop logging even if the boolean says to
        return !MissingTextureAtlasSprite.getLocation().equals(spriteGetter.apply(mat).contents().name());
      };
    }
  }
}
