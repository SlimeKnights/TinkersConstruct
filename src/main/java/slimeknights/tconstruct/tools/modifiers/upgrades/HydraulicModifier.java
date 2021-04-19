package slimeknights.tconstruct.tools.modifiers.upgrades;

import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.FluidTags;

public class HydraulicModifier extends IncrementalModifier {
  public HydraulicModifier() {
    super(0x7CB3A4);
  }

  @Override
  public int getPriority() {
    return 125; // run before trait boosts such as dwarven
  }

  @Override
  public void onBreakSpeed(IModifierToolStack tool, int level, PlayerEntity player, boolean isEffective, float miningSpeedModifier) {
    if (!isEffective) {
      return;
    }
    float bonus = 0;
    // highest bonus in water
    if (player.isSubmergedIn(FluidTags.WATER)) {
      bonus = 8;
      // if not enchanted with aqua affinity, multiply by 5 to cancel out the effects of water
      if (!EnchantmentHelper.hasAquaAffinity(player)) {
        bonus *= 5;
      }
    } else if (player.getEntityWorld().hasRain(player.getBlockPos())) {
      // partial bonus in the rain
      bonus = 4;
    }
    if (bonus > 0) {
      bonus *= level * tool.getDefinition().getBaseStatDefinition().getMiningSpeedModifier() * miningSpeedModifier;
      throw new RuntimeException("CRAB");
      //event.setNewSpeed(event.getNewSpeed() + bonus);
    }
  }
}
