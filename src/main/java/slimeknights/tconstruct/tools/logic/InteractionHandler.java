package slimeknights.tconstruct.tools.logic;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

/**
 * This class handles interaction based event hooks
 */
@EventBusSubscriber(modid = TConstruct.MOD_ID, bus = Bus.FORGE)
public class InteractionHandler {
  public static final EquipmentSlot[] HAND_SLOTS = {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};

  /** Implements {@link EntityInteractionModifierHook#beforeEntityUse(IToolStackView, ModifierEntry, Player, Entity, InteractionHand, InteractionSource)} */
  @SubscribeEvent
  static void beforeEntityInteract(EntityInteract event) {
    ItemStack stack = event.getItemStack();
    Player player = event.getEntity();
    InteractionHand hand = event.getHand();
    InteractionSource source = InteractionSource.RIGHT_CLICK;
    if (!stack.is(TinkerTags.Items.HELD)) {
      // if the hand is empty, allow performing chestplate interaction (assuming a modifiable chestplate)
      if (stack.isEmpty()) {
        stack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (stack.is(TinkerTags.Items.INTERACTABLE_ARMOR)) {
          source = InteractionSource.ARMOR;
        } else {
          return;
        }
      } else {
        return;
      }
    }
    if (!player.getCooldowns().isOnCooldown(stack.getItem())) {
      // actual interaction hook
      ToolStack tool = ToolStack.from(stack);
      Entity target = event.getTarget();
      for (ModifierEntry entry : tool.getModifierList()) {
        // exit on first successful result
        InteractionResult result = entry.getHook(ModifierHooks.ENTITY_INTERACT).beforeEntityUse(tool, entry, player, target, hand, source);
        if (result.consumesAction()) {
          event.setCanceled(true);
          event.setCancellationResult(result);
          return;
        }
      }
    }
  }

  /** Implements {@link EntityInteractionModifierHook#afterEntityUse(IToolStackView, ModifierEntry, Player, LivingEntity, InteractionHand, InteractionSource)} for chestplates */
  @SubscribeEvent(priority = EventPriority.LOWEST)
  static void afterEntityInteract(EntityInteract event) {
    Player player = event.getEntity();
    if (event.getItemStack().isEmpty() && !player.isSpectator()) {
      ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
      if (chestplate.is(TinkerTags.Items.INTERACTABLE_ARMOR) && !player.getCooldowns().isOnCooldown(chestplate.getItem())) {
        // from this point on, we are taking over interaction logic, to ensure chestplate hooks run in the right order
        event.setCanceled(true);

        ToolStack tool = ToolStack.from(chestplate);
        Entity target = event.getTarget();
        InteractionHand hand = event.getHand();

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
            result = entry.getHook(ModifierHooks.ENTITY_INTERACT).afterEntityUse(tool, entry, player, livingTarget, hand, InteractionSource.ARMOR);
            if (result.consumesAction()) {
              event.setCanceled(true);
              event.setCancellationResult(result);
              return;
            }
          }
        }

        // did not interact with an entity? try direct interaction
        // needs to be run here as the interact empty hook does not fire when targeting entities
        result = onChestplateUse(player, chestplate, hand);
        event.setCancellationResult(result);
      }
    }
  }

  /** Runs one of the two blockUse hooks for a chestplate */
  private static InteractionResult onBlockUse(UseOnContext context, IToolStackView tool, ItemStack stack, Function<ModifierEntry, InteractionResult> callback) {
    Player player = context.getPlayer();
    Level world = context.getLevel();
    BlockInWorld info = new BlockInWorld(world, context.getClickedPos(), false);
    if (player != null && !player.getAbilities().mayBuild && !stack.hasAdventureModePlaceTagForBlock(BuiltInRegistries.BLOCK, info)) {
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
  @SubscribeEvent(priority = EventPriority.LOWEST)
  static void chestplateInteractWithBlock(PlayerInteractEvent.RightClickBlock event) {
    // only handle chestplate interacts if the current hand is empty
    Player player = event.getEntity();
    if (event.getItemStack().isEmpty() && !player.isSpectator()) {
      // item must be a chestplate
      ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
      if (chestplate.is(TinkerTags.Items.INTERACTABLE_ARMOR) && !player.getCooldowns().isOnCooldown(chestplate.getItem())) {
        // no turning back, from this point we are fully in charge of interaction logic (since we need to ensure order of the hooks)

        // begin interaction
        ToolStack tool = ToolStack.from(chestplate);
        InteractionHand hand = event.getHand();
        BlockHitResult trace = event.getHitVec();
        UseOnContext context = new UseOnContext(player, hand, trace);

        // first, before block use (in forge, onItemUseFirst)
        if (event.getUseItem() != Result.DENY) {
          InteractionResult result = onBlockUse(context, tool, chestplate, entry -> entry.getHook(ModifierHooks.BLOCK_INTERACT).beforeBlockUse(tool, entry, context, InteractionSource.ARMOR));
          if (result.consumesAction()) {
            event.setCanceled(true);
            event.setCancellationResult(result);
            return;
          }
        }

        // next, block interaction
        // empty stack automatically bypasses sneak, so no need to check the hand we interacted with, just need to check the other hand
        BlockPos pos = event.getPos();
        Result useBlock = event.getUseBlock();
        Level level = player.level();
        if (useBlock == Result.ALLOW || (useBlock != Result.DENY
                                         && (!player.isSecondaryUseActive() || player.getItemInHand(Util.getOpposite(hand)).doesSneakBypassUse(level, pos, player)))) {
          InteractionResult result = level.getBlockState(pos).use(level, player, hand, trace);
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
          InteractionResult result = onBlockUse(context, tool, chestplate, entry -> entry.getHook(ModifierHooks.BLOCK_INTERACT).afterBlockUse(tool, entry, context, InteractionSource.ARMOR));
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

  /** Implements {@link GeneralInteractionModifierHook#onToolUse(IToolStackView, ModifierEntry, Player, InteractionHand, InteractionSource)}, called differently on client and server */
  public static InteractionResult onChestplateUse(Player player, ItemStack chestplate, InteractionHand hand) {
    if (player.getCooldowns().isOnCooldown(chestplate.getItem())) {
      return InteractionResult.PASS;
    }

    // first, run the modifier hook
    ToolStack tool = ToolStack.from(chestplate);
    for (ModifierEntry entry : tool.getModifierList()) {
      InteractionResult result = entry.getHook(ModifierHooks.GENERAL_INTERACT).onToolUse(tool, entry, player, hand, InteractionSource.ARMOR);
      if (result.consumesAction()) {
        return result;
      }
    }
    return InteractionResult.PASS;
  }

  /** Handles attacking using the chestplate */
  @SubscribeEvent(priority = EventPriority.LOW)
  static void onChestplateAttack(AttackEntityEvent event) {
    // Carry On is dumb and fires the attack entity event when they are not attacking entities, causing us to punch instead
    // they should not be doing that, but the author has not done anything to fix it, so just use a hacky check
    if (event.getClass() == AttackEntityEvent.class) {
      Player attacker = event.getEntity();
      if (attacker.getMainHandItem().isEmpty()) {
        ItemStack chestplate = attacker.getItemBySlot(EquipmentSlot.CHEST);
        if (chestplate.is(TinkerTags.Items.UNARMED)) {
          ToolStack tool = ToolStack.from(chestplate);
          if (!tool.isBroken()) {
            ToolAttackUtil.attackEntity(tool, attacker, InteractionHand.MAIN_HAND, event.getTarget(), ToolAttackUtil.getCooldownFunction(attacker, InteractionHand.MAIN_HAND), false, EquipmentSlot.CHEST);
            event.setCanceled(true);
          }
        }
      }
    }
  }

  /**
   * Handles interaction from a helmet
   * @param player  Player instance
   * @return true if the player has a modifiable helmet
   */
  public static boolean startArmorInteract(Player player, EquipmentSlot slotType, TooltipKey modifierKey) {
    if (!player.isSpectator()) {
      ItemStack helmet = player.getItemBySlot(slotType);
      if (helmet.is(TinkerTags.Items.ARMOR)) {
        ToolStack tool = ToolStack.from(helmet);
        for (ModifierEntry entry : tool.getModifierList()) {
          if (entry.getHook(ModifierHooks.ARMOR_INTERACT).startInteract(tool, entry, player, slotType, modifierKey)) {
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
      if (helmet.is(TinkerTags.Items.ARMOR)) {
        ToolStack tool = ToolStack.from(helmet);
        for (ModifierEntry entry : tool.getModifierList()) {
          entry.getHook(ModifierHooks.ARMOR_INTERACT).stopInteract(tool, entry, player, slotType);
        }
        return true;
      }
    }
    return false;
  }

  /** Runs the left click interaction for left click */
  private static InteractionResult onLeftClickInteraction(IToolStackView tool, Player player, InteractionHand hand) {
    for (ModifierEntry entry : tool.getModifierList()) {
      InteractionResult result = entry.getHook(ModifierHooks.GENERAL_INTERACT).onToolUse(tool, entry, player, hand, InteractionSource.LEFT_CLICK);
      if (result.consumesAction()) {
        return result;
      }
    }
    return InteractionResult.PASS;
  }

  /** Runs the left click interaction for left click */
  public static InteractionResult onLeftClickInteraction(Player player, ItemStack held, InteractionHand hand) {
    if (player.getCooldowns().isOnCooldown(held.getItem())) {
      return InteractionResult.PASS;
    }
    return onLeftClickInteraction(ToolStack.from(held), player, hand);
  }

  /** Sets the event result and swings the hand */
  private static void setLeftClickEventResult(PlayerInteractEvent event, InteractionResult result) {
    if (result.consumesAction()) {
      // success means swing hand
      if (result == InteractionResult.SUCCESS) {
        event.getEntity().swing(event.getHand());
      }
      event.setCancellationResult(result);
      // don't cancel the result in survival as it does not actually prevent breaking the block, just causes really weird desyncs
      // leaving uncanceled lets us still do blocky stuff but if you hold click it digs
      if (event.getEntity().getAbilities().instabuild) {
        event.setCanceled(true);
      }
    }
  }

  /** Simple class to track the last tick */
  private static class LastTick {
    private long lastTick = 0;

    /**
     * Attempts to update the given player
     * @return  True if we are ready to interact again
     */
    private boolean update(Player player) {
      if (player.tickCount >= lastTick + 4) {
        lastTick = player.tickCount;
        return true;
      }
      return false;
    }
  }
  /** Key for the tick tracker instance */
  private static final ComputableDataKey<LastTick> LAST_TICK = TConstruct.createKey("last_tick", LastTick::new);

  /** Implements {@link slimeknights.tconstruct.library.modifiers.hook.interaction.BlockInteractionModifierHook} for weapons with left click */
  @SubscribeEvent
  static void leftClickBlock(LeftClickBlock event) {
    // ensure we have not fired this tick
    Player player = event.getEntity();
    if (player.getCapability(TinkerDataCapability.CAPABILITY).filter(data -> data.computeIfAbsent(LAST_TICK).update(player)).isEmpty()) {
      return;
    }
    // must support interaction
    ItemStack stack = event.getItemStack();
    if (!stack.is(TinkerTags.Items.INTERACTABLE_LEFT) || player.getCooldowns().isOnCooldown(stack.getItem())) {
      return;
    }

    // build usage context
    InteractionHand hand = event.getHand();
    BlockPos pos = event.getPos();
    Direction direction = event.getFace();
    if (direction == null) {
      direction = player.getDirection().getOpposite();
    }
    UseOnContext context = new UseOnContext(player, hand, new BlockHitResult(Util.toHitVec(pos, direction), direction, pos, false));

    // run modifier hooks
    ToolStack tool = ToolStack.from(stack);
    List<ModifierEntry> modifiers = tool.getModifierList();
    for (ModifierEntry entry : modifiers) {
      InteractionResult result = entry.getHook(ModifierHooks.BLOCK_INTERACT).beforeBlockUse(tool, entry, context, InteractionSource.LEFT_CLICK);
      if (result.consumesAction()) {
        setLeftClickEventResult(event, result);
        // always cancel block interaction, prevents breaking glows/fires
        event.setCanceled(true);
        return;
      }
    }
    // TODO: don't think there is an equivalence to block interactions
    for (ModifierEntry entry : modifiers) {
      InteractionResult result = entry.getHook(ModifierHooks.BLOCK_INTERACT).afterBlockUse(tool, entry, context, InteractionSource.LEFT_CLICK);
      if (result.consumesAction()) {
        setLeftClickEventResult(event, result);
        // always cancel block interaction, prevents breaking glows/fires
        event.setCanceled(true);
        return;
      }
    }

    // fallback to default interaction
    InteractionResult result = onLeftClickInteraction(tool, player, hand);
    if (result.consumesAction()) {
      setLeftClickEventResult(event, result);
    }
  }

  /** Checks if the shield block angle allows blocking this attack */
  public static boolean canBlock(LivingEntity holder, @Nullable Vec3 sourcePosition, IToolStackView tool) {
    // source position should never be null (checked by livingentity) but safety as its marked nullable
    if (sourcePosition == null) {
      return false;
    }
    // divide by 2 as the stat is 0 to 180 (more intutive) but logic is 0 to 90 (simplier to work with)
    // we could potentially do a quick exit here, but that would mean this method is not applicable for modifiers like reflection
    // that skip the vanilla check first
    float blockAngle = ConditionalStatModifierHook.getModifiedStat(tool, holder, ToolStats.BLOCK_ANGLE) / 2;

    // want the angle between the view vector and the
    Vec3 viewVector = holder.getViewVector(1.0f);
    Vec3 entityPosition = holder.position();
    Vec3 direction = new Vec3(entityPosition.x - sourcePosition.x, 0, entityPosition.z - sourcePosition.z);
    double length = viewVector.length() * direction.length();
    // prevent zero vector from messing with us
    if (length < 1.0E-4D) {
      return false;
    }
    // acos will return between 90 and 270, we want an absolute angle from 0 to 180
    double angle = Math.abs(180 - Math.acos(direction.dot(viewVector) / length) * Mth.RAD_TO_DEG);
    return blockAngle >= angle;
  }

  /** Implements shield stats */
  @SubscribeEvent
  static void onBlock(ShieldBlockEvent event) {
    LivingEntity entity = event.getEntity();
    ItemStack activeStack = entity.getUseItem();
    if (!activeStack.isEmpty() && activeStack.is(TinkerTags.Items.MODIFIABLE)) {
      ToolStack tool = ToolStack.from(activeStack);
      // first check block angle
      if (!tool.isBroken() && canBlock(event.getEntity(), event.getDamageSource().getSourcePosition(), tool)) {
        // TODO: hook for conditioning block amount based on on damage type
        event.setBlockedDamage(Math.min(event.getBlockedDamage(), tool.getStats().get(ToolStats.BLOCK_AMOUNT)));
        // TODO: consider handling the item damage ourself
      } else {
        event.setCanceled(true);
      }
    }
  }
}
