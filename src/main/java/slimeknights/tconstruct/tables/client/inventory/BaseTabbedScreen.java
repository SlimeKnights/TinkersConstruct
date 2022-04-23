package slimeknights.tconstruct.tables.client.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.tables.block.ITabbedBlock;
import slimeknights.tconstruct.tables.client.inventory.module.SideInventoryScreen;
import slimeknights.tconstruct.tables.client.inventory.module.TinkerTabsScreen;
import slimeknights.tconstruct.tables.menu.TabbedContainerMenu;
import slimeknights.tconstruct.tables.menu.module.SideInventoryContainer;
import slimeknights.tconstruct.tables.network.StationTabPacket;

import java.util.List;

public class BaseTabbedScreen<TILE extends BlockEntity, CONTAINER extends TabbedContainerMenu<TILE>> extends MultiModuleScreen<CONTAINER> {
  protected static final Component COMPONENT_WARNING = TConstruct.makeTranslation("gui", "warning");
  protected static final Component COMPONENT_ERROR = TConstruct.makeTranslation("gui", "error");

  public static final ResourceLocation BLANK_BACK = TConstruct.getResource("textures/gui/blank.png");

  protected final TILE tile;
  protected final CONTAINER container;
  protected TinkerTabsScreen tabsScreen;

  public BaseTabbedScreen(CONTAINER container, Inventory playerInventory, Component title) {
    super(container, playerInventory, title);
    this.tile = container.getTile();
    this.container = container;
  }

  @Override
  protected void init() {
    super.init();
    this.tabsScreen = addRenderableWidget(new TinkerTabsScreen(this));

    if (this.tile != null) {
      Level world = this.tile.getLevel();

      if (world != null) {
        for (Pair<BlockPos, BlockState> pair : container.stationBlocks) {
          BlockState state = pair.getRight();
          BlockPos blockPos = pair.getLeft();
          ItemStack stack = state.getBlock().getCloneItemStack(state, null, world, blockPos, this.getMinecraft().player);
          this.tabsScreen.addTab(stack, blockPos);
        }
      }

      // preselect the correct tab
      for (int i = 0; i < this.tabsScreen.tabData.size(); i++) {
        if (this.tabsScreen.tabData.get(i).equals(this.tile.getBlockPos())) {
          this.tabsScreen.tabs.selected = i;
        }
      }
    }
  }

  public TILE getTileEntity() {
    return this.tile;
  }

  protected void drawIcon(PoseStack matrices, Slot slot, ElementScreen element) {
    RenderSystem.setShaderTexture(0, Icons.ICONS);
    element.draw(matrices, slot.x + this.cornerX - 1, slot.y + this.cornerY - 1);
  }

  protected void drawIconEmpty(PoseStack matrices, Slot slot, ElementScreen element) {
    if (slot.hasItem()) {
      return;
    }

    this.drawIcon(matrices, slot, element);
  }

  public void onTabSelection(int selection) {
    if (selection < 0 || selection > this.tabsScreen.tabData.size()) {
      return;
    }

    Level world = this.tile.getLevel();

    if (world == null) {
      return;
    }

    BlockPos pos = this.tabsScreen.tabData.get(selection);
    BlockState state = world.getBlockState(pos);

    if (state.getBlock() instanceof ITabbedBlock) {
//      BlockEntity te = this.tile.getLevel().getBlockEntity(pos);
      TinkerNetwork.getInstance().sendToServer(new StationTabPacket(pos));

      // sound!
      assert this.minecraft != null;
      this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
  }

  public void error(Component message) {
  }

  public void warning(Component message) {
  }

  public void updateDisplay() {
  }

  protected void addChestSideInventory(Inventory inventory) {
    SideInventoryContainer<?> sideInventoryContainer = container.getSubContainer(SideInventoryContainer.class);
    if (sideInventoryContainer != null) {
      // no title if missing one
      Component sideInventoryName = TextComponent.EMPTY;
      BlockEntity te = sideInventoryContainer.getTile();
      if (te instanceof MenuProvider) {
        sideInventoryName = ((MenuProvider) te).getDisplayName();
      }

      this.addModule(new SideInventoryScreen<>(this, sideInventoryContainer, inventory, sideInventoryName, sideInventoryContainer.getSlotCount(), sideInventoryContainer.getColumns()));
    }
  }

  @Override
  public List<Rect2i> getModuleAreas() {
    List<Rect2i> areas = super.getModuleAreas();
    areas.add(tabsScreen.getArea());
    return areas;
  }

  @Override
  protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton) {
    return super.hasClickedOutside(mouseX, mouseY, guiLeft, guiTop, mouseButton)
      && !tabsScreen.isMouseOver(mouseX, mouseY);
  }
}
