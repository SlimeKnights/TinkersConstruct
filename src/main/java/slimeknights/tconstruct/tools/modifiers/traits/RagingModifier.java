package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import java.util.List;

/** Modifier that boosts damage at low health */
public class RagingModifier extends Modifier {
  private static final float LOWEST_HEALTH = 2f;
  private static final float HIGHEST_HEALTH = 10f;
  private static final float DAMAGE_PER_LEVEL = 4f;

  public RagingModifier() {
    super(0xB30000);
  }

  @Override
  public float getEntityDamage(IModifierToolStack tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    LivingEntity attacker = context.getAttacker();
    float health = attacker.getHealth();
    // if the max health is less than our range of boost, decrease the max possible boost
    float max = attacker.getMaxHealth();
    if (max < HIGHEST_HEALTH) {
      health += HIGHEST_HEALTH - max;
    }

    // if we are below the point of lowest health, apply full boost
    if (health <= LOWEST_HEALTH) {
      damage += level * DAMAGE_PER_LEVEL;
      // if below highest health, scale boost
    } else if (health < HIGHEST_HEALTH) {
      damage += level * DAMAGE_PER_LEVEL * (HIGHEST_HEALTH - health)  / (HIGHEST_HEALTH - LOWEST_HEALTH);
    }
    return damage;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed) {
    addDamageTooltip(tool, level * 4, tooltip);
  }
}
