package slimeknights.tconstruct.tools.modules.combat;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.module.weapon.SweepWeaponAttack;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;

/** Module implementing {@link slimeknights.tconstruct.tools.data.ModifierIds#sweeping} */
public record SweepingEdgeModule(LevelingValue value) implements ModifierModule, VolatileDataModifierHook, TooltipModifierHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<SweepingEdgeModule>defaultHooks(ModifierHooks.VOLATILE_DATA, ModifierHooks.TOOLTIP);
  public static final RecordLoadable<SweepingEdgeModule> LOADER = RecordLoadable.create(LevelingValue.LOADABLE.directField(SweepingEdgeModule::value), SweepingEdgeModule::new);

  @Override
  public RecordLoadable<SweepingEdgeModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void addVolatileData(IToolContext context, ModifierEntry modifier, ToolDataNBT volatileData) {
    volatileData.putFloat(SweepWeaponAttack.SWEEP_PERCENT, volatileData.getFloat(SweepWeaponAttack.SWEEP_PERCENT) + value.compute(modifier.getEffectiveLevel()));
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry entry, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    Modifier modifier = entry.getModifier();
    tooltip.add(modifier.applyStyle(
      Component.literal(Util.PERCENT_FORMAT.format(value.compute(entry.getEffectiveLevel())))
        .append(" ").append(Component.translatable(modifier.getTranslationKey() + ".attack_damage"))));
  }
}
