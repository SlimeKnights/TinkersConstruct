package slimeknights.tconstruct.tools.tools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import gnu.trove.set.hash.THashSet;

import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import slimeknights.tconstruct.library.client.particle.Particles;
import slimeknights.tconstruct.library.events.TinkerToolEvent;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.AoeToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

public class LumberAxe extends AoeToolCore {

  public static final ImmutableSet<net.minecraft.block.material.Material> effective_materials =
      ImmutableSet.of(net.minecraft.block.material.Material.WOOD,
                      net.minecraft.block.material.Material.GOURD,
                      net.minecraft.block.material.Material.CACTUS);
  public static final float DURABILITY_MODIFIER = 2f;

  public LumberAxe() {
    super(PartMaterialType.handle(TinkerTools.toughToolRod),
          PartMaterialType.head(TinkerTools.broadAxeHead),
          PartMaterialType.head(TinkerTools.largePlate),
          PartMaterialType.extra(TinkerTools.toughBinding));

    // lumberaxe is not a weapon. it's for lumberjacks. Lumberjacks are manly, they're weapons themselves.
    addCategory(Category.HARVEST);

    this.setHarvestLevel("axe", 0);
  }

  @Override
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
    if(this.isInCreativeTab(tab)) {
      addDefaultSubItems(subItems);
      addInfiTool(subItems, "InfiChopper");
    }
  }

  @Override
  public float miningSpeedModifier() {
    return 0.35f; // a bit slower because it breaks whole trees
  }

  @Override
  public float damagePotential() {
    return 1.2f;
  }

  @Override
  public double attackSpeed() {
    return 0.8f;
  }

  @Override
  public boolean isEffective(IBlockState state) {
    return effective_materials.contains(state.getMaterial()) || ItemAxe.EFFECTIVE_ON.contains(state.getBlock());
  }

  @Override
  public float knockback() {
    return 1.5f;
  }

  @Override
  public boolean dealDamage(ItemStack stack, EntityLivingBase player, Entity entity, float damage) {
    boolean hit = super.dealDamage(stack, player, entity, damage);

    if(hit && readyForSpecialAttack(player)) {
      TinkerTools.proxy.spawnAttackParticle(Particles.LUMBERAXE_ATTACK, player, 0.8d);
    }

    return hit;
  }

  @Override
  public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
    if(!ToolHelper.isBroken(itemstack) && ToolHelper.isToolEffective2(itemstack, player.getEntityWorld().getBlockState(pos)) && detectTree(player.getEntityWorld(), pos)) {
      return fellTree(itemstack, pos, player);
    }
    return super.onBlockStartBreak(itemstack, pos, player);
  }

  @Override
  public ImmutableList<BlockPos> getAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin) {
    if(!ToolHelper.isToolEffective2(stack, world.getBlockState(origin))) {
      return ImmutableList.of();
    }
    return ToolHelper.calcAOEBlocks(stack, world, player, origin, 3, 3, 3);
  }

  @Override
  public int[] getRepairParts() {
    return new int[] { 1, 2 };
  }

  @Override
  public float getRepairModifierForPart(int index) {
    return index == 1 ? DURABILITY_MODIFIER : DURABILITY_MODIFIER * 0.625f;
  }

  @Override
  public ToolNBT buildTagData(List<Material> materials) {
    HandleMaterialStats handle = materials.get(0).getStatsOrUnknown(MaterialTypes.HANDLE);
    HeadMaterialStats head = materials.get(1).getStatsOrUnknown(MaterialTypes.HEAD);
    HeadMaterialStats plate = materials.get(2).getStatsOrUnknown(MaterialTypes.HEAD);
    ExtraMaterialStats binding = materials.get(3).getStatsOrUnknown(MaterialTypes.EXTRA);

    ToolNBT data = new ToolNBT();
    data.head(head, plate);
    data.extra(binding);
    data.handle(handle);

    data.attack += 2;
    data.durability *= DURABILITY_MODIFIER;

    return data;
  }

  public static boolean detectTree(World world, BlockPos origin) {
    BlockPos pos = null;
    Stack<BlockPos> candidates = new Stack<>();
    candidates.add(origin);

    while(!candidates.isEmpty()) {
      BlockPos candidate = candidates.pop();
      if((pos == null || candidate.getY() > pos.getY()) && isLog(world, candidate)) {
        pos = candidate.up();
        // go up
        while(isLog(world, pos)) {
          pos = pos.up();
        }
        // check if we still have a way diagonally up
        candidates.add(pos.north());
        candidates.add(pos.east());
        candidates.add(pos.south());
        candidates.add(pos.west());
      }
    }

    // not even one match, so there were no logs.
    if(pos == null) {
      return false;
    }

    // check if there were enough leaves around the last position
    // pos now contains the block above the topmost log
    // we want at least 5 leaves in the surrounding 26 blocks
    int d = 3;
    int o = -1; // -(d-1)/2
    int leaves = 0;
    for(int x = 0; x < d; x++) {
      for(int y = 0; y < d; y++) {
        for(int z = 0; z < d; z++) {
          BlockPos leaf = pos.add(o + x, o + y, o + z);
          IBlockState state = world.getBlockState(leaf);
          if(state.getBlock().isLeaves(state, world, leaf)) {
            if(++leaves >= 5) {
              return true;
            }
          }
        }
      }
    }

    // not enough leaves. sorreh
    return false;
  }

  private static boolean isLog(World world, BlockPos pos) {
    return world.getBlockState(pos).getBlock().isWood(world, pos);
  }

  public static boolean fellTree(ItemStack itemstack, BlockPos start, EntityPlayer player) {
    if(player.getEntityWorld().isRemote) {
      return true;
    }
    TinkerToolEvent.ExtraBlockBreak event = TinkerToolEvent.ExtraBlockBreak.fireEvent(itemstack, player, player.getEntityWorld().getBlockState(start), 3, 3, 3, -1);
    int speed = Math.round((event.width * event.height * event.depth) / 27f);
    if(event.distance > 0) {
      speed = event.distance + 1;
    }

    MinecraftForge.EVENT_BUS.register(new TreeChopTask(itemstack, start, player, speed));
    return true;
  }

  public static class TreeChopTask {

    public final World world;
    public final EntityPlayer player;
    public final ItemStack tool;
    public final int blocksPerTick;

    public Queue<BlockPos> blocks = Lists.newLinkedList();
    public Set<BlockPos> visited = new THashSet<>();

    public TreeChopTask(ItemStack tool, BlockPos start, EntityPlayer player, int blocksPerTick) {
      this.world = player.getEntityWorld();
      this.player = player;
      this.tool = tool;
      this.blocksPerTick = blocksPerTick;

      this.blocks.add(start);
    }

    @SubscribeEvent
    public void chopChop(TickEvent.WorldTickEvent event) {
      if(event.side.isClient()) {
        finish();
        return;
      }
      // only if same dimension
      if(event.world.provider.getDimension() != world.provider.getDimension()) {
        return;
      }

      // setup
      int left = blocksPerTick;

      // continue running
      BlockPos pos;
      while(left > 0) {
        // completely done or can't do our job anymore?!
        if(blocks.isEmpty() || ToolHelper.isBroken(tool)) {
          finish();
          return;
        }

        pos = blocks.remove();
        if(!visited.add(pos)) {
          continue;
        }

        // can we harvest the block and is effective?
        if(!isLog(world, pos) || !ToolHelper.isToolEffective2(tool, world.getBlockState(pos))) {
          continue;
        }

        // save its neighbours
        for(EnumFacing facing : new EnumFacing[] { EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST }) {
          BlockPos pos2 = pos.offset(facing);
          if(!visited.contains(pos2)) {
            blocks.add(pos2);
          }
        }

        // also add the layer above.. stupid acacia trees
        for(int x = 0; x < 3; x++) {
          for(int z = 0; z < 3; z++) {
            BlockPos pos2 = pos.add(-1 + x, 1, -1 + z);
            if(!visited.contains(pos2)) {
              blocks.add(pos2);
            }
          }
        }

        // break it, wooo!
        ToolHelper.breakExtraBlock(tool, world, player, pos, pos);
        left--;
      }
    }

    private void finish() {
      // goodbye cruel world
      MinecraftForge.EVENT_BUS.unregister(this);
    }
  }
}
