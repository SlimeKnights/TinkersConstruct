package slimeknights.tconstruct.smeltery.client;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.smeltery.SmelteryTank;
import slimeknights.tconstruct.smeltery.network.SmelteryFluidClicked;
import slimeknights.tconstruct.smeltery.tileentity.TileTinkerTank;

public class GuiTinkerTank extends GuiContainer {

  public static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/tinker_tank.png");
  protected GuiElement scala = new GuiElement(122, 0, 106, 106, 256, 256);

  public TileTinkerTank tinkerTank;

  public GuiTinkerTank(Container inventorySlotsIn, TileTinkerTank tinkerTank) {
    super(inventorySlotsIn);

    this.xSize = 122;
    this.ySize = 130;

    this.tinkerTank = tinkerTank;
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    // draw the scale
    this.mc.getTextureManager().bindTexture(BACKGROUND);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    scala.draw(8, 16);

    // draw the tooltips, if any
    // subtract the corner of the GUI location so the mouse location is relative to just the center, rather than the inventory center
    mouseX -= guiLeft;
    mouseY -= guiTop;

    // Liquids
    if(8 <= mouseX && mouseX < 114 && 16 <= mouseY && mouseY < 122) {
      FluidStack hovered = getFluidHovered(122 - mouseY - 1);
      List<String> text = Lists.newArrayList();

      if(hovered == null) {
        int usedCap = tinkerTank.getTank().getFluidAmount();
        int maxCap = tinkerTank.getTank().getCapacity();
        text.add(TextFormatting.WHITE + Util.translate("gui.smeltery.capacity"));
        RenderUtil.liquidToString(null, maxCap, text);
        text.add(Util.translateFormatted("gui.smeltery.capacity_available"));
        RenderUtil.liquidToString(null, maxCap - usedCap, text);
      }
      else {
        text.add(TextFormatting.WHITE + hovered.getLocalizedName());
        RenderUtil.liquidToString(hovered, text);
      }

      this.drawHoveringText(text, mouseX, mouseY);
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);
    drawContainerName();

    // draw liquids
    SmelteryTank liquids = tinkerTank.getTank();
    if(liquids.getFluidAmount() > 0) {
      int capacity = Math.max(liquids.getFluidAmount(), liquids.getCapacity());
      int[] heights = calcLiquidHeights(liquids.getFluids(), capacity);
      int x = 8 + guiLeft;
      int y = 16 + scala.h + guiTop; // y starting position
      int w = scala.w;

      for(int i = 0; i < heights.length; i++) {
        int h = heights[i];
        FluidStack liquid = liquids.getFluids().get(i);
        RenderUtil.renderTiledFluid(x, y - h, w, h, this.zLevel, liquid);

        y -= h;
      }
    }
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    if(mouseButton == 0) {
      mouseX -= guiLeft;
      mouseY -= guiTop;
      if(8 <= mouseX && mouseX < 114 && 16 <= mouseY && mouseY < 122) {
        SmelteryTank tank = tinkerTank.getTank();
        int[] heights = calcLiquidHeights(tank.getFluids(), tank.getCapacity());
        int y = 122 - mouseY - 1;

        for(int i = 0; i < heights.length; i++) {
          if(y < heights[i]) {
            TinkerNetwork.sendToServer(new SmelteryFluidClicked(i));
            return;
          }
          y -= heights[i];
        }
      }

      mouseX += guiLeft;
      mouseY += guiTop;
    }

    super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  protected FluidStack getFluidHovered(int y) {
    SmelteryTank tank = tinkerTank.getTank();
    int[] heights = calcLiquidHeights(tank.getFluids(), tank.getCapacity());

    for(int i = 0; i < heights.length; i++) {
      if(y < heights[i]) {
        return tank.getFluids().get(i);
      }
      y -= heights[i];
    }

    return null;
  }

  protected int[] calcLiquidHeights(List<FluidStack> liquids, int capacity) {
    return SmelteryRenderer.calcLiquidHeights(liquids, capacity, scala.h, 3);
  }

  protected void drawBackground(ResourceLocation background) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    this.mc.getTextureManager().bindTexture(background);
    this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
  }

  protected void drawContainerName() {
    BaseContainer<?> multiContainer = (BaseContainer<?>) this.inventorySlots;
    String localizedName = multiContainer.getInventoryDisplayName();
    if(localizedName != null) {
      this.fontRendererObj.drawString(localizedName, 8 + guiLeft, 6 + guiTop, 0x404040);
    }
  }

}
