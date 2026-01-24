package slimeknights.tconstruct.library.modifiers.hook.interaction;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Collection;
import java.util.List;

/**
 * Hooks for standard interaction logic though entities. See {@link GeneralInteractionModifierHook} for general interaction and {@link BlockInteractionModifierHook} for blocks.
 */
public interface EntityInteractionModifierHook {
  /** Modifier volatile data key to disable melee attacks against monsters */
  ResourceLocation NO_MELEE = TConstruct.getResource("no_melee");

  /**
	 * Called when interacting with an entity before standard entity interaction.
   * In general, its better to use {@link #afterEntityUse(IToolStackView, ModifierEntry, Player, LivingEntity, InteractionHand, InteractionSource)} for behavior more consistent with vanilla.
   * @param tool       Tool performing interaction
   * @param modifier   Modifier instance
   * @param player     Interacting player
   * @param target     Target of interaction
   * @param hand       Hand used for interaction
   * @param source     Source of the interaction
   * @return  Return PASS or FAIL to allow vanilla handling, any other to stop vanilla and later modifiers from running.
	 */
  default InteractionResult beforeEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, Entity target, InteractionHand hand, InteractionSource source) {
    return InteractionResult.PASS;
  }

  /**
   * Called when interacting with an entity after standard entity interaction.
   * @param tool       Tool performing interaction
   * @param modifier   Modifier instance
   * @param player     Interacting player
   * @param target     Target of interaction
   * @param hand       Hand used for interaction
   * @param source     Source of the interaction
   * @return  Return PASS or FAIL to allow vanilla handling, any other to stop vanilla and later modifiers from running.
   */
  default InteractionResult afterEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, LivingEntity target, InteractionHand hand, InteractionSource source) {
    return InteractionResult.PASS;
  }

  /** Logic to merge multiple interaction hooks into one */
  record FirstMerger(Collection<EntityInteractionModifierHook> modules) implements EntityInteractionModifierHook {
    @Override
    public InteractionResult beforeEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, Entity target, InteractionHand hand, InteractionSource source) {
      InteractionResult result = InteractionResult.PASS;
      for (EntityInteractionModifierHook module : modules) {
        result = module.beforeEntityUse(tool, modifier, player, target, hand, source);
        if (result.consumesAction()) {
          return result;
        }
      }
      return result;
    }

    @Override
    public InteractionResult afterEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, LivingEntity target, InteractionHand hand, InteractionSource source) {
      InteractionResult result = InteractionResult.PASS;
      for (EntityInteractionModifierHook module : modules) {
        result = module.afterEntityUse(tool, modifier, player, target, hand, source);
        if (result.consumesAction()) {
          return result;
        }
      }
      return result;
    }
  }


  /** Logic to left click an entity using interaction modifiers */
  static boolean leftClickEntity(ItemStack stack, Player player, Entity target) {
    ToolStack tool = ToolStack.from(stack);
    if (stack.is(TinkerTags.Items.INTERACTABLE_LEFT)) {
      boolean noMelee = tool.getVolatileData().getBoolean(NO_MELEE);
      if (!player.getCooldowns().isOnCooldown(stack.getItem())) {
        List<ModifierEntry> modifiers = tool.getModifierList();
        // TODO: should this be in the event?
        for (ModifierEntry entry : modifiers) {
          if (entry.getHook(ModifierHooks.ENTITY_INTERACT).beforeEntityUse(tool, entry, player, target, InteractionHand.MAIN_HAND, InteractionSource.LEFT_CLICK).consumesAction()) {
            return true;
          }
        }
        if (target instanceof LivingEntity living) {
          for (ModifierEntry entry : modifiers) {
            if (entry.getHook(ModifierHooks.ENTITY_INTERACT).afterEntityUse(tool, entry, player, living, InteractionHand.MAIN_HAND, InteractionSource.LEFT_CLICK).consumesAction()) {
              return true;
            }
          }
        }
        // if melee attacks are disabled, we don't actually end up running right click hooks so run them here
        // also run the hook if we are not a melee weapon, as we don't need to bother with the 1 damage hit then
        if (noMelee || !tool.hasTag(TinkerTags.Items.MELEE)) {
          for (ModifierEntry entry : modifiers) {
            if (entry.getHook(ModifierHooks.GENERAL_INTERACT).onToolUse(tool, entry, player, InteractionHand.MAIN_HAND, InteractionSource.LEFT_CLICK).consumesAction()) {
              return true;
            }
          }
        }
      }
      // block vanilla left click when melee is disabled, not just our left click
      // block even on cooldown
      if (noMelee) {
        return true;
      }
    }
    // no left click modifiers? fallback to standard attack
    return ToolAttackUtil.attackEntity(tool, player, target);
  }
}
