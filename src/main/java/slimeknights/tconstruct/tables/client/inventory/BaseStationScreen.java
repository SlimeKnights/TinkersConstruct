package slimeknights.tconstruct.tables.client.inventory;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.tables.block.ITinkerStationBlock;
import slimeknights.tconstruct.tables.client.inventory.module.TinkerTabsScreen;
import slimeknights.tconstruct.tables.inventory.BaseStationContainer;
import slimeknights.tconstruct.tables.network.StationTabPacket;

public class BaseStationScreen<TILE extends BlockEntity & Inventory, CONTAINER extends BaseStationContainer<TILE>> extends MultiModuleScreen<CONTAINER> {
  protected static final Text COMPONENT_WARNING = Util.makeTranslation("gui", "warning");
  protected static final Text COMPONENT_ERROR = Util.makeTranslation("gui", "error");

  public static final Identifier BLANK_BACK = Util.getResource("textures/gui/blank.png");

  protected final TILE tile;
  protected final CONTAINER handler;
  protected TinkerTabsScreen tabsScreen;

  public BaseStationScreen(CONTAINER container, PlayerInventory playerInventory, Text title) {
    super(container, playerInventory, title);
    this.tile = container.getTile();
    this.handler = container;

    this.tabsScreen = new TinkerTabsScreen(this, container, playerInventory, title);
    this.addModule(this.tabsScreen);

    if (this.tile != null) {
      World world = this.tile.getWorld();

      if (world != null) {
        for (Pair<BlockPos, BlockState> pair : container.stationBlocks) {
          BlockState state = pair.getRight();
          BlockPos blockPos = pair.getLeft();
          ItemStack stack = new ItemStack(state.getBlock()); //.getPickBlock(state, null, world, blockPos, playerInventory.player);
          this.tabsScreen.addTab(stack, blockPos);
        }
      }
    }

    // preselect the correct tab
    for (int i = 0; i < this.tabsScreen.tabData.size(); i++) {
      if (this.tabsScreen.tabData.get(i).equals(this.tile.getPos())) {
        this.tabsScreen.tabs.selected = i;
      }
    }
  }

  public TILE getTileEntity() {
    return this.tile;
  }

  protected void drawIcon(MatrixStack matrices, Slot slot, ElementScreen element) {
    this.client.getTextureManager().bindTexture(Icons.ICONS);
    element.draw(matrices, slot.x + this.cornerX - 1, slot.y + this.cornerY - 1);
  }

  protected void drawIconEmpty(MatrixStack matrices, Slot slot, ElementScreen element) {
    if (slot.hasStack()) {
      return;
    }

    this.drawIcon(matrices, slot, element);
  }

  public void onTabSelection(int selection) {
    if (selection < 0 || selection > this.tabsScreen.tabData.size()) {
      return;
    }

    World world = this.tile.getWorld();

    if (world == null) {
      return;
    }

    BlockPos pos = this.tabsScreen.tabData.get(selection);
    BlockState state = world.getBlockState(pos);

    if (state.getBlock() instanceof ITinkerStationBlock) {
      BlockEntity te = this.tile.getWorld().getBlockEntity(pos);
      TinkerNetwork.getInstance().sendToServer(new StationTabPacket(pos));

      // sound!
      assert this.client != null;
      this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
  }

  public void error(Text message) {
  }

  public void warning(Text message) {
  }

  public void updateDisplay() {
  }
}
