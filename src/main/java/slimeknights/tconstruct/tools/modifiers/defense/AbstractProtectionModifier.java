package slimeknights.tconstruct.tools.modifiers.defense;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.data.ModifierMaxLevel;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Base class for protection modifiers that want to keep track of the largest level for a bonus
 * @deprecated use {@link slimeknights.tconstruct.library.modifiers.modules.technical.MaxArmorLevelModule} and {@link slimeknights.tconstruct.library.modifiers.modules.armor.ProtectionModule}
 */
@Deprecated(forRemoval = true)
@RequiredArgsConstructor
public abstract class AbstractProtectionModifier<T extends ModifierMaxLevel> extends Modifier implements EquipmentChangeModifierHook {
  private final ComputableDataKey<T> key;
  private final boolean allowClient;

  public AbstractProtectionModifier(ComputableDataKey<T> key) {
    this(key, false);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
  }

  /** Called when the last piece of equipment is removed to reset the data */
  protected void reset(T data, EquipmentChangeContext context) {}

  /** Called to apply updates to the piece */
  protected void set(T data, EquipmentSlot slot, float scaledLevel, EquipmentChangeContext context) {
    data.set(slot, scaledLevel);
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    EquipmentSlot slot = context.getChangedSlot();
    if ((allowClient || !context.getEntity().level.isClientSide) && ModifierUtil.validArmorSlot(tool, slot) && !tool.isBroken()) {
      context.getTinkerData().ifPresent(data -> {
        T modData = data.get(key);
        if (modData != null) {
          set(modData, slot, 0, context);
          if (modData.getMax() == 0) {
            reset(modData, context);
          }
        }
      });
    }
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    EquipmentSlot slot = context.getChangedSlot();
    if ((allowClient || !context.getEntity().level.isClientSide) && ModifierUtil.validArmorSlot(tool, slot) && !tool.isBroken()) {
      float scaledLevel = modifier.getEffectiveLevel();
      context.getTinkerData().ifPresent(data -> {
        // add ourself to the data
        set(data.computeIfAbsent(key), slot, scaledLevel, context);
      });
    }
  }
}
