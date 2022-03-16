package slimeknights.tconstruct.tables.client;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.data.ResourceValidator;

/**
 * Stitches all GUI part textures into the texture sheet
 */
public class PatternGuiTextureLoader extends ResourceValidator {
  /** Singleton instance */
  public static final PatternGuiTextureLoader INSTANCE = new PatternGuiTextureLoader();
  private PatternGuiTextureLoader() {
    super("textures/gui/tinker_pattern", "textures", ".png");
  }

  public static void init() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(INSTANCE::onTextureStitch);
  }

  /**
   * Called during texture stitch to add the textures in
   */
  private void onTextureStitch(TextureStitchEvent.Pre event) {
    if (PlayerContainer.LOCATION_BLOCKS_TEXTURE.equals(event.getMap().getTextureLocation())) {
      this.onReloadSafe(Minecraft.getInstance().getResourceManager());
      this.resources.forEach(event::addSprite);
      this.clear();
    }
  }
}
