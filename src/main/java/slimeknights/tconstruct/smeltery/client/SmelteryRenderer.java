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
  public void render(@Nonnull TileSmeltery smeltery, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
    if(!smeltery.isActive()) {
      return;
    }

    BlockPos tilePos = smeltery.getPos();
    BlockPos minPos = smeltery.getMinPos();
    BlockPos maxPos = smeltery.getMaxPos();

    // safety first!
    if(minPos == null || maxPos == null) {
      return;
    }

    renderFluids(smeltery.getTank(), tilePos, minPos, maxPos, x, y, z);

    // calculate x/z parameters
    double x1 = minPos.getX() - tilePos.getX();
    double y1 = minPos.getY() - tilePos.getY();
    double z1 = minPos.getZ() - tilePos.getZ();

    // render items
    int xd = 1 + maxPos.getX() - minPos.getX();
    int zd = 1 + maxPos.getZ() - minPos.getZ();
    int layer = xd * zd;
    //Tessellator tessellator = Tessellator.getInstance();
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
        BlockPos pos = minPos.add(i2 % xd, h, i2 / xd);

        int brightness = smeltery.getWorld().getCombinedLight(pos, 0);

        ItemStack stack = smeltery.getStackInSlot(i);
        boolean isItem = !(stack.getItem() instanceof ItemBlock);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightness % 0x10000 / 1f,
                                              brightness / 0x10000 / 1f);

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
