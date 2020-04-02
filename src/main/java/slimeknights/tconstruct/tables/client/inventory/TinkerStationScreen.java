package slimeknights.tconstruct.tables.client.inventory;

import net.minecraft.block.BlockState;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.tables.block.ITinkerStationBlock;
import slimeknights.tconstruct.tables.client.inventory.module.TinkerTabsScreen;
import slimeknights.tconstruct.tables.inventory.TinkerStationContainer;
import slimeknights.tconstruct.tables.network.TinkerStationTabPacket;

public class TinkerStationScreen<TILE extends TileEntity & IInventory, CONTAINER extends TinkerStationContainer<TILE>> extends MultiModuleScreen<CONTAINER> {

  public static final ResourceLocation BLANK_BACK = Util.getResource("textures/gui/blank.png");

  protected final TILE tile;
  protected final CONTAINER container;
  protected TinkerTabsScreen tinkerTabsScreen;

  public TinkerStationScreen(CONTAINER container, PlayerInventory playerInventory, ITextComponent title) {
    super(container, playerInventory, title);
    this.tile = container.getTileEntity();
    this.container = container;

    this.tinkerTabsScreen = new TinkerTabsScreen(this, container, playerInventory, title);
    this.addModule(this.tinkerTabsScreen);

    if (container.hasCraftingStation) {
      if (this.tile != null) {
        World world = this.tile.getWorld();

        if (world != null) {
          for (Pair<BlockPos, BlockState> pair : container.tinkerStationBlocks) {
            BlockState state = pair.getRight();
            BlockPos blockPos = pair.getLeft();
            ItemStack stack = state.getBlock().getPickBlock(state, null, world, blockPos, playerInventory.player);
            this.tinkerTabsScreen.addTab(stack, blockPos);
          }
        }
      }
    }

    // preselect the correct tab
    for (int i = 0; i < this.tinkerTabsScreen.tabData.size(); i++) {
      if (this.tinkerTabsScreen.tabData.get(i).equals(this.tile.getPos())) {
        this.tinkerTabsScreen.tabs.selected = i;
      }
    }
  }

  public TILE getTileEntity() {
    return this.tile;
  }

  protected void drawIcon(Slot slot, ElementScreen element) {
    this.minecraft.getTextureManager().bindTexture(Icons.ICON);
    element.draw(slot.xPos + this.cornerX - 1, slot.yPos + this.cornerY - 1);
  }

  protected void drawIconEmpty(Slot slot, ElementScreen element) {
    if (slot.getHasStack()) {
      return;
    }

    this.drawIcon(slot, element);
  }

  public void onTabSelection(int selection) {
    if (selection < 0 || selection > this.tinkerTabsScreen.tabData.size()) {
      return;
    }

    BlockPos pos = this.tinkerTabsScreen.tabData.get(selection);
    BlockState state = this.tile.getWorld().getBlockState(pos);

    if (state.getBlock() instanceof ITinkerStationBlock) {
      TileEntity te = this.tile.getWorld().getTileEntity(pos);
      TinkerNetwork.getInstance().sendToServer(new TinkerStationTabPacket(pos));

      // sound!
      this.minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
  }

  public void error(String message) {
  }

  public void warning(String message) {
  }

  public void updateDisplay() {
  }
}
