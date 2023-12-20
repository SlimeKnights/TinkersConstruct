package slimeknights.tconstruct.tools.modifiers.upgrades.ranged;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.item.ModifiableLauncherItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class ScopeModifier extends Modifier {
  public static final ResourceLocation SCOPE = TConstruct.getResource("longbow_scope");

  @Override
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {
    if (context.getEntity().level.isClientSide) {
      IToolStackView replacement = context.getReplacementTool();
      if (replacement == null || replacement.getModifierLevel(this) == 0) {
        context.getTinkerData().ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).remove(SCOPE));
      }
    }
  }

  /**
   * Implementation of using tick that supports scopes
   * @param tool       Tool performing interaction
   * @param entity     Interacting entity
   * @param chargeTime  Amount of ticks the tool has charged for, typically just use duration - tiee left
   */
  public static void scopingUsingTick(IToolStackView tool, LivingEntity entity, int chargeTime) {
    if (entity.level.isClientSide && tool.getModifierLevel(TinkerModifiers.scope.getId()) > 0) {
      float drawTime = tool.getPersistentData().getInt(ModifiableLauncherItem.KEY_DRAWTIME);
      if (chargeTime > 0 && drawTime > 0) {
        entity.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).set(SCOPE, 1 - (0.6f * Math.min(chargeTime / drawTime, 1))));
      }
    }
  }

  /**
   * Cancels the scoping effect for the given entity
   * @param entity  Entity
   */
  public static void stopScoping(LivingEntity entity) {
    if (entity.level.isClientSide) {
      entity.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).remove(SCOPE));
    }
  }
}
