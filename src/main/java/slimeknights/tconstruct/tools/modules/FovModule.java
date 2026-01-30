package slimeknights.tconstruct.tools.modules;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.LogicHelper;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/** Module implementing {@link slimeknights.tconstruct.tools.data.ModifierIds#nearsighted} and {@link slimeknights.tconstruct.tools.data.ModifierIds#farsighted} */
public record FovModule(LevelingValue value, FovAction action) implements ModifierModule, EquipmentChangeModifierHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<FovModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE);
  public static final RecordLoadable<FovModule> LOADER = RecordLoadable.create(
    LevelingValue.LOADABLE.directField(FovModule::value),
    new EnumLoadable<>(FovAction.class).requiredField("action", FovModule::action),
    FovModule::new);

  @Override
  public RecordLoadable<FovModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  /** Gets the key for the given context */
  private static ResourceLocation getKey(ModifierEntry modifier, EquipmentChangeContext context) {
    return modifier.getId().withSuffix('_' + context.getChangedSlot().getName());
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (!tool.isBroken()) {
      TinkerDataCapability.Holder data = LogicHelper.orElseNull(context.getTinkerData());
      if (data != null) {
        data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).set(getKey(modifier, context), action.apply(value.compute(modifier.getEffectiveLevel())));
      }
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (!tool.isBroken()) {
      TinkerDataCapability.Holder data = LogicHelper.orElseNull(context.getTinkerData());
      if (data != null) {
        data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).remove(getKey(modifier, context));
      }
    }
  }

  /** Represents whether we decrease or increase FOV */
  public enum FovAction {
    INCREASE {
      @Override
      public float apply(float amount) {
        return 1 + amount;
      }
    },
    DECREASE {
      @Override
      public float apply(float amount) {
        return 1 / (1 + amount);
      }
    };

    public abstract float apply(float amount);
  }
}
