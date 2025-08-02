package slimeknights.tconstruct.tools.modules.ranged;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;

/** Module implementing the valiant modifier for bows */
public record ValiantArrowModule(LevelingValue value) implements ModifierModule, ProjectileHitModifierHook, TooltipModifierHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ValiantArrowModule>defaultHooks(ModifierHooks.PROJECTILE_HIT, ModifierHooks.TOOLTIP);
  public static final RecordLoadable<ValiantArrowModule> LOADER = RecordLoadable.create(LevelingValue.LOADABLE.directField(ValiantArrowModule::value), ValiantArrowModule::new);

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public RecordLoadable<? extends IHaveLoader> getLoader() {
    return LOADER;
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    if (target != null && projectile instanceof AbstractArrow arrow) {
      int count = 0;
      for (EquipmentSlot slot : ModifiableArmorMaterial.ARMOR_SLOTS) {
        if (!target.getItemBySlot(slot).isEmpty()) {
          count += 1;
        }
      }
      if (count > 0) {
        arrow.setBaseDamage(arrow.getBaseDamage() + value.compute(modifier.getEffectiveLevel()) * count);
      }
    }
    return false;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry entry, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    Modifier modifier = entry.getModifier();
    TooltipModifierHook.addFlatBoost(modifier, TooltipModifierHook.statName(modifier, ToolStats.PROJECTILE_DAMAGE), value.compute(entry.getEffectiveLevel()) * 4, tooltip);
  }
}
