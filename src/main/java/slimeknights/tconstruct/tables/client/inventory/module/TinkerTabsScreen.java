package slimeknights.tconstruct.tables.client.inventory.module;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.ModuleScreen;
import slimeknights.mantle.client.screen.TabsWidget;
import slimeknights.tconstruct.tables.client.inventory.BaseStationScreen;

import java.util.List;

public class TinkerTabsScreen extends ModuleScreen {

  protected static final ElementScreen TAB_ELEMENT = new ElementScreen(0, 2, 28, 28, 256, 256);
  protected static final ElementScreen ACTIVE_TAB_L_ELEMENT = new ElementScreen(0, 32, 28, 32, 256, 256);
  protected static final ElementScreen ACTIVE_TAB_C_ELEMENT = new ElementScreen(28, 32, 28, 32, 256, 256);
  protected static final ElementScreen ACTIVE_TAB_R_ELEMENT = new ElementScreen(140, 32, 28, 32, 256, 256);

  public TabsWidget tabs;
  public List<BlockPos> tabData;

  public final BaseStationScreen parent;

  public TinkerTabsScreen(BaseStationScreen parent, Container container, PlayerInventory playerInventory, ITextComponent title) {
    super(parent, container, playerInventory, title, false, false);

    this.parent = parent;

    this.xSize = ACTIVE_TAB_C_ELEMENT.w;
    this.ySize = ACTIVE_TAB_C_ELEMENT.h;

    this.tabs = new TabsWidget(parent, TAB_ELEMENT, TAB_ELEMENT, TAB_ELEMENT, ACTIVE_TAB_L_ELEMENT, ACTIVE_TAB_C_ELEMENT, ACTIVE_TAB_R_ELEMENT);
    this.tabData = Lists.newArrayList();
  }

  public void addTab(ItemStack icon, BlockPos data) {
    this.tabData.add(data);
    this.tabs.addTab(icon);
  }

  @Override
  public boolean handleMouseClicked(double mouseX, double mouseY, int mouseButton) {
    this.tabs.handleMouseClicked((int) mouseX, (int) mouseY, mouseButton);

    return super.handleMouseClicked(mouseX, mouseY, mouseButton);
  }

  @Override
  public boolean handleMouseReleased(double mouseX, double mouseY, int state) {
    this.tabs.handleMouseReleased();

    return super.handleMouseReleased(mouseX, mouseY, state);
  }

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

    // we actually want to be on top of the parent
    this.guiLeft = parentX;
    this.guiTop = parentY - this.ySize;

    this.tabs.setPosition(this.guiLeft + 4, this.guiTop);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    int sel = this.tabs.selected;
    this.tabs.update(mouseX, mouseY);
    this.tabs.draw(matrices);

    // new selection
    if (sel != this.tabs.selected) {
      this.parent.onTabSelection(this.tabs.selected);
    }
  }

  @Override
  protected void drawGuiContainerForegroundLayer(MatrixStack matrices, int mouseX, int mouseY) {
    // highlighted tooltip
    if (this.tabs.highlighted > -1) {
      BlockPos pos = this.tabData.get(this.tabs.highlighted);
      BlockState state = Minecraft.getInstance().player.getEntityWorld().getBlockState(pos);
      ItemStack stack = new ItemStack(state.getBlock(), 1);

      // the origin has been translated to the top left of this gui rather than the screen, so we have to adjust
      this.renderTooltip(matrices, Lists.newArrayList(stack.getDisplayName()), mouseX - this.guiLeft, mouseY - this.guiTop);
    }
  }
}
