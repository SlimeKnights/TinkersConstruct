package slimeknights.tconstruct.library.client.events;

import lombok.Getter;
import net.minecraftforge.eventbus.api.Event;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialSpriteProvider;
import slimeknights.tconstruct.library.client.data.material.AbstractPartSpriteProvider;

import java.util.ArrayList;
import java.util.List;

/** Event fired when tool textures are being generated to allow adding sprites and materials. Fired on {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS} */
public class GeneratePartTexturesEvent extends Event {
  /** List of materials that can generate sprites */
  @Getter
  private final List<AbstractMaterialSpriteProvider> materialSprites = new ArrayList<>();
  /** List of parts that can generate */
  @Getter
  private final List<AbstractPartSpriteProvider> partSprites = new ArrayList<>();

  /** Adds the given provider to the event */
  public void addMaterialSprites(AbstractMaterialSpriteProvider provider) {
    materialSprites.add(provider);
  }

  /** Adds the given provider to the event */
  public void addPartSprites(AbstractPartSpriteProvider provider) {
    partSprites.add(provider);
  }
}
