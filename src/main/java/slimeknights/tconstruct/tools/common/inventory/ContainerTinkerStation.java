package slimeknights.tconstruct.tools.common.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import slimeknights.mantle.inventory.ContainerMultiModule;
import slimeknights.tconstruct.tools.common.block.BlockToolTable;
import slimeknights.tconstruct.tools.common.block.ITinkerStationBlock;
import slimeknights.tconstruct.tools.common.client.GuiTinkerStation;

public class ContainerTinkerStation<T extends TileEntity & IInventory> extends ContainerMultiModule<T> {

  public final boolean hasCraftingStation;
  public final List<Pair<BlockPos, IBlockState>> tinkerStationBlocks;

  public ContainerTinkerStation(T tile) {
    super(tile);

    tinkerStationBlocks = Lists.newLinkedList();
    hasCraftingStation = detectedTinkerStationParts(tile.getWorld(), tile.getPos());
  }

  @SuppressWarnings("unchecked")
  public <TE extends TileEntity> TE getTinkerTE(Class<TE> clazz) {
    for(Pair<BlockPos, IBlockState> pair : tinkerStationBlocks) {
      TileEntity te = this.world.getTileEntity(pair.getLeft());

      if(te != null && clazz.isAssignableFrom(te.getClass())) {
        return (TE) te;
      }
    }
    return null;
  }

  public boolean detectedTinkerStationParts(World world, BlockPos start) {
    Set<Integer> found = Sets.newHashSet();
    Set<BlockPos> visited = Sets.newHashSet();
    Set<IBlockState> ret = Sets.newHashSet();
    boolean hasMaster = false;

    // BFS for related blocks
    Queue<BlockPos> queue = Queues.newPriorityQueue();
    queue.add(start);

    while(!queue.isEmpty()) {
      BlockPos pos = queue.poll();
      // already visited between adding and call
      if(visited.contains(pos)) {
        continue;
      }

      IBlockState state = world.getBlockState(pos);
      if(!(state.getBlock() instanceof ITinkerStationBlock)) {
        // not a valid block for us
        continue;
      }

      // found a part, add surrounding blocks that haven't been visited yet
      if(!visited.contains(pos.north())) {
        queue.add(pos.north());
      }
      if(!visited.contains(pos.east())) {
        queue.add(pos.east());
      }
      if(!visited.contains(pos.south())) {
        queue.add(pos.south());
      }
      if(!visited.contains(pos.west())) {
        queue.add(pos.west());
      }
      // add to visited
      visited.add(pos);

      // save the thing
      ITinkerStationBlock tinker = (ITinkerStationBlock) state.getBlock();
      Integer number = tinker.getGuiNumber(state);
      if(!found.contains(number)) {
        found.add(number);
        tinkerStationBlocks.add(Pair.of(pos, state));
        ret.add(state);
        if(state.getProperties().containsKey(BlockToolTable.TABLES)) {
          BlockToolTable.TableTypes type = state.getValue(BlockToolTable.TABLES);
          if(type != null && type == BlockToolTable.TableTypes.CraftingStation) {
            hasMaster = true;
          }
        }
      }
    }

    // sort the found blocks by priority
    TinkerBlockComp comp = new TinkerBlockComp();
    tinkerStationBlocks.sort(comp);

    /*
    if(!hasMaster || foundBlocks.size() < 2) {

      // sort all the blocks according to their number
      TinkerBlockComp comp = new TinkerBlockComp(world);
      foundBlocks.sort(comp);

      for(BlockPos pos : foundBlocks) {
        IBlockState state = world.getBlockState(pos);
        ItemStack stack = state.getBlock().getDrops(world, pos, state, 0).get(0);
        tinkerTabs.addTab(stack, pos);
      }
    }*/

    return hasMaster;
  }

  /** Tells the client to take the current state and update its info displays */
  public void updateGUI() {
    if(tile.getWorld().isRemote) {
      Minecraft.getMinecraft().addScheduledTask(new Runnable() {
        @Override
        public void run() {
          ContainerTinkerStation.clientGuiUpdate();
        }
      });
    }
  }

  /** Tells the client to display the LOCALIZED error message */
  public void error(final String message) {
    if(tile.getWorld().isRemote) {
      Minecraft.getMinecraft().addScheduledTask(new Runnable() {
        @Override
        public void run() {
          ContainerTinkerStation.clientError(message);
        }
      });
    }
  }

  /** Tells the client to display the LOCALIZED warning message */
  public void warning(final String message) {
    if(tile.getWorld().isRemote) {
      Minecraft.getMinecraft().addScheduledTask(new Runnable() {
        @Override
        public void run() {
          ContainerTinkerStation.clientWarning(message);
        }
      });
    }
  }

  @SideOnly(Side.CLIENT)
  private static void clientGuiUpdate() {
    GuiScreen screen = Minecraft.getMinecraft().currentScreen;
    if(screen instanceof GuiTinkerStation) {
      ((GuiTinkerStation) screen).updateDisplay();
    }
  }

  @SideOnly(Side.CLIENT)
  private static void clientError(String message) {
    GuiScreen screen = Minecraft.getMinecraft().currentScreen;
    if(screen instanceof GuiTinkerStation) {
      ((GuiTinkerStation) screen).error(message);
    }
  }

  @SideOnly(Side.CLIENT)
  private static void clientWarning(String message) {
    GuiScreen screen = Minecraft.getMinecraft().currentScreen;
    if(screen instanceof GuiTinkerStation) {
      ((GuiTinkerStation) screen).warning(message);
    }
  }

  private static class TinkerBlockComp implements Comparator<Pair<BlockPos, IBlockState>> {

    @Override
    public int compare(Pair<BlockPos, IBlockState> o1, Pair<BlockPos, IBlockState> o2) {
      IBlockState s1 = o1.getRight();
      IBlockState s2 = o2.getRight();

      return ((ITinkerStationBlock) s2.getBlock()).getGuiNumber(s2) - ((ITinkerStationBlock) s1.getBlock()).getGuiNumber(s1);
    }
  }
}
