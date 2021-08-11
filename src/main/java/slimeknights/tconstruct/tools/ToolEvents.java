package slimeknights.tconstruct.tools;

import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.events.TinkerToolEvent.ToolHarvestEvent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.helper.ArmorUtil;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.BlockSideHitListener;

import java.util.List;

/**
 * Event subscriber for tool events
 */
@SuppressWarnings("unused")
@EventBusSubscriber(modid = TConstruct.MOD_ID, bus = Bus.FORGE)
public class ToolEvents {
  @SubscribeEvent
  static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
    // Note the way the subscribers are set up, technically works on anything that has the tic_modifiers tag
    ItemStack stack = event.getPlayer().getHeldItemMainhand();
    if (!TinkerTags.Items.HARVEST.contains(stack.getItem())) {
      return;
    }
    ToolStack tool = ToolStack.from(stack);
    if (!tool.isBroken()) {
      List<ModifierEntry> modifiers = tool.getModifierList();
      if (!modifiers.isEmpty()) {
        // modifiers using additive boosts may want info on the original boosts provided
        PlayerEntity player = event.getPlayer();
        float miningSpeedModifier = Modifier.getMiningModifier(player);
        boolean isEffective = stack.canHarvestBlock(event.getState());
        Direction direction = BlockSideHitListener.getSideHit(player);
        for (ModifierEntry entry : tool.getModifierList()) {
          entry.getModifier().onBreakSpeed(tool, entry.getLevel(), event, direction, isEffective, miningSpeedModifier);
          // if any modifier cancels mining, stop right here
          if (event.isCanceled()) {
            break;
          }
        }
      }
    }
  }

  @SubscribeEvent
  static void interactWithEntity(EntityInteract event) {
    // Note the way the subscribers are set up, technically works on anything that has the tic_modifiers tag
    ItemStack stack = event.getItemStack();
    if (!TinkerTags.Items.HARVEST.contains(stack.getItem())) {
      return;
    }
    ToolStack tool = ToolStack.from(stack);
    PlayerEntity player = event.getPlayer();
    Hand hand = event.getHand();
    Entity target = event.getTarget();
    for (ModifierEntry entry : tool.getModifierList()) {
      // exit on first successful result
      ActionResultType result = entry.getModifier().onEntityUseFirst(tool, entry.getLevel(), player, target, hand);
      if (result.isSuccessOrConsume()) {
        event.setCanceled(true);
        event.setCancellationResult(result);
        return;
      }
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
    World world = event.getWorld();
    BlockPos pos = event.getPos();

    // carve pumpkins
    if (block == Blocks.PUMPKIN) {
      Direction facing = event.getContext().getFace();
      if (facing.getAxis() == Direction.Axis.Y) {
        facing = event.getContext().getPlacementHorizontalFacing().getOpposite();
      }
      // carve block
      world.playSound(null, pos, SoundEvents.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 1.0F, 1.0F);
      world.setBlockState(pos, Blocks.CARVED_PUMPKIN.getDefaultState().with(CarvedPumpkinBlock.FACING, facing), 11);
      // spawn seeds
      ItemEntity itemEntity = new ItemEntity(
        world,
        pos.getX() + 0.5D + facing.getXOffset() * 0.65D,
        pos.getY() + 0.1D,
        pos.getZ() + 0.5D + facing.getZOffset() * 0.65D,
        new ItemStack(Items.PUMPKIN_SEEDS, 4));
      itemEntity.setMotion(
        0.05D * facing.getXOffset() + world.rand.nextDouble() * 0.02D,
        0.05D,
        0.05D * facing.getZOffset() + world.rand.nextDouble() * 0.02D);
      world.addEntity(itemEntity);
      event.setResult(Result.ALLOW);
    }

    // hives: get the honey
    if (block instanceof BeehiveBlock) {
      BeehiveBlock beehive = (BeehiveBlock) block;
      int level = state.get(BeehiveBlock.HONEY_LEVEL);
      if (level >= 5) {
        // first, spawn the honey
        world.playSound(null, pos, SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        Block.spawnAsEntity(world, pos, new ItemStack(Items.HONEYCOMB, 3));

        // if not smoking, make the bees angry
        if (!CampfireBlock.isSmokingBlockAt(world, pos)) {
          if (beehive.hasBees(world, pos)) {
            beehive.angerNearbyBees(world, pos);
          }
          beehive.takeHoney(world, state, pos, event.getPlayer(), BeehiveTileEntity.State.EMERGENCY);
        } else {
          beehive.takeHoney(world, state, pos);
        }
        event.setResult(Result.ALLOW);
      } else {
        event.setResult(Result.DENY);
      }
    }
  }

  @SubscribeEvent
  static void enderDragonDamage(LivingDamageEvent event) {
    // dragon being damaged
    LivingEntity entity = event.getEntityLiving();
    if (entity.getType() == EntityType.ENDER_DRAGON && event.getAmount() > 0 && !entity.getEntityWorld().isRemote) {
      // player caused explosion, end crystals and TNT are examples
      DamageSource source = event.getSource();
      if (source.isExplosion() && source.getTrueSource() != null && source.getTrueSource().getType() == EntityType.PLAYER) {
        // drops 1 - 8 scales
        ModifierUtil.dropItem(entity, new ItemStack(TinkerModifiers.dragonScale, 1 + entity.getEntityWorld().rand.nextInt(8)));
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
  static void entityDamage(LivingHurtEvent event) {
    LivingEntity entity = event.getEntityLiving();

    // determine if there is any modifiable armor, if not nothing to do
    // TODO: shields should support this hook too, probably with a separate tag so holding armor does not count as a shield
    boolean hasTinkersArmor = false;
    ToolStack[] toolStacks = new ToolStack[4];
    for (EquipmentSlotType slotType : ModifiableArmorMaterial.ARMOR_SLOTS) {
      ItemStack stack = entity.getItemStackFromSlot(slotType);
      if (!stack.isEmpty() && TinkerTags.Items.ARMOR.contains(stack.getItem())) {
        hasTinkersArmor = true;
        toolStacks[slotType.getIndex()] = ToolStack.from(stack);
      }
    }
    if (!hasTinkersArmor) {
      return;
    }

    // first, fetch vanilla enchant level, assuming its not bypassed in vanilla
    DamageSource source = event.getSource();
    int vanillaModifier = 0;
    if (!source.isDamageAbsolute()) {
      vanillaModifier = EnchantmentHelper.getEnchantmentModifierDamage(entity.getArmorInventoryList(), source);
    }

    // next, determine how much tinkers armor wants to change it
    // note that armor modifiers can choose to block "absolute damage" if they wish, currently just starving damage I think
    float modifierValue = vanillaModifier;
    float originalDamage = event.getAmount();
    for (EquipmentSlotType slotType : ModifiableArmorMaterial.ARMOR_SLOTS) {
      ToolStack tool = toolStacks[slotType.getIndex()];
      if (tool != null) {
        for (ModifierEntry entry : tool.getModifierList()) {
          modifierValue = entry.getModifier().getProtectionModifier(tool, entry.getLevel(), entity, source, slotType, modifierValue);
        }
      }
    }

    // TODO: consider hook for modifiers to change damage directly
    // if we changed anything, run our logic
    if (vanillaModifier != modifierValue) {
      // fetch armor and toughness if blockable, passing in 0 to the logic will skip the armor calculations
      float armor = 0, toughness = 0;
      if (!source.isUnblockable()) {
        armor = entity.getTotalArmorValue();
        toughness = (float)entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
      }

      // set the final dealt damage
      float finalDamage = ArmorUtil.getDamageForEvent(originalDamage, armor, toughness, vanillaModifier, modifierValue);
      event.setAmount(finalDamage);

      // armor is damaged less as a result of our math, so damage the armor based on the difference if there is one
      if (!source.isUnblockable()) {
        int damageMissed = getArmorDamage(originalDamage) - getArmorDamage(finalDamage);
        // TODO: is this check sufficient for whether the armor should be damaged? I partly wonder if I need to use reflection to call damageArmor
        if (damageMissed > 0 && entity instanceof PlayerEntity) {
          for (EquipmentSlotType slotType : ModifiableArmorMaterial.ARMOR_SLOTS) {
            // for our own armor, saves effort to damage directly with our utility
            ToolStack tool = toolStacks[slotType.getIndex()];
            if (tool != null && (!source.isFireDamage() || !tool.getItem().isImmuneToFire())) {
              ToolDamageUtil.damageAnimated(tool, damageMissed, entity, slotType);
            } else {
              // if not our armor, damage using vanilla like logic
              ItemStack armorStack = entity.getItemStackFromSlot(slotType);
              if (!armorStack.isEmpty() && (!source.isFireDamage() || !armorStack.getItem().isImmuneToFire()) && armorStack.getItem() instanceof ArmorItem) {
                armorStack.damageItem(damageMissed, entity, e -> e.sendBreakAnimation(slotType));
              }
            }
          }
        }
      }
    }
  }
}
