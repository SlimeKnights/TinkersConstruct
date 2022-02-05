package slimeknights.tconstruct.tools.modifiers.upgrades.melee;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

public class PiercingModifier extends IncrementalModifier {
  private static final ResourceLocation PIERCING_DEBUFF = TConstruct.getResource("piercing_debuff");

  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    float toRemove = 0.5f * getScaledLevel(context, level);
    float baseDamage = context.getBaseStats().get(ToolStats.ATTACK_DAMAGE);
    if (baseDamage < toRemove) {
      volatileData.putFloat(PIERCING_DEBUFF, toRemove - baseDamage);
    }
  }

  @Override
  public void addToolStats(ToolRebuildContext context, int level, ModifierStatsBuilder builder) {
    float toRemove = 0.5f * getScaledLevel(context, level) - context.getVolatileData().getFloat(PIERCING_DEBUFF);
    ToolStats.ATTACK_DAMAGE.add(builder, -toRemove);
  }

  @Override
  public int afterEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damageDealt) {
    // deals 0.5 pierce damage per level, scaled, half of sharpness
    DamageSource source;
    Player player = context.getPlayerAttacker();
    if (player != null) {
      source = DamageSource.playerAttack(player);
    } else {
      source = DamageSource.mobAttack(context.getAttacker());
    }
    source.bypassArmor();
    float secondaryDamage = (getScaledLevel(tool, level) * tool.getMultiplier(ToolStats.ATTACK_DAMAGE) - tool.getVolatileData().getFloat(PIERCING_DEBUFF)) * context.getCooldown();
    if (context.isCritical()) {
      secondaryDamage *= 1.5f;
    }
    ToolAttackUtil.attackEntitySecondary(source, secondaryDamage, context.getTarget(), context.getLivingTarget(), true);
    return 0;
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    addDamageTooltip(tool, getScaledLevel(tool, level) - tool.getVolatileData().getFloat(PIERCING_DEBUFF), tooltip);
  }
}
