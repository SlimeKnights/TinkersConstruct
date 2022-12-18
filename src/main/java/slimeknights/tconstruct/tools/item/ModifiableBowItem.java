package slimeknights.tconstruct.tools.item;

import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableLauncherItem;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.NamespacedNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

import java.util.function.Predicate;

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

    // TODO: move this to left click later once left click hooks are implemented
    ToolStack tool = ToolStack.from(bow);
    if (player.isCrouching()) {
      InteractionResult result = ToolInventoryCapability.tryOpenContainer(bow, tool, player, Util.getSlotType(hand));
      if (result.consumesAction()) {
        return new InteractionResultHolder<>(result, bow);
      }
    }

    // no need to ask the modifiers for ammo if we have it in the inventory, as there is no way for a modifier to say not to use ammo if its present
    // inventory search is probably a bit faster on average than modifier search as its already parsed
    boolean hasAmmo = !player.getProjectile(bow).isEmpty();
    if (!hasAmmo) {
      Predicate<ItemStack> ammoPredicate = getAllSupportedProjectiles();
      for (ModifierEntry entry : tool.getModifierList()) {
        if (!entry.getHook(TinkerHooks.BOW_AMMO).findAmmo(tool, entry, player, ItemStack.EMPTY, ammoPredicate).isEmpty()) {
          hasAmmo = true;
          break;
        }
      }
    }

    // ask forge if it has any different opinions
    InteractionResultHolder<ItemStack> override = ForgeEventFactory.onArrowNock(bow, level, player, hand, hasAmmo);
    if (override != null) {
      return override;
    }
    if (!player.getAbilities().instabuild && !hasAmmo) {
      return InteractionResultHolder.fail(bow);
    }
    player.startUsingItem(hand);
    return InteractionResultHolder.consume(bow);
  }

  @Override
  public void releaseUsing(ItemStack bow, Level level, LivingEntity living, int timeLeft) {
    // need player
    if (!(living instanceof Player player)) {
      return;
    }
    // no broken
    ToolStack tool = ToolStack.from(bow);
    if (tool.isBroken()) {
      return;
    }

    // for the sake of compat with custom arrows, we cannot do an infinity hook, as its up to each arrow to decide if it supports infinity or not and the only way to decide that is enchants
    // we may in the future we may toss a conditional enchantment hook here though if we end up needing other bow enchants
    boolean hasInfinity = player.getAbilities().instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, bow) > 0;

    // find ammo
    ItemStack standardAmmo = player.getProjectile(bow);
    ItemStack ammo = ItemStack.EMPTY;
    ModifierEntry ammoFindingModifier = null;
    Predicate<ItemStack> ammoPredicate = getAllSupportedProjectiles();
    for (ModifierEntry entry : tool.getModifierList()) {
      ammo = entry.getHook(TinkerHooks.BOW_AMMO).findAmmo(tool, entry, living, standardAmmo, ammoPredicate);
      if (!ammo.isEmpty()) {
        ammoFindingModifier = entry;
        break;
      }
    }
    if (ammo.isEmpty()) {
      ammo = standardAmmo;
    }

    // ask forge its thoughts on shooting
    int chargeTime = this.getUseDuration(bow) - timeLeft;
    chargeTime = ForgeEventFactory.onArrowLoose(bow, level, player, chargeTime, !ammo.isEmpty() || hasInfinity);

    // no ammo? no charge? nothing to do
    if (chargeTime < 0 || (ammo.isEmpty() && !hasInfinity)) {
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
    boolean arrowInfinite = player.getAbilities().instabuild || (ammo.getItem() instanceof ArrowItem arrow && arrow.isInfinite(ammo, bow, player));
    if (!level.isClientSide) {
      ArrowItem arrowItem = ammo.getItem() instanceof ArrowItem arrow ? arrow : (ArrowItem)Items.ARROW;
      AbstractArrow arrowEntity = arrowItem.createArrow(level, ammo, player);
      float accuracy = ConditionalStatModifierHook.getModifiedStat(tool, living, ToolStats.ACCURACY);
      arrowEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 3*(1/accuracy-1) * velocity);
      if (charge == 1.0F) {
        arrowEntity.setCritArrow(true);
      }
      // vanilla arrows have a base damage of 2, cancel that out then add in our base damage to account for custom arrows with higher base damage
      float baseArrowDamage = (float)(arrowEntity.getBaseDamage() - 2 + tool.getStats().get(ToolStats.PROJECTILE_DAMAGE));
      arrowEntity.setBaseDamage(ConditionalStatModifierHook.getModifiedStat(tool, living, ToolStats.PROJECTILE_DAMAGE, baseArrowDamage));

      // just store all modifiers on the tool for simplicity
      ModifierNBT modifiers = tool.getModifiers();
      arrowEntity.getCapability(EntityModifierCapability.CAPABILITY).ifPresent(cap -> cap.setModifiers(modifiers));

      // fetch the persistent data for the arrow as modifiers may want to store data
      NamespacedNBT arrowData = PersistentDataCapability.getOrWarn(arrowEntity);

      // let modifiers such as fiery and punch set properties
      for (ModifierEntry entry : modifiers.getModifiers()) {
        entry.getHook(TinkerHooks.ARROW_LAUNCH).onArrowLaunch(tool, entry, living, arrowEntity, arrowData);
      }

      ToolDamageUtil.damageAnimated(tool, 1, player, player.getUsedItemHand());
      // if infinite, skip pickup
      if (arrowInfinite) {
        arrowEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
      }
      level.addFreshEntity(arrowEntity);
    }

    // consume items
    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + charge * 0.5F);
    if (!arrowInfinite) {
      if (ammoFindingModifier != null) {
        ammoFindingModifier.getHook(TinkerHooks.BOW_AMMO).shrinkAmmo(tool, ammoFindingModifier, living, ammo);
      } else {
        ammo.shrink(1);
      }
      // there is a chance the ammo came from the inventory either way, does not hurt to call this if it did not
      if (ammo.isEmpty()) {
        player.getInventory().removeItem(ammo);
      }
    }

    player.awardStat(Stats.ITEM_USED.get(this));
  }

  @Override
  public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
    if (this.allowdedIn(group)) {
      ToolBuildHandler.addDefaultSubItems(this, items, null, null, null, MaterialIds.string);
    }
  }
}
