package slimeknights.tconstruct.smeltery.client.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ForgeI18n;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.client.util.FluidTooltipHandler;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.smeltery.inventory.MelterContainer;
import slimeknights.tconstruct.smeltery.tileentity.MelterTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.inventory.MelterFuelWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class MelterScreen extends ContainerScreen<MelterContainer> {
  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/melter.png");
  private static final ElementScreen SCALA = new ElementScreen(176, 0, 52, 52);
  // progress bars
  private static final ScalableElementScreen PROGRESS_BAR = new ScalableElementScreen(176, 150, 3, 16, 256, 256);
  private static final ScalableElementScreen NO_HEAT_BAR = new ScalableElementScreen(179, 150, 3, 16);
  private static final ScalableElementScreen NO_SPACE_BAR = new ScalableElementScreen(182, 150, 3, 16);
  private static final ScalableElementScreen UNMELTABLE_BAR = new ScalableElementScreen(185, 150, 3, 16);
  // progress bar tooltips
  private static final String TOOLTIP_NO_HEAT = Util.makeTranslationKey("gui", "melting.no_heat");
  private static final String TOOLTIP_NO_SPACE = Util.makeTranslationKey("gui", "melting.no_space");
  private static final String TOOLTIP_UNMELTABLE = Util.makeTranslationKey("gui", "melting.no_recipe");
  // fuel tooltips
  private static final String TOOLTIP_NO_FUEL = Util.makeTranslationKey("gui", "melting.fuel.empty");
  private static final String TOOLTIP_TEMPERATURE = Util.makeTranslationKey("gui", "melting.fuel.temperature");
  private static final String TOOLTIP_INVALID_FUEL = Util.makeTranslationKey("gui", "melting.fuel.invalid");
  // fluid tooltips
  private static final String TOOLTIP_CAPACITY = Util.makeTranslationKey("gui", "melting.capacity");
  private static final String TOOLTIP_AVAILABLE = Util.makeTranslationKey("gui", "melting.available");

  public MelterScreen(MelterContainer container, PlayerInventory inv, ITextComponent name) {
    super(container, inv, name);
  }

  @Override
  public void render(int x, int y, float partialTicks) {
    this.renderBackground();
    super.render(x, y, partialTicks);
    this.renderHoveredToolTip(x, y);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    GuiUtil.drawBackground(this, BACKGROUND);

    // fluids
    MelterTileEntity melter = container.getTileEntity();
    FluidTankAnimated tank = melter.getTank();
    GuiUtil.renderFluidTank(this, tank.getFluid(), tank.getCapacity(), 90, 16, 52, 52, 100);

    // fuel
    MelterFuelWrapper wrapper = melter.getFuelInventory();
    if (wrapper != null) {
      GuiUtil.renderFluidTank(this, wrapper.getFluidStack(), wrapper.getCapacity(), 153, 16, 12, 52, 100);
    }
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    GuiUtil.drawContainerNames(this, this.font, this.playerInventory);

    int checkX = mouseX - this.guiLeft;
    int checkY = mouseY - this.guiTop;
    MelterTileEntity melter = container.getTileEntity();

    // highlight hovered fluid
    if (GuiUtil.isHovered(checkX, checkY, 89, 15, 54, 54)) {
      FluidTankAnimated tank = melter.getTank();
      int fluidHeight = 52 * tank.getFluidAmount() / tank.getCapacity();
      int middle = 68 - fluidHeight;
      // highlight just fluid
      if (checkY > middle) {
        GuiUtil.renderHighlight(90, middle, 52, fluidHeight);
      } else {
        // or highlight empty
        GuiUtil.renderHighlight(90, 16, 52, 52 - fluidHeight);
      }
    }

    // highlight hovered fuel
    if (GuiUtil.isHovered(checkX, checkY, 152, 15, 14, 54)) {
      GuiUtil.renderHighlight(153, 16, 12, 52);
    }

    minecraft.getTextureManager().bindTexture(BACKGROUND);
    RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    SCALA.draw(90, 16);
    
    drawHeatBars();
  }

  @Override
  protected void renderHoveredToolTip(int mouseX, int mouseY) {
    super.renderHoveredToolTip(mouseX, mouseY);

    int checkX = mouseX - this.guiLeft;
    int checkY = mouseY - this.guiTop;
    MelterTileEntity melter = container.getTileEntity();
    if (GuiUtil.isHovered(checkX, checkY, 89, 15, 54, 54)) {
      FluidTankAnimated tank = melter.getTank();
      int amount = tank.getFluidAmount();
      int capacity = tank.getCapacity();

      // if hovering over the fluid, display with name
      final List<String> tooltip;
      if (checkY > 68 - (52 * amount / capacity)) {
        tooltip = FluidTooltipHandler.getFluidTooltip(tank.getFluid());
      } else {
        // function to call for amounts
        BiConsumer<Integer, List<String>> formatter = Util.isShiftKeyDown()
          ? FluidTooltipHandler::appendBuckets
          : FluidTooltipHandler::appendIngots;

        // add tooltips
        tooltip = new ArrayList<>();
        tooltip.add(ForgeI18n.getPattern(TOOLTIP_CAPACITY));
        formatter.accept(capacity, tooltip);
        if (capacity != amount) {
          tooltip.add(ForgeI18n.getPattern(TOOLTIP_AVAILABLE));
          formatter.accept(capacity - amount, tooltip);
        }

        // add shift message
        //tooltip.add("");
        FluidTooltipHandler.appendShift(tooltip);
      }

      this.renderTooltip(tooltip, mouseX, mouseY);
    }
    
    drawHeatTooltips(mouseX, mouseY);

    // fuel tooltip
    if (GuiUtil.isHovered(checkX, checkY, 152, 15, 14, 54)) {
      List<String> tooltip = null;
      // make sure we have a tank below
      MelterFuelWrapper wrapper = melter.getFuelInventory();
      if (wrapper != null) {
        FluidStack fluid = wrapper.getFluidStack();
        if (!fluid.isEmpty()) {
          tooltip = FluidTooltipHandler.getFluidTooltip(fluid);
          // we are displaying current tank, so a match means matches current contents
          MeltingFuel fuel = melter.findMeltingFuel();
          if (fuel != null) {
            tooltip.add(1, TextFormatting.GRAY + (TextFormatting.ITALIC + ForgeI18n.parseMessage(TOOLTIP_TEMPERATURE, fuel.getTemperature())));
          } else {
            // invalid fuel
            tooltip.add(1, TextFormatting.RED + ForgeI18n.getPattern(TOOLTIP_INVALID_FUEL));
          }
        }
      }
      // null means either empty or we have no wrapper
      if (tooltip == null) {
        tooltip = Collections.singletonList(ForgeI18n.getPattern(TOOLTIP_NO_FUEL));
      }

      this.renderTooltip(tooltip, mouseX, mouseY);
    }
  }

  /**
   * Draws the heat bars on each slot
   */
  private void drawHeatBars() {
    MelterTileEntity melter = container.getTileEntity();
    for(Slot slot : this.container.getInputs()) {
      if(slot.getHasStack()) {
        // determine the bar to draw and the progress
        ScalableElementScreen bar = PROGRESS_BAR;
        float progress = melter.getHeatingProgress(slot.getSlotIndex());
        // NaN means 0 progress for 0 need, unmeltable
        if(Float.isNaN(progress)) {
          progress = 1f;
          bar = UNMELTABLE_BAR;
        }
        // -1 error state if temperature is too low
        else if(progress < 0) {
          bar = NO_HEAT_BAR;
          progress = 1f;
        }
        // scale back normal progress if too large
        else if((progress > 1f && progress < 2f) || progress == Float.POSITIVE_INFINITY) {
          progress = 1f;
        }
        else if(progress >= 2f) {
          bar = NO_SPACE_BAR;
          progress = 1f;
        }

        // draw the bar
        GuiUtil.drawProgressUp(bar, slot.xPos - 4, slot.yPos, progress);
      }
    }
  }

  /**
   * Draws the tooltip for the hovered hear slot
   * @param mouseX  Mouse X position
   * @param mouseY  Mouse Y position
   */
  private void drawHeatTooltips(int mouseX, int mouseY) {
    int checkX = mouseX - this.guiLeft;
    int checkY = mouseY - this.guiTop;

    // skip the fourth slot if it exists
    for(Slot slot : container.getInputs()) {
      // must have a stack
      if(slot.getHasStack()) {
        // mouse must be within the slot
        if (GuiUtil.isHovered(checkX, checkY, slot.xPos - 5, slot.yPos - 1, PROGRESS_BAR.w + 1, PROGRESS_BAR.h + 2)) {
          float progress = container.getTileEntity().getHeatingProgress(slot.getSlotIndex());
          String tooltipKey = null;

          // NaN means 0 progress for 0 need, unmeltable
          if(Float.isNaN(progress)) {
            tooltipKey = TOOLTIP_UNMELTABLE;
          }
          // -1 error state if temperature is too low
          else if(progress < 0) {
            tooltipKey = TOOLTIP_NO_HEAT;
          }
          // 2x error state if no space
          else if(progress >= 2f) {
            tooltipKey = TOOLTIP_NO_SPACE;
          }
          // TODO: wrong fluid?

          // draw tooltip if relevant
          if (tooltipKey != null) {
            this.renderTooltip(ForgeI18n.getPattern(tooltipKey), mouseX, mouseY);
          }

          // cannot hover two slots, so done
          break;
        }
      }
    }
  }
}
