package slimeknights.tconstruct.tools;

import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.events.TinkerToolEvent.ToolHarvestEvent;
import slimeknights.tconstruct.library.tools.helper.BlockSideHitListener;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.List;

/**
 * Event subscriber for tool events
 */
@SuppressWarnings("unused")
@EventBusSubscriber(modid = TConstruct.modID, bus = Bus.FORGE)
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

  /** Applies the looting bonus for this modifier */
  @SubscribeEvent
  static void onLooting(LootingLevelEvent event) {
    // must be an attacker with our tool
    DamageSource damageSource = event.getDamageSource();
    if (damageSource == null) {
      return;
    }
    Entity source = damageSource.getTrueSource();
    if (source instanceof LivingEntity) {
      // TODO: consider offhand usage, bows or daggers
      // TODO: extend to armor eventually
      LivingEntity holder = ((LivingEntity)source);
      ItemStack held = holder.getHeldItemMainhand();
      if (TinkerTags.Items.MODIFIABLE.contains(held.getItem())) {
        ToolStack tool = ToolStack.from(held);
        int newLevel = ModifierUtil.getLootingLevel(tool, holder,  event.getEntityLiving(), damageSource);
        if (newLevel > event.getLootingLevel()) {
          event.setLootingLevel(newLevel);
        }
      }
    }
  }
}
