package slimeknights.tconstruct.tools.item;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.ToolMaterialStats;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

public class Scythe extends ToolCore {
  public static final ImmutableSet<net.minecraft.block.material.Material> effective_materials =
      ImmutableSet.of(net.minecraft.block.material.Material.web,
                      net.minecraft.block.material.Material.leaves,
                      net.minecraft.block.material.Material.plants,
                      net.minecraft.block.material.Material.grass,
                      net.minecraft.block.material.Material.vine,
                      net.minecraft.block.material.Material.gourd);


  public Scythe() {
    super(new PartMaterialType.ToolPartType(TinkerTools.toolRod),
          new PartMaterialType.ToolPartType(TinkerTools.toolRod),
          new PartMaterialType.ToolPartType(TinkerTools.toolRod),
          new PartMaterialType.ToolPartType(TinkerTools.toolRod)); // todo
  }

  @Override
  public float damagePotential() {
    return 1.3f;
  }

  @Override
  public boolean isEffective(Block block) {
    return effective_materials.contains(block.getMaterial());
  }

  // special AOE block breaking against leaves
  @Override
  public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
    World world = player.worldObj;
    if(isEffective(world.getBlockState(pos).getBlock())) {
      for(BlockPos extraPos : ToolHelper.calcAOEBlocks(itemstack, player.worldObj, player, pos, 5, 5, 5, 4)) {
        if(isEffective(world.getBlockState(extraPos).getBlock())) {
          breakBlock(itemstack, extraPos, player);
        }
      }
    }

    return super.onBlockStartBreak(itemstack, pos, player);
  }

  private boolean isLeaves(BlockPos pos, EntityPlayer player) {
    return player.worldObj.getBlockState(pos).getBlock().isLeaves(player.worldObj, pos);
  }

  protected void breakBlock(ItemStack stack, BlockPos pos, EntityPlayer player) {
    // silktouch gives us shears :D
    if(EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, stack) > 0) {
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
    AxisAlignedBB box = AxisAlignedBB.fromBounds(entity.posX, entity.posY, entity.posZ, entity.posX + 1.0D, entity.posY + 1.0D, entity.posZ + 1.0D).expand(1.0D, 1.0D, 1.0D);
    @SuppressWarnings("unchecked")
    List<Entity> entities = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, box);

    for(Entity target : entities) {
      hit |= ToolHelper.attackEntity(stack, this, player, target);
    }

    return hit;
  }

  @Override
  public boolean canUseSecondaryItem() {
    return false;
  }

  @Override
  public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {

    BlockPos start = new BlockPos(player);

    int d = 3;
    int distance = 3;

    for(int xp = start.getX() - d; xp != start.getX() + d; xp++) {
      for(int yp = start.getY() - d; yp != start.getY() + d; yp++) {
        for(int zp = start.getZ() - d; zp != start.getZ() + d; zp++) {
          if(MathHelper.abs_int(xp - start.getX()) + MathHelper.abs_int(yp - start.getY()) + MathHelper.abs_int(zp - start.getZ()) >= distance) {
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

    return itemStack;
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    ToolMaterialStats handle = materials.get(0).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats head = materials.get(2).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats handle2 = materials.get(1).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats binding = materials.get(3).getStats(ToolMaterialStats.TYPE);

    ToolNBT data = new ToolNBT(head);

    return data.get();
  }
}
