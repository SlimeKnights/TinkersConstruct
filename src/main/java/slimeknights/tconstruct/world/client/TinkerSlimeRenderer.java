package slimeknights.tconstruct.world.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Slime;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.world.entity.ArmoredSlimeEntity;

public class TinkerSlimeRenderer extends SlimeRenderer {
  public static final Factory SKY_SLIME_FACTORY = new Factory(TConstruct.getResource("textures/entity/sky_slime.png"), TConstruct.getResource("textures/entity/steel_slime.png"));
  public static final Factory ENDER_SLIME_FACTORY = new Factory(TConstruct.getResource("textures/entity/ender_slime.png"), TConstruct.getResource("textures/entity/knightmetal_slime.png"));

  private final ResourceLocation slime, metal;
  public TinkerSlimeRenderer(EntityRendererProvider.Context context, ResourceLocation slime, ResourceLocation metal) {
    super(context);
    this.slime = slime;
    this.metal = metal;
    addLayer(new SlimeArmorLayer<>(this, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelSet(), false));
  }

  @Override
  public ResourceLocation getTextureLocation(Slime entity) {
    if (slime != metal && ((ArmoredSlimeEntity) entity).isMetal()) {
      return metal;
    }
    return slime;
  }

  private record Factory(ResourceLocation slime, ResourceLocation metal) implements EntityRendererProvider<Slime> {
    public Factory(ResourceLocation texture) {
      this(texture, texture);
    }

    @Override
    public EntityRenderer<Slime> create(Context context) {
      return new TinkerSlimeRenderer(context, slime, metal);
    }
  }
}
