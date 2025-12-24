package slimeknights.tconstruct.library.modifiers.hook.interaction;

import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.recipe.worktable.ModifierSetWorktableRecipe;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Enum representing different sources of interaction
 */
public enum InteractionSource {
  /**
   * Standard interaction hook from right-clicking with a tool
   */
  RIGHT_CLICK("interact_modifiers"),
  /**
   * Interaction from left-clicking a tool, used on bows and slimestaffs
   */
  LEFT_CLICK("attack_modifiers"),
  /**
   * Interaction from chestplates with an empty hand. See also {@link KeybindInteractModifierHook}
   */
  ARMOR("unarmed_modifiers");

  /** All interaction sources on held tools */
  public static InteractionSource[] HELD = { RIGHT_CLICK, LEFT_CLICK };
  /** Key for denoting toggled to attack in tooltip */
  private static final String ATTACK_FORMAT = TConstruct.makeTranslationKey("modifier", "attack_toggled");
  /** Key for denoting toggled to interaction in tooltip */
  private static final String INTERACT_FORMAT = TConstruct.makeTranslationKey("modifier", "interact_toggled");

  /** Persistent data key for toggling modifiers */
  @Getter
  private final ResourceLocation key;

  InteractionSource(String key) {
    this.key = TConstruct.getResource(key);
  }

  /** Translates the context to a slot for the sake of breaking animations */
  public EquipmentSlot getSlot(InteractionHand hand) {
    return switch (this) {
      case RIGHT_CLICK -> switch (hand) {
        case MAIN_HAND -> EquipmentSlot.MAINHAND;
        case OFF_HAND -> EquipmentSlot.OFFHAND;
      };
      case LEFT_CLICK -> EquipmentSlot.MAINHAND;
      case ARMOR -> EquipmentSlot.CHEST;
    };
  }

  /**
   * Translates the equipment slot to an interaction source. Will never return {@link #LEFT_CLICK}.
   * @param slot  Original slot
   * @return  Proper interaction source
   */
  public static InteractionSource fromEquipmentSlot(EquipmentSlot slot) {
    return switch (slot.getType()) {
      case ARMOR -> ARMOR;
      case HAND -> RIGHT_CLICK;
    };
  }

  /** Adds the format string to the modifier name */
  public static Component formatModifierName(IToolStackView tool, Modifier modifier, Component originalName) {
    if (ModifierSetWorktableRecipe.isInSet(tool.getPersistentData(), InteractionSource.LEFT_CLICK.getKey(), modifier.getId())) {
      return modifier.applyStyle(Component.translatable(ATTACK_FORMAT, originalName));
    }
    if (ModifierSetWorktableRecipe.isInSet(tool.getPersistentData(), InteractionSource.RIGHT_CLICK.getKey(), modifier.getId())) {
      return modifier.applyStyle(Component.translatable(INTERACT_FORMAT, originalName));
    }
    return originalName;
  }
}
