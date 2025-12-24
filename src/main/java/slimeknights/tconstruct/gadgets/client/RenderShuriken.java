package slimeknights.tconstruct.gadgets.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import slimeknights.tconstruct.gadgets.entity.shuriken.ShurikenEntityBase;
import slimeknights.tconstruct.tools.client.material.ThrownShurikenRenderer;

/** @deprecated use {@link ThrownShurikenRenderer} */
@Deprecated(forRemoval = true)
public class RenderShuriken extends ThrownShurikenRenderer<ShurikenEntityBase> {
  public RenderShuriken(EntityRendererProvider.Context context) {
    super(context);
  }
}
