package slimeknights.tconstruct.smeltery.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.smeltery.SmelteryTank;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;

public class SmelteryRenderer extends TileEntitySpecialRenderer<TileSmeltery> {

  @Override
  public void renderTileEntityAt(@Nonnull TileSmeltery smeltery, double x, double y, double z, float partialTicks, int destroyStage) {
    if(!smeltery.isActive()) {
      return;
    }

    // get liquids
    SmelteryTank tank = smeltery.getTank();

    // safety first!
    if(tank == null || smeltery.minPos == null || smeltery.maxPos == null) {
      return;
    }

    List<FluidStack> fluids = tank.getFluids();

    // calculate x/z parameters. they'll be the same for all liquids
    double x1 = smeltery.minPos.getX() - smeltery.getPos().getX();
    double y1 = smeltery.minPos.getY() - smeltery.getPos().getY();
    double z1 = smeltery.minPos.getZ() - smeltery.getPos().getZ();

    double x2 = smeltery.maxPos.getX() - smeltery.getPos().getX();
    double z2 = smeltery.maxPos.getZ() - smeltery.getPos().getZ();

    // empty smeltery :(
    if(!fluids.isEmpty()) {

      BlockPos minPos = new BlockPos(x1, y1, z1);
      BlockPos maxPos = new BlockPos(x2, y1, z2);

      // calc heights, we use mB capacities and then convert it over to blockheights during rendering
      int yd = 1 + Math.max(0, smeltery.maxPos.getY() - smeltery.minPos.getY());
      // one block height = 1000 mb
      int[] heights = calcLiquidHeights(fluids, tank.getCapacity(), yd * 1000 - (int) (RenderUtil.FLUID_OFFSET * 2000d), 100);

      double curY = RenderUtil.FLUID_OFFSET;
      // rendering time
      for(int i = 0; i < fluids.size(); i++) {
        double h = (double) heights[i] / 1000d;
        // minpos as start instead of smeltery.pos because we want to use the lighting inside the smeltery
        RenderUtil.renderStackedFluidCuboid(fluids.get(i), x, y, z, smeltery.minPos, minPos, maxPos, curY, curY + h);
        curY += h;
      }
    }

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
//    tessellator.draw();
    RenderUtil.post();
  }

  /**
   * calculate the rendering heights for all the liquids
   *
   * @param liquids  The liquids
   * @param capacity Max capacity of smeltery, to calculate how much height one liquid takes up
   * @param height   Maximum height, basically represents how much height full capacity is
   * @param min      Minimum amount of height for a fluid. A fluid can never have less than this value height returned
   * @return Array with heights corresponding to input-list liquids
   */
  public static int[] calcLiquidHeights(List<FluidStack> liquids, int capacity, int height, int min) {
    int fluidHeights[] = new int[liquids.size()];

    if(liquids.size() > 0) {

      for(int i = 0; i < liquids.size(); i++) {
        FluidStack liquid = liquids.get(i);

        float h = (float) liquid.amount / (float) capacity;
        fluidHeights[i] = Math.max(min, (int) Math.ceil(h * (float) height));
      }

      // check if we have enough height to render everything
      int sum = 0;
      do {
        sum = 0;
        int biggest = -1;
        int m = 0;
        for(int i = 0; i < fluidHeights.length; i++) {
          sum += fluidHeights[i];
          if(liquids.get(i).amount > biggest) {
            biggest = liquids.get(i).amount;
            m = i;
          }
        }

        // we can't get a result without going negative
        if(fluidHeights[m] == 0) {
          break;
        }

        // remove a pixel from the biggest one
        if(sum > height) {
          fluidHeights[m]--;
        }
      } while(sum > height);
    }

    return fluidHeights;
  }
}
