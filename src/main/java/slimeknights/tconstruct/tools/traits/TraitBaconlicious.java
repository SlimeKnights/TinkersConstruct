package slimeknights.tconstruct.tools.traits;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.shared.TinkerCommons;

public class TraitBaconlicious extends AbstractTrait {

  public TraitBaconlicious() {
    super("baconlicious", 0xffaaaa);
  }

  @Override
  public void afterBlockBreak(ItemStack tool, World world, IBlockState state, BlockPos pos, EntityLivingBase player, boolean wasEffective) {
    dropBacon(player.getEntityWorld(), pos.getX(), pos.getY(), pos.getZ(), 0.005f);
  }

  @Override
  public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
    // did the target die?
    if(!target.isEntityAlive() && wasHit) {
      dropBacon(target.getEntityWorld(), target.posX, target.posY, target.posZ, 0.05f);
    }
  }

  protected void dropBacon(World world, double x, double y, double z, float chance) {
    if(!world.isRemote && random.nextFloat() < chance) {
      EntityItem entity = new EntityItem(world, x, y, z, TinkerCommons.bacon.copy());
      world.spawnEntity(entity);
    }
  }
}
