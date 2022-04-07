package slimeknights.tconstruct.tools.modifiers.traits.general;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.shared.TinkerCommons;

import java.util.List;

public class TastyModifier extends Modifier {
  private static final ResourceLocation IS_EATING = TConstruct.getResource("eating_tasty");

  @Override
  public InteractionResult onToolUse(IToolStackView tool, int level, Level world, Player player, InteractionHand hand, EquipmentSlot slotType) {
    if (slotType.getType() == Type.HAND) {
      if (!tool.isBroken() && player.canEat(false)) {
        player.startUsingItem(hand);
        // mark tool as eating as use action is only stack sensitive
        tool.getPersistentData().putBoolean(IS_EATING, true);
        return InteractionResult.CONSUME;
      } else {
        // clear is eating boolean if we cannot eat, prevents messing with other modifier's animations
        tool.getPersistentData().remove(IS_EATING);
      }
    }
    return InteractionResult.PASS;
  }

  @Override
  public boolean onStoppedUsing(IToolStackView tool, int level, Level world, LivingEntity entity, int timeLeft) {
    tool.getPersistentData().remove(IS_EATING);
    return false;
  }

  @Override
  public boolean onFinishUsing(IToolStackView tool, int level, Level world, LivingEntity entity) {
    // remove is eating tag to prevent from messing with other modifiers
    ModDataNBT persistentData = tool.getPersistentData();
    boolean wasEating = persistentData.getBoolean(IS_EATING);
    persistentData.remove(IS_EATING);

    if (!tool.isBroken() && wasEating && entity instanceof Player) {
      // clear eating marker
      Player player = (Player) entity;
      if (player.canEat(false)) {
        // eat the food
        player.getFoodData().eat(level, level * 0.1f);
        player.awardStat(Stats.ITEM_USED.get(tool.getItem()));
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 1.0F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_BURP, SoundSource.NEUTRAL, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);

        // 15 damage for a bite per level, does not process reinforced/overslime, your teeth are tough
        if (ToolDamageUtil.directDamage(tool, 15 * level, player, player.getUseItem())) {
          player.broadcastBreakEvent(player.getUsedItemHand());
        }
        return true;
      }
    }
    return false;
  }

  @Override
  public UseAnim getUseAction(IToolStackView tool, int level) {
    return tool.getPersistentData().getBoolean(IS_EATING) ? UseAnim.EAT : UseAnim.NONE;
  }

  @Override
  public int getUseDuration(IToolStackView tool, int level) {
    return tool.getPersistentData().getBoolean(IS_EATING) ? 16 : 0;
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
