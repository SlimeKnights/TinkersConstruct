package slimeknights.tconstruct.library.modifiers.hook.interaction;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.interaction.edible.EdibleModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;
import java.util.List;

/**
 * Hook called by {@link EdibleModule} to apply effects after eating other than restoring hunger and saturation.
 * Effects may include durability usage, clearing effects, and representative items.
 */
public interface EdibleEffectHook {
  /**
   * Adds representative items upon eating food with this modifier.
   * @param tool                  Tool being eaten.
   * @param modifier              Modifier running the hook.
   * @param player                Player eating.
   * @param hunger                Hunger restored.
   * @param saturation            Saturation restored.
   * @param representativeItems   List of representative items to fill with item stacks for the Diet mod.
   */
  void onToolEaten(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot eatenSlot, int hunger, float saturation, List<ItemStack> representativeItems);

  /** Merger running all nested modules */
  record AllMerger(Collection<EdibleEffectHook> modules) implements EdibleEffectHook {
    @Override
    public void onToolEaten(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot eatenSlot, int hunger, float saturation, List<ItemStack> representativeItems) {
      for (EdibleEffectHook module : modules) {
        module.onToolEaten(tool, modifier, player, eatenSlot, hunger, saturation, representativeItems);
      }
    }
  }
}
