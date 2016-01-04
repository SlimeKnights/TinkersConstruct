package slimeknights.tconstruct.smeltery.client;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;

import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.mantle.client.gui.GuiMultiModule;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.TinkerRegistryClient;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.SmelteryTank;
import slimeknights.tconstruct.smeltery.client.module.GuiSmelterySideinventory;
import slimeknights.tconstruct.smeltery.inventory.ContainerSmeltery;
import slimeknights.tconstruct.smeltery.network.SmelteryFluidClicked;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;
import slimeknights.tconstruct.tools.inventory.ContainerSideInventory;

public class GuiSmeltery extends GuiMultiModule {

  public static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/smeltery.png");

  protected GuiElement scala = new GuiElement(176, 76, 52, 52, 256, 256);

  protected final GuiSmelterySideinventory sideinventory;
  protected final TileSmeltery smeltery;

  private TileSmeltery.FuelInfo fuelInfo;

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

    // draw the scale
    this.mc.getTextureManager().bindTexture(BACKGROUND);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    scala.draw(8, 16);

    // draw the tooltips, if any
    mouseX -= cornerX;
    mouseY -= cornerY;

    // Liquids
    if(8 <= mouseX && mouseX < 60 && 16 <= mouseY && mouseY < 68) {
      FluidStack hovered = getFluidHovered(68 - mouseY - 1);
      List<String> text = Lists.newArrayList();

      if(hovered == null) {
        int usedCap = smeltery.getTank().getUsedCapacity();
        int maxCap = smeltery.getTank().getMaxCapacity();
        text.add(EnumChatFormatting.WHITE + Util.translate("gui.smeltery.capacity"));
        text.add(EnumChatFormatting.GRAY.toString() + maxCap + Util.translate("gui.smeltery.liquid.millibucket"));
        text.add(Util.translateFormatted("gui.smeltery.capacity_available"));
        text.add(EnumChatFormatting.GRAY.toString() + (maxCap - usedCap) + Util.translate("gui.smeltery.liquid.millibucket"));
      }
      else {
        text.add(EnumChatFormatting.WHITE + hovered.getLocalizedName());
        liquidToString(hovered, text);
      }

      this.drawHoveringText(text, mouseX, mouseY);
    }
    // Fuel
    else if(71 <= mouseX && mouseX < 83 && 16 <= mouseY && mouseY < 68) {
      List<String> text = Lists.newArrayList();
      FluidStack fuel = fuelInfo.fluid;
      text.add(EnumChatFormatting.WHITE + Util.translate("gui.smeltery.fuel"));
      if(fuel != null) {
        text.add(fuel.getLocalizedName());
        liquidToString(fuel, text);
      }
      else {
        text.add(Util.translate("gui.smeltery.fuel.empty"));
      }
      text.add(Util.translateFormatted("gui.smeltery.fuel.heat", fuelInfo.heat));
      this.drawHoveringText(text, mouseX, mouseY);
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

    // draw liquids
    SmelteryTank liquids = smeltery.getTank();
    if(liquids.getUsedCapacity() > 0) {
      int capacity = Math.max(liquids.getUsedCapacity(), liquids.getMaxCapacity());
      int[] heights = calcLiquidHeights(liquids.getFluids(), capacity);
      int x = 8 + cornerX;
      int y = 16 + scala.h + cornerY; // y starting position
      int w = scala.w;

      for(int i = 0; i < heights.length; i++) {
        int h = heights[i];
        FluidStack liquid = liquids.getFluids().get(i);
        RenderUtil.renderTiledFluid(x, y-h, w, h, this.zLevel, liquid);

        y -= h;
      }
    }

    // update fuel info
    fuelInfo = smeltery.getFuelDisplay();

    if(fuelInfo.fluid != null && fuelInfo.fluid.amount > 0) {
      int x = 71 + cornerX;
      int y = 16 + cornerY + 52;
      int w = 12;
      int h = (int)(52f * (float)fuelInfo.fluid.amount / (float)fuelInfo.maxCap);

      RenderUtil.renderTiledFluid(x, y-h, w, h, this.zLevel, fuelInfo.fluid);
    }
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    if(mouseButton == 0) {
      mouseX -= cornerX;
      mouseY -= cornerY;
      if(8 <= mouseX && mouseX < 60 && 16 <= mouseY && mouseY < 68) {
        SmelteryTank tank = smeltery.getTank();
        int[] heights = calcLiquidHeights(tank.getFluids(), tank.getMaxCapacity());
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
    int[] heights = calcLiquidHeights(tank.getFluids(), tank.getMaxCapacity());

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


  public void liquidToString(FluidStack fluid, List<String> text) {
    liquidToString(TinkerRegistryClient.getFluidDisplayType(fluid.getFluid()), fluid.amount, text);
  }

  public void liquidToString(TinkerRegistryClient.FluidDisplayType type, int amount, List<String> text) {
    if(Util.isShiftKeyDown()) {
      type = TinkerRegistryClient.FluidDisplayType.BUCKETS;
    }
    // Ingots
    if(type == TinkerRegistryClient.FluidDisplayType.INGOTS) {
      amount = calcLiquidText(amount, Material.VALUE_Block, Util.translate("gui.smeltery.liquid.block"), text);
      amount = calcLiquidText(amount, Material.VALUE_Ingot, Util.translate("gui.smeltery.liquid.ingot"), text);
      amount = calcLiquidText(amount, Material.VALUE_Nugget, Util.translate("gui.smeltery.liquid.nugget"), text);
    }
    // Seared ingots/blocks
    if(type == TinkerRegistryClient.FluidDisplayType.SEARED) {
      amount = calcLiquidText(amount, Material.VALUE_SearedBlock, Util.translate("gui.smeltery.liquid.block"), text);
    }
    // Glass
    if(type == TinkerRegistryClient.FluidDisplayType.GLASS) {
      amount = calcLiquidText(amount, Material.VALUE_Glass, Util.translate("gui.smeltery.liquid.block"), text);
    }
    // Blocks
    else if(type == TinkerRegistryClient.FluidDisplayType.BLOCKS) {
      amount = calcLiquidText(amount, Material.VALUE_Block, Util.translate("gui.smeltery.liquid.block"), text);
    }
    // Gems
    else if(type == TinkerRegistryClient.FluidDisplayType.GEMS) {
      amount = calcLiquidText(amount, Material.VALUE_Gem, Util.translate("gui.smeltery.liquid.gem"), text);
    }
    // Buckets
    else {
      // we go up to kiloBuckets because we can
      amount = calcLiquidText(amount, 1000000, Util.translate("gui.smeltery.liquid.kilobucket"), text);
      amount = calcLiquidText(amount, 1000, Util.translate("gui.smeltery.liquid.bucket"), text);
    }
    calcLiquidText(amount, 1, Util.translate("gui.smeltery.liquid.millibucket"), text);
  }

  private int calcLiquidText(int amount, int divider, String unit, List<String> text) {
    int full = amount/divider;
    if(full > 0) {
      text.add(String.format("%d %s%s", full, EnumChatFormatting.GRAY, unit));
    }

    return amount % divider;
  }
}
