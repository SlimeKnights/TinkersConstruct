package slimeknights.tconstruct.smeltery.client;

import java.util.List;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.mantle.client.gui.GuiElementScalable;
import slimeknights.mantle.client.gui.GuiMultiModule;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.smeltery.client.module.GuiSearedFurnaceSideInventory;
import slimeknights.tconstruct.smeltery.inventory.ContainerSearedFurnace;
import slimeknights.tconstruct.smeltery.tileentity.TileHeatingStructureFuelTank;
import slimeknights.tconstruct.smeltery.tileentity.TileSearedFurnace;
import slimeknights.tconstruct.tools.inventory.ContainerSideInventory;

public class GuiSearedFurnace extends GuiMultiModule {

  public static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/seared_furnace.png");
  
  protected GuiElement flame = new GuiElementScalable(176, 76, 28, 28, 256, 256);

  protected final GuiSearedFurnaceSideInventory sideinventory;
  protected final TileSearedFurnace furnace;
  private TileHeatingStructureFuelTank.FuelInfo fuelInfo;

  public GuiSearedFurnace(ContainerSearedFurnace container, TileSearedFurnace tile) {
    super(container);
    
    this.furnace = tile;

    sideinventory = new GuiSearedFurnaceSideInventory(this, container.getSubContainer(ContainerSideInventory.class),
                                                      furnace, furnace.getSizeInventory(), container.calcColumns());
    addModule(sideinventory);
  }
  
  @Override
  public void updateScreen() {
    super.updateScreen();

    // furnace size changed
    if(furnace.getSizeInventory() != sideinventory.inventorySlots.inventorySlots.size()) {
      // close screen
      this.mc.thePlayer.closeScreen();
    }
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    // we don't need to add the corner since the mouse is already relative to the module's corner
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);

    // draw the tooltips, if any
    // subtract the corner of the main module so the mouse location is relative to just the center, rather than the side inventory
    mouseX -= cornerX;
    mouseY -= cornerY;

    // Fuel tooltips
    if(71 <= mouseX && mouseX < 83 && 16 <= mouseY && mouseY < 68) {
      List<String> text = Lists.newArrayList();
      FluidStack fuel = fuelInfo.fluid;
      text.add(TextFormatting.WHITE + Util.translate("gui.smeltery.fuel"));
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

    // update fuel info
    fuelInfo = furnace.getFuelDisplay();

    // draw the flame, shows how much fuel is left of the last consumed liquid
    this.mc.getTextureManager().bindTexture(BACKGROUND);
    float fuel = furnace.getFuelPercentage();
    if(fuel > 0) {
      GuiElement flame = this.flame;
      int height = 1 + Math.round(fuel * (flame.h - 1));
      int x = 26 + cornerX;
      int y = 41 + cornerY + flame.h - height;
      
      GuiScreen.drawModalRectWithCustomSizedTexture(x, y, flame.x, flame.y + flame.h - height, flame.w, height, flame.texW, flame.texH);
    }

    // draw the fuel
    if(fuelInfo.fluid != null && fuelInfo.fluid.amount > 0) {
      int x = 71 + cornerX;
      int y = 16 + cornerY + 52;
      int w = 12;
      int h = (int) (52f * fuelInfo.fluid.amount / fuelInfo.maxCap);

      RenderUtil.renderTiledFluid(x, y - h, w, h, this.zLevel, fuelInfo.fluid);
    }
  }

  /* Fluid amount displays */
  public void liquidToString(FluidStack fluid, List<String> text) {
    int amount = fluid.amount;
    
    // standard display: bucket amounts
    // we go up to kiloBuckets because we can, not that is possible for the tank...
    amount = calcLiquidText(amount, 1000000, Util.translate("gui.smeltery.liquid.kilobucket"), text);
    amount = calcLiquidText(amount, 1000, Util.translate("gui.smeltery.liquid.bucket"), text);
    calcLiquidText(amount, 1, Util.translate("gui.smeltery.liquid.millibucket"), text);
  }
  
  private int calcLiquidText(int amount, int divider, String unit, List<String> text) {
    int full = amount / divider;
    if(full > 0) {
      text.add(String.format("%d %s%s", full, TextFormatting.GRAY, unit));
    }

    return amount % divider;
  }

}
