package slimeknights.tconstruct.tables.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import slimeknights.mantle.inventory.MultiModuleContainer;
import slimeknights.tconstruct.tables.block.ITinkerStationBlock;
import slimeknights.tconstruct.tables.client.inventory.TinkerStationScreen;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class TinkerStationContainer<TILE extends TileEntity & IInventory> extends MultiModuleContainer<TILE> {

  public final List<Pair<BlockPos, BlockState>> tinkerStationBlocks;

  public TinkerStationContainer(ContainerType<?> containerType, int id, @Nullable PlayerInventory inv, @Nullable TILE tile) {
    super(containerType, id, inv, tile);

    this.tinkerStationBlocks = Lists.newLinkedList();

    if (tile != null && tile.getWorld() != null) {
      this.detectedTinkerStationParts(tile.getWorld(), tile.getPos());
    }
  }

  public void detectedTinkerStationParts(World world, BlockPos start) {
    Set<Integer> found = Sets.newHashSet();
    Set<BlockPos> visited = Sets.newHashSet();

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
      Integer number = tinker.getType().getSort();

      if (!found.contains(number)) {
        found.add(number);
        this.tinkerStationBlocks.add(Pair.of(pos, state));
      }
    }

    // sort the found blocks by priority
    TinkerStationContainer.TinkerBlockComp comp = new TinkerStationContainer.TinkerBlockComp();
    this.tinkerStationBlocks.sort(comp);
  }

  public void updateGUI() {
    if (this.tile != null) {
      if (this.tile.getWorld() != null) {
        if (tile.getWorld().isRemote) {
          Minecraft.getInstance().execute(TinkerStationContainer::clientGuiUpdate);
        }
      }
    }
  }

  /**
   * Tells the client to display the LOCALIZED error message
   */
  public void error(final String message) {
    if (this.tile != null) {
      if (this.tile.getWorld() != null) {
        if (tile.getWorld().isRemote) {
          Minecraft.getInstance().execute(() -> TinkerStationContainer.clientError(message));
        }
      }
    }
  }

  /**
   * Tells the client to display the LOCALIZED warning message
   */
  public void warning(final String message) {
    if (this.tile != null) {
      if (this.tile.getWorld() != null) {
        if (tile.getWorld().isRemote) {
          Minecraft.getInstance().execute(() -> TinkerStationContainer.clientWarning(message));
        }
      }
    }
  }

  @OnlyIn(Dist.CLIENT)
  private static void clientGuiUpdate() {
    Screen screen = Minecraft.getInstance().currentScreen;
    if (screen instanceof TinkerStationScreen) {
      ((TinkerStationScreen) screen).updateDisplay();
    }
  }

  @OnlyIn(Dist.CLIENT)
  private static void clientError(String message) {
    Screen screen = Minecraft.getInstance().currentScreen;
    if (screen instanceof TinkerStationScreen) {
      ((TinkerStationScreen) screen).error(message);
    }
  }

  @OnlyIn(Dist.CLIENT)
  private static void clientWarning(String message) {
    Screen screen = Minecraft.getInstance().currentScreen;
    if (screen instanceof TinkerStationScreen) {
      ((TinkerStationScreen) screen).warning(message);
    }
  }

  private static class TinkerBlockComp implements Comparator<Pair<BlockPos, BlockState>> {

    @Override
    public int compare(Pair<BlockPos, BlockState> o1, Pair<BlockPos, BlockState> o2) {
      BlockState s1 = o1.getRight();
      BlockState s2 = o2.getRight();

      return ((ITinkerStationBlock) s2.getBlock()).getType().getSort() - ((ITinkerStationBlock) s1.getBlock()).getType().getSort();
    }
  }
}
