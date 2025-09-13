package slimeknights.tconstruct.tools.modifiers.ability.interaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.eventbus.api.Event.Result;
import slimeknights.tconstruct.library.events.TinkerToolEvent.ToolShearEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolActionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.ArmorLootingModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.LootingModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ShowOffhandModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.LootingContext;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;

@RequiredArgsConstructor
public class ShearsAbilityModifier extends NoLevelsModifier implements EntityInteractionModifierHook, ToolActionModifierHook {
  private final int range;
  @Getter
  private final int priority;

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ShowOffhandModule.DISALLOW_BROKEN);
    hookBuilder.addHook(this, ModifierHooks.ENTITY_INTERACT, ModifierHooks.TOOL_ACTION);
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, @Nullable RegistryAccess access) {
    return InteractionSource.formatModifierName(tool, this, super.getDisplayName(tool, entry, access));
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return priority > Short.MIN_VALUE;
  }
  
  /**
   * Swings the given's player hand
   *
   * @param player the current player
   * @param hand the given hand the tool is in
   */
  protected void swingTool(Player player, InteractionHand hand) {
    player.swing(hand);
    player.sweepAttack();
  }

  @Override
  public boolean canPerformAction(IToolStackView tool, ModifierEntry modifier, ToolAction toolAction) {
    if (isShears(tool)) {
      return toolAction == ToolActions.SHEARS_DIG || toolAction == ToolActions.SHEARS_HARVEST || toolAction == ToolActions.SHEARS_CARVE || toolAction == ToolActions.SHEARS_DISARM;
    }
    return false;
  }

  /**
   * Checks whether the tool counts as shears for modifier logic
   *
   * @param tool  Current tool instance
   */
  protected boolean isShears(IToolStackView tool) {
    return true;
  }

  @Override
  public InteractionResult beforeEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, Entity target, InteractionHand hand, InteractionSource source) {
    if (tool.isBroken() || !tool.getHook(ToolHooks.INTERACTION).canInteract(tool, modifier.getId(), source)) {
      return InteractionResult.PASS;
    }
    EquipmentSlot slotType = source.getSlot(hand);
    ItemStack stack = player.getItemBySlot(slotType);

    // use looting instead of fortune, as that is our hook with entity access
    // modifier can always use tags or the nullable parameter to distinguish if needed
    LootingContext context = new LootingContext(player, target, null, Util.getSlotType(hand));
    int looting = LootingModifierHook.getLooting(tool, context, player.getItemInHand(hand).getEnchantmentLevel(Enchantments.MOB_LOOTING));
    looting = ArmorLootingModifierHook.getLooting(tool, context, looting);
    Level world = player.getCommandSenderWorld();
    if (isShears(tool) && shearEntity(stack, tool, world, player, target, looting)) {
      boolean broken = ToolDamageUtil.damageAnimated(tool, 1, player, slotType);
      this.swingTool(player, hand);
      runShearHook(tool, player, target, true);

      // AOE shearing
      if (!broken) {
        // if expanded, shear all in range
        int expanded = range + tool.getModifierLevel(TinkerModifiers.expanded.getId());
        if (expanded > 0) {
          for (LivingEntity aoeTarget : player.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(expanded, 0.25D, expanded))) {
            if (aoeTarget != player && aoeTarget != target && (!(aoeTarget instanceof ArmorStand) || !((ArmorStand)aoeTarget).isMarker())) {
              if (shearEntity(stack, tool, world, player, aoeTarget, looting)) {
                broken = ToolDamageUtil.damageAnimated(tool, 1, player, slotType);
                runShearHook(tool, player, aoeTarget, false);
                if (broken) {
                  break;
                }
              }
            }
          }
        }
      }

      return InteractionResult.SUCCESS;
    }

    return InteractionResult.PASS;
  }

  /** Runs the hook after shearing an entity */
  private static void runShearHook(IToolStackView tool, Player player, Entity entity, boolean isTarget) {
    for (ModifierEntry entry : tool.getModifierList()) {
      entry.getHook(ModifierHooks.SHEAR_ENTITY).afterShearEntity(tool, entry, player, entity, isTarget);
    }
  }

  /**
   * Tries to shear an given entity, returns false if it fails and true if it succeeds
   *
   * @param itemStack the current item stack
   * @param world the current world
   * @param player the current player
   * @param entity the entity to try to shear
   * @param fortune the fortune to apply to the sheared entity
   * @return if the sheering of the entity was performed or not
   */
  private static boolean shearEntity(ItemStack itemStack, IToolStackView tool, Level world, Player player, Entity entity, int fortune) {
    // event to override entity shearing
    Result result = new ToolShearEvent(itemStack, tool, world, player, entity, fortune).fire();
    if (result != Result.DEFAULT) {
      return result == Result.ALLOW;
    }
    // fallback to forge shearable
    if (entity instanceof IForgeShearable target && target.isShearable(itemStack, world, entity.blockPosition())) {
      if (!world.isClientSide) {
        target.onSheared(player, itemStack, world, entity.blockPosition(), fortune)
              .forEach(stack -> ModifierUtil.dropItem(entity, stack));
      }
      return true;
    }
    return false;
  }
}
