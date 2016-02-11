package slimeknights.tconstruct.smeltery.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.mantle.client.gui.GuiMultiModule;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.TinkerRegistryClient;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.library.smeltery.SmelteryTank;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
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
    super.drawGuiContainerForegroundLayer(mouseX + cornerX, mouseY + cornerY);

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

  /* Fluid amount displays */
  private static Map<Fluid, List<FluidGuiEntry>> fluidGui = Maps.newHashMap();

  public void liquidToString(FluidStack fluid, List<String> text) {
    int amount = fluid.amount;
    if(!Util.isShiftKeyDown()) {
      List<FluidGuiEntry> entries = fluidGui.get(fluid.getFluid());
      if(entries == null) {
        entries = calcFluidGuiEntries(fluid.getFluid());
        fluidGui.put(fluid.getFluid(), entries);
      }

      for(FluidGuiEntry entry : entries) {
        amount = calcLiquidText(amount, entry.amount, entry.getText(), text);
      }
    }

    // standard display: bucket amounts
    // we go up to kiloBuckets because we can
    amount = calcLiquidText(amount, 1000000, Util.translate("gui.smeltery.liquid.kilobucket"), text);
    amount = calcLiquidText(amount, 1000, Util.translate("gui.smeltery.liquid.bucket"), text);
    calcLiquidText(amount, 1, Util.translate("gui.smeltery.liquid.millibucket"), text);
  }

  private List<FluidGuiEntry> calcFluidGuiEntries(Fluid fluid) {
    List<FluidGuiEntry> list = Lists.newArrayList();

    // go through all casting recipes for the fluids and check for known "units" like blocks, ingots,...
    for(CastingRecipe recipe : TinkerRegistry.getAllBasinCastingRecipes()) {
      // search for a block recipe
      if(recipe.getFluid().getFluid() == fluid && recipe.cast == null) {
        // it's a block that is cast solely from the material, using no cast, therefore it's a block made out of the material
        list.add(new FluidGuiEntry(recipe.getFluid().amount, "gui.smeltery.liquid.block"));
      }
    }
    // table casting
    for(CastingRecipe recipe : TinkerRegistry.getAllTableCastingRecipes()) {
      if(recipe.getFluid().getFluid() == fluid && recipe.cast != null) {
        // nugget
        if(recipe.cast.matches(new ItemStack[]{TinkerSmeltery.castNugget}) != null) {
          list.add(new FluidGuiEntry(recipe.getFluid().amount, "gui.smeltery.liquid.nugget"));
        }
        // ingot
        if(recipe.cast.matches(new ItemStack[]{TinkerSmeltery.castIngot}) != null) {
          list.add(new FluidGuiEntry(recipe.getFluid().amount, "gui.smeltery.liquid.ingot"));
        }
        // gem
        if(recipe.cast.matches(new ItemStack[]{TinkerSmeltery.castGem}) != null) {
          list.add(new FluidGuiEntry(recipe.getFluid().amount, "gui.smeltery.liquid.gem"));
        }
      }
    }

    // sort by amount descending because the order in which they're accessed is important since it changes the remaining value during processing
    list.sort(new Comparator<FluidGuiEntry>() {
      @Override
      public int compare(FluidGuiEntry o1, FluidGuiEntry o2) {
        return o2.amount - o1.amount;
      }
    });

    return ImmutableList.copyOf(list);
  }

  private int calcLiquidText(int amount, int divider, String unit, List<String> text) {
    int full = amount/divider;
    if(full > 0) {
      text.add(String.format("%d %s%s", full, EnumChatFormatting.GRAY, unit));
    }

    return amount % divider;
  }

  private static class FluidGuiEntry {
    public final int amount;
    public final String unlocName;

    private FluidGuiEntry(int amount, String unlocName) {
      this.amount = amount;
      this.unlocName = unlocName;
    }

    public String getText() {
      return Util.translate(unlocName);
    }
  }
}
