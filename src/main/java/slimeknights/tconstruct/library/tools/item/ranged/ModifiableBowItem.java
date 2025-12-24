package slimeknights.tconstruct.library.tools.item.ranged;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import slimeknights.tconstruct.TConstruct;
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
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.entity.ThrownTool;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.BlockingModifier;

import java.util.function.Predicate;

public class ModifiableBowItem extends ModifiableLauncherItem {
  /** Predicate for checking for ballisa ammo with no arrows in the bow */
  private static final Predicate<ItemStack> BALLISTA_ONLY = stack -> stack.is(TinkerTags.Items.BALLISTA_AMMO);
  /** Predicate for checking for ballisa ammo in the bow */
  private static final Predicate<ItemStack> ARROWS_OR_BALLISTA = stack -> stack.is(ItemTags.ARROWS) || stack.is(TinkerTags.Items.BALLISTA_AMMO);

  /** Volatile flag activating the ballista functionality, and persistent int for bow actively firing a ballista */
  public static final ResourceLocation KEY_BALLISTA = TConstruct.getResource("ballista");
  /** Value for {@link #KEY_BALLISTA} when the ballista was found in the mainhand or offhand. Used to ensure inventory minimally messes with firing stack */
  public static final int FLAG_BALLISTA_HELD = 1;
  /** Value for {@link #KEY_BALLISTA} when the ballista was found in a modifier hook, such as quiver. Important this value is larger than {@link #FLAG_BALLISTA_HELD}. */
  public static final int FLAG_BALLISTA_QUIVER = 2;
  /** Value for {@link #KEY_BALLISTA} when we support ballistas, but are not actively firing one.  Important this value is larger than {@link #FLAG_BALLISTA_HELD}. */
  public static final int FLAG_NO_BALLISTA = 3;

  /** If true, adds the item data to the drawback model. It's a bit less efficient but produces better models. False will just set a boolean. */
  private final boolean storeDrawingItem;

  public ModifiableBowItem(Properties properties, ToolDefinition toolDefinition, boolean storeDrawingItem) {
    super(properties, toolDefinition);
    this.storeDrawingItem = storeDrawingItem;
  }

  public ModifiableBowItem(Properties properties, ToolDefinition toolDefinition) {
    this(properties, toolDefinition, false);
  }


  /* Properties */

  /** Ammo allowed when firing in ballista mode */
  public Predicate<ItemStack> getSupportedBallistaAmmo() {
    return ARROWS_OR_BALLISTA;
  }

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

  /** Checks if the given tool is a ballista */
  public static boolean isBallista(IToolStackView tool) {
    return tool.hasTag(TinkerTags.Items.BALLISTAS) && tool.getVolatileData().getBoolean(KEY_BALLISTA);
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack bow = player.getItemInHand(hand);
    ToolStack tool = ToolStack.from(bow);
    if (tool.isBroken()) {
      return InteractionResultHolder.fail(bow);
    }

    // locate ammo as requested by the item properties
    // if we have ballista capabilities, use the broader predicate
    boolean isBallista = isBallista(tool);
    ItemStack ammo = BowAmmoModifierHook.getAmmo(tool, bow, player, isBallista ? getSupportedBallistaAmmo() : getSupportedHeldProjectiles());
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
      // if its ballista ammo, mark that in NBT
      if (isBallista) {
        int flag;
        if (ammo.is(TinkerTags.Items.BALLISTA_AMMO)) {
          flag = ammo == player.getMainHandItem() || ammo == player.getOffhandItem() ? FLAG_BALLISTA_HELD : FLAG_BALLISTA_QUIVER;
        } else {
          flag = FLAG_NO_BALLISTA;
        }
        tool.getPersistentData().putInt(KEY_BALLISTA, flag);
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
    Predicate<ItemStack> ammoPredicate = switch (tool.getPersistentData().getInt(KEY_BALLISTA)) {
      case FLAG_BALLISTA_HELD -> null; // main hand comes via an event, we just call the hook for merging
      case FLAG_BALLISTA_QUIVER -> BALLISTA_ONLY;
      case FLAG_NO_BALLISTA -> getSupportedHeldProjectiles();
      default -> isBallista(tool) ? getSupportedBallistaAmmo() : getSupportedHeldProjectiles();
    };
    ItemStack foundAmmo = BowAmmoModifierHook.getAmmo(tool, bow, living, ammoPredicate);
    boolean hasAmmo = creative || !foundAmmo.isEmpty();

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
      int originalSlot = -1;
      int desiredProjectiles = 1;
      // if it's a ballista shot, locate the original slot so we can store it on the entity
      if (foundAmmo.is(TinkerTags.Items.BALLISTA_AMMO)) {
        if (player != null) {
          if (foundAmmo == living.getOffhandItem()) {
            originalSlot = Inventory.SLOT_OFFHAND;
          } else {
            Inventory inventory = player.getInventory();
            for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
              if (inventory.getItem(i) == foundAmmo) {
                originalSlot = i;
                break;
              }
            }
          }
        }
      } else {
        // TODO: remove multishot logic? or keep it around for addons?
        desiredProjectiles = BowAmmoModifierHook.getDesiredProjectiles(tool);
      }
      // filter ammo based on request from current ballista settings
      ItemStack ammo = BowAmmoModifierHook.consumeAmmo(tool, bow, living, player, ammoPredicate, desiredProjectiles);
      // could only be empty at this point if we are creative, as hasAmmo returned true above
      if (ammo.isEmpty()) {
        ammo = new ItemStack(Items.ARROW);
      }

      // prepare the arrows
      ArrowItem arrowItem = ammo.getItem() instanceof ArrowItem arrow ? arrow : (ArrowItem)Items.ARROW;
      // shenanigans: if the ammo is a melee tool, fire that using the thrown tool projectile
      boolean thrownTool = ammo.is(TinkerTags.Items.BALLISTA_AMMO);
      float waterInertia = 0.6f;
      SoundEvent sound = SoundEvents.ARROW_SHOOT;
      if (thrownTool) {
        sound = SoundEvents.TRIDENT_THROW;
        IToolStackView thrown = ToolStack.from(ammo);
        float thrownVelocity = ConditionalStatModifierHook.getModifiedStat(thrown, living, ToolStats.VELOCITY);
        power *= thrownVelocity * ConditionalStatModifierHook.getModifiedStat(thrown, living, ToolStats.DRAW_SPEED) / 1.5f;
        if (ammo.is(TinkerTags.Items.MELEE_WEAPON)) {
          power *= thrown.getStats().get(ToolStats.ATTACK_SPEED);
        }
        velocity *= thrownVelocity;
        waterInertia = ConditionalStatModifierHook.getModifiedStat(thrown, living, ToolStats.WATER_INERTIA);
      }
      float inaccuracy = ModifierUtil.getInaccuracy(tool, living);
      float startAngle = getAngleStart(ammo.getCount());
      int primaryIndex = ammo.getCount() / 2;
      for (int arrowIndex = 0; arrowIndex < ammo.getCount(); arrowIndex++) {
        AbstractArrow arrow;
        if (thrownTool) {
          ThrownTool thrown = new ThrownTool(level, living, ammo, charge, velocity, waterInertia);
          thrown.setOriginalSlot(originalSlot);
          arrow = thrown;
        } else {
          arrow = arrowItem.createArrow(level, ammo, living);
        }
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
        EntityModifierCapability.getCapability(arrow).addModifiers(modifiers);

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

        // allow ballista to run a few remaining hooks if present
        if (thrownTool) {
          // know the cast is valid as we created the instance above
          ((ThrownTool) arrow).onRelease(living, arrowData);
        }

        level.addFreshEntity(arrow);
        level.playSound(null, living.getX(), living.getY(), living.getZ(), sound, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + charge * 0.5F + (angle / 10f));
      }
      int damage = ammo.getCount();
      if (thrownTool) {
        damage *= 3;
      }
      ToolDamageUtil.damageAnimated(tool, damage, living, living.getUsedItemHand());
    }

    // stats and sounds
    if (player != null) {
      player.awardStat(Stats.ITEM_USED.get(this));
    }
  }

  @Override
  protected void onStopUsing(IToolStackView tool, LivingEntity entity, int timeLeft) {
    super.onStopUsing(tool, entity, timeLeft);
    tool.getPersistentData().remove(KEY_BALLISTA);
  }
}
