package slimeknights.tconstruct.tables.client.inventory.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.TabsWidget;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.tables.client.inventory.BaseTabbedScreen;

import java.util.List;

public class TinkerTabsWidget implements Widget, GuiEventListener, NarratableEntry {
  private static final ResourceLocation TAB_IMAGE = TConstruct.getResource("textures/gui/icons.png");
  protected static final ElementScreen TAB_ELEMENT = new ElementScreen(0, 18, 26, 30, 256, 256);
  protected static final ElementScreen ACTIVE_TAB_L_ELEMENT = new ElementScreen(26, 18, 26, 30, 256, 256);
  protected static final ElementScreen ACTIVE_TAB_C_ELEMENT = new ElementScreen(52, 18, 26, 30, 256, 256);
  protected static final ElementScreen ACTIVE_TAB_R_ELEMENT = new ElementScreen(78, 18, 26, 30, 256, 256);

  private final int leftPos;
  private final int topPos;
  private int imageWidth;
  private final int imageHeight;

  public final TabsWidget tabs;
  public final List<BlockPos> tabData = Lists.newArrayList();
  private final BaseTabbedScreen<?, ?> parent;

  public TinkerTabsWidget(BaseTabbedScreen<?, ?> parent) {
    this.parent = parent;

    this.imageWidth = ACTIVE_TAB_C_ELEMENT.w;
    this.imageHeight = ACTIVE_TAB_C_ELEMENT.h;

    this.tabs = new TabsWidget(parent, TAB_ELEMENT, TAB_ELEMENT, TAB_ELEMENT, ACTIVE_TAB_L_ELEMENT, ACTIVE_TAB_C_ELEMENT, ACTIVE_TAB_R_ELEMENT);
    this.tabs.tabsResource = TAB_IMAGE;

    this.leftPos = parent.cornerX;
    this.topPos = parent.cornerY - this.imageHeight;

    this.tabs.setPosition(this.leftPos + 4, this.topPos);
  }

  public void addTab(ItemStack icon, BlockPos data) {
    this.tabData.add(data);
    this.tabs.addTab(icon);
    this.imageWidth += ACTIVE_TAB_C_ELEMENT.w + this.tabs.spacing;
  }

  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    return mouseX >= this.leftPos - 1 && mouseX < this.guiRight() + 1 && mouseY >= this.topPos - 1 && mouseY < this.guiBottom() + 1;
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
    if (isMouseOver(mouseX, mouseY)) {
      this.tabs.handleMouseClicked((int) mouseX, (int) mouseY, mouseButton);
      return true;
    }

    return false;
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
    this.tabs.handleMouseReleased();

    return true;
  }

  public int guiRight() {
    return this.leftPos + this.imageWidth;
  }

  public int guiBottom() {
    return this.topPos + this.imageHeight;
  }

  public Rect2i getArea() {
    return new Rect2i(this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
  }

  @Override
  public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    int sel = this.tabs.selected;
    this.tabs.update(mouseX, mouseY);
    this.tabs.draw(poseStack);

    // new selection
    if (sel != this.tabs.selected) {
      this.parent.onTabSelection(this.tabs.selected);
    }

    renterTooltip(poseStack, mouseX, mouseY);
  }

  protected void renterTooltip(PoseStack poseStack, int mouseX, int mouseY) {
    // highlighted tooltip
    Level world = parent.getMinecraft().level;
    if (this.tabs.highlighted > -1 && world != null) {
      BlockPos pos = this.tabData.get(this.tabs.highlighted);
      Component title;
      BlockEntity te = world.getBlockEntity(pos);
      if (te instanceof MenuProvider) {
        title = ((MenuProvider)te).getDisplayName();
      } else {
        title = world.getBlockState(pos).getBlock().getName();
      }

      // TODO: renderComponentTooltip->renderTooltip
      parent.renderComponentTooltip(poseStack, Lists.newArrayList(title), mouseX, mouseY);
    }
  }

  @Override
  public NarrationPriority narrationPriority() {
    return NarrationPriority.NONE;
  }

  @Override
  public void updateNarration(NarrationElementOutput pNarrationElementOutput) {}
}
