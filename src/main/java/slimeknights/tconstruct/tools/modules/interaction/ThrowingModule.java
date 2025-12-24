package slimeknights.tconstruct.tools.modules.interaction;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.interaction.UsingToolModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableLauncherItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.entity.ThrownTool;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.BlockingModifier;

import java.util.List;

/** Module allowing throwing a tool */
public enum ThrowingModule implements ModifierModule, GeneralInteractionModifierHook, UsingToolModifierHook {
  INSTANCE;

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ThrowingModule>defaultHooks(ModifierHooks.GENERAL_INTERACT, ModifierHooks.TOOL_USING);
  public static final RecordLoadable<ThrowingModule> LOADER = new SingletonLoader<>(INSTANCE);

  @Override
  public RecordLoadable<ThrowingModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public Integer getPriority() {
    return 110; // want to run before sling modifiers so we can sling throw
  }

  @Override
  public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
    return 72000;
  }

  @Override
  public UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
    return BlockingModifier.blockWhileCharging(tool, UseAnim.SPEAR);
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    // can't throw something with no melee stats, will do nothing
    if (!tool.isBroken() && source == InteractionSource.RIGHT_CLICK) {
      float speed = ConditionalStatModifierHook.getModifiedStat(tool, player, ToolStats.DRAW_SPEED);
      if (tool.hasTag(TinkerTags.Items.MELEE_WEAPON)) {
        speed *= tool.getStats().get(ToolStats.ATTACK_SPEED);
      }
      // use attack speed together with drawspeed to ensure you are not making insanely slow weapons and throwing to bypass
      tool.getPersistentData().putInt(KEY_DRAWTIME, (int)Math.ceil(20f / speed));
      GeneralInteractionModifierHook.startUsing(tool, modifier.getId(), player, hand);
      return InteractionResult.CONSUME;
    }
    return InteractionResult.PASS;
  }

  @Override
  public void onStoppedUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
    if (entity instanceof Player player) {
      int chargeTime = getUseDuration(tool, modifier) - timeLeft;
      if (chargeTime > 10) {
        Level level = player.level();
        ItemStack stack = player.getUseItem();

        // unlike the trident, we actually consider how long you charged for, and change the power of the projectile
        float charge = GeneralInteractionModifierHook.getToolCharge(tool, chargeTime);
        float velocity = ConditionalStatModifierHook.getModifiedStat(tool, entity, ToolStats.VELOCITY);
        ThrownTool thrown = new ThrownTool(level, player, stack, charge, velocity, ConditionalStatModifierHook.getModifiedStat(tool, entity, ToolStats.WATER_INERTIA));
        if (player.getUsedItemHand() == InteractionHand.OFF_HAND) {
          thrown.setOriginalSlot(Inventory.SLOT_OFFHAND);
        } else {
          thrown.setOriginalSlot(player.getInventory().selected);
        }
        thrown.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, charge * velocity * 2, ModifierUtil.getInaccuracy(tool, entity));
        if (player.getAbilities().instabuild) {
          thrown.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }

        // alert modifiers we are leaving, though most of these won't have much impact
        thrown.onRelease(entity, PersistentDataCapability.getOrWarn(thrown));

        // don't run projectile hooks, as the projectile has the tool already for that. Throwing runs melee hooks
        level.addFreshEntity(thrown);
        level.playSound(null, thrown, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1, 1);
        if (!player.getAbilities().instabuild) {
          player.getInventory().removeItem(stack);
        }
        player.awardStat(Stats.ITEM_USED.get(tool.getItem()));
      }
    }
  }

  @Override
  public void beforeReleaseUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
    // when using a tool that doesn't fire right click hooks, allow throwing here provided its out of ammo
    if (activeModifier == ModifierEntry.EMPTY && !tool.getPersistentData().contains(ModifiableLauncherItem.KEY_DRAWBACK_AMMO)) {
      onStoppedUsing(tool, modifier, entity, timeLeft);
    }
  }
}
