package slimeknights.tconstruct.tools.modifiers.traits.general;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/** @deprecated use {@link slimeknights.tconstruct.tools.modules.durability.ToolDamageRangeModule} */
@Deprecated(forRemoval = true)
public class TannedModifier extends NoLevelsModifier implements ToolDamageModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, ModifierHooks.TOOL_DAMAGE);
  }

  @Override
  public int getPriority() {
    // higher than stoneshield, overslime, and reinforced
    return 200;
  }

  @Override
  public int onDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder) {
    return amount >= 1 ? 1 : 0;
  }
}
