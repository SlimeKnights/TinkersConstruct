package slimeknights.tconstruct.tools.logic;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorInteractModifier;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.item.IModifiableWeapon;
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
   * Implements {@link slimeknights.tconstruct.library.modifiers.Modifier#beforeEntityUse(IModifierToolStack, int, PlayerEntity, Entity, Hand, EquipmentSlotType)}
   * Also implements {@link slimeknights.tconstruct.library.modifiers.Modifier#afterEntityUse(IModifierToolStack, int, PlayerEntity, LivingEntity, Hand, EquipmentSlotType)} for chestplates
   */
  @SubscribeEvent
  static void interactWithEntity(EntityInteract event) {
    ItemStack stack = event.getItemStack();
    PlayerEntity player = event.getPlayer();
    Hand hand = event.getHand();
    EquipmentSlotType slotType = Util.getSlotType(hand);
    if (!TinkerTags.Items.HELD.contains(stack.getItem())) {
      // if the hand is empty, allow performing chestplate interaction (assuming a modifiable chestplate)
      if (stack.isEmpty()) {
        stack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (TinkerTags.Items.CHESTPLATES.contains(stack.getItem())) {
          slotType = EquipmentSlotType.CHEST;
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
      ActionResultType result = entry.getModifier().beforeEntityUse(tool, entry.getLevel(), player, target, hand, slotType);
      if (result.isSuccessOrConsume()) {
        event.setCanceled(true);
        event.setCancellationResult(result);
        return;
      }
    }

    if (slotType == EquipmentSlotType.CHEST) {
      // from this point on, we are taking over interaction logic, to ensure chestplate hooks run in the right order
      event.setCanceled(true);

      // initial entity interaction
      ActionResultType result = target.processInitialInteract(player, hand);
      if (result.isSuccessOrConsume()) {
        event.setCancellationResult(result);
        return;
      }

      // after entity use for chestplates
      if (target instanceof LivingEntity) {
        LivingEntity livingTarget = (LivingEntity) target;
        for (ModifierEntry entry : tool.getModifierList()) {
          // exit on first successful result
          result = entry.getModifier().afterEntityUse(tool, entry.getLevel(), player, livingTarget, hand, slotType);
          if (result.isSuccessOrConsume()) {
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
  private static ActionResultType onBlockUse(ItemUseContext context, IModifierToolStack tool, ItemStack stack, Function<ModifierEntry, ActionResultType> callback) {
    PlayerEntity player = context.getPlayer();
    CachedBlockInfo cachedblockinfo = new CachedBlockInfo(context.getWorld(), context.getPos(), false);
    if (player != null && !player.abilities.allowEdit && !stack.canPlaceOn(context.getWorld().getTags(), cachedblockinfo)) {
      return ActionResultType.PASS;
    }

    // run modifier hook
    for (ModifierEntry entry : tool.getModifierList()) {
      ActionResultType result = callback.apply(entry);
      if (result.isSuccessOrConsume()) {
        if (player != null) {
          player.addStat(Stats.ITEM_USED.get(stack.getItem()));
        }
        return result;
      }
    }
    return ActionResultType.PASS;
  }

  /** Implements modifier hooks for a chestplate right clicking a block with an empty hand */
  @SubscribeEvent
  static void chestplateInteractWithBlock(PlayerInteractEvent.RightClickBlock event) {
    // only handle chestplate interacts if the current hand is empty
    PlayerEntity player = event.getPlayer();
    if (event.getItemStack().isEmpty() && !player.isSpectator()) {
      // item must be a chestplate
      ItemStack chestplate = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
      if (TinkerTags.Items.CHESTPLATES.contains(chestplate.getItem())) {
        // no turning back, from this point we are fully in charge of interaction logic (since we need to ensure order of the hooks)

        // begin interaction
        ToolStack tool = ToolStack.from(chestplate);
        Hand hand = event.getHand();
        BlockRayTraceResult trace = event.getHitVec();
        ItemUseContext context = new ItemUseContext(player, hand, trace);

        // first, before block use (in forge, onItemUseFirst)
        if (event.getUseItem() != Result.DENY) {
          ActionResultType result = onBlockUse(context, tool, chestplate, entry -> entry.getModifier().beforeBlockUse(tool, entry.getLevel(), context, EquipmentSlotType.CHEST));
          if (result.isSuccessOrConsume()) {
            event.setCanceled(true);
            event.setCancellationResult(result);
            return;
          }
        }

        // next, block interaction, simplified by declaring that the chestplate never bypasses use
        BlockPos pos = event.getPos();
        Result useBlock = event.getUseBlock();
        if (useBlock == Result.ALLOW || (useBlock != Result.DENY && !player.isSecondaryUseActive())) {
          World world = player.getEntityWorld();
          ActionResultType result = world.getBlockState(pos).onBlockActivated(world, player, hand, trace);
          if (result.isSuccessOrConsume()) {
            if (player instanceof ServerPlayerEntity) {
              CriteriaTriggers.RIGHT_CLICK_BLOCK_WITH_ITEM.test((ServerPlayerEntity)player, pos, ItemStack.EMPTY);
            }
            event.setCanceled(true);
            event.setCancellationResult(result);
            return;
          }
        }

        // regular item interaction: must not be deny, and either be allow or not have a cooldown
        Result useItem = event.getUseItem();
        event.setCancellationResult(ActionResultType.PASS);
        if (useItem != Result.DENY && (useItem == Result.ALLOW || !player.getCooldownTracker().hasCooldown(chestplate.getItem()))) {
          // finally, after block use (in forge, onItemUse)
          ActionResultType result = onBlockUse(context, tool, chestplate, entry -> entry.getModifier().afterBlockUse(tool, entry.getLevel(), context, EquipmentSlotType.CHEST));
          if (result.isSuccessOrConsume()) {
            event.setCanceled(true);
            event.setCancellationResult(result);
            if (player instanceof ServerPlayerEntity) {
              CriteriaTriggers.RIGHT_CLICK_BLOCK_WITH_ITEM.test((ServerPlayerEntity) player, pos, ItemStack.EMPTY);
            }
            return;
          }
        }

        // did not interact with an entity? try direct interaction
        // needs to be run here as the interact empty hook does not fire when targeting blocks
        ActionResultType result = onChestplateUse(player, chestplate, hand);
        event.setCanceled(true);
        event.setCancellationResult(result);
      }
    }
  }

  /** Implements {@link slimeknights.tconstruct.library.modifiers.Modifier#onToolUse(IModifierToolStack, int, World, PlayerEntity, Hand, EquipmentSlotType)}, called differently on client and server */
  public static ActionResultType onChestplateUse(PlayerEntity player, ItemStack chestplate, Hand hand) {
    if (player.getCooldownTracker().hasCooldown(chestplate.getItem())) {
      return ActionResultType.PASS;
    }

    // first, run the modifier hook
    ToolStack tool = ToolStack.from(chestplate);
    World world = player.getEntityWorld();
    for (ModifierEntry entry : tool.getModifierList()) {
      ActionResultType result = entry.getModifier().onToolUse(tool, entry.getLevel(), world, player, hand, EquipmentSlotType.CHEST);
      if (result.isSuccessOrConsume()) {
        return result;
      }
    }
    return ActionResultType.PASS;
  }

  /** Handles attacking using the chestplate */
  @SubscribeEvent
  static void onChestplateAttack(AttackEntityEvent event) {
    PlayerEntity attacker = event.getPlayer();
    if (attacker.getHeldItemMainhand().isEmpty()) {
      ItemStack chestplate = attacker.getItemStackFromSlot(EquipmentSlotType.CHEST);
      if (TinkerTags.Items.CHESTPLATES.contains(chestplate.getItem())) {
        ToolStack tool = ToolStack.from(chestplate);
        if (!tool.isBroken() && tool.getModifierLevel(TinkerModifiers.unarmed.get()) > 0) {
          ToolAttackUtil.attackEntity(IModifiableWeapon.DEFAULT, tool, attacker, Hand.MAIN_HAND, event.getTarget(), ToolAttackUtil.getCooldownFunction(attacker, Hand.MAIN_HAND), false, EquipmentSlotType.CHEST);
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
  public static boolean startHelmetInteract(PlayerEntity player) {
    if (!player.isSpectator()) {
      ItemStack helmet = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
      if (TinkerTags.Items.HELMETS.contains(helmet.getItem())) {
        ToolStack tool = ToolStack.from(helmet);
        for (ModifierEntry entry : tool.getModifierList()) {
          IArmorInteractModifier helmetInteract = entry.getModifier().getModule(IArmorInteractModifier.class);
          if (helmetInteract != null && helmetInteract.startArmorInteract(tool, entry.getLevel(), player, EquipmentSlotType.HEAD)) {
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
  public static boolean stopHelmetInteract(PlayerEntity player) {
    if (!player.isSpectator()) {
      ItemStack helmet = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
      if (TinkerTags.Items.HELMETS.contains(helmet.getItem())) {
        ToolStack tool = ToolStack.from(helmet);
        for (ModifierEntry entry : tool.getModifierList()) {
          IArmorInteractModifier helmetInteract = entry.getModifier().getModule(IArmorInteractModifier.class);
          if (helmetInteract != null) {
            helmetInteract.stopArmorInteract(tool, entry.getLevel(), player, EquipmentSlotType.HEAD);
          }
        }
        return true;
      }
    }
    return false;
  }
}
