package slimeknights.tconstruct.tables.client;

import net.minecraft.inventory.container.PlayerContainer;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.resource.VanillaResourceType;
import slimeknights.tconstruct.library.client.util.ResourceValidator;

/**
 * Stitches all GUI part textures into the texture sheet
 */
public class PatternGuiTextureLoader extends ResourceValidator {
  /** Singleton instance */
  public static final PatternGuiTextureLoader INSTANCE = new PatternGuiTextureLoader();
  private PatternGuiTextureLoader() {
    super(VanillaResourceType.TEXTURES, "textures/gui/tinker_pattern", "textures", ".png");
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onTextureStitch);
  }

  /**
   * Called during texture stitch to add the textures in
   * @param event
   */
  private void onTextureStitch(TextureStitchEvent.Pre event) {
    if (PlayerContainer.LOCATION_BLOCKS_TEXTURE.equals(event.getMap().getTextureLocation())) {
      this.resources.forEach(event::addSprite);
    }
  }
}
