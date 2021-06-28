package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.UUID;
import java.util.function.BiConsumer;

public class HeavyModifier extends Modifier {
  private static final UUID MAINHAND_ATTRIBUTE_UUID = UUID.fromString("f8a6e738-642b-11eb-ae93-0242ac130002");
  private static final UUID OFFHAND_ATTRIBUTE_UUID = UUID.fromString("9720e9f3-c123-4b0b-bdb2-b4ba52eb39c9");
  private static final String ATTRIBUTE_NAME = Util.prefix("heavy");
  public HeavyModifier() {
    super(0x575E79);
  }

  @Override
  public void addToolStats(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder) {
    ToolStats.ATTACK_DAMAGE.multiply(builder, 1 + (0.1f * level));
  }

  /**
   * Adds attributes from this modifier's effect
	 * @param tool      Current tool instance
	 * @param level     Modifier level
	 * @param slot      Equipment slot to add attributes into
	 * @param consumer  Attribute consumer
	 */
  @Override
  public void addAttributes(IModifierToolStack tool, int level, EquipmentSlotType slot, BiConsumer<Attribute,AttributeModifier> consumer) {
    if (slot.getSlotType() == Group.HAND) {
      consumer.accept(Attributes.MOVEMENT_SPEED, new AttributeModifier(slot == EquipmentSlotType.OFFHAND ? OFFHAND_ATTRIBUTE_UUID : MAINHAND_ATTRIBUTE_UUID,
                                                                       ATTRIBUTE_NAME, level * (-0.1), Operation.MULTIPLY_BASE));
    }
  }
}
