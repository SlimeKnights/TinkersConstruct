package slimeknights.tconstruct.smeltery.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.ForgeHooksClient;
import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;

public class SmelteryRenderer extends SmelteryTankRenderer<TileSmeltery> {

  @Override
  public void renderTileEntityAt(@Nonnull TileSmeltery smeltery, double x, double y, double z, float partialTicks, int destroyStage) {
    if(!smeltery.isActive()) {
      return;
    }

    // get liquids

    // safety first!
    if(smeltery.minPos == null || smeltery.maxPos == null) {
      return;
    }

    renderFluids(smeltery.getTank(), smeltery.getPos(), smeltery.minPos, smeltery.maxPos, x, y, z);

    // calculate x/z parameters
    double x1 = smeltery.minPos.getX() - smeltery.getPos().getX();
    double y1 = smeltery.minPos.getY() - smeltery.getPos().getY();
    double z1 = smeltery.minPos.getZ() - smeltery.getPos().getZ();

    // render items
    int xd = 1 + smeltery.maxPos.getX() - smeltery.minPos.getX();
    int zd = 1 + smeltery.maxPos.getZ() - smeltery.minPos.getZ();
    int layer = xd * zd;
    //Tessellator tessellator = Tessellator.getInstance();
    //WorldRenderer renderer = tessellator.getWorldRenderer();
    //renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
    Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    RenderUtil.pre(x, y, z);
    GlStateManager.disableCull();
    GlStateManager.translate(x1, y1, z1);
    GlStateManager.translate(0.5f, 0.5f, 0.5f);

    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    RenderHelper.enableStandardItemLighting();

    for(int i = 0; i < smeltery.getSizeInventory(); i++) {
      if(smeltery.isStackInSlot(i)) {
        // calculate position inside the smeltery from slot index
        int h = i / layer;
        int i2 = i % layer;
        BlockPos pos = smeltery.minPos.add(i2 % xd, h, i2 / xd);

        int brightness = smeltery.getWorld().getCombinedLight(pos, 0);

        ItemStack stack = smeltery.getStackInSlot(i);
        boolean isItem = !(stack.getItem() instanceof ItemBlock);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) (brightness % 0x10000) / 1f,
                                              (float) (brightness / 0x10000) / 1f);

        //GlStateManager.pushMatrix();
        GlStateManager.translate(i2 % xd, h, i2 / xd);
        if(isItem) {
          GlStateManager.rotate(-90, 1, 0, 0);
        }
        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, smeltery.getWorld(), null);
        model = ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.NONE, false);
        //Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(smeltery.getWorld(), model, Blocks.bedrock.getDefaultState(), pos, renderer, false);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, model);
        if(isItem) {
          GlStateManager.rotate(90, 1, 0, 0);
        }
        GlStateManager.translate(-i2 % xd, -h, -i2 / xd);
        //GlStateManager.popMatrix();
      }
    }
    RenderHelper.enableStandardItemLighting();
    GlStateManager.enableCull();
    //tessellator.draw();
    RenderUtil.post();
  }
}
