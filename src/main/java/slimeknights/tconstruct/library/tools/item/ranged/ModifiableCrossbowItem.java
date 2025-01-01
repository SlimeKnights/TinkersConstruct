package slimeknights.tconstruct.library.tools.item.ranged;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.NamespacedNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.BlockingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.ranged.ScopeModifier;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook.KEY_DRAWTIME;

public class ModifiableCrossbowItem extends ModifiableLauncherItem {
  /** Key containing the stored crossbow ammo */
  public static final ResourceLocation KEY_CROSSBOW_AMMO = TConstruct.getResource("crossbow_ammo");
  private static final String PROJECTILE_KEY = "item.minecraft.crossbow.projectile";
  public ModifiableCrossbowItem(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }


  /* Properties */

  @Override
  public Predicate<ItemStack> getSupportedHeldProjectiles() {
    return ARROW_OR_FIREWORK;
  }

  @Override
  public Predicate<ItemStack> getAllSupportedProjectiles() {
    return ARROW_ONLY;
  }

  @Override
  public int getDefaultProjectileRange() {
    return 8;
  }

  @Override
  public UseAnim getUseAnimation(ItemStack stack) {
    // crossbow is superhardcoded to crossbows, so use none and rely on the model
    return BlockingModifier.blockWhileCharging(ToolStack.from(stack), UseAnim.NONE);
  }

  @Override
  public boolean useOnRelease(ItemStack stack) {
    return true;
  }


  /* Arrow launching */

  /** Gets the arrow pitch */
  private static float getRandomShotPitch(float angle, RandomSource pRandom) {
    if (angle == 0) {
      return 1.0f;
    }
    return 1.0F / (pRandom.nextFloat() * 0.5F + 1.8F) + 0.53f + (angle / 10f);
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack bow = player.getItemInHand(hand);

    ToolStack tool = ToolStack.from(bow);
    if (tool.isBroken()) {
      return InteractionResultHolder.fail(bow);
    }

    // yeah, its hardcoded, I cannot see a need to not hardcode this, request it if you need it
    boolean sinistral = hand == InteractionHand.MAIN_HAND && tool.getModifierLevel(TinkerModifiers.sinistral.getId()) > 0;

    // no ammo? not charged
    ModDataNBT persistentData = tool.getPersistentData();
    CompoundTag heldAmmo = persistentData.getCompound(KEY_CROSSBOW_AMMO);
    if (heldAmmo.isEmpty()) {
      // do not charge if sneaking and we have sinistral, gives you a way to activate the offhand when the crossbow is not charged
      if (sinistral && !player.getOffhandItem().isEmpty() && player.isCrouching()) {
        return InteractionResultHolder.pass(bow);
      }

      // if we have ammo, start charging
      if (BowAmmoModifierHook.hasAmmo(tool, bow, player, getSupportedHeldProjectiles())) {
        GeneralInteractionModifierHook.startDrawtime(tool, player, 1);
        player.startUsingItem(hand);
        if (!level.isClientSide) {
          level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CROSSBOW_QUICK_CHARGE_1, SoundSource.PLAYERS, 0.75F, 1.0F);
        }
        return InteractionResultHolder.consume(bow);
        // no ammo still lets us block
      } else if (ModifierUtil.canPerformAction(tool, ToolActions.SHIELD_BLOCK)) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(bow);
      } else {
        return InteractionResultHolder.fail(bow);
      }
    }

    // sinistral shoots on left click when in main hand, and lets us block instead of shooting if the offhand is empty
    if (sinistral) {
      ItemStack offhand = player.getOffhandItem();
      if (!offhand.isEmpty() && !offhand.is(Items.FIREWORK_ROCKET)) {
        return InteractionResultHolder.pass(bow);
      }
      if (ModifierUtil.canPerformAction(tool, ToolActions.SHIELD_BLOCK)) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(bow);
      }
    }

    // ammo already loaded? time to fire
    fireCrossbow(tool, player, hand, heldAmmo);
    return InteractionResultHolder.consume(bow);
  }

  /**
   * Fires the crossbow
   * @param tool       Tool instance
   * @param player     Player firing
   * @param hand       Hand fired from
   * @param heldAmmo   Ammo used to fire, should be non-empty
   */
  public static void fireCrossbow(IToolStackView tool, Player player, InteractionHand hand, CompoundTag heldAmmo) {
    // ammo already loaded? time to fire
    Level level = player.level();
    if (!level.isClientSide) {
      // shoot the projectile
      int damage = 0;

      // don't need to calculate these multiple times
      float velocity = ConditionalStatModifierHook.getModifiedStat(tool, player, ToolStats.VELOCITY);
      float inaccuracy = ModifierUtil.getInaccuracy(tool, player);
      boolean creative = player.getAbilities().instabuild;

      // the ammo has a stack size that may be greater than 1 (meaning multishot)
      // when creating the ammo stacks, we use split, so its getting smaller each time
      ItemStack ammo = ItemStack.of(heldAmmo);
      float startAngle = getAngleStart(ammo.getCount());
      int primaryIndex = ammo.getCount() / 2;
      for (int arrowIndex = 0; arrowIndex < ammo.getCount(); arrowIndex++) {
        // setup projectile
        AbstractArrow arrow = null;
        Projectile projectile;
        float speed;
        if (ammo.is(Items.FIREWORK_ROCKET)) {
          // TODO: don't hardcode fireworks, perhaps use a map or a JSON behavior list
          projectile = new FireworkRocketEntity(level, ammo, player, player.getX(), player.getEyeY() - 0.15f, player.getZ(), true);
          speed = 1.5f;
          damage += 3;
        } else {
          ArrowItem arrowItem = ammo.getItem() instanceof ArrowItem a ? a : (ArrowItem)Items.ARROW;
          arrow = arrowItem.createArrow(level, ammo, player);
          projectile = arrow;
          arrow.setCritArrow(true);
          arrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
          arrow.setShotFromCrossbow(true);
          speed = 3f;
          damage += 1;

          // vanilla arrows have a base damage of 2, cancel that out then add in our base damage to account for custom arrows with higher base damage
          float baseArrowDamage = (float)(arrow.getBaseDamage() - 2 + tool.getStats().get(ToolStats.PROJECTILE_DAMAGE));
          arrow.setBaseDamage(ConditionalStatModifierHook.getModifiedStat(tool, player, ToolStats.PROJECTILE_DAMAGE, baseArrowDamage));

          // fortunately, don't need to deal with vanilla infinity here, our infinity was dealt with during loading
          if (creative) {
            arrow.pickup = Pickup.CREATIVE_ONLY;
          }
        }

        // TODO: can we get piglins/illagers to use our crossbow?

        // setup projectile
        Vec3 upVector = player.getUpVector(1.0f);
        float angle = startAngle + (10 * arrowIndex);
        Vector3f targetVector = player.getViewVector(1.0f).toVector3f().rotate((new Quaternionf()).setAngleAxis(angle * Math.PI / 180F, upVector.x, upVector.y, upVector.z));
        projectile.shoot(targetVector.x(), targetVector.y(), targetVector.z(), velocity * speed, inaccuracy);

        // add modifiers to the projectile, will let us use them on impact
        ModifierNBT modifiers = tool.getModifiers();
        projectile.getCapability(EntityModifierCapability.CAPABILITY).ifPresent(cap -> cap.setModifiers(modifiers));

        // fetch the persistent data for the arrow as modifiers may want to store data
        NamespacedNBT projectileData = PersistentDataCapability.getOrWarn(projectile);

        // let modifiers set properties
        for (ModifierEntry entry : modifiers.getModifiers()) {
          entry.getHook(ModifierHooks.PROJECTILE_LAUNCH).onProjectileLaunch(tool, entry, player, projectile, arrow, projectileData, arrowIndex == primaryIndex);
        }

        // finally, fire the projectile
        level.addFreshEntity(projectile);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, getRandomShotPitch(angle, player.getRandom()));
      }

      // clear the ammo, damage the bow
      tool.getPersistentData().remove(KEY_CROSSBOW_AMMO);
      ToolDamageUtil.damageAnimated(tool, damage, player, hand);

      // stats
      if (player instanceof ServerPlayer serverPlayer) {
        CriteriaTriggers.SHOT_CROSSBOW.trigger(serverPlayer, player.getItemInHand(hand));
        serverPlayer.awardStat(Stats.ITEM_USED.get(tool.getItem()));
      }
    }
  }

  @Override
  public void releaseUsing(ItemStack bow, Level level, LivingEntity living, int chargeRemaining) {
    // clear zoom regardless, does not matter if the tool broke, we should not be zooming
    ScopeModifier.stopScoping(living);
    if (!(living instanceof Player player)) {
      return;
    }
    ToolStack tool = ToolStack.from(bow);
    ModDataNBT persistentData = tool.getPersistentData();
    if (tool.isBroken() || persistentData.contains(KEY_CROSSBOW_AMMO, Tag.TAG_COMPOUND)) {
      return;
    }

    // did we charge enough?
    int drawtime = persistentData.getInt(KEY_DRAWTIME);
    persistentData.remove(KEY_DRAWTIME);
    if ((getUseDuration(bow) - chargeRemaining) < drawtime) {
      return;
    }

    // find ammo and store it on the bow
    ItemStack ammo = BowAmmoModifierHook.findAmmo(tool, bow, player, getSupportedHeldProjectiles());
    if (!ammo.isEmpty()) {
      level.playSound(null, living.getX(), living.getY(), living.getZ(), SoundEvents.CROSSBOW_LOADING_END, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
      if (!level.isClientSide) {
        CompoundTag ammoNBT = ammo.save(new CompoundTag());
        persistentData.put(KEY_CROSSBOW_AMMO, ammoNBT);
        // if the crossbow broke during loading, fire immediately
        if (tool.isBroken()) {
          fireCrossbow(tool, player, player.getUsedItemHand(), ammoNBT);
        }
      }
    }
  }

  @Override
  public List<Component> getStatInformation(IToolStackView tool, @Nullable Player player, List<Component> tooltips, TooltipKey key, TooltipFlag tooltipFlag) {
    tooltips = super.getStatInformation(tool, player, tooltips, key, tooltipFlag);

    // if we have ammo, render that in the tooltip
    CompoundTag heldAmmo = tool.getPersistentData().getCompound(KEY_CROSSBOW_AMMO);
    if (!heldAmmo.isEmpty()) {
      ItemStack heldStack = ItemStack.of(heldAmmo);
      if (!heldStack.isEmpty()) {
        // basic info: item and count
        MutableComponent component = Component.translatable(PROJECTILE_KEY);
        int count = heldStack.getCount();
        if (count > 1) {
          component.append(" " + count + " ");
        } else {
          component.append(" ");
        }
        tooltips.add(component.append(heldStack.getDisplayName()));

        // copy the stack's tooltip if advanced
        if (tooltipFlag.isAdvanced() && player != null) {
          List<Component> nestedTooltip = new ArrayList<>();
          heldStack.getItem().appendHoverText(heldStack, player.level(), nestedTooltip, tooltipFlag);
          for (Component nested : nestedTooltip) {
            tooltips.add(Component.literal("  ").append(nested).withStyle(ChatFormatting.GRAY));
          }
        }
      }
    }
    return tooltips;
  }
}
