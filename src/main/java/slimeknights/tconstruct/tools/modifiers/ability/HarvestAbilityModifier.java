package slimeknights.tconstruct.tools.modifiers.ability;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Event.Result;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.events.TinkerToolEvent.ToolHarvestEvent;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic.AOEMatchType;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class HarvestAbilityModifier extends SingleUseModifier {
  private final int priority;
  
  public HarvestAbilityModifier(int color, int priority) {
    super(color);
    this.priority = priority;
  }
  
  @Override
  public int getPriority() {
    return priority;
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return priority > Short.MIN_VALUE;
  }
  
  /**
   * Harvests a block that is harvested on interaction, such a berry bushes
   * @param context  Item use context of the original block clicked
   * @param world    World instance
   * @param state    State to harvest
   * @param pos      Position to harvest
   * @param player   Player instance
   * @return  True if harvested
   */

  private static boolean harvestInteract(ItemUseContext context, ServerWorld world, BlockState state, BlockPos pos, @Nullable PlayerEntity player) {
    if (player == null) {
      return false;
    }
    BlockRayTraceResult trace = new BlockRayTraceResult(context.getHitVec(), context.getFace(), pos, false);
    ActionResultType result = state.onBlockActivated(world, player, context.getHand(), trace);
    return result.isSuccessOrConsume();
  }

  /**
   * Harvests a stackable block, like sugar cane or kelp
   * @param world   World instance
   * @param state   Block state
   * @param pos     Block position
   * @param player  Player instance
   * @return True if the block was harvested
   */
  private static boolean harvestStackable(ServerWorld world, BlockState state, BlockPos pos, @Nullable PlayerEntity player) {
    // if the block below is the same, break this block
    if (world.getBlockState(pos.down()).getBlock() == state.getBlock()) {
      world.destroyBlock(pos, true, player);
      return true;
    } else {
      // if the block above is the same, break it
      BlockPos up = pos.up();
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
   * @param world   World instance
   * @param state   Block state
   * @param pos     Block position
   * @param player  Player instance
   * @return  True if the crop was successfully harvested
   */
  private static boolean harvestCrop(ItemStack stack, ServerWorld world, BlockState state, BlockPos pos, @Nullable PlayerEntity player) {
    Block block = state.getBlock();
    BlockState replant;
    // if crops block, its easy
    if (block instanceof CropsBlock) {
      CropsBlock crops = (CropsBlock)block;
      if (!crops.isMaxAge(state)) {
        return false;
      }
      replant = crops.withAge(0);
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
        Collection<Integer> allowedValues = age.getAllowedValues();
        if (!allowedValues.contains(0)) {
          return false;
        }
        // crop must be max age
        int maxAge = age.getAllowedValues().stream().max(Integer::compareTo).orElse(Integer.MAX_VALUE);
        if (state.get(age) < maxAge) {
          return false;
        }
        replant = state.with(age, 0);
      }
    }

    // crop is fully grown, get loot context
    LootContext.Builder lootContext = new LootContext.Builder(world)
      .withRandom(world.rand)
      .withParameter(LootParameters.field_237457_g_, Vector3d.copyCentered(pos))
      .withParameter(LootParameters.TOOL, ItemStack.EMPTY)
      .withNullableParameter(LootParameters.BLOCK_ENTITY, world.getTileEntity(pos));
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
      world.setBlockState(pos, replant);
      state.spawnAdditionalDrops(world, pos, stack);
      // set block state will not play sounds, destory block will
      world.playSound(null, pos, state.getSoundType(world, pos, player).getBreakSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
    } else {
      world.destroyBlock(pos, false);
    }

    // drop items
    for (ItemStack drop : drops) {
      Block.spawnAsEntity(world, pos, drop);
    }

    return true;
  }

  /**
   * Tries to harvest the crop at the given position
   * @param context  Item use context of the original block clicked
   * @param world    World instance
   * @param state    State to harvest
   * @param pos      Position to harvest
   * @param stack    Stack used to break
   * @return  True if harvested
   */
  private static boolean harvest(ItemUseContext context, ItemStack stack, ToolStack tool, ServerWorld world, BlockState state, BlockPos pos, @Nullable PlayerEntity player) {
    // first, check main harvestable tag
    Block block = state.getBlock();
    if (!TinkerTags.Blocks.HARVESTABLE.contains(block)) {
      return false;
    }
    // try harvest event
    Result result = new ToolHarvestEvent(stack, tool, context, world, state, pos, player).fire();
    if (result != Result.DEFAULT) {
      return result == Result.ALLOW;
    }
    // crops that work based on right click interact (berry bushes)
    if (TinkerTags.Blocks.HARVESTABLE_INTERACT.contains(block)) {
      return harvestInteract(context, world, state, pos, player);
    }
    // next, try sugar cane like blocks
    if (TinkerTags.Blocks.HARVESTABLE_STACKABLE.contains(block)) {
      return harvestStackable(world, state, pos, player);
    }
    // normal crops like wheat or carrots
    if (TinkerTags.Blocks.HARVESTABLE_CROPS.contains(block)) {
      return harvestCrop(stack, world, state, pos, player);
    }
    return false;
  }

  @Override
  public ActionResultType onItemUse(ToolStack tool, int level, ItemStack stack, ItemUseContext context) {
    Item item = stack.getItem();
    if (item instanceof ToolCore) {
      ToolCore toolCore = (ToolCore) item;
      PlayerEntity player = context.getPlayer();
      if (player != null && player.isSneaking()) {
        return ActionResultType.PASS;
      }
      // fetch tool
      if (tool.isBroken()) {
        return ActionResultType.PASS;
      }
  
      // try harvest first
      World world = context.getWorld();
      BlockPos pos = context.getPos();
      BlockState state = world.getBlockState(pos);
      if (TinkerTags.Blocks.HARVESTABLE.contains(state.getBlock())) {
        if (world instanceof ServerWorld) {
          boolean survival = player == null || !player.isCreative();
          ServerWorld server = (ServerWorld)world;
  
          // try harvesting the crop, if successful and survival, damage the tool
          boolean didHarvest = false;
          boolean broken = false;
          if (harvest(context, stack, tool, server, state, pos, player)) {
            didHarvest = true;
            broken = survival && ToolDamageUtil.damage(tool, 1, player, stack);
          }
  
          // if we have a player, try doing AOE harvest
          if (!broken && player != null) {
            for (BlockPos newPos : toolCore.getToolHarvestLogic().getAOEBlocks(tool, stack, player, state, world, pos, context.getFace(), AOEMatchType.TRANSFORM)) {
              // try harvesting the crop, if successful and survival, damage the tool
              if (harvest(context, stack, tool, server, world.getBlockState(newPos), newPos, player)) {
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
              player.spawnSweepParticles();
            }
            if (broken) {
              player.sendBreakAnimation(context.getHand());
            }
          }
        }
        return ActionResultType.SUCCESS;
      }
    }
    return ActionResultType.PASS;
  }

}
