package slimeknights.tconstruct.tools.common.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Pair;

import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.mantle.client.gui.GuiMultiModule;
import slimeknights.mantle.common.IInventoryGui;
import slimeknights.mantle.inventory.ContainerMultiModule;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.tools.common.block.ITinkerStationBlock;
import slimeknights.tconstruct.tools.common.client.module.GuiTinkerTabs;
import slimeknights.tconstruct.tools.common.inventory.ContainerTinkerStation;
import slimeknights.tconstruct.tools.common.network.TinkerStationTabPacket;

@SideOnly(Side.CLIENT)
// Takes care of the tinker station pseudo-multiblock
public class GuiTinkerStation extends GuiMultiModule {

  public static final ResourceLocation BLANK_BACK = Util.getResource("textures/gui/blank.png");

  protected final ContainerMultiModule<?> container;

  protected GuiTinkerTabs tinkerTabs;
  private final World world;

  public GuiTinkerStation(World world, BlockPos pos, ContainerTinkerStation<?> container) {
    super(container);

    this.world = world;
    this.container = container;

    tinkerTabs = new GuiTinkerTabs(this, container);
    addModule(tinkerTabs);

    // add tab data
    if(container.hasCraftingStation) {
      for(Pair<BlockPos, IBlockState> pair : container.tinkerStationBlocks) {
        IBlockState state = pair.getRight();
        BlockPos blockPos = pair.getLeft();
        ItemStack stack = state.getBlock().getDrops(world, blockPos, state, 0).get(0);
        tinkerTabs.addTab(stack, blockPos);
      }
    }

    // preselect the correct tab
    for(int i = 0; i < tinkerTabs.tabData.size(); i++) {
      if(tinkerTabs.tabData.get(i).equals(pos)) {
        tinkerTabs.tabs.selected = i;
      }
    }
  }

  protected void drawIcon(Slot slot, GuiElement element) {
    this.mc.getTextureManager().bindTexture(Icons.ICON);
    element.draw(slot.xPos + this.cornerX - 1, slot.yPos + this.cornerY - 1);
  }

  protected void drawIconEmpty(Slot slot, GuiElement element) {
    if(slot.getHasStack()) {
      return;
    }
    drawIcon(slot, element);
  }

  public void onTabSelection(int selection) {
    if(selection < 0 || selection > tinkerTabs.tabData.size()) {
      return;
    }

    BlockPos pos = tinkerTabs.tabData.get(selection);
    IBlockState state = world.getBlockState(pos);
    if(state.getBlock() instanceof ITinkerStationBlock) {
      TileEntity te = world.getTileEntity(pos);
      if(te instanceof IInventoryGui) {
        TinkerNetwork.sendToServer(new TinkerStationTabPacket(pos));
      }

      // sound!
      mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
  }

  public void error(String message) {
  }

  public void warning(String message) {
  }

  public void updateDisplay() {
  }
}
