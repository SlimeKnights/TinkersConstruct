package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import slimeknights.mantle.client.model.fluid.FluidCuboid;
import slimeknights.mantle.client.model.fluid.FluidCuboid.FluidFace;
import slimeknights.mantle.client.render.FluidRenderer;
import slimeknights.mantle.client.render.MantleRenderTypes;
import slimeknights.tconstruct.tools.modifiers.ability.fluid.SpittingModifier.FluidSpitEntity;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class FluidSpitRenderer extends EntityRenderer<FluidSpitEntity> {
  // TODO: make public in mantle
  private static final Map<Direction,FluidFace> FACES;
  static {
    FACES = new EnumMap<>(Direction.class);
    for (Direction direction : Direction.values()) {
      FACES.put(direction, FluidFace.NORMAL);
    }
  }

  private final List<FluidCuboid> fluids;
  public FluidSpitRenderer(Context context) {
    super(context);
    this.fluids = List.of(
      new FluidCuboid(new Vector3f(-4,  0,  0), new Vector3f(-2,  2,  2), FACES),
      new FluidCuboid(new Vector3f( 0, -4,  0), new Vector3f( 2, -2,  2), FACES),
      new FluidCuboid(new Vector3f( 0,  0, -4), new Vector3f( 2,  2, -2), FACES),
      new FluidCuboid(new Vector3f( 2,  0,  0), new Vector3f( 4,  2,  2), FACES),
      new FluidCuboid(new Vector3f( 0,  0,  0), new Vector3f( 2,  4,  2), FACES),
      new FluidCuboid(new Vector3f( 0,  0,  2), new Vector3f( 2,  2,  4), FACES));
  }

  @Override
  public void render(FluidSpitEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
    pMatrixStack.pushPose();
    pMatrixStack.translate(0.0D, 0.15F, 0.0D);
    pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
    pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot())));
    FluidRenderer.renderCuboids(pMatrixStack, pBuffer.getBuffer(MantleRenderTypes.FLUID), fluids, pEntity.getFluid(), pPackedLight);
    pMatrixStack.popPose();
    super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
  }

  @Override
  public ResourceLocation getTextureLocation(FluidSpitEntity pEntity) {
    return InventoryMenu.BLOCK_ATLAS;
  }
}
