package slimeknights.tconstruct.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import slimeknights.tconstruct.TConstruct;

public abstract class BlockInventoryTinkers extends BlockInventory {

  protected BlockInventoryTinkers(Material material) {
    super(material);
  }

  @Override
  protected boolean openGui(EntityPlayer player, World world, BlockPos pos) {
    player.openGui(TConstruct.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
    return true;
  }
}
