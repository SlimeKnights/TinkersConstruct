package slimeknights.tconstruct.tools.logic;

import com.google.common.collect.Multiset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingVisibilityEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.events.TinkerToolEvent.ToolHarvestEvent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.ProtectionModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.armor.MobDisguiseModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorStatModule;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.helper.ArmorUtil;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.NamespacedNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.BlockSideHitListener;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.List;
import java.util.Objects;

/**
 * Event subscriber for tool events
 */
@SuppressWarnings("unused")
@EventBusSubscriber(modid = TConstruct.MOD_ID, bus = Bus.FORGE)
public class ToolEvents {
  @SubscribeEvent
  static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
    Player player = event.getEntity();

    // tool break speed hook
    ItemStack stack = player.getMainHandItem();
    if (stack.is(TinkerTags.Items.HARVEST)) {
      ToolStack tool = ToolStack.from(stack);
      if (!tool.isBroken()) {
        List<ModifierEntry> modifiers = tool.getModifierList();
        if (!modifiers.isEmpty()) {
          // modifiers using additive boosts may want info on the original boosts provided
          float miningSpeedModifier = Modifier.getMiningModifier(player);
          boolean isEffective = stack.isCorrectToolForDrops(event.getState());
          Direction direction = BlockSideHitListener.getSideHit(player);
          for (ModifierEntry entry : tool.getModifierList()) {
            entry.getHook(ModifierHooks.BREAK_SPEED).onBreakSpeed(tool, entry, event, direction, isEffective, miningSpeedModifier);
            // if any modifier cancels mining, stop right here
            if (event.isCanceled()) {
              return;
            }
          }
        }
      }
    }

    // next, add in armor haste
    float armorHaste = ArmorStatModule.getStat(player, TinkerDataKeys.MINING_SPEED);
    if (armorHaste > 0) {
      // adds in 10% per level
      event.setNewSpeed(event.getNewSpeed() * (1 + armorHaste));
    }
  }

  @SubscribeEvent
  static void onHarvest(ToolHarvestEvent event) {
    // prevent processing if already processed
    if (event.getResult() != Result.DEFAULT) {
      return;
    }
    BlockState state = event.getState();
    Block block = state.getBlock();
    Level world = event.getWorld();
    BlockPos pos = event.getPos();

    // carve pumpkins
    if (block == Blocks.PUMPKIN) {
      Direction facing = event.getContext().getClickedFace();
      if (facing.getAxis() == Direction.Axis.Y) {
        facing = event.getContext().getHorizontalDirection().getOpposite();
      }
      // carve block
      world.playSound(null, pos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);
      world.setBlock(pos, Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, facing), 11);
      // spawn seeds
      ItemEntity itemEntity = new ItemEntity(
        world,
        pos.getX() + 0.5D + facing.getStepX() * 0.65D,
        pos.getY() + 0.1D,
        pos.getZ() + 0.5D + facing.getStepZ() * 0.65D,
        new ItemStack(Items.PUMPKIN_SEEDS, 4));
      itemEntity.setDeltaMovement(
        0.05D * facing.getStepX() + world.random.nextDouble() * 0.02D,
        0.05D,
        0.05D * facing.getStepZ() + world.random.nextDouble() * 0.02D);
      world.addFreshEntity(itemEntity);
      event.setResult(Result.ALLOW);
    }

    // hives: get the honey
    if (block instanceof BeehiveBlock beehive) {
      int level = state.getValue(BeehiveBlock.HONEY_LEVEL);
      if (level >= 5) {
        // first, spawn the honey
        world.playSound(null, pos, SoundEvents.BEEHIVE_SHEAR, SoundSource.NEUTRAL, 1.0F, 1.0F);
        Block.popResource(world, pos, new ItemStack(Items.HONEYCOMB, 3));

        // if not smoking, make the bees angry
        if (!CampfireBlock.isSmokeyPos(world, pos)) {
          if (beehive.hiveContainsBees(world, pos)) {
            beehive.angerNearbyBees(world, pos);
          }
          beehive.releaseBeesAndResetHoneyLevel(world, state, pos, event.getPlayer(), BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
        } else {
          beehive.resetHoneyLevel(world, state, pos);
        }
        event.setResult(Result.ALLOW);
      } else {
        event.setResult(Result.DENY);
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  static void livingAttack(LivingAttackEvent event) {
    LivingEntity entity = event.getEntity();
    // client side always returns false, so this should be fine?
    if (entity.level.isClientSide() || entity.isDeadOrDying()) {
      return;
    }
    // I cannot think of a reason to run when invulnerable
    DamageSource source = event.getSource();
    if (entity.isInvulnerableTo(source)) {
      return;
    }

    // a lot of counterattack hooks want to detect direct attacks, so save time by calculating once
    boolean isDirectDamage = OnAttackedModifierHook.isDirectDamage(source);

    // determine if there is any modifiable armor, handles the target wearing modifiable armor
    EquipmentContext context = new EquipmentContext(entity);
    float amount = event.getAmount();
    if (context.hasModifiableArmor()) {
      // first we need to determine if any of the four slots want to cancel the event
      for (EquipmentSlot slotType : EquipmentSlot.values()) {
        if (ModifierUtil.validArmorSlot(entity, slotType)) {
          IToolStackView toolStack = context.getToolInSlot(slotType);
          if (toolStack != null && !toolStack.isBroken()) {
            for (ModifierEntry entry : toolStack.getModifierList()) {
              if (entry.getHook(ModifierHooks.DAMAGE_BLOCK).isDamageBlocked(toolStack, entry, context, slotType, source, amount)) {
                event.setCanceled(true);
                return;
              }
            }
          }
        }
      }

      // then we need to determine if any want to respond assuming its not canceled
      OnAttackedModifierHook.handleAttack(ModifierHooks.ON_ATTACKED, context, source, amount, isDirectDamage);
    }

    // next, consider the attacker is wearing modifiable armor
    Entity attacker = source.getEntity();
    if (attacker instanceof LivingEntity livingAttacker) {
      context = new EquipmentContext(livingAttacker);
      if (context.hasModifiableArmor()) {
        for (EquipmentSlot slotType : ModifiableArmorMaterial.ARMOR_SLOTS) {
          IToolStackView toolStack = context.getToolInSlot(slotType);
          if (toolStack != null && !toolStack.isBroken()) {
            for (ModifierEntry entry : toolStack.getModifierList()) {
              entry.getHook(ModifierHooks.DAMAGE_DEALT).onDamageDealt(toolStack, entry, context, slotType, entity, source, amount, isDirectDamage);
            }
          }
        }
      }
    }
  }

  /**
   * Determines how much to damage armor based on the given damage to the player
   * @param damage  Amount to damage the player
   * @return  Amount to damage the armor
   */
  private static int getArmorDamage(float damage) {
    damage /= 4;
    if (damage < 1) {
      return 1;
    }
    return (int)damage;
  }

  // low priority to minimize conflict as we apply reduction as if we are the final change to damage before vanilla
  @SubscribeEvent(priority = EventPriority.LOW)
  static void livingHurt(LivingHurtEvent event) {
    LivingEntity entity = event.getEntity();

    // determine if there is any modifiable armor, if not nothing to do
    DamageSource source = event.getSource();
    EquipmentContext context = new EquipmentContext(entity);
    int vanillaModifier = 0;
    float modifierValue = 0;
    float originalDamage = event.getAmount();

    // run shulking global damage "boost", its a bit hardcoded Java wise to make it softcoded in JSON
    Entity attacker = event.getSource().getEntity();
    if (attacker != null && attacker.isCrouching()) {
      float crouchDamage = ArmorStatModule.getStat(attacker, TinkerDataKeys.CROUCH_DAMAGE);
      if (crouchDamage != 0) {
        originalDamage = originalDamage * (1 + crouchDamage);
      }
    }

    // for our own armor, we have boosts from modifiers to consider
    if (context.hasModifiableArmor()) {
      // first, allow modifiers to change the damage being dealt and respond to it happening
      originalDamage = ModifyDamageModifierHook.modifyDamageTaken(ModifierHooks.MODIFY_HURT, context, source, originalDamage, OnAttackedModifierHook.isDirectDamage(source));
      event.setAmount(originalDamage);
      if (originalDamage <= 0) {
        event.setCanceled(true);
        return;
      }

      // remaining logic is reducing damage like vanilla protection
      // fetch vanilla enchant level, assuming its not bypassed in vanilla
      if (DamageSourcePredicate.CAN_PROTECT.matches(source)) {
        modifierValue = vanillaModifier = EnchantmentHelper.getDamageProtection(entity.getArmorSlots(), source);
      }

      // next, determine how much tinkers armor wants to change it
      // note that armor modifiers can choose to block "absolute damage" if they wish, currently just starving damage I think
      for (EquipmentSlot slotType : EquipmentSlot.values()) {
        if (ModifierUtil.validArmorSlot(entity, slotType)) {
          IToolStackView tool = context.getToolInSlot(slotType);
          if (tool != null && !tool.isBroken()) {
            for (ModifierEntry entry : tool.getModifierList()) {
              modifierValue = entry.getHook(ModifierHooks.PROTECTION).getProtectionModifier(tool, entry, context, slotType, source, modifierValue);
            }
          }
        }
      }

      // give slimes a 4x armor boost
      if (entity.getType().is(TinkerTags.EntityTypes.SMALL_ARMOR)) {
        modifierValue *= 4;
      }
    } else if (DamageSourcePredicate.CAN_PROTECT.matches(source) && entity.getType().is(TinkerTags.EntityTypes.SMALL_ARMOR)) {
      vanillaModifier = EnchantmentHelper.getDamageProtection(entity.getArmorSlots(), source);
      modifierValue = vanillaModifier * 4;
    }

    // if we changed anything, run our logic. Changing the cap has 2 problematic cases where same value will work:
    // * increased cap and vanilla is over the vanilla cap
    // * decreased cap and vanilla is now under the cap
    // that said, don't actually care about cap unless we have some protection, can use vanilla to simplify logic
    float cap = 20f;
    if (modifierValue > 0) {
      cap = ProtectionModifierHook.getProtectionCap(context.getTinkerData());
    }
    if (vanillaModifier != modifierValue || (cap > 20 && vanillaModifier > 20) || (cap < 20 && vanillaModifier > cap)) {
      // fetch armor and toughness if blockable, passing in 0 to the logic will skip the armor calculations
      float armor = 0, toughness = 0;
      if (!source.isBypassArmor()) {
        armor = entity.getArmorValue();
        toughness = (float)entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
      }

      // set the final dealt damage
      float finalDamage = ArmorUtil.getDamageForEvent(originalDamage, armor, toughness, vanillaModifier, modifierValue, cap);
      event.setAmount(finalDamage);

      // armor is damaged less as a result of our math, so damage the armor based on the difference if there is one
      if (!source.isBypassArmor()) {
        int damageMissed = getArmorDamage(originalDamage) - getArmorDamage(finalDamage);
        // TODO: is this check sufficient for whether the armor should be damaged? I partly wonder if I need to use reflection to call damageArmor
        if (damageMissed > 0 && entity instanceof Player) {
          for (EquipmentSlot slotType : ModifiableArmorMaterial.ARMOR_SLOTS) {
            // for our own armor, saves effort to damage directly with our utility
            IToolStackView tool = context.getToolInSlot(slotType);
            if (tool != null && (!source.isFire() || !tool.getItem().isFireResistant())) {
              // damaging the tool twice is generally not an issue, except for tanned where there is a difference between damaging by the sum and damaging twoce in pieces
              // so work around this by hardcoding a tanned check. Not making this a hook as this whole chunk of code should hopefully be unneeded in 1.21
              if (tool.getModifierLevel(TinkerModifiers.tanned.getId()) == 0) {
                ToolDamageUtil.damageAnimated(tool, damageMissed, entity, slotType);
              }
            } else {
              // if not our armor, damage using vanilla like logic
              ItemStack armorStack = entity.getItemBySlot(slotType);
              if (!armorStack.isEmpty() && (!source.isFire() || !armorStack.getItem().isFireResistant()) && armorStack.getItem() instanceof ArmorItem) {
                armorStack.hurtAndBreak(damageMissed, entity, e -> e.broadcastBreakEvent(slotType));
              }
            }
          }
        }
      }
    }
  }

  @SubscribeEvent
  static void livingDamage(LivingDamageEvent event) {
    LivingEntity entity = event.getEntity();
    DamageSource source = event.getSource();

    // give modifiers a chance to respond to damage happening
    EquipmentContext context = new EquipmentContext(entity);
    if (context.hasModifiableArmor()) {
      float amount = ModifyDamageModifierHook.modifyDamageTaken(ModifierHooks.MODIFY_DAMAGE, context, source, event.getAmount(), OnAttackedModifierHook.isDirectDamage(source));
      event.setAmount(amount);
      if (amount <= 0) {
        event.setCanceled(true);
        return;
      }
    }

    // when damaging ender dragons, may drop scales - must be player caused explosion, end crystals and TNT are examples
    if (Config.COMMON.dropDragonScales.get() && entity.getType() == EntityType.ENDER_DRAGON && event.getAmount() > 0
        && source.isExplosion() && source.getEntity() != null && source.getEntity().getType() == EntityType.PLAYER) {
      // drops 1 - 8 scales
      ModifierUtil.dropItem(entity, new ItemStack(TinkerModifiers.dragonScale, 1 + entity.level.random.nextInt(8)));
    }
  }

  /** Called the modifier hook when an entity's position changes */
  @SubscribeEvent
  static void livingWalk(LivingTickEvent event) {
    LivingEntity living = event.getEntity();
    // this event runs before vanilla updates prevBlockPos
    BlockPos pos = living.blockPosition();
    if (!living.isSpectator() && !living.level.isClientSide() && living.isAlive() && !Objects.equals(living.lastPos, pos)) {
      ItemStack boots = living.getItemBySlot(EquipmentSlot.FEET);
      if (!boots.isEmpty() && boots.is(TinkerTags.Items.BOOTS)) {
        ToolStack tool = ToolStack.from(boots);
        for (ModifierEntry entry : tool.getModifierList()) {
          entry.getHook(ModifierHooks.BOOT_WALK).onWalk(tool, entry, living, living.lastPos, pos);
        }
      }
    }
  }

  /** Handles visibility effects of mob disguise and projectile protection */
  @SubscribeEvent
  static void livingVisibility(LivingVisibilityEvent event) {
    // always nonnull in vanilla, not sure when it would be nullable but I dont see a need for either modifier
    Entity lookingEntity = event.getLookingEntity();
    if (lookingEntity == null) {
      return;
    }
    LivingEntity living = event.getEntity();
    living.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
      // mob disguise
      Multiset<EntityType<?>> disguises = data.get(MobDisguiseModule.DISGUISES);
      if (disguises != null) {
        int count = disguises.count(living.getType());
        if (count > 0) {
          // halves the range per level
          event.modifyVisibility(1 / Math.pow(2, count));
        }
      }
    });
  }

  /** Implements projectile hit hook */
  @SubscribeEvent
  static void projectileHit(ProjectileImpactEvent event) {
    Projectile projectile = event.getProjectile();
    ModifierNBT modifiers = EntityModifierCapability.getOrEmpty(projectile);
    if (!modifiers.isEmpty()) {
      NamespacedNBT nbt = PersistentDataCapability.getOrWarn(projectile);
      HitResult hit = event.getRayTraceResult();
      HitResult.Type type = hit.getType();
      // extract a firing entity as that is a common need
      LivingEntity attacker = projectile.getOwner() instanceof LivingEntity l ? l : null;
      switch(type) {
        case ENTITY -> {
          EntityHitResult entityHit = (EntityHitResult)hit;
          // cancel all effects on endermen unless we have enderference, endermen like to teleport away
          // yes, hardcoded to enderference, if you need your own enderference for whatever reason, talk to us
          if (entityHit.getEntity().getType() != EntityType.ENDERMAN || modifiers.getLevel(TinkerModifiers.enderference.getId()) > 0) {
            // extract a living target as that is the most common need
            LivingEntity target = ToolAttackUtil.getLivingEntity(entityHit.getEntity());
            for (ModifierEntry entry : modifiers.getModifiers()) {
              if (entry.getHook(ModifierHooks.PROJECTILE_HIT).onProjectileHitEntity(modifiers, nbt, entry, projectile, entityHit, attacker, target)) {
                event.setCanceled(true);
              }
            }
          }
        }
        case BLOCK -> {
          BlockHitResult blockHit = (BlockHitResult)hit;
          for (ModifierEntry entry : modifiers.getModifiers()) {
            if (entry.getHook(ModifierHooks.PROJECTILE_HIT).onProjectileHitBlock(modifiers, nbt, entry, projectile, blockHit, attacker)) {
              event.setCanceled(true);
            }
          }
        }
      }
    }
  }
}
