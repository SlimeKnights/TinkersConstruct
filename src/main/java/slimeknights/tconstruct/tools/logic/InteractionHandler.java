package slimeknights.tconstruct.tools.logic;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorInteractModifier;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.function.Function;

/**
 * This class handles interaction based event hooks
 */
@EventBusSubscriber(modid = TConstruct.MOD_ID, bus = Bus.FORGE)
public class InteractionHandler {

  /**
   * Implements {@link slimeknights.tconstruct.library.modifiers.Modifier#beforeEntityUse(IModifierToolStack, int, Player, Entity, InteractionHand, EquipmentSlot)}
   * Also implements {@link slimeknights.tconstruct.library.modifiers.Modifier#afterEntityUse(IModifierToolStack, int, Player, LivingEntity, InteractionHand, EquipmentSlot)} for chestplates
   * TODO: update for main branch
   */
  @SubscribeEvent(priority = EventPriority.LOW)
  static void interactWithEntity(EntityInteract event) {
    ItemStack stack = event.getItemStack();
    Player player = event.getPlayer();
    InteractionHand hand = event.getHand();
    EquipmentSlot slotType = Util.getSlotType(hand);
    if (!TinkerTags.Items.HELD.contains(stack.getItem())) {
      // if the hand is empty, allow performing chestplate interaction (assuming a modifiable chestplate)
      if (stack.isEmpty()) {
        stack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (TinkerTags.Items.CHESTPLATES.contains(stack.getItem())) {
          slotType = EquipmentSlot.CHEST;
        } else {
          return;
        }
      } else {
        return;
      }
    }
    // actual interaction hook
    ToolStack tool = ToolStack.from(stack);
    Entity target = event.getTarget();
    for (ModifierEntry entry : tool.getModifierList()) {
      // exit on first successful result
      InteractionResult result = entry.getModifier().beforeEntityUse(tool, entry.getLevel(), player, target, hand, slotType);
      if (result.consumesAction()) {
        event.setCanceled(true);
        event.setCancellationResult(result);
        return;
      }
    }

    if (slotType == EquipmentSlot.CHEST) {
      // from this point on, we are taking over interaction logic, to ensure chestplate hooks run in the right order
      event.setCanceled(true);

      // initial entity interaction
      InteractionResult result = target.interact(player, hand);
      if (result.consumesAction()) {
        event.setCancellationResult(result);
        return;
      }

      // after entity use for chestplates
      if (target instanceof LivingEntity livingTarget) {
        for (ModifierEntry entry : tool.getModifierList()) {
          // exit on first successful result
          result = entry.getModifier().afterEntityUse(tool, entry.getLevel(), player, livingTarget, hand, slotType);
          if (result.consumesAction()) {
            event.setCanceled(true);
            event.setCancellationResult(result);
            return;
          }
        }
      }

      // did not interact with an entity? try direct interaction
      // needs to be run here as the interact empty hook does not fire when targeting entities
      result = onChestplateUse(player, stack, hand);
      event.setCancellationResult(result);
    }
  }

  /** Runs one of the two blockUse hooks for a chestplate */
  private static InteractionResult onBlockUse(UseOnContext context, IModifierToolStack tool, ItemStack stack, Function<ModifierEntry, InteractionResult> callback) {
    Player player = context.getPlayer();
    Level world = context.getLevel();
    BlockInWorld info = new BlockInWorld(world, context.getClickedPos(), false);
    if (player != null && !player.getAbilities().mayBuild && !stack.hasAdventureModePlaceTagForBlock(world.getTagManager(), info)) {
      return InteractionResult.PASS;
    }

    // run modifier hook
    for (ModifierEntry entry : tool.getModifierList()) {
      InteractionResult result = callback.apply(entry);
      if (result.consumesAction()) {
        if (player != null) {
          player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
        }
        return result;
      }
    }
    return InteractionResult.PASS;
  }

  /** Implements modifier hooks for a chestplate right clicking a block with an empty hand */
  @SubscribeEvent(priority = EventPriority.LOW)
  static void chestplateInteractWithBlock(PlayerInteractEvent.RightClickBlock event) {
    // only handle chestplate interacts if the current hand is empty
    Player player = event.getPlayer();
    if (event.getItemStack().isEmpty() && !player.isSpectator()) {
      // item must be a chestplate
      ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
      if (TinkerTags.Items.CHESTPLATES.contains(chestplate.getItem())) {
        // no turning back, from this point we are fully in charge of interaction logic (since we need to ensure order of the hooks)

        // begin interaction
        ToolStack tool = ToolStack.from(chestplate);
        InteractionHand hand = event.getHand();
        BlockHitResult trace = event.getHitVec();
        UseOnContext context = new UseOnContext(player, hand, trace);

        // first, before block use (in forge, onItemUseFirst)
        if (event.getUseItem() != Result.DENY) {
          InteractionResult result = onBlockUse(context, tool, chestplate, entry -> entry.getModifier().beforeBlockUse(tool, entry.getLevel(), context, EquipmentSlot.CHEST));
          if (result.consumesAction()) {
            event.setCanceled(true);
            event.setCancellationResult(result);
            return;
          }
        }

        // next, block interaction, simplified by declaring that the chestplate never bypasses use
        BlockPos pos = event.getPos();
        Result useBlock = event.getUseBlock();
        if (useBlock == Result.ALLOW || (useBlock != Result.DENY && !player.isSecondaryUseActive())) {
          InteractionResult result = player.level.getBlockState(pos).use(player.level, player, hand, trace);
          if (result.consumesAction()) {
            if (player instanceof ServerPlayer serverPlayer) {
              CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, ItemStack.EMPTY);
            }
            event.setCanceled(true);
            event.setCancellationResult(result);
            return;
          }
        }

        // regular item interaction: must not be deny, and either be allow or not have a cooldown
        Result useItem = event.getUseItem();
        event.setCancellationResult(InteractionResult.PASS);
        if (useItem != Result.DENY && (useItem == Result.ALLOW || !player.getCooldowns().isOnCooldown(chestplate.getItem()))) {
          // finally, after block use (in forge, onItemUse)
          InteractionResult result = onBlockUse(context, tool, chestplate, entry -> entry.getModifier().afterBlockUse(tool, entry.getLevel(), context, EquipmentSlot.CHEST));
          if (result.consumesAction()) {
            event.setCanceled(true);
            event.setCancellationResult(result);
            if (player instanceof ServerPlayer serverPlayer) {
              CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, ItemStack.EMPTY);
            }
            return;
          }
        }

        // did not interact with an entity? try direct interaction
        // needs to be run here as the interact empty hook does not fire when targeting blocks
        InteractionResult result = onChestplateUse(player, chestplate, hand);
        event.setCanceled(true);
        event.setCancellationResult(result);
      }
    }
  }

  /** Implements {@link slimeknights.tconstruct.library.modifiers.Modifier#onToolUse(IModifierToolStack, int, net.minecraft.world.level.Level, Player, InteractionHand, EquipmentSlot)}, called differently on client and server */
  public static InteractionResult onChestplateUse(Player player, ItemStack chestplate, InteractionHand hand) {
    if (player.getCooldowns().isOnCooldown(chestplate.getItem())) {
      return InteractionResult.PASS;
    }

    // first, run the modifier hook
    ToolStack tool = ToolStack.from(chestplate);
    for (ModifierEntry entry : tool.getModifierList()) {
      InteractionResult result = entry.getModifier().onToolUse(tool, entry.getLevel(), player.level, player, hand, EquipmentSlot.CHEST);
      if (result.consumesAction()) {
        return result;
      }
    }
    return InteractionResult.PASS;
  }

  /** Handles attacking using the chestplate */
  @SubscribeEvent
  static void onChestplateAttack(AttackEntityEvent event) {
    Player attacker = event.getPlayer();
    if (attacker.getMainHandItem().isEmpty()) {
      ItemStack chestplate = attacker.getItemBySlot(EquipmentSlot.CHEST);
      if (TinkerTags.Items.CHESTPLATES.contains(chestplate.getItem())) {
        ToolStack tool = ToolStack.from(chestplate);
        if (!tool.isBroken() && tool.getModifierLevel(TinkerModifiers.unarmed.get()) > 0) {
          ToolAttackUtil.attackEntity(tool, attacker, InteractionHand.MAIN_HAND, event.getTarget(), ToolAttackUtil.getCooldownFunction(attacker, InteractionHand.MAIN_HAND), false, EquipmentSlot.CHEST);
          event.setCanceled(true);
        }
      }
    }
  }

  /**
   * Handles interaction from a helmet
   * @param player  Player instance
   * @return true if the player has a modifiable helmet
   */
  public static boolean startArmorInteract(Player player, EquipmentSlot slotType) {
    if (!player.isSpectator()) {
      ItemStack helmet = player.getItemBySlot(slotType);
      if (TinkerTags.Items.ARMOR.contains(helmet.getItem())) {
        ToolStack tool = ToolStack.from(helmet);
        for (ModifierEntry entry : tool.getModifierList()) {
          IArmorInteractModifier helmetInteract = entry.getModifier().getModule(IArmorInteractModifier.class);
          if (helmetInteract != null && helmetInteract.startArmorInteract(tool, entry.getLevel(), player, slotType)) {
            break;
          }
        }
        return true;
      }
    }
    return false;
  }

  /**
   * Notifies modifiers the helmet keybind was released
   * @param player  Player instance
   * @return true if the player has a modifiable helmet
   */
  public static boolean stopArmorInteract(Player player, EquipmentSlot slotType) {
    if (!player.isSpectator()) {
      ItemStack helmet = player.getItemBySlot(slotType);
      if (TinkerTags.Items.ARMOR.contains(helmet.getItem())) {
        ToolStack tool = ToolStack.from(helmet);
        for (ModifierEntry entry : tool.getModifierList()) {
          IArmorInteractModifier helmetInteract = entry.getModifier().getModule(IArmorInteractModifier.class);
          if (helmetInteract != null) {
            helmetInteract.stopArmorInteract(tool, entry.getLevel(), player, slotType);
          }
        }
        return true;
      }
    }
    return false;
  }
}
