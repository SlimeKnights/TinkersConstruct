package slimeknights.tconstruct.tools.modifiers.traits.harvest;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.item.Item;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.UUID;
import java.util.function.BiConsumer;

public class SharpweightModifier extends Modifier {
  private static final UUID MAINHAND_ATTRIBUTE_UUID = UUID.fromString("695891a0-a5d3-4d6d-8872-0b5c855821d4");
  private static final UUID OFFHAND_ATTRIBUTE_UUID = UUID.fromString("f401c376-2d66-4dc8-a8bd-44c7cb14eb4d");
  private static final String ATTRIBUTE_NAME = TConstruct.prefix("sharpweight");
  public SharpweightModifier() {
    super(0xD1C08B);
  }

  @Override
  public void addToolStats(Item item, ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder) {
    ToolStats.MINING_SPEED.multiply(builder, 1 + (0.1f * level));
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
