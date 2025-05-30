package slimeknights.tconstruct.tools.modifiers.upgrades.ranged;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.interaction.UsingToolModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.data.ModifierIds;
import slimeknights.tconstruct.tools.modules.ZoomModule;

/** @deprecated use {@link ZoomModule} */
@Deprecated(forRemoval = true)
public class ScopeModifier extends Modifier {
  @Deprecated(forRemoval = true)
  public static final ResourceLocation SCOPE = ModifierIds.scope;

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ZoomModule.SCOPE);
  }

  /** @deprecated no longer needed in modifiers. Call {@link UsingToolModifierHook#onUsingTick(IToolStackView, ModifierEntry, LivingEntity, int, int, ModifierEntry)} in tools. */
  @Deprecated(forRemoval = true)
  public static void scopingUsingTick(IToolStackView tool, LivingEntity entity, int chargeTime) {}

  /**
   * Cancels the scoping effect for the given entity.
   * @param entity  Entity
   * @deprecated No longer necessary to call in your modifier. For custom tools, see {@link UsingToolModifierHook#afterStopUsing(IToolStackView, LivingEntity, int)}
   */
  @Deprecated(forRemoval = true)
  public static void stopScoping(LivingEntity entity) {
    if (entity.level().isClientSide) {
      entity.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).remove(SCOPE));
    }
  }
}
