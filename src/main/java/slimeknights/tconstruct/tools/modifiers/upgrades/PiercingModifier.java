package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.helper.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

public class PiercingModifier extends IncrementalModifier {
  public PiercingModifier() {
    super(0x9FA76D);
  }

  @Override
  public int afterEntityHit(IModifierToolStack tool, int level, ToolAttackContext context, float damageDealt) {
    // deals 0.5 pierce damage per level, scaled, half of sharpness
    DamageSource source;
    PlayerEntity player = context.getPlayerAttacker();
    if (player != null) {
      source = DamageSource.causePlayerDamage(player);
    } else {
      source = DamageSource.causeMobDamage(context.getAttacker());
    }
    source.setDamageBypassesArmor();
    float secondaryDamage = getScaledLevel(tool, level) * tool.getDefinition().getBaseStatDefinition().getModifier(ToolStats.ATTACK_DAMAGE) * 0.5f * context.getCooldown();
    ToolAttackUtil.attackEntitySecondary(source, secondaryDamage, context.getTarget(), context.getLivingTarget(), true);
    return 0;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed) {
    ScaledTypeDamageModifier.addDamageTooltip(this, tool, level, tool.getDefinition().getBaseStatDefinition().getModifier(ToolStats.ATTACK_DAMAGE) * 0.5f, tooltip);
  }
}
