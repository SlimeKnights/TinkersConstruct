package slimeknights.tconstruct.tools.item;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.ToolMaterialStats;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class LumberAxe extends Hatchet {

  public LumberAxe() {
    super(); // todo
  }

  @Override
  public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
    if(detectTree(player.worldObj, pos)) {
      return fellTree(itemstack, pos, player);
    }
    return super.onBlockStartBreak(itemstack, pos, player);
  }

  @Override
  public ImmutableList<BlockPos> getAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin) {
    return ToolHelper.calcAOEBlocks(stack, world, player, origin, 3, 3, 3);
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    ToolMaterialStats handle = materials.get(0).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats head = materials.get(1).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats plate = materials.get(2).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats binding = materials.get(3).getStats(ToolMaterialStats.TYPE);

    ToolNBT data = new ToolNBT(head);

    // as with the hatchet, the binding is very important. Except this time the plate also factors in

    data.durability *= 0.9f;
    data.durability += plate.durability * binding.extraQuality;
    data.durability *= 0.8f + 0.2f * handle.handleQuality;
    data.durability += 0.03f * handle.durability + 0.28 * binding.durability;

    // since it's a big axe.. we calculate the coefficient the same way as with the hatchet :D
    float coeff = (0.5f + handle.handleQuality / 2) * (0.5f + binding.extraQuality / 2);

    data.speed += 0.11f * plate.miningspeed;
    data.speed *= 0.6f + 0.4f * coeff;

    data.attack *= 0.3f + (0.4f + 0.1f * plate.extraQuality) * coeff;

    // 3 free modifiers
    data.modifiers = DEFAULT_MODIFIERS;

    return data.get();
  }

  public static boolean detectTree(World world, BlockPos pos) {
    // todo
    return false;
  }

  public static boolean fellTree(ItemStack itemstack, BlockPos start, EntityPlayer player) {
    // todo
    return false;
  }

}
