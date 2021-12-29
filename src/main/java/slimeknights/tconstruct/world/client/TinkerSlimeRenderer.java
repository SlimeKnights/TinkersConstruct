package slimeknights.tconstruct.world.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Slime;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.tconstruct.TConstruct;

@OnlyIn(Dist.CLIENT)
public class TinkerSlimeRenderer extends SlimeRenderer {
  public static final Factory SKY_SLIME_FACTORY = new Factory(TConstruct.getResource("textures/entity/sky_slime.png"));
  public static final Factory ENDER_SLIME_FACTORY = new Factory(TConstruct.getResource("textures/entity/ender_slime.png"));

  private final ResourceLocation texture;
  public TinkerSlimeRenderer(EntityRendererProvider.Context context, ResourceLocation texture) {
    super(context);
    this.texture = texture;
  }

  @Override
  public ResourceLocation getTextureLocation(Slime entity) {
    return texture;
  }

  private static class Factory implements EntityRendererProvider<Slime> {
    private final ResourceLocation texture;
    public Factory(ResourceLocation texture) {
      this.texture = texture;
    }

    @Override
    public EntityRenderer<Slime> create(Context context) {
      return new TinkerSlimeRenderer(context, this.texture);
    }
  }
}
