package slimeknights.tconstruct.tables.client.inventory.module;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.ModuleScreen;
import slimeknights.mantle.client.screen.TabsWidget;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.tables.client.inventory.BaseStationScreen;

import java.util.List;

public class TinkerTabsScreen extends ModuleScreen {
  private static final Identifier TAB_IMAGE = Util.getResource("textures/gui/icons.png");
  protected static final ElementScreen TAB_ELEMENT = new ElementScreen(0, 18, 26, 30, 256, 256);
  protected static final ElementScreen ACTIVE_TAB_L_ELEMENT = new ElementScreen(26, 18, 26, 30, 256, 256);
  protected static final ElementScreen ACTIVE_TAB_C_ELEMENT = new ElementScreen(52, 18, 26, 30, 256, 256);
  protected static final ElementScreen ACTIVE_TAB_R_ELEMENT = new ElementScreen(78, 18, 26, 30, 256, 256);

  public TabsWidget tabs;
  public List<BlockPos> tabData;

  public final BaseStationScreen parent;

  public TinkerTabsScreen(BaseStationScreen parent, ScreenHandler container, PlayerInventory playerInventory, Text title) {
    super(parent, container, playerInventory, title, false, false);

    this.parent = parent;

    this.backgroundWidth = ACTIVE_TAB_C_ELEMENT.w;
    this.backgroundHeight = ACTIVE_TAB_C_ELEMENT.h;

    this.tabs = new TabsWidget(parent, TAB_ELEMENT, TAB_ELEMENT, TAB_ELEMENT, ACTIVE_TAB_L_ELEMENT, ACTIVE_TAB_C_ELEMENT, ACTIVE_TAB_R_ELEMENT);
    this.tabs.tabsResource = TAB_IMAGE;
    this.tabData = Lists.newArrayList();
  }

  public void addTab(ItemStack icon, BlockPos data) {
    this.tabData.add(data);
    this.tabs.addTab(icon);
    this.backgroundWidth += ACTIVE_TAB_C_ELEMENT.w + this.tabs.spacing;
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
    this.x = parentX;
    this.y = parentY - this.backgroundHeight;

    this.tabs.setPosition(this.x + 4, this.y);
  }

  @Override
  protected void drawBackground(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
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
  protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
    // highlighted tooltip
    World world = MinecraftClient.getInstance().world;
    if (this.tabs.highlighted > -1 && world != null) {
      BlockPos pos = this.tabData.get(this.tabs.highlighted);
      Text title;
      BlockEntity te = world.getBlockEntity(pos);
      if (te instanceof NamedScreenHandlerFactory) {
        title = ((NamedScreenHandlerFactory)te).getDisplayName();
      } else {
        title = world.getBlockState(pos).getBlock().getName();
      }

      // the origin has been translated to the top left of this gui rather than the screen, so we have to adjust
      // TODO: func_243308_b->renderTooltip
      this.renderTooltip(matrices, Lists.newArrayList(title), mouseX - this.x, mouseY - this.y);
    }
  }
}
