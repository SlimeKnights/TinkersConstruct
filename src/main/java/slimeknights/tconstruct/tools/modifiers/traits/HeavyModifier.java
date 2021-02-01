package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.ToolStatsModifierBuilder;

import java.util.UUID;
import java.util.function.BiConsumer;

public class HeavyModifier extends Modifier {
  private static final UUID ATTRIBUTE_UUID = UUID.fromString("f8a6e738-642b-11eb-ae93-0242ac130002");
  public HeavyModifier() {
    super(0x4d4968);
  }

  /**
   * Adds raw stats to the tool. Called whenever modifiers are rebuilt
   * @param level    Modifier level
   * @param builder  Tool stat builder
   */
  @Override
  public void addToolStats(int level, ToolStatsModifierBuilder builder) {
    builder.multiplyAttackDamage(1 + (0.2f * level));
  }

  /**
   * Adds attributes from this modifier's effect
   * @param tool      Current tool instance
   * @param level     Modifier level
   * @param consumer  Attribute consumer
   */
  @Override
  public void addAttributes(IModifierToolStack tool, int level, BiConsumer<Attribute,AttributeModifier> consumer) {
    consumer.accept(Attributes.MOVEMENT_SPEED, new AttributeModifier(ATTRIBUTE_UUID, Util.prefix("heavy"), level * (-0.1), Operation.MULTIPLY_BASE));
  }
}
