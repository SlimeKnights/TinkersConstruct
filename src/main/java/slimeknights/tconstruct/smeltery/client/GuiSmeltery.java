package slimeknights.tconstruct.smeltery.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.mantle.client.gui.GuiMultiModule;
import slimeknights.mantle.inventory.ContainerMultiModule;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.smeltery.SmelteryTank;
import slimeknights.tconstruct.smeltery.client.module.GuiSmelterySideinventory;
import slimeknights.tconstruct.smeltery.inventory.ContainerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;
import slimeknights.tconstruct.tools.inventory.ContainerSideInventory;

public class GuiSmeltery extends GuiMultiModule {

  public static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/smeltery.png");

  protected GuiElement scala = new GuiElement(176, 76, 52, 52, 256, 256);

  protected final GuiSmelterySideinventory sideinventory;
  protected final TileSmeltery smeltery;

  public GuiSmeltery(ContainerSmeltery container, TileSmeltery smeltery) {
    super(container);

    this.smeltery = smeltery;

    sideinventory = new GuiSmelterySideinventory(this, container.getSubContainer(ContainerSideInventory.class),
                                                 smeltery, smeltery.getSizeInventory(), container.calcColumns());
    addModule(sideinventory);
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);

    this.mc.getTextureManager().bindTexture(BACKGROUND);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    scala.draw(8, 16);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

    // draw liquids
    SmelteryTank liquids = smeltery.getTank();
    if(liquids.getUsedCapacity() > 0) {
      int capacity = Math.max(liquids.getUsedCapacity(), liquids.getMaxCapacity());
      int[] heights = calcLiquidHeights(liquids.getFluids(), capacity, scala.h);
      int x = 8 + cornerX;
      int y = 16 + scala.h + cornerY; // y starting position
      int w = scala.w;

      // prepare rendering
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.startDrawingQuads();
      mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);

      for(int i = 0; i < heights.length; i++) {
        int h = heights[i];
        FluidStack liquid = liquids.getFluids().get(i);
        TextureAtlasSprite fluidSprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(liquid.getFluid().getStill().toString());

        RenderUtil.putTiledTextureQuads(worldrenderer, x, y, w, h, this.zLevel, fluidSprite);
      }

      tessellator.draw();
    }

    // todo: draw fuel
  }

  // calculate the rendering heights for all the liquids
  protected int[] calcLiquidHeights (List<FluidStack> liquids, int capacity, int height)
  {
    int fluidHeights[] = new int[liquids.size()];

    for (int i = 0; i < liquids.size(); i++)
    {
      FluidStack liquid = liquids.get(i);

      float h = (float) liquid.amount / (float) capacity;
      fluidHeights[i] = Math.max(3, (int) Math.ceil(h * (float)height));
    }

    // check if we have enough height to render everything
    int sum = 0;
    do
    {
      sum = 0;
      int biggest = -1;
      int m = 0;
      for (int i = 0; i < fluidHeights.length; i++)
      {
        sum += fluidHeights[i];
        if (liquids.get(i).amount > biggest)
        {
          biggest = liquids.get(i).amount;
          m = i;
        }
      }

      // remove a pixel from the biggest one
      if (sum > height)
        fluidHeights[m]--;
    } while (sum > height);

    return fluidHeights;
  }
}
