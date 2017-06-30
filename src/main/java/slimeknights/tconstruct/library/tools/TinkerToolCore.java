package slimeknights.tconstruct.library.tools;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import javax.annotation.Nullable;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.utils.ToolHelper;

public abstract class TinkerToolCore extends ToolCore {

  public TinkerToolCore(PartMaterialType... requiredComponents) {
    super(requiredComponents);
  }

  @Override
  public final NBTTagCompound buildTag(List<Material> materials) {
    return buildTagData(materials).get();
  }

  protected abstract ToolNBT buildTagData(List<Material> materials);

  /* Dual tool usage */



  @Override
  public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
    if(DualToolHarvestUtils.shouldUseOffhand(player, pos, itemstack)) {
      ItemStack offhand = player.getHeldItemOffhand();
      return offhand.getItem().onBlockStartBreak(offhand, pos, player);
    }
    return super.onBlockStartBreak(itemstack, pos, player);
  }

  @Override
  public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase player) {
    if(DualToolHarvestUtils.shouldUseOffhand(player, pos, stack)) {
      ItemStack offhand = player.getHeldItemOffhand();
      return offhand.getItem().onBlockDestroyed(offhand, worldIn, state, pos, player);
    }
    return super.onBlockDestroyed(stack, worldIn, state, pos, player);
  }

  @Override
  public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState) {
    if(ToolHelper.isBroken(stack)) {
      return -1;
    }
    if(player != null && DualToolHarvestUtils.shouldUseOffhand(player, blockState, stack)) {
      ItemStack offhand = player.getHeldItemOffhand();
      return offhand.getItem().getHarvestLevel(offhand, toolClass, player, blockState);
    }
    return super.getHarvestLevel(stack, toolClass, player, blockState);
  }
}
