package tconstruct.tools.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import tconstruct.tools.block.ITinkerStationBlock;
import tconstruct.tools.client.module.GuiTinkerTabs;
import tconstruct.tools.inventory.ContainerMultiModule;

@SideOnly(Side.CLIENT)
// Takes care of the tinker station pseudo-multiblock
public class GuiTinkerStation extends GuiMultiModule {

  protected GuiTinkerTabs tinkerTabs;

  public GuiTinkerStation(World world, BlockPos pos, ContainerMultiModule container) {
    super(container);

    tinkerTabs = new GuiTinkerTabs(this, container);
    addModule(tinkerTabs);

    detectedTinkerStationParts(world, pos);
    // preselect the correct tab
    for(int i = 0; i < tinkerTabs.tabData.size(); i++) {
      if(tinkerTabs.tabData.get(i).equals(pos))
        tinkerTabs.tabs.selected = i;
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }

  public void detectedTinkerStationParts(World world, BlockPos start) {
    Set<Integer> found = Sets.newHashSet();
    List<BlockPos> foundBlocks = Lists.newLinkedList();
    Set<BlockPos> visited = Sets.newHashSet();
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
        foundBlocks.add(pos);
        if(tinker.isMaster(state))
          hasMaster = true;
      }
    }

    // all blocks found, at least 2 and is a master present?
    if(!hasMaster || foundBlocks.size() < 2)
      return;

    // sort all the blocks according to their number
    TinkerBlockComp comp = new TinkerBlockComp(world);
    foundBlocks.sort(comp);

    for(BlockPos pos : foundBlocks) {
      IBlockState state = world.getBlockState(pos);
      ItemStack stack = state.getBlock().getDrops(world, pos, state, 0).get(0);
      tinkerTabs.addTab(stack, pos);
    }
  }

  private static class TinkerBlockComp implements Comparator<BlockPos> {
    private final World world;

    private TinkerBlockComp(World world) {
      this.world = world;
    }

    @Override
    public int compare(BlockPos o1, BlockPos o2) {
      IBlockState s1 = world.getBlockState(o1);
      IBlockState s2 = world.getBlockState(o2);


      return ((ITinkerStationBlock)s1.getBlock()).getGuiNumber(s1) - ((ITinkerStationBlock)s2.getBlock()).getGuiNumber(s2);
    }
  }
}
