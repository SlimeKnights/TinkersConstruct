package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.behavior.AttributeModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.AttributeModule.TooltipStyle;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.shared.TinkerAttributes;
import slimeknights.tconstruct.tools.logic.DoubleJumpHandler;

import static slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial.ARMOR_SLOTS;

/** @deprecated use {@link TinkerAttributes#JUMP_COUNT} */
@Deprecated(forRemoval = true)
public class DoubleJumpModifier extends Modifier {
  private Component levelOneName = null;
  private Component levelTwoName = null;

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(AttributeModule.builder(TinkerAttributes.JUMP_COUNT.get(), Operation.ADDITION).slots(ARMOR_SLOTS).tooltipStyle(TooltipStyle.NONE).flat(1));
  }

  @Override
  public Component getDisplayName(int level) {
    if (level == 1) {
      if (levelOneName == null) {
        levelOneName = applyStyle(Component.translatable(getTranslationKey() + ".1"));
      }
      return levelOneName;
    }
    if (level == 2) {
      if (levelTwoName == null) {
        levelTwoName = applyStyle(Component.translatable(getTranslationKey() + ".2"));
      }
      return levelTwoName;
    }
    return super.getDisplayName(level);
  }

  /** @deprecated use {@link slimeknights.tconstruct.tools.logic.DoubleJumpHandler#extraJump(Player)} */
  @Deprecated(forRemoval = true)
  public static boolean extraJump(Player entity) {
    return DoubleJumpHandler.extraJump(entity);
  }
}
