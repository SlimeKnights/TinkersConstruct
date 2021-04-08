package slimeknights.tconstruct.world.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.SlimeEntityRenderer;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import slimeknights.tconstruct.library.Util;

@Environment(EnvType.CLIENT)
public class TinkerSlimeRenderer extends SlimeEntityRenderer {
  public static final Factory BLUE_SLIME_FACTORY = new Factory(Util.getResource("textures/entity/blue_slime.png"));

  private final Identifier texture;
  public TinkerSlimeRenderer(EntityRenderDispatcher renderManagerIn, Identifier texture) {
    super(renderManagerIn);
    this.texture = texture;
  }

  @Override
  public Identifier getTexture(SlimeEntity entity) {
    return texture;
  }

  private static class Factory implements IRenderFactory<SlimeEntity> {
    private final Identifier texture;
    public Factory(Identifier texture) {
      this.texture = texture;
    }

    @Override
    public EntityRenderer<? super SlimeEntity> createRenderFor(EntityRenderDispatcher manager) {
      return new TinkerSlimeRenderer(manager, this.texture);
    }
  }
}
