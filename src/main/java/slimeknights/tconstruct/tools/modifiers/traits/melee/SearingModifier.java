package slimeknights.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipFlag;
import slimeknights.tconstruct.library.utils.TooltipKey;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;

public class SearingModifier extends Modifier {
  private static final float BASELINE_TEMPERATURE = 0.75f;

  public SearingModifier() {
    super(0x4F4A47);
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    TinkerModifiers.tank.get().addCapacity(volatileData, FluidValues.INGOT * 2);
  }

  /** Applies the temperature boost */
  private static float temperatureBoost(LivingEntity living, int level) {
    // produces 0 at 0.75t. Caps at level * 2.5 at 2.0t, or at level * -2.5 at -0.5t
    BlockPos attackerPos = living.getPosition();
    return (living.world.getBiome(attackerPos).getTemperature(attackerPos) - BASELINE_TEMPERATURE) * (level * 2);
  }

  @Override
  public float getEntityDamage(IModifierToolStack tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    return damage + temperatureBoost(context.getAttacker(), level) * tool.getModifier(ToolStats.ATTACK_DAMAGE);
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, @Nullable PlayerEntity player, List<ITextComponent> tooltip, TooltipKey key, TooltipFlag flag) {
    float bonus;
    if (player != null && key == TooltipKey.SHIFT) {
      bonus = temperatureBoost(player, level);
    } else {
      bonus = level * 2.5f;
    }
    addDamageTooltip(tool, bonus, tooltip);
  }
}
