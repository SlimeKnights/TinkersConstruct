package slimeknights.tconstruct.tools.modifiers.traits;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

import java.util.UUID;
import java.util.function.BiConsumer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;

public class HeavyModifier extends Modifier {
  private static final UUID ATTRIBUTE_UUID = UUID.fromString("f8a6e738-642b-11eb-ae93-0242ac130002");
  private static final String ATTRIBUTE_NAME = Util.prefix("heavy");
  public HeavyModifier() {
    super(0x4d4968);
  }

  @Override
  public void addToolStats(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder) {
    builder.multiplyAttackDamage(1 + (0.2f * level));
  }

  /**
   * Adds attributes from this modifier's effect
   * @param tool      Current tool instance
   * @param level     Modifier level
   * @param consumer  Attribute consumer
   */
  @Override
  public void addAttributes(IModifierToolStack tool, int level, BiConsumer<EntityAttribute,EntityAttributeModifier> consumer) {
    consumer.accept(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(ATTRIBUTE_UUID, ATTRIBUTE_NAME, level * (-0.1), Operation.MULTIPLY_BASE));
  }
}
