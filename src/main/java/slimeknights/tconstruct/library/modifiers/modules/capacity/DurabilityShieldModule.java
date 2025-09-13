package slimeknights.tconstruct.library.modifiers.modules.capacity;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.loadable.common.ColorLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.DurabilityDisplayModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.CapacityBarHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Module using a capacity bar to shield the tool from durability.
 * @param color  Color of the bar
 */
public record DurabilityShieldModule(int color) implements ModifierModule, ToolDamageModifierHook, DurabilityDisplayModifierHook {
  private static final List<ModuleHook<?>> HOOKS = HookProvider.<DurabilityShieldModule>defaultHooks(ModifierHooks.TOOL_DAMAGE, ModifierHooks.DURABILITY_DISPLAY);
  public static final RecordLoadable<DurabilityShieldModule> LOADER = RecordLoadable.create(
    ColorLoadable.NO_ALPHA.requiredField("color", DurabilityShieldModule::color),
    DurabilityShieldModule::new);

  @Override
  public RecordLoadable<DurabilityShieldModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return HOOKS;
  }

  /** Logic for damaging a tool with the durability shield */
  public static int onDamageTool(CapacityBarHook bar, IToolStackView tool, ModifierEntry modifier, int amount) {
    int shield = bar.getAmount(tool);
    if (shield > 0) {
      // if we have more overslime than amount, remove some overslime
      if (shield >= amount) {
        bar.setAmount(tool, modifier, shield - amount);
        return 0;
      }
      // amount is more than overslime, reduce and clear overslime
      amount -= shield;
      bar.setAmount(tool, modifier, 0);
    }
    return amount;
  }

  @Override
  public int onDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder) {
    return onDamageTool(modifier.getHook(ModifierHooks.CAPACITY_BAR), tool, modifier, amount);
  }

  @Override
  public int getDurabilityWidth(IToolStackView tool, ModifierEntry modifier) {
    CapacityBarHook bar = modifier.getHook(ModifierHooks.CAPACITY_BAR);
    int shield = bar.getAmount(tool);
    if (shield > 0) {
      return DurabilityDisplayModifierHook.getWidthFor(shield, bar.getCapacity(tool, modifier));
    }
    return 0;
  }

  @Nullable
  @Override
  public Boolean showDurabilityBar(IToolStackView tool, ModifierEntry modifier) {
    return modifier.getHook(ModifierHooks.CAPACITY_BAR).getAmount(tool) > 0 ? true : null;
  }

  @Override
  public int getDurabilityRGB(IToolStackView tool, ModifierEntry modifier) {
    if (modifier.getHook(ModifierHooks.CAPACITY_BAR).getAmount(tool) > 0) {
      return color;
    }
    return -1;
  }
}
