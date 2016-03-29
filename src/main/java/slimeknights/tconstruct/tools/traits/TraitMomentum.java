package slimeknights.tconstruct.tools.traits;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.potion.TinkerPotion;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TraitMomentum extends AbstractTrait {

  public static final TinkerPotion Momentum = new TinkerPotion(Util.getResource("momentum"), false, false);

  public TraitMomentum() {
    super("momentum", TextFormatting.BLUE);
  }

  @Override
  public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
    float boost = Momentum.getLevel(event.getEntityPlayer());
    boost /= 80f; // 40% boost max

    event.setNewSpeed(event.getNewSpeed() + event.getOriginalSpeed() * boost);
  }

  @Override
  public void afterBlockBreak(ItemStack tool, World world, IBlockState state, BlockPos pos, EntityLivingBase player, boolean wasEffective) {
    int level = 1;
    level += Momentum.getLevel(player);

    level = Math.min(32, level);
    int duration = (int) ((10f / ToolHelper.getActualMiningSpeed(tool)) * 1.5f * 20f);

    Momentum.apply(player, duration, level);
  }
}
