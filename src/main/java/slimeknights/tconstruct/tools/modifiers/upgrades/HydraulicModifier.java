package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class HydraulicModifier extends IncrementalModifier {
  public HydraulicModifier() {
    super(0x7CB3A4);
  }

  @Override
  public int getPriority() {
    return 125; // run before trait boosts such as dwarven
  }

  @Override
  public void onBreakSpeed(IModifierToolStack tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if (!isEffective) {
      return;
    }
    PlayerEntity player = event.getPlayer();
    float bonus = 0;
    // highest bonus in water
    if (player.areEyesInFluid(FluidTags.WATER)) {
      bonus = 8;
      // if not enchanted with aqua affinity, multiply by 5 to cancel out the effects of water
      if (!EnchantmentHelper.hasAquaAffinity(player)) {
        bonus *= 5;
      }
    } else if (player.getEntityWorld().isRainingAt(player.getPosition())) {
      // partial bonus in the rain
      bonus = 4;
    }
    if (bonus > 0) {
      bonus *= level * tool.getDefinition().getBaseStatDefinition().getModifier(ToolStats.DURABILITY) * miningSpeedModifier;
      event.setNewSpeed(event.getNewSpeed() + bonus);
    }
  }
}
