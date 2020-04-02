package slimeknights.tconstruct.tables.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import slimeknights.mantle.inventory.MultiModuleContainer;
import slimeknights.tconstruct.tables.block.ITinkerStationBlock;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class TinkerStationContainer<TILE extends TileEntity & IInventory> extends MultiModuleContainer<TILE> {

  public final boolean hasCraftingStation;
  public final List<Pair<BlockPos, BlockState>> tinkerStationBlocks;

  public TinkerStationContainer(ContainerType<?> containerType, int id, @Nullable PlayerInventory inv, TILE tile) {
    super(containerType, id, inv, tile);

    this.tinkerStationBlocks = Lists.newLinkedList();
    if (tile == null) {
      this.hasCraftingStation = false;
    } else {
      if (tile.getWorld() == null) {
        this.hasCraftingStation = false;
      } else {
        this.hasCraftingStation = this.detectedTinkerStationParts(tile.getWorld(), tile.getPos());
      }
    }
  }

  @SuppressWarnings("unchecked")
  public <TE extends TileEntity> TE getTinkerTE(Class<TE> clazz) {
    if (this.tile == null) {
      return null;
    } else {
      if (this.tile.getWorld() == null) {
        return null;
      }
      for (Pair<BlockPos, BlockState> pair : this.tinkerStationBlocks) {
        TileEntity te = this.tile.getWorld().getTileEntity(pair.getLeft());

        if (te != null && clazz.isAssignableFrom(te.getClass())) {
          return (TE) te;
        }
      }
    }
    return null;
  }

  public boolean detectedTinkerStationParts(World world, BlockPos start) {
    Set<Integer> found = Sets.newHashSet();
    Set<BlockPos> visited = Sets.newHashSet();
    boolean hasMaster = false;

    // BFS for related blocks
    Queue<BlockPos> queue = Queues.newPriorityQueue();
    queue.add(start);

    while (!queue.isEmpty()) {
      BlockPos pos = queue.poll();
      // already visited between adding and call
      if (visited.contains(pos)) {
        continue;
      }

      BlockState state = world.getBlockState(pos);
      if (!(state.getBlock() instanceof ITinkerStationBlock)) {
        // not a valid block for us
        continue;
      }

      // found a part, add surrounding blocks that haven't been visited yet
      if (!visited.contains(pos.north())) {
        queue.add(pos.north());
      }
      if (!visited.contains(pos.east())) {
        queue.add(pos.east());
      }
      if (!visited.contains(pos.south())) {
        queue.add(pos.south());
      }
      if (!visited.contains(pos.west())) {
        queue.add(pos.west());
      }
      // add to visited
      visited.add(pos);

      // save the thing
      ITinkerStationBlock tinker = (ITinkerStationBlock) state.getBlock();
      Integer number = tinker.getGuiNumber(state);

      if (!found.contains(number)) {
        found.add(number);
        tinkerStationBlocks.add(Pair.of(pos, state));

        if (tinker.isMaster()) {
          hasMaster = true;
        }
      }
    }

    // sort the found blocks by priority
    TinkerStationContainer.TinkerBlockComp comp = new TinkerStationContainer.TinkerBlockComp();
    tinkerStationBlocks.sort(comp);

    return hasMaster;
  }

  private static class TinkerBlockComp implements Comparator<Pair<BlockPos, BlockState>> {

    @Override
    public int compare(Pair<BlockPos, BlockState> o1, Pair<BlockPos, BlockState> o2) {
      BlockState s1 = o1.getRight();
      BlockState s2 = o2.getRight();

      return ((ITinkerStationBlock) s2.getBlock()).getGuiNumber(s2) - ((ITinkerStationBlock) s1.getBlock()).getGuiNumber(s1);
    }
  }
}
