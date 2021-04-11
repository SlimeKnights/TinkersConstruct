package slimeknights.tconstruct.tools.harvest;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.Event.Result;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.events.TinkerToolEvent.ToolHarvestEvent;
import slimeknights.tconstruct.library.tools.helper.AOEToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

public class KamaTool extends HarvestTool {
  /** Tool harvest logic to damage when breaking instant break blocks */
  public static final AOEToolHarvestLogic HARVEST_LOGIC = new HarvestLogic(1, 1, 1);

  public KamaTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public AOEToolHarvestLogic getToolHarvestLogic() {
    return HARVEST_LOGIC;
  }

  /** Checks if the given tool acts as shears */
  protected boolean isShears(ToolStack tool) {
    return true;
  }

  @Override
  public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
    // only run AOE on shearable entities
    ToolStack tool = ToolStack.from(stack);
    if (isShears(tool) && target instanceof IForgeShearable) {
      int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);

      if (!tool.isBroken() && this.shearEntity(stack, playerIn.getEntityWorld(), playerIn, target, fortune)) {
        ToolDamageUtil.damageAnimated(tool, 1, playerIn, hand == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND);
        this.swingTool(playerIn, hand);
        return ActionResultType.SUCCESS;
      }
    }

    return ActionResultType.PASS;
  }

  /**
   * Swings the given's player hand
   *
   * @param player the current player
   * @param hand the given hand the tool is in
   */
  protected void swingTool(PlayerEntity player, Hand hand) {
    player.swingArm(hand);
    player.spawnSweepParticles();
  }

  /**
   * Tries to shear an given entity, returns false if it fails and true if it succeeds
   *
   * @param itemStack the current item stack
   * @param world the current world
   * @param playerEntity the current player
   * @param entity the entity to try to shear
   * @param fortune the fortune to apply to the sheared entity
   * @return if the sheering of the entity was performed or not
   */
  private boolean shearEntity(ItemStack itemStack, World world, PlayerEntity playerEntity, Entity entity, int fortune) {
    if (!(entity instanceof IForgeShearable)) {
      return false;
    }

    IForgeShearable target = (IForgeShearable) entity;

    if (target.isShearable(itemStack, world, entity.getPosition())) {
      if (!world.isRemote) {
        List<ItemStack> drops = target.onSheared(playerEntity, itemStack, world, entity.getPosition(), fortune);
        Random rand = world.rand;

        drops.forEach(d -> {
          ItemEntity ent = entity.entityDropItem(d, 1.0F);

          if (ent != null) {
            ent.setMotion(ent.getMotion().add((rand.nextFloat() - rand.nextFloat()) * 0.1F, rand.nextFloat() * 0.05F, (rand.nextFloat() - rand.nextFloat()) * 0.1F));
          }
        });
      }
      return true;
    }

    return false;
  }

  /**
   * Tries to harvest the crop at the given position
   * @param world  World instance
   * @param state  State to harvest
   * @param pos    Position to harvest
   * @param stack  Stack used to break
   * @return  True if harvested
   */
  private static boolean harvestCrop(ItemStack stack, ToolStack tool, ServerWorld world, BlockState state, BlockPos pos, @Nullable PlayerEntity player) {
    // first, check harvestable
    Block block = state.getBlock();
    if (!TinkerTags.Blocks.HARVESTABLE.contains(block)) {
      return false;
    }

    // try harvest event
    Result result = new ToolHarvestEvent(stack, tool, world, state, pos, player).fire();
    if (result != Result.DEFAULT) {
      return result == Result.ALLOW;
    }

    // next, try sugar cane like blocks
    if (TinkerTags.Blocks.BLOCK_CROPS.contains(block)) {
      // if the block below is the same, break this block
      if (world.getBlockState(pos.down()).getBlock() == block) {
        world.destroyBlock(pos, true, player);
        return true;
      } else {
        // if the block above is the same, break it
        BlockPos up = pos.up();
        if (world.getBlockState(up).getBlock() == block) {
          world.destroyBlock(up, true, player);
          return true;
        }
      }
      return false;
    }

    // not event, not sugar cane, so do normal crops
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

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    PlayerEntity player = context.getPlayer();
    if (player != null && player.isSneaking()) {
      return ActionResultType.PASS;
    }
    // fetch tool
    ItemStack stack = context.getItem();
    ToolStack tool = ToolStack.from(context.getItem());
    if (tool.isBroken()) {
      return ActionResultType.PASS;
    }

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
        if (harvestCrop(stack, tool, server, state, pos, player)) {
          didHarvest = true;
          broken = survival && ToolDamageUtil.damage(tool, 1, player, stack);
        }

        // if we have a player, try doing AOE harvest
        if (!broken && player != null) {
          for (BlockPos newPos : getToolHarvestLogic().getAOEBlocks(tool, player, pos, Direction.UP, context.getHitVec(), s -> true)) {
            // try harvesting the crop, if successful and survival, damage the tool
            if (harvestCrop(stack, tool, server, world.getBlockState(newPos), newPos, player)) {
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

    return getToolHarvestLogic().transformBlocks(context, ToolType.HOE, SoundEvents.ITEM_HOE_TILL, true);
  }

  /** Harvets logic to match shears and hoes */
  public static class HarvestLogic extends AOEToolHarvestLogic {
    private static final Set<Material> EFFECTIVE_MATERIALS = Sets.newHashSet(
      Material.LEAVES, Material.WEB, Material.WOOL,
      Material.TALL_PLANTS, Material.NETHER_PLANTS, Material.OCEAN_PLANT);

    public HarvestLogic(int width, int height, int depth) {
      super(width, height, depth);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState blockState) {
      float speed = super.getDestroySpeed(stack, blockState);
      if (blockState.getMaterial() == Material.WOOL) {
        speed /= 3;
      }
      return speed;
    }

    @Override
    public boolean isEffectiveAgainst(ToolStack tool, ItemStack stack, BlockState state) {
      return state.getBlock() == Blocks.TRIPWIRE || EFFECTIVE_MATERIALS.contains(state.getMaterial()) || super.isEffectiveAgainst(tool, stack, state);
    }

    @Override
    public int getDamage(ToolStack tool, ItemStack stack, World world, BlockPos pos, BlockState state) {
      return state.isIn(BlockTags.FIRE) ? 0 : 1;
    }

    @Override
    public List<BlockPos> getAOEBlocks(ToolStack tool, PlayerEntity player, BlockPos origin, Direction sideHit, Vector3d hitVec, Predicate<BlockState> predicate) {
      // only works with modifiable harvest
      if (tool.isBroken()) {
        return Collections.emptyList();
      }

      // include depth in boost
      int expanded = tool.getModifierLevel(TinkerModifiers.expanded.get());
      return calculateAOEBlocks(player, origin, width + expanded, height + expanded, depth + expanded, sideHit, hitVec, predicate);
    }
  }
}
