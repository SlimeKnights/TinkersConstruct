package slimeknights.tconstruct.tools.modifiers.traits.general;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
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
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.shared.TinkerCommons;

import java.util.List;

public class TastyModifier extends Modifier implements GeneralInteractionModifierHook {
  // TODO: consider making this modifier dynamic and letting addons swap out representative items and food rewards
  private static final Lazy<ItemStack> BACON_STACK = Lazy.of(() -> new ItemStack(TinkerCommons.bacon));

  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, TinkerHooks.CHARGEABLE_INTERACT);
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (source == InteractionSource.RIGHT_CLICK && !tool.isBroken() && player.canEat(false)) {
      ModifierUtil.startUsingItem(tool, modifier.getId(), player, hand);
      return InteractionResult.CONSUME;
    }
    return InteractionResult.PASS;
  }

  @Override
  public boolean onFinishUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity) {
    // remove is eating tag to prevent from messing with other modifiers
    ModDataNBT persistentData = tool.getPersistentData();
    if (!tool.isBroken() && entity instanceof Player player && player.canEat(false)) {
      // eat the food
      int level = modifier.getLevel();
      Level world = entity.getLevel();
      player.getFoodData().eat(level, level * 0.1f);
      ModifierUtil.foodConsumer.onConsume(player, BACON_STACK.get(), level, level * 0.1f);
      player.awardStat(Stats.ITEM_USED.get(tool.getItem()));
      world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 1.0F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
      world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_BURP, SoundSource.NEUTRAL, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);

      // 15 damage for a bite per level, does not process reinforced/overslime, your teeth are tough
      if (ToolDamageUtil.directDamage(tool, 15 * level, player, player.getUseItem())) {
        player.broadcastBreakEvent(player.getUsedItemHand());
      }
      // TODO - nutrition mod support
      return true;
    }
    return false;
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
  public List<ItemStack> processLoot(IToolStackView tool, int level, List<ItemStack> generatedLoot, LootContext context) {
    // if no damage source, probably not a mob
    // otherwise blocks breaking (where THIS_ENTITY is the player) start dropping bacon
    if (!context.hasParam(LootContextParams.DAMAGE_SOURCE)) {
      return generatedLoot;
    }

    // must have an entity
    Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
    if (entity != null && entity.getType().is(TinkerTags.EntityTypes.BACON_PRODUCER)) {
      // at tasty 1, 2, 3, and 4 its a 2%, 4.15%, 6.25%, 8% per level
      int looting = context.getLootingModifier();
      if (RANDOM.nextInt(48 / level) <= looting) {
        // bacon
        generatedLoot.add(new ItemStack(TinkerCommons.bacon));
      }
    }
    return generatedLoot;
  }
}
