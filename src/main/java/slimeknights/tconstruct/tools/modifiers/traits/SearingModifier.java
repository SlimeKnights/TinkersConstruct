package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.List;

public class SearingModifier extends Modifier {
  private static final float BASELINE_TEMPERATURE = 0.75f;

  public SearingModifier() {
    super(0x4F4A47);
  }

  @Override
  public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    TinkerModifiers.tank.get().addCapacity(volatileData, FluidValues.INGOT * 2);
  }

  /** Applies the temperature boost */
  private static float temperatureBoost(IModifierToolStack tool, float temperature, int level) {
    // produces 0 at 0.75t. Caps at level * 2.5 at 2.0t, or at level * -2.5 at -0.5t
    return (temperature - BASELINE_TEMPERATURE) * (level * 2) * tool.getModifier(ToolStats.ATTACK_DAMAGE);
  }

  @Override
  public float getEntityDamage(IModifierToolStack tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    LivingEntity attacker = context.getAttacker();
    BlockPos attackerPos = attacker.getPosition();
    return damage + temperatureBoost(tool, attacker.world.getBiome(attackerPos).getTemperature(attackerPos), level);
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed) {
    addDamageTooltip(tool, level * 2.5f, tooltip);
  }
}
