package slimeknights.tconstruct.tools.modules.interaction;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.mantle.util.OffhandCooldownTracker;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolActionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerToolActions;
import slimeknights.tconstruct.tools.entity.CombatFishingHook;
import slimeknights.tconstruct.tools.entity.CombatFishingHook.GrappleType;

import javax.annotation.Nullable;
import java.util.List;

/** Module implementing fishing behavior */
public enum FishingModule implements ModifierModule, GeneralInteractionModifierHook, ToolActionModifierHook, EquipmentChangeModifierHook, DisplayNameModifierHook {
  INSTANCE;

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<FishingModule>defaultHooks(ModifierHooks.GENERAL_INTERACT, ModifierHooks.TOOL_ACTION, ModifierHooks.EQUIPMENT_CHANGE, ModifierHooks.DISPLAY_NAME);
  public static final ResourceLocation HOOK_MATERIAL = TConstruct.getResource("hook_material");
  public static final RecordLoadable<FishingModule> LOADER = new SingletonLoader<>(INSTANCE);

  @Override
  public RecordLoadable<FishingModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, Component name, @Nullable RegistryAccess access) {
    return InteractionSource.formatModifierName(tool, entry.getModifier(), name);
  }

  @Override
  public boolean canPerformAction(IToolStackView tool, ModifierEntry modifier, ToolAction toolAction) {
    return toolAction == ToolActions.FISHING_ROD_CAST;
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    // disallow casting if the main hand can cast. Only comes up if the main hand is doing left click fishing; vanilla limitations means we can't support that
    if (source != InteractionSource.ARMOR && !tool.isBroken() && tool.getHook(ToolHooks.INTERACTION).canInteract(tool, modifier.getId(), source) && (hand == InteractionHand.MAIN_HAND || !player.getMainHandItem().canPerformAction(ToolActions.FISHING_ROD_CAST))) {
      Level level = player.level();
      if (player.fishing != null) {
        ItemStack stack = player.getItemInHand(hand);
        // due to fishing rod buggy behavior, chance we end up retrieving someone else's cast, so keep this logic 1 to 1 with vanilla
        if (!level.isClientSide) {
          int damage = player.fishing.retrieve(stack);
          if (damage > 0) {
            ToolDamageUtil.damageAnimated(tool, damage, player, Util.getSlotType(hand));
            // we apply cooldown as this is a weapon, don't want to let you spam it. But only need the cooldown if something happened
            ModifierUtil.addCooldown(tool, player);
          }
        }

        level.playSound( null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1, 0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f));
        player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
      } else {
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5f, 0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f));
        if (!level.isClientSide) {
          float luck = ConditionalStatModifierHook.getModifiedStat(tool, player, ToolStats.SEA_LUCK);
          float lure = ConditionalStatModifierHook.getModifiedStat(tool, player, ToolStats.LURE);
          float velocity = ConditionalStatModifierHook.getModifiedStat(tool, player, ToolStats.VELOCITY);
          float inaccuracy = ModifierUtil.getInaccuracy(tool, player);
          CombatFishingHook hook = new CombatFishingHook(player, level, (int) luck, (int) lure, velocity, inaccuracy);
          hook.setPower(ConditionalStatModifierHook.getModifiedStat(tool, player, ToolStats.PROJECTILE_DAMAGE));
          // apply material for the renderer
          hook.setMaterial(tool.getMaterial(tool.getVolatileData().getInt(HOOK_MATERIAL)).getVariant());

          // copy tool data to the bobber for modifier hooks
          ModifierNBT modifiers = tool.getModifiers();
          EntityModifierCapability.getCapability(hook).setModifiers(modifiers);
          // apply grapple or drill
          if (ModifierUtil.canPerformAction(tool, TinkerToolActions.GRAPPLE_HOOK)) {
            hook.setGrapple(ModifierUtil.canPerformAction(tool, TinkerToolActions.DRILL_ATTACK) ? GrappleType.DRILL : GrappleType.DASH);
          }
          // apply collecting
          if (ModifierUtil.canPerformAction(tool, TinkerToolActions.ITEM_HOOK)) {
            hook.setCollecting();
          }

          // fetch the persistent data for the hook as modifiers may want to store data
          ModDataNBT arrowData = PersistentDataCapability.getOrWarn(hook);

          // let modifiers such as fiery and punch set properties
          for (ModifierEntry entry : modifiers.getModifiers()) {
            entry.getHook(ModifierHooks.PROJECTILE_LAUNCH).onProjectileLaunch(tool, entry, player, ItemStack.EMPTY, hook, null, arrowData, true);
          }
          level.addFreshEntity(hook);
        }

        player.awardStat(Stats.ITEM_USED.get(tool.getItem()));
        player.gameEvent(GameEvent.ITEM_INTERACT_START);
      }

      if (level.isClientSide) {
        OffhandCooldownTracker.swingHand(player, hand, false);
      }
      return InteractionResult.CONSUME;
    }
    return InteractionResult.PASS;
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    // if actively fishing, switching to a new rod means we need to retrieve to prevent a cheese
    if (context.getEntity() instanceof Player player && player.fishing != null) {
      IToolStackView replacement = context.getReplacementTool();
      if (replacement == null || !replacement.getModifiers().equals(tool.getModifiers())) {
        player.fishing.discard();
      }
    }
  }

  @Override
  public void onEquipmentChange(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context, EquipmentSlot slotType) {
    // if the main hand changed such that it gained the ability to fish, then vanilla is going to move our fishing bobber to attach to the mainhand
    // so just retrieve it to prevent a cheese
    // there is technically an issue with us inheriting someone elses bobber, but thats just a worse version of our bobber, so not really a cheese
    if (slotType == EquipmentSlot.OFFHAND && context.getChangedSlot() == EquipmentSlot.MAINHAND && context.getEntity() instanceof Player player && player.fishing != null && !context.getOriginal().canPerformAction(ToolActions.FISHING_ROD_CAST) && context.getReplacement().canPerformAction(ToolActions.FISHING_ROD_CAST)) {
      player.fishing.discard();
    }
  }
}
