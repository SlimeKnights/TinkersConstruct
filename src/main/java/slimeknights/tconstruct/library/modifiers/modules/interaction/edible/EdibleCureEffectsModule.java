package slimeknights.tconstruct.library.modifiers.modules.interaction.edible;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.data.loadable.common.ItemStackLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EdibleEffectHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/** Module for making eating a tool remove all effects using an item. */
public record EdibleCureEffectsModule(ItemStack curativeItem, ModifierCondition<IToolStackView> condition) implements ModifierModule, EdibleEffectHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<EdibleCureEffectsModule>defaultHooks(ModifierHooks.EDIBLE_EFFECT);
  public static final RecordLoadable<EdibleCureEffectsModule> LOADER = RecordLoadable.create(
    ItemStackLoadable.REQUIRED_ITEM_NBT.requiredField("curative_item", EdibleCureEffectsModule::curativeItem),
    ModifierCondition.TOOL_FIELD, EdibleCureEffectsModule::new);

  public EdibleCureEffectsModule(ItemLike item) {
    this(new ItemStack(item), ModifierCondition.ANY_TOOL);
  }

  @Override
  public RecordLoadable<EdibleCureEffectsModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onToolEaten(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot eatenSlot, int hunger, float saturation, List<ItemStack> representativeItems) {
    if (condition.matches(tool, modifier)) {
      player.curePotionEffects(curativeItem);
    }
  }
}
