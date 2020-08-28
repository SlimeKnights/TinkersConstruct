package slimeknights.tconstruct.smeltery.client.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.client.util.FluidTooltipHandler;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.smeltery.SmelteryTank;
import slimeknights.tconstruct.smeltery.client.inventory.module.SmelterySideInventoryScreen;
import slimeknights.tconstruct.smeltery.inventory.SmelteryContainer;
import slimeknights.tconstruct.smeltery.tileentity.inventory.MelterFuelWrapper;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryTileEntity;
import slimeknights.tconstruct.tables.inventory.SideInventoryContainer;

import java.util.Collections;
import java.util.List;

public class SmelteryScreen extends MultiModuleScreen<SmelteryContainer> {
  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/smeltery.png");
  private static final ElementScreen SCALA = new ElementScreen(176, 76, 52, 52, 256, 256);
  // fuel tooltips
  private static final String TOOLTIP_NO_FUEL = Util.makeTranslationKey("gui", "melting.fuel.empty");
  private static final String TOOLTIP_TEMPERATURE = Util.makeTranslationKey("gui", "melting.fuel.temperature");
  private static final String TOOLTIP_INVALID_FUEL = Util.makeTranslationKey("gui", "melting.fuel.invalid");
  // fluid tooltips
  private static final String TOOLTIP_CAPACITY = Util.makeTranslationKey("gui", "melting.capacity");
  private static final String TOOLTIP_AVAILABLE = Util.makeTranslationKey("gui", "melting.available");

  protected final SmelterySideInventoryScreen sideInvScreen;
  public SmelteryScreen(SmelteryContainer container, PlayerInventory inv, ITextComponent name) {
    super(container, inv, name);
    sideInvScreen = new SmelterySideInventoryScreen(this, container.getSubContainer(SideInventoryContainer.class),
      container.getTile(), inv, name, container.getTile().getSizeInventory(), 3);
    this.addModule(sideInvScreen);
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(matrices);
    super.render(matrices, mouseX, mouseY, partialTicks);
    this.renderHoveredTooltip(matrices, mouseX, mouseY);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack matrices, float partialTicks, int x, int y) {
    GuiUtil.drawBackground(matrices, this, BACKGROUND);

    // fluids
    SmelteryTileEntity smeltery = container.getTile();
    if (smeltery != null) {
      // fuel
      MelterFuelWrapper wrapper = smeltery.getFuelInventory();
      if (wrapper != null) {
        GuiUtil.renderFluidTank(matrices, this, wrapper.getFluidStack(), wrapper.getCapacity(), 71, 16, 12, 52, 100);
      }
    }
  }

  @Override
  protected void drawGuiContainerForegroundLayer(MatrixStack matrices, int mouseX, int mouseY) {
    GuiUtil.drawContainerNames(matrices, this, this.font, this.playerInventory);
    int checkX = mouseX - this.guiLeft;
    int checkY = mouseY - this.guiTop;
    SmelteryTileEntity smeltery = container.getTile();

    // highlight hovered fluid
    if (GuiUtil.isHovered(checkX, checkY, 7, 15, 54, 54) && smeltery != null) {
      SmelteryTank tank = smeltery.getLiquids();
    }

    // highlight hovered fuel
    if (GuiUtil.isHovered(checkX, checkY, 70, 15, 14, 54)) {
      GuiUtil.renderHighlight(matrices, 71, 16, 12, 52);
    }

    assert minecraft != null;
    minecraft.getTextureManager().bindTexture(BACKGROUND);
    RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    SCALA.draw(matrices, 8, 16);
  }

  @Override
  protected void renderHoveredTooltip(MatrixStack matrices, int mouseX, int mouseY) {
    super.renderHoveredTooltip(matrices, mouseX, mouseY);

    int checkX = mouseX - this.guiLeft;
    int checkY = mouseY - this.guiTop;
    SmelteryTileEntity smeltery = container.getTile();
    if (smeltery == null) {
      return;
    }
    if (GuiUtil.isHovered(checkX, checkY, 7, 15, 54, 54)) {
      final List<ITextComponent> tooltip = Collections.emptyList();
      // TODO: func_243308_b->renderTooltip
      this.func_243308_b(matrices, tooltip, mouseX, mouseY);
    }

    // fuel tooltip
    if (GuiUtil.isHovered(checkX, checkY, 152, 15, 14, 54)) {
      List<ITextComponent> tooltip = null;
      // make sure we have a tank below
      MelterFuelWrapper wrapper = smeltery.getFuelInventory();
      if (wrapper != null) {
        FluidStack fluid = wrapper.getFluidStack();
        if (!fluid.isEmpty()) {
          tooltip = FluidTooltipHandler.getFluidTooltip(fluid);
          // we are displaying current tank, so a match means matches current contents
          MeltingFuel fuel = smeltery.findMeltingFuel();
          if (fuel != null) {
            tooltip.add(1, new TranslationTextComponent(TOOLTIP_TEMPERATURE, fuel.getTemperature()).mergeStyle(TextFormatting.GRAY, TextFormatting.ITALIC));
          } else {
            // invalid fuel
            tooltip.add(1, new TranslationTextComponent(TOOLTIP_INVALID_FUEL).mergeStyle(TextFormatting.RED));
          }
        }
      }
      // null means either empty or we have no wrapper
      if (tooltip == null) {
        tooltip = Collections.singletonList(new TranslationTextComponent(TOOLTIP_NO_FUEL));
      }

      // TODO: func_243308_b->renderTooltip
      this.func_243308_b(matrices, tooltip, mouseX, mouseY);
    }
  }
}
