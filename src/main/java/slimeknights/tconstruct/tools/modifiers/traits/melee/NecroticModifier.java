package slimeknights.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.TooltipKey;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.ReinforcedModifier;

import javax.annotation.Nullable;
import java.util.List;

public class NecroticModifier extends Modifier {
  private static final Component LIFE_STEAL = TConstruct.makeTranslation("modifier", "necrotic.lifesteal");

  @Override
  public int afterEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damageDealt) {
    if (context.isFullyCharged() && context.isCritical() && damageDealt > 0) {
      // heals a percentage of damage dealt, using same rate as reinforced
      float percent = ReinforcedModifier.diminishingPercent(level);
      if (percent > 0) {
        LivingEntity attacker = context.getAttacker();
        attacker.heal(percent * damageDealt);
        attacker.level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), Sounds.NECROTIC_HEAL.getSound(), SoundSource.PLAYERS, 1.0f, 1.0f);
        // take a bit of extra damage to heal
        return level;
      }
    }
    return 0;
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    float lifesteal = ReinforcedModifier.diminishingPercent(level);
    if (lifesteal > 0) {
      tooltip.add(applyStyle(new TextComponent(Util.PERCENT_FORMAT.format(lifesteal) + " ").append(LIFE_STEAL)));
    }
  }
}
