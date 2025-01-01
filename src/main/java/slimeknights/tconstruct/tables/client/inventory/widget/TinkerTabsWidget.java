package slimeknights.tconstruct.tables.client.inventory.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.TabsWidget;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.tables.block.ITabbedBlock;
import slimeknights.tconstruct.tables.client.inventory.BaseTabbedScreen;
import slimeknights.tconstruct.tables.menu.TabbedContainerMenu;
import slimeknights.tconstruct.tables.network.StationTabPacket;

import java.util.List;

public class TinkerTabsWidget implements Renderable, GuiEventListener, NarratableEntry {
  private static final ResourceLocation TAB_IMAGE = TConstruct.getResource("textures/gui/icons.png");
  protected static final ElementScreen TAB_ELEMENT = new ElementScreen(TAB_IMAGE, 0, 18, 26, 30, 256, 256);
  protected static final ElementScreen ACTIVE_TAB_L_ELEMENT = new ElementScreen(TAB_IMAGE, 26, 18, 26, 30, 256, 256);
  protected static final ElementScreen ACTIVE_TAB_C_ELEMENT = new ElementScreen(TAB_IMAGE, 52, 18, 26, 30, 256, 256);
  protected static final ElementScreen ACTIVE_TAB_R_ELEMENT = new ElementScreen(TAB_IMAGE, 78, 18, 26, 30, 256, 256);

  private final int leftPos;
  private final int topPos;
  private final int imageWidth;
  private final int imageHeight;

  private final TabsWidget tabs;
  private final List<BlockPos> tabData;
  private final BaseTabbedScreen<?, ?> parent;

  public TinkerTabsWidget(BaseTabbedScreen<?, ?> parent) {
    this.parent = parent;

    var tabs = collectTabs(this.parent.getMinecraft(), this.parent.getMenu());

    this.tabs = new TabsWidget(parent, TAB_ELEMENT, TAB_ELEMENT, TAB_ELEMENT, ACTIVE_TAB_L_ELEMENT, ACTIVE_TAB_C_ELEMENT, ACTIVE_TAB_R_ELEMENT);

    int count = tabs.size();
    this.imageWidth = count * ACTIVE_TAB_C_ELEMENT.w + (count - 1) * this.tabs.spacing;
    this.imageHeight = ACTIVE_TAB_C_ELEMENT.h;

    this.leftPos = parent.cornerX + 4;
    this.topPos = parent.cornerY - this.imageHeight;

    this.tabs.setPosition(this.leftPos, this.topPos);

    tabs.stream().map(Pair::getLeft).forEach(this.tabs::addTab);
    tabData = tabs.stream().map(Pair::getRight).toList();

    // preselect the correct tab
    BlockEntity blockEntity = this.parent.getTileEntity();
    if (blockEntity != null)
      selectTabForPos(blockEntity.getBlockPos());
  }

  private static List<Pair<ItemStack, BlockPos>> collectTabs(Minecraft minecraft, TabbedContainerMenu<?> menu) {
    List<Pair<ItemStack, BlockPos>> tabs = Lists.newArrayList();

    Level level = minecraft.level;
    if (level != null) {
      for (Pair<BlockPos, BlockState> pair : menu.stationBlocks) {
        BlockState state = pair.getRight();
        BlockPos blockPos = pair.getLeft();
        ItemStack stack = state.getBlock().getCloneItemStack(state, null, level, blockPos, minecraft.player);
        tabs.add(Pair.of(stack, blockPos));
      }
    }
    return tabs;
  }

  private void selectTabForPos(BlockPos pos) {
    for (int i = 0; i < this.tabData.size(); i++) {
      if (this.tabData.get(i).equals(pos)) {
        this.tabs.selected = i;
        return;
      }
    }
  }

  private void onNewTabSelection(BlockPos pos) {
    Level level = this.parent.getMinecraft().level;

    if (level != null) {
      BlockState state = level.getBlockState(pos);
      if (state.getBlock() instanceof ITabbedBlock) {
        TinkerNetwork.getInstance().sendToServer(new StationTabPacket(pos));

        // sound!
        this.parent.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      }
    }
  }

  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    return mouseX >= this.leftPos - 1 && mouseX < this.guiRight() + 1 && mouseY >= this.topPos - 1 && mouseY < this.guiBottom() + 1;
  }

  @Override
  public void setFocused(boolean pFocused) {
    // TODO: do I need to do anything with focusing?
  }

  @Override
  public boolean isFocused() {
    return false;
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
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    int sel = this.tabs.selected;
    this.tabs.update(mouseX, mouseY);
    this.tabs.draw(graphics);

    // new selection
    if (sel != this.tabs.selected) {
      if (0 <= this.tabs.selected && this.tabs.selected < this.tabData.size())
        onNewTabSelection(this.tabData.get(this.tabs.selected));
    }

    renterTooltip(graphics, mouseX, mouseY);
  }

  protected void renterTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
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
      graphics.renderComponentTooltip(parent.font, Lists.newArrayList(title), mouseX, mouseY);
    }
  }

  @Override
  public NarrationPriority narrationPriority() {
    return NarrationPriority.NONE;
  }

  @Override
  public void updateNarration(NarrationElementOutput narrationOutput) {}
}
