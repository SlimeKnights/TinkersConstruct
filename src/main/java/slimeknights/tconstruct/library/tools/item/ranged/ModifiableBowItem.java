package slimeknights.tconstruct.library.tools.item.ranged;

import net.minecraft.nbt.CompoundTag;
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
import slimeknights.tconstruct.common.TinkerTags;
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
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.BlockingModifier;

import java.util.function.Predicate;

public class ModifiableBowItem extends ModifiableLauncherItem {
  /** If true, adds the item data to the drawback model. Its a bit less efficient but produces better models. False will just set a boolean. */
  private final boolean storeDrawingItem;

  public ModifiableBowItem(Properties properties, ToolDefinition toolDefinition, boolean storeDrawingItem) {
    super(properties, toolDefinition);
    this.storeDrawingItem = storeDrawingItem;
  }

  public ModifiableBowItem(Properties properties, ToolDefinition toolDefinition) {
    this(properties, toolDefinition, false);
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

    // locate ammo as requested by the item properties
    ItemStack ammo = BowAmmoModifierHook.getAmmo(tool, bow, player, getSupportedHeldProjectiles());
    // ask forge if it has any different opinions
    InteractionResultHolder<ItemStack> override = ForgeEventFactory.onArrowNock(bow, level, player, hand, !ammo.isEmpty());
    if (override != null) {
      return override;
    }
    // if no ammo, cannot fire
    // however, we can use a modifier if enabled
    if (!player.getAbilities().instabuild && ammo.isEmpty() && !tool.getModifiers().has(TinkerTags.Modifiers.CHARGE_EMPTY_BOW_WITH_DRAWTIME)) {
      // however, modifiers such as block can trigger for no drawtime
      if (tool.getModifiers().has(TinkerTags.Modifiers.CHARGE_EMPTY_BOW_WITHOUT_DRAWTIME)) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(bow);
      }
      return InteractionResultHolder.fail(bow);
    }
    GeneralInteractionModifierHook.startDrawtime(tool, player, 1);
    // store either ammo or boolean as requested
    if (!ammo.isEmpty()) {
      if (storeDrawingItem) {
        tool.getPersistentData().put(KEY_DRAWBACK_AMMO, ammo.save(new CompoundTag()));
      } else {
        // boolean is enough to get detected by the property override, but won't bother the model
        tool.getPersistentData().putBoolean(KEY_DRAWBACK_AMMO, true);
      }
    }
    player.startUsingItem(hand);
    if (!level.isClientSide) {
      level.playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.LONGBOW_CHARGE.getSound(), SoundSource.PLAYERS, 0.75F, 1.0F);
    }
    return InteractionResultHolder.consume(bow);
  }

  @Override
  public void releaseUsing(ItemStack bow, Level level, LivingEntity living, int timeLeft) {
    // call the stop using hook
    ToolStack tool = ToolStack.from(bow);
    int duration = getUseDuration(bow);
    for (ModifierEntry entry : tool.getModifiers()) {
      entry.getHook(ModifierHooks.TOOL_USING).beforeReleaseUsing(tool, entry, living, duration, timeLeft, ModifierEntry.EMPTY);
    }

    // no broken
    if (tool.isBroken()) {
      return;
    }

    // just not handling vanilla infinity at all, we have our own hooks which someone could use to mimic infinity if they wish with a bit of effort
    Player player = living instanceof Player p ? p : null;
    boolean creative = player != null && player.getAbilities().instabuild;
    // its a little redundant to search for ammo twice, but otherwise we risk shrinking the stack before we know if we can fire
    // also helps blocking, as you can block without ammo
    boolean hasAmmo = creative || BowAmmoModifierHook.hasAmmo(tool, bow, living, getSupportedHeldProjectiles());

    // ask forge its thoughts on shooting
    int chargeTime = duration - timeLeft;
    if (player != null) {
      chargeTime = ForgeEventFactory.onArrowLoose(bow, level, player, chargeTime, hasAmmo);
    }

    // no ammo? no charge? nothing to do
    if (!hasAmmo || chargeTime < 0) {
      return;
    }

    // calculate arrow power
    float charge = GeneralInteractionModifierHook.getToolCharge(tool, chargeTime);
    float velocity = ConditionalStatModifierHook.getModifiedStat(tool, living, ToolStats.VELOCITY);
    float power = charge * velocity;
    if (power < 0.1f) {
      return;
    }

    // launch the arrow
    if (!level.isClientSide) {
      // find ammo after the return above, as otherwise we might consume ammo before
      ItemStack ammo = BowAmmoModifierHook.consumeAmmo(tool, bow, living, player, getSupportedHeldProjectiles());
      // could only be empty at this point if we are creative, as hasAmmo returned true above
      if (ammo.isEmpty()) {
        ammo = new ItemStack(Items.ARROW);
      }

      // prepare the arrows
      ArrowItem arrowItem = ammo.getItem() instanceof ArrowItem arrow ? arrow : (ArrowItem)Items.ARROW;
      float inaccuracy = ModifierUtil.getInaccuracy(tool, living);
      float startAngle = getAngleStart(ammo.getCount());
      int primaryIndex = ammo.getCount() / 2;
      for (int arrowIndex = 0; arrowIndex < ammo.getCount(); arrowIndex++) {
        AbstractArrow arrow = arrowItem.createArrow(level, ammo, living);
        float angle = startAngle + (10 * arrowIndex);
        arrow.shootFromRotation(living, living.getXRot() + angle, living.getYRot(), 0, power * 3.0F, inaccuracy);
        if (charge == 1.0F) {
          arrow.setCritArrow(true);
        }

        // vanilla arrows have a base damage of 2, cancel that out then add in our base damage to account for custom arrows with higher base damage
        // calculate it just once as all four arrows are the same item, they should have the same damage
        float baseArrowDamage = (float)(arrow.getBaseDamage() - 2 + tool.getStats().get(ToolStats.PROJECTILE_DAMAGE));
        arrow.setBaseDamage(ConditionalStatModifierHook.getModifiedStat(tool, living, ToolStats.PROJECTILE_DAMAGE, baseArrowDamage));

        // just store all modifiers on the tool for simplicity
        ModifierNBT modifiers = tool.getModifiers();
        arrow.getCapability(EntityModifierCapability.CAPABILITY).ifPresent(cap -> cap.setModifiers(modifiers));

        // fetch the persistent data for the arrow as modifiers may want to store data
        ModDataNBT arrowData = PersistentDataCapability.getOrWarn(arrow);

        // if infinite, skip pickup
        if (creative) {
          arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }

        // let modifiers such as fiery and punch set properties
        for (ModifierEntry entry : modifiers.getModifiers()) {
          entry.getHook(ModifierHooks.PROJECTILE_LAUNCH).onProjectileLaunch(tool, entry, living, ammo, arrow, arrow, arrowData, arrowIndex == primaryIndex);
        }
        level.addFreshEntity(arrow);
        level.playSound(null, living.getX(), living.getY(), living.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + charge * 0.5F + (angle / 10f));
      }
      ToolDamageUtil.damageAnimated(tool, ammo.getCount(), living, living.getUsedItemHand());
    }

    // stats and sounds
    if (player != null) {
      player.awardStat(Stats.ITEM_USED.get(this));
    }
  }
}
