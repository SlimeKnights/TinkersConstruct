package slimeknights.tconstruct.tools.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.BowAmmoModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableLauncherItem;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.NamespacedNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.function.Predicate;

import static slimeknights.tconstruct.tools.modifiers.upgrades.ranged.ScopeModifier.SCOPE;

public class ModifiableBowItem extends ModifiableLauncherItem {
  public ModifiableBowItem(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }


  /* Properties */

  @Override
  public Predicate<ItemStack> getAllSupportedProjectiles() {
    return ProjectileWeaponItem.ARROW_ONLY;
  }

  @Override
  public int getDefaultProjectileRange() {
    return 15;
  }

  @Override
  public UseAnim getUseAnimation(ItemStack pStack) {
    return UseAnim.BOW;
  }


  /* Arrow launching */

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack bow = player.getItemInHand(hand);
    ToolStack tool = ToolStack.from(bow);
    if (tool.isBroken()) {
      return InteractionResultHolder.fail(bow);
    }

    boolean hasAmmo = BowAmmoModifierHook.hasAmmo(tool, bow, player, getSupportedHeldProjectiles());
    // ask forge if it has any different opinions
    InteractionResultHolder<ItemStack> override = ForgeEventFactory.onArrowNock(bow, level, player, hand, hasAmmo);
    if (override != null) {
      return override;
    }
    if (!player.getAbilities().instabuild && !hasAmmo) {
      return InteractionResultHolder.fail(bow);
    }
    player.startUsingItem(hand);
    // property for scope, release, and item model
    float drawspeed = ConditionalStatModifierHook.getModifiedStat(tool, player, ToolStats.DRAW_SPEED) / 20f;
    player.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.put(DRAWSPEED, drawspeed));
    // we want an int version to make sounds more precise
    tool.getPersistentData().putInt(KEY_DRAWTIME, (int)Math.ceil(1 / drawspeed));
    if (!level.isClientSide) {
      level.playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.LONGBOW_CHARGE.getSound(), SoundSource.PLAYERS, 0.75F, 1.0F);
    }
    return InteractionResultHolder.consume(bow);
  }

  @Override
  public void releaseUsing(ItemStack bow, Level level, LivingEntity living, int timeLeft) {
    // clear zoom regardless, does not matter if the tool broke, we should not be zooming
    if (level.isClientSide) {
      living.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).remove(SCOPE));
    }

    // need player
    if (!(living instanceof Player player)) {
      return;
    }
    // no broken
    ToolStack tool = ToolStack.from(bow);
    if (tool.isBroken()) {
      return;
    }

    // find ammo
    ItemStack ammo = BowAmmoModifierHook.findAmmo(tool, bow, player, getSupportedHeldProjectiles());

    // just not handling vanilla infinity at all, we have our own hooks which someone could use to mimic infinity if they wish with a bit of effort
    boolean creative = player.getAbilities().instabuild;
    // ask forge its thoughts on shooting
    int chargeTime = this.getUseDuration(bow) - timeLeft;
    chargeTime = ForgeEventFactory.onArrowLoose(bow, level, player, chargeTime, !ammo.isEmpty() || creative);

    // no ammo? no charge? nothing to do
    if (chargeTime < 0 || (ammo.isEmpty() && !creative)) {
      return;
    }
    // could only be empty at this point if we had infinity
    if (ammo.isEmpty()) {
      ammo = new ItemStack(Items.ARROW);
    }

    // calculate arrow power
    float charge = chargeTime * ConditionalStatModifierHook.getModifiedStat(tool, living, ToolStats.DRAW_SPEED) / 20f;
    charge = (charge * charge + charge * 2) / 3;
    if (charge > 1) {
      charge = 1;
    }
    float velocity = ConditionalStatModifierHook.getModifiedStat(tool, living, ToolStats.VELOCITY);
    float power = charge * velocity;
    if (power < 0.1f) {
      return;
    }

    // launch the arrow
    if (!level.isClientSide) {
      ArrowItem arrowItem = ammo.getItem() instanceof ArrowItem arrow ? arrow : (ArrowItem)Items.ARROW;
      float inaccuracy = 3 * (1 / ConditionalStatModifierHook.getModifiedStat(tool, living, ToolStats.ACCURACY) - 1) * velocity;
      float startAngle = getAngleStart(ammo.getCount());
      int primaryIndex = ammo.getCount() / 2;
      for (int arrowIndex = 0; arrowIndex < ammo.getCount(); arrowIndex++) {
        AbstractArrow arrow = arrowItem.createArrow(level, ammo, player);
        float angle = startAngle + (10 * arrowIndex);
        arrow.shootFromRotation(player, player.getXRot() + angle, player.getYRot(), 0, power * 3.0F, inaccuracy);
        if (charge == 1.0F) {
          arrow.setCritArrow(true);
        }

        // vanilla arrows have a base damage of 2, cancel that out then add in our base damage to account for custom arrows with higher base damage
        // calculate it just once as all four arrows are the same item, they should have the same damage
        float baseArrowDamage = (float)(arrow.getBaseDamage() - 2 + tool.getStats().get(ToolStats.PROJECTILE_DAMAGE));
        arrow.setBaseDamage(ConditionalStatModifierHook.getModifiedStat(tool, player, ToolStats.PROJECTILE_DAMAGE, baseArrowDamage));

        // just store all modifiers on the tool for simplicity
        ModifierNBT modifiers = tool.getModifiers();
        arrow.getCapability(EntityModifierCapability.CAPABILITY).ifPresent(cap -> cap.setModifiers(modifiers));

        // fetch the persistent data for the arrow as modifiers may want to store data
        NamespacedNBT arrowData = PersistentDataCapability.getOrWarn(arrow);

        // if infinite, skip pickup
        if (creative) {
          arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }

        // let modifiers such as fiery and punch set properties
        for (ModifierEntry entry : modifiers.getModifiers()) {
          entry.getHook(TinkerHooks.PROJECTILE_LAUNCH).onProjectileLaunch(tool, entry, living, arrow, arrow, arrowData, arrowIndex == primaryIndex);
        }
        level.addFreshEntity(arrow);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + charge * 0.5F + (angle / 10f));
      }
      ToolDamageUtil.damageAnimated(tool, ammo.getCount(), player, player.getUsedItemHand());
    }

    // stats and sounds
    player.awardStat(Stats.ITEM_USED.get(this));
  }

  @SuppressWarnings("deprecation") // forge is being dumb here, their method is identical to the vanilla one
  @Override
  public void onUseTick(Level level, LivingEntity living, ItemStack bow, int chargeRemaining) {
    // play the sound at the end of loading as an indicator its loaded, texture is another indicator
    if (!level.isClientSide) {
      if (getUseDuration(bow) - chargeRemaining == ModifierUtil.getPersistentInt(bow, KEY_DRAWTIME, 0)) {
        level.playSound(null, living.getX(), living.getY(), living.getZ(), SoundEvents.CROSSBOW_LOADING_MIDDLE, SoundSource.PLAYERS, 0.75F, 1.0F);
      }
    }
    else if (ModifierUtil.getModifierLevel(bow, TinkerModifiers.scope.getId()) > 0) {
      int chargeTime = this.getUseDuration(bow) - chargeRemaining;
      if (chargeTime > 0) {
        living.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
          float totalTime = data.get(DRAWSPEED, 0f);
          if (totalTime > 0) {
            data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).set(SCOPE, 1 - (0.6f * Math.min(totalTime * chargeTime, 1)));
          }
        });
      }
    }
  }
}
