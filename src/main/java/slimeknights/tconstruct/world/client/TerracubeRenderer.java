package slimeknights.tconstruct.world.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.LavaSlimeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Slime;
import slimeknights.tconstruct.TConstruct;

public class TerracubeRenderer extends MobRenderer<Slime,LavaSlimeModel<Slime>> {
  private static final ResourceLocation TEXTURE = TConstruct.getResource("textures/entity/terracube.png");
  public TerracubeRenderer(EntityRendererProvider.Context context) {
    super(context, new LavaSlimeModel<>(context.bakeLayer(ModelLayers.MAGMA_CUBE)), 0.25F);
    addLayer(new SlimeArmorLayer<>(this, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelSet(), true));
  }

  @Override
  public ResourceLocation getTextureLocation(Slime entity) {
    return TEXTURE;
  }

  @Override
  protected void scale(Slime slime, PoseStack matrices, float partialTickTime) {
    int size = slime.getSize();
    float squishFactor = Mth.lerp(partialTickTime, slime.oSquish, slime.squish) / ((float)size * 0.5F + 1.0F);
    float invertedSquish = 1.0F / (squishFactor + 1.0F);
    matrices.scale(invertedSquish * (float)size, 1.0F / invertedSquish * (float)size, invertedSquish * (float)size);
  }
}
