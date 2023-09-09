package slimeknights.tconstruct.library.modifiers.hook.build;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * Modifier hook for adding attributes to a tool when in the correct slot.
 */
public interface AttributesModifierHook {
  /**
   * Adds attributes from this modifier's effect. Called whenever the item stack refreshes attributes, typically on equipping and unequipping.
   * It is important that you return the same list when equipping and unequipping the item.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link ToolStatsModifierHook}: Limited context, but can affect durability, mining level, and mining speed.</li>
   * </ul>
   * @param tool      Current tool instance
   * @param modifier  Modifier level
   * @param slot      Slot for the attributes
   * @param consumer  Attribute consumer
   */
  void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute,AttributeModifier> consumer);

  /** Merger that runs all hooks */
  record AllMerger(Collection<AttributesModifierHook> modules) implements AttributesModifierHook {
    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute,AttributeModifier> consumer) {
      for (AttributesModifierHook module : modules) {
        module.addAttributes(tool, modifier, slot, consumer);
      }
    }
  }
}
