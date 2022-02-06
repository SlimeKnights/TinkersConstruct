package slimeknights.tconstruct.tools.logic;

import com.google.common.collect.Multiset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
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
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingVisibilityEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.events.TinkerToolEvent.ToolHarvestEvent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorWalkModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.helper.ArmorUtil;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.BlockSideHitListener;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.defense.ProjectileProtectionModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.MobDisguiseModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.harvest.HasteModifier;

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
    Player player = event.getPlayer();

    // if we are underwater, have the aqua affinity modifier, and are not under the effects of vanilla aqua affinity, cancel the underwater modifier
    if (player.isEyeInFluid(FluidTags.WATER) && ModifierUtil.getTotalModifierLevel(player, TinkerDataKeys.AQUA_AFFINITY) > 0 && !EnchantmentHelper.hasAquaAffinity(player)) {
      event.setNewSpeed(event.getNewSpeed() * 5);
    }

    // tool break speed hook
    ItemStack stack = player.getMainHandItem();
    if (TinkerTags.Items.HARVEST.contains(stack.getItem())) {
      ToolStack tool = ToolStack.from(stack);
      if (!tool.isBroken()) {
        List<ModifierEntry> modifiers = tool.getModifierList();
        if (!modifiers.isEmpty()) {
          // modifiers using additive boosts may want info on the original boosts provided
          float miningSpeedModifier = Modifier.getMiningModifier(player);
          boolean isEffective = stack.isCorrectToolForDrops(event.getState());
          Direction direction = BlockSideHitListener.getSideHit(player);
          for (ModifierEntry entry : tool.getModifierList()) {
            entry.getModifier().onBreakSpeed(tool, entry.getLevel(), event, direction, isEffective, miningSpeedModifier);
            // if any modifier cancels mining, stop right here
            if (event.isCanceled()) {
              return;
            }
          }
        }
      }
    }

    // next, add in armor haste
    float armorHaste = ModifierUtil.getTotalModifierFloat(player, HasteModifier.HASTE);
    if (armorHaste > 0) {
      // adds in 10% per level
      event.setNewSpeed(event.getNewSpeed() * (1 + 0.1f * armorHaste));
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

  @SubscribeEvent
  static void enderDragonDamage(LivingDamageEvent event) {
    if (!Config.COMMON.dropDragonScales.get()) {
      return;
    }
    // dragon being damaged
    LivingEntity entity = event.getEntityLiving();
    if (entity.getType() == EntityType.ENDER_DRAGON && event.getAmount() > 0 && !entity.level.isClientSide) {
      // player caused explosion, end crystals and TNT are examples
      DamageSource source = event.getSource();
      if (source.isExplosion() && source.getEntity() != null && source.getEntity().getType() == EntityType.PLAYER) {
        // drops 1 - 8 scales
        ModifierUtil.dropItem(entity, new ItemStack(TinkerModifiers.dragonScale, 1 + entity.level.random.nextInt(8)));
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  static void livingAttack(LivingAttackEvent event) {
    LivingEntity entity = event.getEntityLiving();
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
    boolean isDirectDamage = source.getEntity() != null && source instanceof EntityDamageSource entityDamage && !entityDamage.isThorns();

    // determine if there is any modifiable armor, handles the target wearing modifiable armor
    EquipmentContext context = new EquipmentContext(entity);
    float amount = event.getAmount();
    if (context.hasModifiableArmor()) {
      // first we need to determine if any of the four slots want to cancel the event, then we need to determine if any want to respond assuming its not canceled
      for (EquipmentSlot slotType : ModifiableArmorMaterial.ARMOR_SLOTS) {
        IToolStackView toolStack = context.getToolInSlot(slotType);
        if (toolStack != null && !toolStack.isBroken()) {
          for (ModifierEntry entry : toolStack.getModifierList()) {
            if (entry.getModifier().isSourceBlocked(toolStack, entry.getLevel(), context, slotType, source, amount)) {
              event.setCanceled(true);
              return;
            }
          }
        }
      }

      // next, give modifiers a chance to respond to the entity being attacked, for counterattack hooks mainly
      // first we need to determine if any of the four slots want to cancel the event, then we need to determine if any want to respond assuming its not canceled
      for (EquipmentSlot slotType : ModifiableArmorMaterial.ARMOR_SLOTS) {
        IToolStackView toolStack = context.getToolInSlot(slotType);
        if (toolStack != null && !toolStack.isBroken()) {
          for (ModifierEntry entry : toolStack.getModifierList()) {
            entry.getModifier().onAttacked(toolStack, entry.getLevel(), context, slotType, source, amount, isDirectDamage);
          }
        }
      }
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
              entry.getModifier().attackWithArmor(toolStack, entry.getLevel(), context, slotType, entity, source, amount, isDirectDamage);
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
    LivingEntity entity = event.getEntityLiving();

    // determine if there is any modifiable armor, if not nothing to do
    // TODO: shields should support this hook too, probably with a separate tag so holding armor does not count as a shield
    EquipmentContext context = new EquipmentContext(entity);
    if (!context.hasModifiableArmor()) {
      return;
    }

    // first, fetch vanilla enchant level, assuming its not bypassed in vanilla
    DamageSource source = event.getSource();
    int vanillaModifier = 0;
    if (!source.isBypassMagic()) {
      vanillaModifier = EnchantmentHelper.getDamageProtection(entity.getArmorSlots(), source);
    }

    // next, determine how much tinkers armor wants to change it
    // note that armor modifiers can choose to block "absolute damage" if they wish, currently just starving damage I think
    float modifierValue = vanillaModifier;
    float originalDamage = event.getAmount();
    for (EquipmentSlot slotType : ModifiableArmorMaterial.ARMOR_SLOTS) {
      IToolStackView tool = context.getToolInSlot(slotType);
      if (tool != null && !tool.isBroken()) {
        for (ModifierEntry entry : tool.getModifierList()) {
          modifierValue = entry.getModifier().getProtectionModifier(tool, entry.getLevel(), context, slotType, source, modifierValue);
        }
      }
    }

    // TODO: consider hook for modifiers to change damage directly
    // if we changed anything, run our logic
    if (vanillaModifier != modifierValue) {
      // fetch armor and toughness if blockable, passing in 0 to the logic will skip the armor calculations
      float armor = 0, toughness = 0;
      if (!source.isBypassArmor()) {
        armor = entity.getArmorValue();
        toughness = (float)entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
      }

      // set the final dealt damage
      float finalDamage = ArmorUtil.getDamageForEvent(originalDamage, armor, toughness, vanillaModifier, modifierValue);
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
              ToolDamageUtil.damageAnimated(tool, damageMissed, entity, slotType);
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

  /** Called the modifier hook when an entity's position changes */
  @SubscribeEvent
  static void livingWalk(LivingUpdateEvent event) {
    LivingEntity living = event.getEntityLiving();
    // this event runs before vanilla updates prevBlockPos
    BlockPos pos = living.blockPosition();
    if (!living.isSpectator() && !living.level.isClientSide() && living.isAlive() && !Objects.equals(living.lastPos, pos)) {
      ItemStack boots = living.getItemBySlot(EquipmentSlot.FEET);
      if (!boots.isEmpty() && TinkerTags.Items.BOOTS.contains(boots.getItem())) {
        ToolStack tool = ToolStack.from(boots);
        for (ModifierEntry entry : tool.getModifierList()) {
          IArmorWalkModifier hook = entry.getModifier().getModule(IArmorWalkModifier.class);
          if (hook != null) {
            hook.onWalk(tool, entry.getLevel(), living, living.lastPos, pos);
          }
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
    LivingEntity living = event.getEntityLiving();
    living.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
      // mob disguise
      Multiset<EntityType<?>> disguises = data.get(MobDisguiseModifier.DISGUISES);
      if (disguises != null && disguises.contains(lookingEntity.getType())) {
        // not as good as a real head
        event.modifyVisibility(0.65f);
      }

      // projectile protection
      ModifierMaxLevel projData = data.get(ProjectileProtectionModifier.PROJECTILE_DATA);
      if (projData != null) {
        float max = projData.getMax();
        if (max > 0) {
          // reduces visibility by 5% per level
          event.modifyVisibility(Math.max(0, 1 - (max * 0.05)));
        }
      }
    });
  }
}
