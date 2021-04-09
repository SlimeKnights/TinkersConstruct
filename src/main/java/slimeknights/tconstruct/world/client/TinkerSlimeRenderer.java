package slimeknights.tconstruct.world.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.SlimeEntityRenderer;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TinkerSlimeRenderer extends SlimeEntityRenderer {

  private final Identifier texture;
  public TinkerSlimeRenderer(EntityRenderDispatcher renderManagerIn, Identifier texture) {
    super(renderManagerIn);
    this.texture = texture;
  }

  @Override
  public Identifier getTexture(SlimeEntity entity) {
    return texture;
  }
}
