package slimeknights.tconstruct.library.modifiers.hooks;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;
import java.util.function.Function;

/**
 * Hooks for interacting on a helmet, left generic for addons to use for other armor pieces
 * TODO: swap level parameters for ModifierEntry
 */
public interface IArmorInteractModifier {
  /** Default implementation of the hook */
  IArmorInteractModifier DEFAULT = new IArmorInteractModifier() {};
  /** Merger to combine multiple interactions into one */
  Function<Collection<IArmorInteractModifier>,IArmorInteractModifier> MERGER = FirstMerger::new;

  /**
   * Called when the helmet keybinding is pressed to interact with this helmet modifier
   * @param tool     Tool instance
   * @param modifier  Modifier and level used to call this hook
   * @param player   Player wearing the helmet
   * @param slot     Slot source of the interaction
   * @return  True if no other modifiers should process
   */
  default boolean startArmorInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot) {
    return false;
  }

  /**
   * Called when the helmet keybinding is released to interact with this modifier
   * @param tool     Tool instance
   * @param modifier Modifier and level used to call this hook
   * @param player   Player wearing the helmet
   * @param slot     Slot source of the interaction
   */
  default void stopArmorInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot) {}

  /** Merging strategy that runs the first hook that passes */
  @RequiredArgsConstructor
  class FirstMerger implements IArmorInteractModifier {
    private final Collection<IArmorInteractModifier> modules;

    @Override
    public boolean startArmorInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot) {
      for (IArmorInteractModifier module : modules) {
        if (module.startArmorInteract(tool, modifier, player, slot)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public void stopArmorInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot) {
      for (IArmorInteractModifier module : modules) {
        module.stopArmorInteract(tool, modifier, player, slot);
      }
    }
  }
}
