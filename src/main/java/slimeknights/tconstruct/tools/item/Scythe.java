package slimeknights.tconstruct.tools.item;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

public class Scythe extends ToolCore {

  public static final ImmutableSet<net.minecraft.block.material.Material> effective_materials =
      ImmutableSet.of(net.minecraft.block.material.Material.WEB,
                      net.minecraft.block.material.Material.LEAVES,
                      net.minecraft.block.material.Material.PLANTS,
                      net.minecraft.block.material.Material.GRASS,
                      net.minecraft.block.material.Material.VINE,
                      net.minecraft.block.material.Material.GOURD);


  public Scythe() {
    super(PartMaterialType.handle(TinkerTools.toolRod),
          PartMaterialType.head(TinkerTools.toolRod),
          PartMaterialType.handle(TinkerTools.toolRod),
          PartMaterialType.extra(TinkerTools.toughBinding)); // todo
  }

  @Override
  public float damagePotential() {
    return 1.3f;
  }

  @Override
  public double attackSpeed() {
    return 1f;
  }

  @Override
  public boolean isEffective(IBlockState state) {
    return effective_materials.contains(state.getMaterial());
  }

  // special AOE block breaking against leaves
  @Override
  public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
    World world = player.worldObj;
    if(isEffective(world.getBlockState(pos))) {
      for(BlockPos extraPos : ToolHelper.calcAOEBlocks(itemstack, player.worldObj, player, pos, 5, 5, 5, 4)) {
        if(isEffective(world.getBlockState(extraPos))) {
          breakBlock(itemstack, extraPos, player);
        }
      }
    }

    return super.onBlockStartBreak(itemstack, pos, player);
  }

  private boolean isLeaves(BlockPos pos, EntityPlayer player) {
    IBlockState state = player.worldObj.getBlockState(pos);
    return state.getBlock().isLeaves(state, player.worldObj, pos);
  }

  protected void breakBlock(ItemStack stack, BlockPos pos, EntityPlayer player) {
    // silktouch gives us shears :D
    if(EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0) {
      if(ToolHelper.shearBlock(stack, player.worldObj, player, pos)) {
        return;
      }
    }

    // can't be sheared or no silktouch. break it
    ToolHelper.breakExtraBlock(stack, player.worldObj, player, pos, pos);
  }


  @Override
  public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
    // AOE attack!
    boolean hit = false;
    AxisAlignedBB box = new AxisAlignedBB(entity.posX, entity.posY, entity.posZ, entity.posX + 1.0D, entity.posY + 1.0D, entity.posZ + 1.0D).expand(1.0D, 1.0D, 1.0D);
    @SuppressWarnings("unchecked")
    List<Entity> entities = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, box);

    for(Entity target : entities) {
      hit |= ToolHelper.attackEntity(stack, this, player, target);
    }

    return hit;
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
    BlockPos start = new BlockPos(player);

    int d = 3;
    int distance = 3;

    for(int xp = start.getX() - d; xp != start.getX() + d; xp++) {
      for(int yp = start.getY() - d; yp != start.getY() + d; yp++) {
        for(int zp = start.getZ() - d; zp != start.getZ() + d; zp++) {
          if(MathHelper.abs_int(xp - start.getX()) + MathHelper.abs_int(yp - start.getY()) + MathHelper
              .abs_int(zp - start.getZ()) >= distance) {
            continue;
          }
          BlockPos pos = new BlockPos(xp, yp, zp);
          // chop it down
          if(ToolHelper.isToolEffective2(itemStack, world.getBlockState(pos))) {
            breakBlock(itemStack, pos, player);
          }
        }
      }
    }

    return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);
  }

  @Override
  public int[] getRepairParts() {
    return new int[]{1, 2};
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    HandleMaterialStats handle  = materials.get(0).getStatsOrUnknown(HandleMaterialStats.TYPE);
    HeadMaterialStats head      = materials.get(2).getStatsOrUnknown(HeadMaterialStats.TYPE);
    HandleMaterialStats handle2 = materials.get(1).getStatsOrUnknown(HandleMaterialStats.TYPE);
    ExtraMaterialStats binding  = materials.get(3).getStatsOrUnknown(ExtraMaterialStats.TYPE);

    ToolNBT data = new ToolNBT();

    return data.get();
  }
}
