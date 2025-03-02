package slimeknights.tconstruct.library.tools.item.ranged;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.ForgeEventFactory;
import org.joml.Vector3f;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.BlockingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.ranged.ScopeModifier;

import java.util.function.Predicate;

import static slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook.KEY_DRAWTIME;

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
  public UseAnim getUseAnimation(ItemStack stack) {
    return BlockingModifier.blockWhileCharging(ToolStack.from(stack), UseAnim.BOW);
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
    // if no ammo, cannot fire
    if (!player.getAbilities().instabuild && !hasAmmo) {
      // however, we can block if enabled
      if (ModifierUtil.canPerformAction(tool, ToolActions.SHIELD_BLOCK)) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(bow);
      }
      return InteractionResultHolder.fail(bow);
    }
    GeneralInteractionModifierHook.startDrawtime(tool, player, 1);
    player.startUsingItem(hand);
    if (!level.isClientSide) {
      level.playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.LONGBOW_CHARGE.getSound(), SoundSource.PLAYERS, 0.75F, 1.0F);
    }
    return InteractionResultHolder.consume(bow);
  }

  @Override
  public void playShotSound(LivingEntity user, float charge, float angle, RandomSource random) {
    user.level().playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F,
      1.0F / (random.nextFloat() * 0.4F + 1.2F) + charge * 0.5F + (angle / 10f));
  }

  @Override
  public Vector3f modifyShootAngle(LivingEntity user, Vec3 target, float angle) {
    return target.xRot(angle * Mth.DEG_TO_RAD).toVector3f();
  }

  @Override
  public void releaseUsing(ItemStack bow, Level level, LivingEntity living, int timeLeft) {
    // clear zoom regardless, does not matter if the tool broke, we should not be zooming
    ScopeModifier.stopScoping(living);

    // need player
    if (!(living instanceof Player player)) {
      return;
    }
    // no broken
    ToolStack tool = ToolStack.from(bow);
    if (tool.isBroken()) {
      tool.getPersistentData().remove(KEY_DRAWTIME);
      return;
    }

    // just not handling vanilla infinity at all, we have our own hooks which someone could use to mimic infinity if they wish with a bit of effort
    boolean creative = player.getAbilities().instabuild;
    // its a little redundant to search for ammo twice, but otherwise we risk shrinking the stack before we know if we can fire
    // sldo helps blocking, as you can block without ammo
    boolean hasAmmo = creative || BowAmmoModifierHook.hasAmmo(tool, bow, player, getSupportedHeldProjectiles());

    // ask forge its thoughts on shooting
    int chargeTime = this.getUseDuration(bow) - timeLeft;
    chargeTime = ForgeEventFactory.onArrowLoose(bow, level, player, chargeTime, hasAmmo);

    // no ammo? no charge? nothing to do
    if (!hasAmmo || chargeTime < 0) {
      tool.getPersistentData().remove(KEY_DRAWTIME);
      return;
    }

    // calculate arrow power
    float charge = GeneralInteractionModifierHook.getToolCharge(tool, chargeTime);
    tool.getPersistentData().remove(KEY_DRAWTIME);

    // may we just use charge to test if we can fire the arrows?
    float power = charge * ConditionalStatModifierHook.getModifiedStat(tool, living, ToolStats.VELOCITY);
    if (power < 0.1f) {
      return;
    }

    // launch the arrow
    if (!level.isClientSide) {
      // find ammo after the return above, as otherwise we might consume ammo before
      ItemStack ammo = BowAmmoModifierHook.findAmmo(tool, bow, player, getSupportedHeldProjectiles());
      // could only be empty at this point if we are creative, as hasAmmo returned true above
      if (ammo.isEmpty()) {
        ammo = new ItemStack(Items.ARROW);
      }
      shootProjectilesAndDamageWeapon(player, tool, player.getUsedItemHand(), ammo, player.getViewVector(1.0f), charge, 3, 1, creative, !creative);
    }

    // stats and sounds
    player.awardStat(Stats.ITEM_USED.get(this));
  }
}
