package slimeknights.tconstruct.tools.modifiers.traits.general;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.slotless.OverslimeModifier;

public class OvercastModifier extends Modifier implements VolatileDataModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.VOLATILE_DATA);
  }

  @Override
  public int getPriority() {
    return 90; // after overslime and overforced
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, ModifierEntry modifier, ModDataNBT volatileData) {
    OverslimeModifier overslime = TinkerModifiers.overslime.get();
    overslime.setFriend(volatileData);
    int level = modifier.getLevel();
    overslime.addCapacity(volatileData, level * 25);
    overslime.multiplyCapacity(volatileData, 1f + (level * 0.5f));
  }
}
