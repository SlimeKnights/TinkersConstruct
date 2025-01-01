package slimeknights.tconstruct.tools.modifiers.traits.general;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.util.Lazy;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.shared.TinkerCommons;

import java.util.List;

public class TastyModifier extends Modifier implements GeneralInteractionModifierHook, OnAttackedModifierHook, ProcessLootModifierHook {
  // TODO: consider making this modifier dynamic and letting addons swap out representative items and food rewards
  private static final Lazy<ItemStack> BACON_STACK = Lazy.of(() -> new ItemStack(TinkerCommons.bacon));

  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, ModifierHooks.GENERAL_INTERACT, ModifierHooks.ON_ATTACKED, ModifierHooks.PROCESS_LOOT);
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (source == InteractionSource.RIGHT_CLICK && !tool.isBroken() && player.canEat(false)) {
      GeneralInteractionModifierHook.startUsing(tool, modifier.getId(), player, hand);
      return InteractionResult.CONSUME;
    }
    return InteractionResult.PASS;
  }

  /** Takes a nibble of the tool */
  private void eat(IToolStackView tool, ModifierEntry modifier, LivingEntity entity) {
    int level = modifier.intEffectiveLevel();
    if (level > 0 && entity instanceof Player player && player.canEat(false)) {
      Level world = entity.level();
      player.getFoodData().eat(level, 0.4F);
      ModifierUtil.foodConsumer.onConsume(player, BACON_STACK.get(), level, 0.6F);
      world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 1.0F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
      world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_BURP, SoundSource.NEUTRAL, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);

      // 15 damage for a bite per level, does not process reinforced/overslime, your teeth are tough
      if (ToolDamageUtil.directDamage(tool, 15 * level, player, player.getUseItem())) {
        player.broadcastBreakEvent(player.getUsedItemHand());
      }
    }
  }

  @Override
  public void onFinishUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity) {
    if (!tool.isBroken()) {
      eat(tool, modifier, entity);
    }
  }

  @Override
  public UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
    return UseAnim.EAT;
  }

  @Override
  public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
    return 16;
  }

  @Override
  public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    // 15% chance of working per level, doubled bonus on shields
    float level = modifier.getEffectiveLevel();
    if (slotType.getType() == Type.HAND) {
      level *= 2;
    }
    if (RANDOM.nextFloat() < (level * 0.15f)) {
      eat(tool, modifier, context.getEntity());
    }
  }

  @Override
  public void processLoot(IToolStackView tool, ModifierEntry modifier, List<ItemStack> generatedLoot, LootContext context) {
    // if no damage source, probably not a mob
    // otherwise blocks breaking (where THIS_ENTITY is the player) start dropping bacon
    if (!context.hasParam(LootContextParams.DAMAGE_SOURCE)) {
      return;
    }

    // must have an entity
    Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
    if (entity != null && entity.getType().is(TinkerTags.EntityTypes.BACON_PRODUCER)) {
      // at tasty 1, 2, 3, and 4 its a 2%, 4.15%, 6.25%, 8% per level
      int looting = context.getLootingModifier();
      if (RANDOM.nextInt(48 / modifier.intEffectiveLevel()) <= looting) {
        // bacon
        generatedLoot.add(new ItemStack(TinkerCommons.bacon));
      }
    }
  }
}
