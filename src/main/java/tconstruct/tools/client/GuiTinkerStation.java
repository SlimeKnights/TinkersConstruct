package tconstruct.tools.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import tconstruct.TConstruct;
import tconstruct.common.client.gui.GuiElement;
import tconstruct.common.client.gui.GuiMultiModule;
import tconstruct.library.Util;
import tconstruct.library.mantle.IInventoryGui;
import tconstruct.tools.block.ITinkerStationBlock;
import tconstruct.tools.client.module.GuiTinkerTabs;
import tconstruct.common.inventory.ContainerMultiModule;
import tconstruct.tools.network.TinkerStationTabPacket;

@SideOnly(Side.CLIENT)
// Takes care of the tinker station pseudo-multiblock
public class GuiTinkerStation extends GuiMultiModule {
  public static final ResourceLocation ICONS = Util.getResource("textures/gui/icons.png");

  public static final GuiElement ICON_Pickaxe = new GuiElement(18*0, 18*13, 18, 18, 256, 256);
  public static final GuiElement ICON_Dust = new GuiElement(18*1, 18*13, 18, 18);
  public static final GuiElement ICON_Lapis = new GuiElement(18*2, 18*13, 18, 18);
  public static final GuiElement ICON_Ingot = new GuiElement(18*3, 18*13, 18, 18);
  public static final GuiElement ICON_Gem = new GuiElement(18*4, 18*13, 18, 18);
  public static final GuiElement ICON_Quartz = new GuiElement(18*5, 18*13, 18, 18);
  public static final GuiElement ICON_Anvil = new GuiElement(18*3, 0, 18, 18);
  public static final GuiElement ICON_Pattern = new GuiElement(18*3,18*12,18,18);


  protected final ContainerMultiModule container;

  protected GuiTinkerTabs tinkerTabs;
  private final World world;

  public GuiTinkerStation(World world, BlockPos pos, ContainerMultiModule container) {
    super(container);

    this.world = world;
    this.container = container;

    tinkerTabs = new GuiTinkerTabs(this, container);
    addModule(tinkerTabs);

    detectedTinkerStationParts(world, pos);
    // preselect the correct tab
    for(int i = 0; i < tinkerTabs.tabData.size(); i++) {
      if(tinkerTabs.tabData.get(i).equals(pos))
        tinkerTabs.tabs.selected = i;
    }
  }

  protected void drawBackground(ResourceLocation background) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    this.mc.getTextureManager().bindTexture(background);
    this.drawTexturedModalRect(cornerX, cornerY, 0, 0, realWidth, realHeight);
  }

  protected void drawIcon(Slot slot, GuiElement element) {
    this.mc.getTextureManager().bindTexture(ICONS);
    element.draw(slot.xDisplayPosition + this.cornerX - 1, slot.yDisplayPosition + this.cornerY - 1);
  }

  public void onTabSelection(int selection) {
    if(selection < 0 || selection > tinkerTabs.tabData.size())
      return;

    BlockPos pos = tinkerTabs.tabData.get(selection);
    IBlockState state = world.getBlockState(pos);
    if(state.getBlock() instanceof ITinkerStationBlock) {
      TileEntity te = world.getTileEntity(pos);
      if(te instanceof IInventoryGui) {
        TConstruct.network.network.sendToServer(new TinkerStationTabPacket(pos));
      }
      //Minecraft.getMinecraft().thePlayer.openGui(TConstruct.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
      //state.getBlock().onBlockActivated(world, tinkerTabs.tabData.get(selection), state, Minecraft.getMinecraft().thePlayer, EnumFacing.UP, 0, 0, 0);
    }
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
