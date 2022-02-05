package slimeknights.tconstruct.tables.client.inventory.module;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.ModuleScreen;
import slimeknights.mantle.client.screen.TabsWidget;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.tables.client.inventory.BaseTabbedScreen;

import java.util.List;

public class TinkerTabsScreen extends ModuleScreen {
  private static final ResourceLocation TAB_IMAGE = TConstruct.getResource("textures/gui/icons.png");
  protected static final ElementScreen TAB_ELEMENT = new ElementScreen(0, 18, 26, 30, 256, 256);
  protected static final ElementScreen ACTIVE_TAB_L_ELEMENT = new ElementScreen(26, 18, 26, 30, 256, 256);
  protected static final ElementScreen ACTIVE_TAB_C_ELEMENT = new ElementScreen(52, 18, 26, 30, 256, 256);
  protected static final ElementScreen ACTIVE_TAB_R_ELEMENT = new ElementScreen(78, 18, 26, 30, 256, 256);

  public TabsWidget tabs;
  public List<BlockPos> tabData;

  public final BaseTabbedScreen parent;

  public TinkerTabsScreen(BaseTabbedScreen parent, AbstractContainerMenu container, Inventory playerInventory, Component title) {
    super(parent, container, playerInventory, title, false, false);

    this.parent = parent;

    this.imageWidth = ACTIVE_TAB_C_ELEMENT.w;
    this.imageHeight = ACTIVE_TAB_C_ELEMENT.h;

    this.tabs = new TabsWidget(parent, TAB_ELEMENT, TAB_ELEMENT, TAB_ELEMENT, ACTIVE_TAB_L_ELEMENT, ACTIVE_TAB_C_ELEMENT, ACTIVE_TAB_R_ELEMENT);
    this.tabs.tabsResource = TAB_IMAGE;
    this.tabData = Lists.newArrayList();
  }

  public void addTab(ItemStack icon, BlockPos data) {
    this.tabData.add(data);
    this.tabs.addTab(icon);
    this.imageWidth += ACTIVE_TAB_C_ELEMENT.w + this.tabs.spacing;
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
    this.leftPos = parentX;
    this.topPos = parentY - this.imageHeight;

    this.tabs.setPosition(this.leftPos + 4, this.topPos);
  }

  @Override
  protected void renderBg(PoseStack matrices, float partialTicks, int mouseX, int mouseY) {
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    int sel = this.tabs.selected;
    this.tabs.update(mouseX, mouseY);
    this.tabs.draw(matrices);

    // new selection
    if (sel != this.tabs.selected) {
      this.parent.onTabSelection(this.tabs.selected);
    }
  }

  @Override
  protected void renderLabels(PoseStack matrices, int mouseX, int mouseY) {
    // highlighted tooltip
    Level world = Minecraft.getInstance().level;
    if (this.tabs.highlighted > -1 && world != null) {
      BlockPos pos = this.tabData.get(this.tabs.highlighted);
      Component title;
      BlockEntity te = world.getBlockEntity(pos);
      if (te instanceof MenuProvider) {
        title = ((MenuProvider)te).getDisplayName();
      } else {
        title = world.getBlockState(pos).getBlock().getName();
      }

      // the origin has been translated to the top left of this gui rather than the screen, so we have to adjust
      // TODO: renderComponentTooltip->renderTooltip
      this.renderComponentTooltip(matrices, Lists.newArrayList(title), mouseX - this.leftPos, mouseY - this.topPos);
    }
  }
}
