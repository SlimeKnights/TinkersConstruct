package slimeknights.tconstruct.smeltery.client;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;

import java.io.IOException;
import java.util.List;

import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.smeltery.SmelteryTank;
import slimeknights.tconstruct.smeltery.client.module.GuiSmelterySideInventory;
import slimeknights.tconstruct.smeltery.inventory.ContainerSmeltery;
import slimeknights.tconstruct.smeltery.network.SmelteryFluidClicked;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;
import slimeknights.tconstruct.tools.inventory.ContainerSideInventory;

public class GuiSmeltery extends GuiHeatingStructureFuelTank {

  public static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/smeltery.png");

  protected GuiElement scala = new GuiElement(176, 76, 52, 52, 256, 256);

  protected final GuiSmelterySideInventory sideinventory;
  protected final TileSmeltery smeltery;

  public GuiSmeltery(ContainerSmeltery container, TileSmeltery smeltery) {
    super(container);

    this.smeltery = smeltery;

    sideinventory = new GuiSmelterySideInventory(this, container.getSubContainer(ContainerSideInventory.class),
                                                 smeltery, smeltery.getSizeInventory(), container.calcColumns());
    addModule(sideinventory);
  }

  // this is the same for both structures, but the superclass does not have (nor need) access to the side inventory
  @Override
  public void updateScreen() {
    super.updateScreen();

    // smeltery size changed
    if(smeltery.getSizeInventory() != sideinventory.inventorySlots.inventorySlots.size()) {
      // close screen
      this.mc.thePlayer.closeScreen();
    }
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    // we don't need to add the corner since the mouse is already reletive to the corner
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);

    // draw the scale
    this.mc.getTextureManager().bindTexture(BACKGROUND);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    scala.draw(8, 16);

    // draw the tooltips, if any
    // subtract the corner of the main module so the mouse location is relative to just the center, rather than the side inventory
    mouseX -= cornerX;
    mouseY -= cornerY;

    // Liquids
    if(8 <= mouseX && mouseX < 60 && 16 <= mouseY && mouseY < 68) {
      FluidStack hovered = getFluidHovered(68 - mouseY - 1);
      List<String> text = Lists.newArrayList();

      if(hovered == null) {
        int usedCap = smeltery.getTank().getFluidAmount();
        int maxCap = smeltery.getTank().getCapacity();
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
    // Fuel
    else if(71 <= mouseX && mouseX < 83 && 16 <= mouseY && mouseY < 68) {
      drawFuelTooltip(mouseX, mouseY);
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

    // draw liquids
    SmelteryTank liquids = smeltery.getTank();
    if(liquids.getFluidAmount() > 0) {
      int capacity = Math.max(liquids.getFluidAmount(), liquids.getCapacity());
      int[] heights = calcLiquidHeights(liquids.getFluids(), capacity);
      int x = 8 + cornerX;
      int y = 16 + scala.h + cornerY; // y starting position
      int w = scala.w;

      for(int i = 0; i < heights.length; i++) {
        int h = heights[i];
        FluidStack liquid = liquids.getFluids().get(i);
        RenderUtil.renderTiledFluid(x, y - h, w, h, this.zLevel, liquid);

        y -= h;
      }
    }

    // update fuel info
    fuelInfo = smeltery.getFuelDisplay();
    drawFuel(71, 16, 12, 52);
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    if(mouseButton == 0) {
      mouseX -= cornerX;
      mouseY -= cornerY;
      if(8 <= mouseX && mouseX < 60 && 16 <= mouseY && mouseY < 68) {
        SmelteryTank tank = smeltery.getTank();
        int[] heights = calcLiquidHeights(tank.getFluids(), tank.getCapacity());
        int y = 68 - mouseY - 1;

        for(int i = 0; i < heights.length; i++) {
          if(y < heights[i]) {
            TinkerNetwork.sendToServer(new SmelteryFluidClicked(i));
            return;
          }
          y -= heights[i];
        }
      }
      mouseX += cornerX;
      mouseY += cornerY;
    }
    super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  protected FluidStack getFluidHovered(int y) {
    SmelteryTank tank = smeltery.getTank();
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

}
