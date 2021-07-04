package slimeknights.tconstruct.world.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import slimeknights.tconstruct.TConstruct;

@OnlyIn(Dist.CLIENT)
public class TinkerSlimeRenderer extends SlimeRenderer {
  public static final Factory SKY_SLIME_FACTORY = new Factory(TConstruct.getResource("textures/entity/sky_slime.png"));
  public static final Factory ENDER_SLIME_FACTORY = new Factory(TConstruct.getResource("textures/entity/ender_slime.png"));

  private final ResourceLocation texture;
  public TinkerSlimeRenderer(EntityRendererManager renderManagerIn, ResourceLocation texture) {
    super(renderManagerIn);
    this.texture = texture;
  }

  @Override
  public ResourceLocation getEntityTexture(SlimeEntity entity) {
    return texture;
  }

  private static class Factory implements IRenderFactory<SlimeEntity> {
    private final ResourceLocation texture;
    public Factory(ResourceLocation texture) {
      this.texture = texture;
    }

    @Override
    public EntityRenderer<? super SlimeEntity> createRenderFor(EntityRendererManager manager) {
      return new TinkerSlimeRenderer(manager, this.texture);
    }
  }
}
