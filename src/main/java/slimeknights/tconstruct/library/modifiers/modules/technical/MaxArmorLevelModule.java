package slimeknights.tconstruct.library.modifiers.modules.technical;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import slimeknights.mantle.util.IdExtender;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.data.ModifierMaxLevel;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

/** Shared logic for modules that run an effect based on the max level of the modifier across equipment slots */
public interface MaxArmorLevelModule extends HookProvider, EquipmentChangeModifierHook, ModifierCondition.ConditionalModule<IToolStackView> {
  List<ModuleHook<?>> NO_TOOLTIP_HOOKS = HookProvider.<MaxArmorLevelModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE);
  List<ModuleHook<?>> TOOLTIP_HOOKS = HookProvider.defaultHooks(ModifierHooks.EQUIPMENT_CHANGE, ModifierHooks.TOOLTIP);

  /**
   * Max level key
   * @see #createKey(ResourceLocation)
   */
  ComputableDataKey<ModifierMaxLevel> maxLevel();

  /** If true, this modifier applies its effect on broken tools */
  boolean allowBroken();

  /** Tag determining which held tools get the effect, if null none do */
  @Nullable
  TagKey<Item> heldTag();

  @Override
  default void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    updateLevel(tool, modifier, modifier.getEffectiveLevel(), context);
  }

  @Override
  default void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    updateLevel(tool, modifier, 0, context);
  }

  /** Updates the level of this module, properly evaluating the condition and setting the max level */
  default void updateLevel(IToolStackView tool, ModifierEntry modifier, float effectiveLevel, EquipmentChangeContext context) {
    if (condition().matches(tool, modifier) && ArmorLevelModule.validSlot(tool, context.getChangedSlot(), heldTag()) && (!tool.isBroken() || allowBroken())) {
      context.getTinkerData().ifPresent(data -> {
        ModifierMaxLevel maxLevel = data.computeIfAbsent(maxLevel());
        float oldLevel = maxLevel.getMax();
        maxLevel.set(context.getChangedSlot(), effectiveLevel);
        float newLevel = maxLevel.getMax();
        if (oldLevel != newLevel) {
          updateValue(tool, modifier, context, data, newLevel, oldLevel);
        }
      });
    }
  }

  /**
   * Updates stats associated with the level change
   * @param tool      Tool instance
   * @param modifier  Modifier holding this module
   * @param context   Equipment context
   * @param data      Data instance to update other entity data
   * @param newLevel  New max level
   * @param oldLevel  Old max level
   */
  void updateValue(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context, TinkerDataCapability.Holder data, float newLevel, float oldLevel);

  /** Checks if the tooltip can be madded for this module */
  static boolean shouldAddTooltip(MaxArmorLevelModule module, IToolStackView tool, ModifierEntry modifier, @Nullable Player player) {
    TagKey<Item> heldTag = module.heldTag();
    if (module.condition().matches(tool, modifier) && (tool.hasTag(TinkerTags.Items.WORN_ARMOR) || heldTag != null && tool.hasTag(heldTag)) && (!tool.isBroken() || module.allowBroken())) {
      // FIXME: this does not handle the case of multiple slots being equally max, would require slot/stack access to figure that out
      return player == null || player.getCapability(TinkerDataCapability.CAPABILITY).filter(data -> data.computeIfAbsent(module.maxLevel()).getMax() <= modifier.getEffectiveLevel()).isPresent();
    }
    return false;
  }


  /* Helpers */

  /** Creates a new max level key for the given ID. Key should be unique instance per usage */
  static ComputableDataKey<ModifierMaxLevel> createKey(@Nullable ResourceLocation id) {
    if (id == null) {
      id = new ResourceLocation("missingno");
    }
    return ComputableDataKey.of(IdExtender.LocationExtender.INSTANCE.prefix(id, "_data"), ModifierMaxLevel::new);
  }
}
