package slimeknights.tconstruct.smeltery.client.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.ForgeI18n;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.client.util.FluidTooltipHandler;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.smeltery.inventory.MelterContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class MelterScreen extends ContainerScreen<MelterContainer> {
  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/melter.png");
  private static final ElementScreen SCALA = new ElementScreen(176, 0, 52, 52);

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
    FluidTankAnimated tank = container.getTileEntity().getTank();
    GuiUtil.renderFluidTank(this, tank.getFluid(), tank.getCapacity(), 90, 16, 52, 52, 100);
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
    GuiUtil.drawContainerNames(this, this.font, this.playerInventory);

    minecraft.getTextureManager().bindTexture(BACKGROUND);
    RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    SCALA.draw(90, 16);
  }

  @Override
  protected void renderHoveredToolTip(int mouseX, int mouseY) {
    super.renderHoveredToolTip(mouseX, mouseY);

    int checkY = mouseY - this.guiTop;
    if (GuiUtil.isHovered(mouseX - this.guiLeft, checkY, 90, 16, 52, 52)) {
      FluidTankAnimated tank = container.getTileEntity().getTank();
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
        tooltip.add(ForgeI18n.getPattern(Util.makeTranslationKey("gui", "melting.capacity")));
        formatter.accept(capacity, tooltip);
        tooltip.add(ForgeI18n.getPattern(Util.makeTranslationKey("gui", "melting.available")));
        formatter.accept(capacity - amount, tooltip);

        // add shift message
        //tooltip.add("");
        FluidTooltipHandler.appendShift(tooltip);
      }

      this.renderTooltip(tooltip, mouseX, mouseY);
    }
  }
}
