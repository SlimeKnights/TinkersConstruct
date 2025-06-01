package slimeknights.tconstruct.smeltery.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.smeltery.block.controller.ControllerBlock;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.FuelModule;
import slimeknights.tconstruct.smeltery.client.screen.module.GuiFuelModule;
import slimeknights.tconstruct.smeltery.client.screen.module.GuiMeltingModule;
import slimeknights.tconstruct.smeltery.client.screen.module.GuiSmelteryTank;
import slimeknights.tconstruct.smeltery.client.screen.module.HeatingStructureSideInventoryScreen;
import slimeknights.tconstruct.smeltery.menu.HeatingStructureContainerMenu;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class HeatingStructureScreen extends MultiModuleScreen<HeatingStructureContainerMenu> implements IScreenWithFluidTank {
  public static final ResourceLocation BACKGROUND = TConstruct.getResource("textures/gui/heating_structure.png");
  private static final ElementScreen SCALA = new ElementScreen(BACKGROUND, 176, 0, 80, 106, 256, 256);
  // slot modes
  private static final ElementScreen MODE_AUTO = SCALA.move(176, 186, 16, 16);
  private static final ElementScreen MODE_EMPTY = MODE_AUTO.shift(16, 0);
  private static final ElementScreen MODE_FILL = MODE_EMPTY.shift(16, 0);
  private static final ElementScreen BUCKET_ICON = MODE_FILL.shift(16, 0);
  private static final ElementScreen BUTTON_HOVER = SCALA.move(176, 202, 18, 18);

  /** Tooltips for buckets */
  private static final Component TOOLTIP_CLICKABLE = TConstruct.makeTranslation("gui", "tank.bucket.clickable").withStyle(ChatFormatting.GRAY);
  private static final List<Component> TOOLTIP_AUTO = makeBucketTooltip("auto");
  private static final List<Component> TOOLTIP_EMPTY = makeBucketTooltip("empty_item");
  private static final List<Component> TOOLTIP_FILL = makeBucketTooltip("fill_item");
  private static List<Component> makeBucketTooltip(String name) {
    return List.of(
      TConstruct.makeTranslation("gui", "tank.bucket." + name + ".title"),
      TConstruct.makeTranslation("gui", "tank.bucket." + name + ".description").withStyle(ChatFormatting.GRAY),
      TOOLTIP_CLICKABLE);
  }

  private final HeatingStructureSideInventoryScreen sideInventory;
  private final HeatingStructureBlockEntity te;
  private final GuiSmelteryTank tank;
  public final GuiMeltingModule melting;
  private final GuiFuelModule fuel;

  @SuppressWarnings("deprecation")  // no you're deprecated Forge
  public HeatingStructureScreen(HeatingStructureContainerMenu container, Inventory playerInventory, Component title) {
    super(container, playerInventory, title);
    this.imageHeight = 220;

    HeatingStructureBlockEntity te = container.getTile();
    if (te != null) {
      this.te = te;
      this.tank = new GuiSmelteryTank(this, te.getTank(), 8, 16, 106, 106, Objects.requireNonNull(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(te.getType())));
      int slots = te.getMeltingInventory().getSlots();
      this.sideInventory = new HeatingStructureSideInventoryScreen(this, container.getSideInventory(), playerInventory, slots, HeatingStructureContainerMenu.calcColumns(slots));
      addModule(sideInventory);
      FuelModule fuelModule = te.getFuelModule();
      // we have 2 slots before the melting slots - the bucket input and the bucket output
      this.melting = new GuiMeltingModule(this, te.getMeltingInventory(), 2, fuelModule::getTemperature, sideInventory::shouldDrawSlot, BACKGROUND);
      this.fuel = new GuiFuelModule(this, fuelModule, 152, 32, 16, 90, 153, 15, false, BACKGROUND);
    } else {
      this.te = null;
      this.tank = null;
      this.melting = null;
      this.fuel = null;
      this.sideInventory = null;
    }
  }


  @Override
  protected void containerTick() {
    super.containerTick();
    // if the smeltery becomes invalid or the slot size changes, kill the UI
    if (te == null || !te.getBlockState().getValue(ControllerBlock.IN_STRUCTURE)
        || te.getMeltingInventory().getSlots() != sideInventory.getSlotCount()) {
      this.onClose();
    }
  }
  @Override
  protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
    // draw stuff with background
    GuiUtil.drawBackground(graphics, this, BACKGROUND);
    // fuel
    if (fuel != null) {
      fuel.draw(graphics);
    }

    // draw other components
    super.renderBg(graphics, partialTicks, mouseX, mouseY);

    // render fluids
    if (tank != null) tank.renderFluids(graphics.pose());
  }

  /** Checks if the bucket button is hovered */
  private boolean bucketButtonHovered(int mouseX, int mouseY) {
    return GuiUtil.isHovered(mouseX, mouseY, 124, 69, 18, 18);
  }

  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    super.renderLabels(graphics, mouseX, mouseY);

    SCALA.draw(graphics, 8, 16, 110);

    // draw the bucket icon if nothing is in the bucket slot
    if (menu.getBucketContainer().getItem(0).isEmpty()) {
      BUCKET_ICON.draw(graphics, 125, 46);
    }

    // if hovering, tint the button
    if (bucketButtonHovered(mouseX - leftPos, mouseY - topPos)) {
      BUTTON_HOVER.draw(graphics, 124, 69);
    }
    // draw the button icon
    (switch (menu.getTransferDirection()) {
      default -> MODE_AUTO;
      case EMPTY_ITEM -> MODE_EMPTY;
      case FILL_ITEM -> MODE_FILL;
    }).draw(graphics, 125, 70);

    // highlight hovered fluids
    if (tank != null) tank.renderHighlight(graphics, mouseX, mouseY);
    if (fuel != null) fuel.renderHighlight(graphics, mouseX - this.leftPos, mouseY - this.topPos);

    // while this might make sense to draw in the side inventory logic, slots are rendered by the parent screen it seems
    // so we get the most accurate offset rendering it here, as we offset the foreground of submodules but they don't draw their own slots
    // I hate the whole multimodule system right now
    if (melting != null) melting.drawHeatBars(graphics);
  }

  @Override
  protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
    super.renderTooltip(graphics, mouseX, mouseY);

    // add the hover text giving info on the button
    if (bucketButtonHovered(mouseX - leftPos, mouseY - topPos)) {
      graphics.renderTooltip(font, switch (menu.getTransferDirection()) {
        default -> TOOLTIP_AUTO;
        case EMPTY_ITEM -> TOOLTIP_EMPTY;
        case FILL_ITEM -> TOOLTIP_FILL;
      }, Optional.empty(), mouseX, mouseY);
    }

    // fluid tooltips
    if (tank != null) tank.drawTooltip(graphics, mouseX, mouseY);
    if (fuel != null) {
      boolean hasTank = false;
      if (te.getStructure() != null) {
        hasTank = te.getStructure().hasTanks();
      }
      fuel.addTooltip(graphics, mouseX, mouseY, hasTank);
    }
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    assert minecraft != null && minecraft.player != null && minecraft.gameMode != null;
    if (!minecraft.player.isSpectator()) {
      int checkX = (int)mouseX - cornerX;
      int checkY = (int)mouseY - cornerY;
      // clicking the bucket button toggles its mode
      if (button == 0 && bucketButtonHovered(checkX, checkY) && this.menu.clickMenuButton(minecraft.player, 0)) {
        minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        return true;
      }

      // then try fuel, uses index 1 to indicate fill item and index 2 to indicate drain item
      if ((button == 0 || button == 1) && fuel != null && fuel.tryClick(checkX, checkY, button, 1)) {
        return true;
      }

      // index 3 means empty spot on smeltery tank
      // index 4+ means smeltery fluids, change order or dump
      if (tank != null && (button == 0 || button == 1)) {
        int index;
        if (button == 1 && tank.withinTank(checkX, checkY)) {
          // right click always dumps
          index = 3;
        } else {
          // index range goes from -1 to max fluid - 1
          // however, indexes we will send to the server are 3+
          index = tank.getFluidClicked(checkX, checkY) + 4;
        }
        // if the index is valid, use it
        if (index > 2 && menu.clickMenuButton(minecraft.player, index)) {
          minecraft.gameMode.handleInventoryButtonClick(menu.containerId, index);
          return true;
        }
      }
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  @Nullable
  @Override
  public FluidLocation getFluidUnderMouse(int mouseX, int mouseY) {
    int checkX = mouseX - cornerX;
    int checkY = mouseY - cornerY;

    // try fuel first, its faster
    if (fuel != null)  {
      FluidLocation ingredient = fuel.getFluidUnderMouse(checkX, checkY);
      if (ingredient != null) {
        return ingredient;
      }
    }
    // then try tank
    if (tank != null) {
      return tank.getFluidUnderMouse(checkX, checkY);
    }
    return null;
  }
}
