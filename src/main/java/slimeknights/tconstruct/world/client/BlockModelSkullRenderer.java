package slimeknights.tconstruct.world.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.joml.Quaternionf;
import slimeknights.tconstruct.smeltery.client.util.TintedVertexBuilder;

/**
 * Skull model instance for the sake of making a Slimeskull with a block item
 * Requires {@link net.minecraft.world.inventory.InventoryMenu#BLOCK_ATLAS} as the texture for the skull.
 **/
@RequiredArgsConstructor
public class BlockModelSkullRenderer extends SkullModelBase {
  private final ItemRenderer itemRenderer;
  private final BakedModel model;
  private final ItemStack stack;
  private float yRot = 0;
  private float xRot = 0;

  public BlockModelSkullRenderer(ItemRenderer itemRenderer, ItemStack stack) {
    this(itemRenderer, itemRenderer.getModel(stack, null, null, 0), stack);
  }

  @Override
  public void setupAnim(float pMouthAnimation, float yRot, float xRot) {
    this.yRot = yRot * ((float)Math.PI / 180F);
    this.xRot = xRot * ((float)Math.PI / 180F);
  }

  @Override
  public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
    poseStack.pushPose();

    // from CustomHeadLayer#translateToHead, with final scale adjusted
    poseStack.translate(0.0F, -0.25F, 0.0F);
    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
    poseStack.scale(0.5F, -0.5F, -0.5F);

    // simplified from ItemRender#render
    BakedModel model = ForgeHooksClient.handleCameraTransforms(poseStack, this.model, ItemDisplayContext.HEAD, false);
    poseStack.translate(-0.5F, -0.5F, -0.5F);
    // we don't really use rotation, but just in case
    if (yRot != 0 || xRot != 0) {
      poseStack.mulPose((new Quaternionf()).rotationZYX(0, yRot, xRot));
    }
    // applying tint is a pain with these, sop hope we don't need it
    if (red != 1 || green != 1 || blue != 1 || alpha != 1) {
      buffer = new TintedVertexBuilder(buffer, (int) (red * 255), (int) (green * 255), (int) (blue * 255), (int) (alpha * 255));
    }
    itemRenderer.renderModelLists(model, stack, light, overlay, poseStack, buffer);

    poseStack.popPose();
  }
}
