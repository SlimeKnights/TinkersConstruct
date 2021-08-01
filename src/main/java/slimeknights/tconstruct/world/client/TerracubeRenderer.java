package slimeknights.tconstruct.world.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.MagmaCubeModel;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import slimeknights.tconstruct.TConstruct;

public class TerracubeRenderer extends MobRenderer<SlimeEntity,MagmaCubeModel<SlimeEntity>> {
  public static final Factory TERRACUBE_RENDERER = new Factory(TConstruct.getResource("textures/entity/terracube.png"));

  private final ResourceLocation texture;
  public TerracubeRenderer(EntityRendererManager manager, ResourceLocation texture) {
    super(manager, new MagmaCubeModel<>(), 0.25F);
    this.texture = texture;
  }

  @Override
  protected int getBlockLight(SlimeEntity entityIn, BlockPos pos) {
    return 15;
  }

  /**
   * Returns the location of an entity's texture.
   */
  @Override
  public ResourceLocation getEntityTexture(SlimeEntity entity) {
    return texture;
  }

  @Override
  protected void preRenderCallback(SlimeEntity slime, MatrixStack matrices, float partialTickTime) {
    int size = slime.getSlimeSize();
    float squishFactor = MathHelper.lerp(partialTickTime, slime.prevSquishFactor, slime.squishFactor) / ((float)size * 0.5F + 1.0F);
    float invertedSquish = 1.0F / (squishFactor + 1.0F);
    matrices.scale(invertedSquish * (float)size, 1.0F / invertedSquish * (float)size, invertedSquish * (float)size);
  }

  @RequiredArgsConstructor
  public static class Factory implements IRenderFactory<SlimeEntity> {
    private final ResourceLocation texture;

    @Override
    public EntityRenderer<? super SlimeEntity> createRenderFor(EntityRendererManager manager) {
      return new TerracubeRenderer(manager, texture);
    }
  }
}
