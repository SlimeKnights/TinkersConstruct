package slimeknights.tconstruct.tools.modifiers.ability.interaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Event.Result;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.events.TinkerToolEvent.ToolHarvestEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hooks.IHarvestModifier;
import slimeknights.tconstruct.library.modifiers.impl.InteractionModifier;
import slimeknights.tconstruct.library.tools.definition.aoe.IAreaOfEffectIterator;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
public class HarvestAbilityModifier extends InteractionModifier.SingleUse {
  @Getter
  private final int priority;

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return priority > Short.MIN_VALUE;
  }
  
  /**
   * Harvests a block that is harvested on interaction, such a berry bushes
   * @param context  Item use context of the original block clicked
   * @param world    Level instance
   * @param state    State to harvest
   * @param pos      Position to harvest
   * @param player   Player instance
   * @return  True if harvested
   */

  private static boolean harvestInteract(UseOnContext context, ServerLevel world, BlockState state, BlockPos pos, @Nullable Player player) {
    if (player == null) {
      return false;
    }
    BlockHitResult trace = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), pos, false);
    InteractionResult result = state.use(world, player, context.getHand(), trace);
    return result.consumesAction();
  }

  /**
   * Harvests a stackable block, like sugar cane or kelp
   * @param world   Level instance
   * @param state   Block state
   * @param pos     Block position
   * @param player  Player instance
   * @return True if the block was harvested
   */
  private static boolean harvestStackable(ServerLevel world, BlockState state, BlockPos pos, @Nullable Player player) {
    // if the block below is the same, break this block
    if (world.getBlockState(pos.below()).getBlock() == state.getBlock()) {
      world.destroyBlock(pos, true, player);
      return true;
    } else {
      // if the block above is the same, break it
      BlockPos up = pos.above();
      if (world.getBlockState(up).getBlock() == state.getBlock()) {
        world.destroyBlock(up, true, player);
        return true;
      }
    }
    return false;
  }

  /**
   * Tries harvesting a normal crop, that is a crop that goes through a set number of stages and is broken to drop produce and seeds
   * @param stack   Tool stack
   * @param world   Level instance
   * @param state   Block state
   * @param pos     Block position
   * @param player  Player instance
   * @return  True if the crop was successfully harvested
   */
  private static boolean harvestCrop(ItemStack stack, ServerLevel world, BlockState state, BlockPos pos, @Nullable Player player) {
    Block block = state.getBlock();
    BlockState replant;
    // if crops block, its easy
    if (block instanceof CropBlock crops) {
      if (!crops.isMaxAge(state)) {
        return false;
      }
      replant = crops.getStateForAge(0);
    } else {
      // try to find an age property
      IntegerProperty age = null;
      for (Property<?> prop : state.getProperties()) {
        if (prop.getName().equals("age") && prop instanceof IntegerProperty) {
          age = (IntegerProperty)prop;
          break;
        }
      }
      // must have an age property
      if (age == null) {
        return false;
      } else {
        // property must have 0 as valid
        Collection<Integer> allowedValues = age.getPossibleValues();
        if (!allowedValues.contains(0)) {
          return false;
        }
        // crop must be max age
        int maxAge = age.getPossibleValues().stream().max(Integer::compareTo).orElse(Integer.MAX_VALUE);
        if (state.getValue(age) < maxAge) {
          return false;
        }
        replant = state.setValue(age, 0);
      }
    }

    // crop is fully grown, get loot context
    LootContext.Builder lootContext = new LootContext.Builder(world)
      .withRandom(world.random)
      .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
      .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
      .withOptionalParameter(LootContextParams.BLOCK_ENTITY, world.getBlockEntity(pos));
    // find drops
    List<ItemStack> drops = state.getDrops(lootContext);

    // find a seed to remove from the drops
    Iterator<ItemStack> iterator = drops.iterator();
    boolean hasSeed = false;
    while (iterator.hasNext()) {
      ItemStack drop = iterator.next();
      if (TinkerTags.Items.SEEDS.contains(drop.getItem())) {
        hasSeed = true;
        drop.shrink(1);
        if (drop.isEmpty()) {
          iterator.remove();
        }
        break;
      }
    }

    // if we found one, replant, no seed means break
    if (hasSeed) {
      world.setBlockAndUpdate(pos, replant);
      state.spawnAfterBreak(world, pos, stack);
      // set block state will not play sounds, destory block will
      world.playSound(null, pos, state.getSoundType(world, pos, player).getBreakSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
    } else {
      world.destroyBlock(pos, false);
    }

    // drop items
    for (ItemStack drop : drops) {
      Block.popResource(world, pos, drop);
    }

    return true;
  }

  /**
   * Tries to harvest the crop at the given position
   * @param context  Item use context of the original block clicked
   * @param world    Level instance
   * @param state    State to harvest
   * @param pos      Position to harvest
   * @param slotType Slot used to harvest
   * @return  True if harvested
   */
  private static boolean harvest(UseOnContext context, IToolStackView tool, ServerLevel world, BlockState state, BlockPos pos, EquipmentSlot slotType) {
    Player player = context.getPlayer();
    // first, check main harvestable tag
    Block block = state.getBlock();
    if (!TinkerTags.Blocks.HARVESTABLE.contains(block)) {
      return false;
    }
    // try harvest event
    boolean didHarvest = false;
    Result result = new ToolHarvestEvent(tool, context, world, state, pos, slotType).fire();
    if (result != Result.DEFAULT) {
      didHarvest = result == Result.ALLOW;

      // crops that work based on right click interact (berry bushes)
    } else if (TinkerTags.Blocks.HARVESTABLE_INTERACT.contains(block)) {
      didHarvest = harvestInteract(context, world, state, pos, player);

      // next, try sugar cane like blocks
    } else if (TinkerTags.Blocks.HARVESTABLE_STACKABLE.contains(block)) {
      didHarvest = harvestStackable(world, state, pos, player);

      // normal crops like wheat or carrots
    } else if (TinkerTags.Blocks.HARVESTABLE_CROPS.contains(block)) {
      didHarvest = harvestCrop(context.getItemInHand(), world, state, pos, player);
    }

    // if we successfully harvested, run the modifier hook
    if (didHarvest) {
      for (ModifierEntry entry : tool.getModifierList()) {
        IHarvestModifier harvest = entry.getModifier().getModule(IHarvestModifier.class);
        if (harvest != null) {
          harvest.afterHarvest(tool, entry.getLevel(), context, world, state, pos);
        }
      }
    }

    return didHarvest;
  }

  @Override
  public InteractionResult beforeBlockUse(IToolStackView tool, int level, UseOnContext context, EquipmentSlot slotType) {
    if (tool.isBroken()) {
      return InteractionResult.PASS;
    }

    // skip if sneaking
    Player player = context.getPlayer();
    if (player != null && player.isShiftKeyDown()) {
      return InteractionResult.PASS;
    }

    // try harvest first
    Level world = context.getLevel();
    BlockPos pos = context.getClickedPos();
    BlockState state = world.getBlockState(pos);
    if (TinkerTags.Blocks.HARVESTABLE.contains(state.getBlock())) {
      if (world instanceof ServerLevel server) {
        boolean survival = player == null || !player.isCreative();

        // try harvesting the crop, if successful and survival, damage the tool
        boolean didHarvest = false;
        boolean broken = false;
        ItemStack stack = context.getItemInHand();
        if (harvest(context, tool, server, state, pos, slotType)) {
          didHarvest = true;
          broken = survival && ToolDamageUtil.damage(tool, 1, player, stack);
        }

        // if we have a player and harvest logic, try doing AOE harvest
        Item item = stack.getItem();
        if (!broken && player != null) {
          for (BlockPos newPos : tool.getDefinition().getData().getAOE().getBlocks(tool, stack, player, state, world, pos, context.getClickedFace(), IAreaOfEffectIterator.AOEMatchType.TRANSFORM)) {
            // try harvesting the crop, if successful and survival, damage the tool
            if (harvest(context, tool, server, world.getBlockState(newPos), newPos, slotType)) {
              didHarvest = true;
              if (survival && ToolDamageUtil.damage(tool, 1, player, stack)) {
                broken = true;
                break;
              }
            }
          }
        }
        // animations
        if (player != null) {
          if (didHarvest) {
            player.sweepAttack();
          }
          if (broken) {
            player.broadcastBreakEvent(context.getHand());
          }
        }
      }
      return InteractionResult.SUCCESS;
    }
    return InteractionResult.PASS;
  }
}
