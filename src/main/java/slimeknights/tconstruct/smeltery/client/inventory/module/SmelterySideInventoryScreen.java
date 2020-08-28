package slimeknights.tconstruct.smeltery.client.inventory.module;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.smeltery.client.inventory.SmelteryScreen;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryTileEntity;
import slimeknights.tconstruct.tables.client.inventory.module.SideInventoryScreen;

import java.util.Collections;
import java.util.List;

public class SmelterySideInventoryScreen extends SideInventoryScreen {
  public static final ResourceLocation SLOT_LOCATION = SmelteryScreen.BACKGROUND_LOCATION;
  protected final SmelteryTileEntity smeltery;

  // progress bars
  protected static final ScalableElementScreen PROGRESS_BAR = new ScalableElementScreen(176, 150, 3, 16, 256, 256);
  protected static final ScalableElementScreen NO_HEAT_BAR = new ScalableElementScreen(179, 150, 3, 16);
  protected static final ScalableElementScreen NO_SPACE_BAR = new ScalableElementScreen(182, 150, 3, 16);
  protected static final ScalableElementScreen UNMELTABLE_BAR = new ScalableElementScreen(185, 150, 3, 16);
  // progress bar tooltips
  private static final String TOOLTIP_NO_HEAT = Util.makeTranslationKey("gui", "melting.no_heat");
  private static final String TOOLTIP_NO_SPACE = Util.makeTranslationKey("gui", "melting.no_space");
  private static final String TOOLTIP_UNMELTABLE = Util.makeTranslationKey("gui", "melting.no_recipe");


  public SmelterySideInventoryScreen(MultiModuleScreen<?> parent, Container container, SmelteryTileEntity tile, PlayerInventory playerInventory, ITextComponent title, int slotCount, int columns) {
    super(parent, container, playerInventory, title, slotCount, columns, false, true);
    this.smeltery = tile;
    ScalableElementScreen.defaultTexH = 256;
    ScalableElementScreen.defaultTexW = 256;
    slot = new ScalableElementScreen(0, 166, 22, 18);
    slotEmpty = new ScalableElementScreen(22, 166, 22, 18);
    yOffset = 0;
  }

  @Override
  protected boolean shouldDrawName() {
    return false;
  }

  @Override
  protected void updateSlots() {
    // adjust for the heat bar
    xOffset += 4;
    super.updateSlots();
    xOffset -= 4;
  }

  @Override
  public int drawSlots(MatrixStack matrices, int xPos, int yPos) {
    assert minecraft != null;
    this.minecraft.getTextureManager().bindTexture(SLOT_LOCATION);
    int ret = super.drawSlots(matrices, xPos, yPos);
    this.minecraft.getTextureManager().bindTexture(GENERIC_INVENTORY);
    return ret;
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(matrices);
    super.render(matrices, mouseX, mouseY, partialTicks);
    this.renderHoveredTooltip(matrices, mouseX, mouseY);
  }

  @Override
  public void drawGuiContainerForegroundLayer(MatrixStack matrices, int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(matrices, mouseX, mouseY);

    this.minecraft.getTextureManager().bindTexture(SLOT_LOCATION);
    RenderHelper.disableStandardItemLighting();

    String tooltipText = null;
    for (Slot slot : container.inventorySlots) {
      if (slot.getHasStack() && shouldDrawSlot(slot)) {

      }
    }

    if (tooltipText != null) {
      this.renderTooltip(matrices, new TranslationTextComponent(tooltipText), mouseX, mouseY);
    }
    drawHeatBars(matrices);
  }

  @Override
  protected void renderHoveredTooltip(MatrixStack matrices, int mouseX, int mouseY) {
    super.renderHoveredTooltip(matrices, mouseX, mouseY);
    int checkX = mouseX - this.guiLeft;
    int checkY = mouseY - this.guiTop;
    if (smeltery == null) {
      return;
    }

    if (GuiUtil.isHovered(checkX, checkY, 7, 15, 54, 54)) {
      // if hovering over the fluid, display with name
      final List<ITextComponent> tooltip = Collections.emptyList();
      // TODO: func_243308_b->renderTooltip
      this.func_243308_b(matrices, tooltip, mouseX, mouseY);
    }

    drawHeatTooltips(matrices, mouseX, mouseY);
  }

  /**
   * Draws the heat bars on each slot
   */
  private void drawHeatBars(MatrixStack matrices) {
    if (smeltery == null) {
      System.out.println("smeltery == null");
      return;
    }
    for (Slot slot : container.inventorySlots) {
      if (slot.getHasStack()) {
        // determine the bar to draw and the progress
        ScalableElementScreen bar = PROGRESS_BAR;
        float progress = smeltery.getHeatingProgress(slot.getSlotIndex());
        // NaN means 0 progress for 0 need, unmeltable
        if (Float.isNaN(progress)) {
          progress = 1f;
          bar = UNMELTABLE_BAR;
        }
        // -1 error state if temperature is too low
        else if (progress < 0) {
          bar = NO_HEAT_BAR;
          progress = 1f;
        }
        // scale back normal progress if too large
        else if ((progress > 1f && progress < 2f) || progress == Float.POSITIVE_INFINITY) {
          progress = 1f;
        }
        else if (progress >=2f) {
          bar = NO_SPACE_BAR;
          progress = 1f;
        }

        // draw the bar
        GuiUtil.drawProgressUp(matrices, bar, slot.xPos - 4, slot.yPos, progress);
      }
    }
  }

  /**
   *
   */
  private void drawHeatTooltips(MatrixStack matrices, int mouseX, int mouseY) {
    int checkX = mouseX - this.guiLeft;
    int checkY = mouseY - this.guiTop;

    if (smeltery == null) {
      return;
    }

    for (Slot slot : container.inventorySlots) {
      // must have a stack
      if (slot.getHasStack()) {
        // mouse must be within the slot
        if (GuiUtil.isHovered(checkX, checkY, slot.xPos - 5, slot.yPos -1, PROGRESS_BAR.w + 1, PROGRESS_BAR.h + 2)) {
          float progress = smeltery.getHeatingProgress(slot.getSlotIndex());
          String tooltipKey = null;

          // NaN means 0 progress for 0 need, unmeltable
          if (Float.isNaN(progress)) {
            tooltipKey = TOOLTIP_UNMELTABLE;
          }
          // -1 error state if temperature is too low
          else if (progress < 0) {
            tooltipKey = TOOLTIP_NO_HEAT;
          }
          // 2x error state if no space
          else if (progress >= 2f) {
            tooltipKey = TOOLTIP_NO_SPACE;
          }

          // draw tooltip if relevant
          if (tooltipKey != null) {
            this.renderTooltip(matrices, new TranslationTextComponent(tooltipKey), mouseX, mouseY);
          }

          // cannot hover two slots, so done
          break;
        }
      }
    }
  }
}
