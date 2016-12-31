package slimeknights.tconstruct.tools.traits;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitSplinters extends AbstractTrait {

  public static DamageSource splinter = new DamageSource("splinter").setDamageBypassesArmor();
  private static int chance = 150; // 1/X chance of getting the effect

  public TraitSplinters() {
    super("splinters", TextFormatting.GREEN);
  }

  @Override
  public void afterBlockBreak(ItemStack tool, World world, IBlockState state, BlockPos pos, EntityLivingBase player, boolean wasEffective) {
    splinter(player);
  }

  @Override
  public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
    splinter(player);
  }

  private void splinter(EntityLivingBase player) {
    // SPLINTERS!
    if(!player.getEntityWorld().isRemote && random.nextInt(chance) == 0) {
      int oldTime = player.hurtResistantTime;
      attackEntitySecondary(splinter, 0.1f, player, true, true);
      player.hurtResistantTime = oldTime; // keep old invulv time
    }
  }
}
