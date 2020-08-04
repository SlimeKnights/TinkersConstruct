package slimeknights.tconstruct.tools.harvest;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.AoeToolInteractionUtil;
import slimeknights.tconstruct.library.tools.helper.ToolInteractionUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolData;

import java.util.List;
import java.util.Random;

public class KamaTool extends ToolCore {

  public static final ImmutableSet<Material> effective_materials =
    ImmutableSet.of(Material.WOOD,
      Material.TALL_PLANTS,
      Material.PLANTS,
      Material.GOURD,
      Material.CACTUS,
      Material.BAMBOO);

  public KamaTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public boolean isEffective(BlockState state) {
    return effective_materials.contains(state.getMaterial());
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    PlayerEntity playerentity = context.getPlayer();
    ItemStack itemStack = playerentity.getHeldItem(context.getHand());

    if (ToolData.isBroken(itemStack)) {
      return ActionResultType.FAIL;
    }

    ActionResultType resultType = Items.DIAMOND_HOE.onItemUse(context);

    if (resultType == ActionResultType.SUCCESS) {
      //TODO event
    }

    return resultType;
  }

  @Override
  public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
    // only run AOE on shearable entities
    if (target instanceof IShearable) {
      int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);

      if (this.shearEntity(stack, playerIn.getEntityWorld(), playerIn, target, fortune)) {
        this.swingTool(playerIn, hand);
        return true;
      }
    }

    return false;
  }

  protected void swingTool(PlayerEntity player, Hand hand) {
    player.swingArm(hand);
    player.spawnSweepParticles();
  }

  public boolean shearEntity(ItemStack itemStack, World world, PlayerEntity playerEntity, Entity entity, int fortune) {
    if (!(entity instanceof IShearable)) {
      return false;
    }

    IShearable target = (IShearable) entity;

    if (target.isShearable(itemStack, world, entity.getPosition())) {
      if (!world.isRemote) {
        List<ItemStack> drops = target.onSheared(itemStack, world, entity.getPosition(), fortune);
        Random rand = world.rand;

        drops.forEach(d -> {
          ItemEntity ent = entity.entityDropItem(d, 1.0F);

          if (ent != null) {
            ent.setMotion(ent.getMotion().add((rand.nextFloat() - rand.nextFloat()) * 0.1F, rand.nextFloat() * 0.05F, (rand.nextFloat() - rand.nextFloat()) * 0.1F));
          }
        });
      }

      ToolInteractionUtil.damageTool(itemStack, 1, playerEntity);

      return true;
    }

    return false;
  }

  public boolean harvestCrop(ItemStack stack, World world, PlayerEntity player, BlockPos pos, int fortune) {
    BlockState state = world.getBlockState(pos);

    boolean canHarvest = this.canHarvestCrop(state);

    // do not harvest bottom row reeds
    if (state.getBlock() instanceof SugarCaneBlock && !(world.getBlockState(pos.down()).getBlock() instanceof SugarCaneBlock)) {
      canHarvest = false;
    }

    if (!canHarvest) {
      return false;
    }

    // can be harvested, always just return true clientside for the animation stuff
    if (!world.isRemote) {
      this.doHarvestCrop(stack, world, player, pos, state);
    }

    return true;
  }

  protected boolean canHarvestCrop(BlockState state) {
    boolean canHarvest = state.getBlock() instanceof SugarCaneBlock;

    if (state.getBlock() instanceof CropsBlock && ((CropsBlock) state.getBlock()).isMaxAge(state)) {
      canHarvest = true;
    }

    if (state.getBlock() instanceof NetherWartBlock && state.get(NetherWartBlock.AGE) == 3) {
      canHarvest = true;
    }

    return canHarvest;
  }

  protected void doHarvestCrop(ItemStack stack, World world, PlayerEntity player, BlockPos pos, BlockState state) {
    // first, try getting a seed from the drops, if we don't have one we don't replant
    List<ItemStack> drops = Block.getDrops(state, (ServerWorld) world, pos, world.getTileEntity(pos), player, player.getHeldItem(Hand.MAIN_HAND));

    IPlantable seed = null;
    for (ItemStack drop : drops) {
      if (!drop.isEmpty() && drop.getItem() instanceof IPlantable) {
        seed = (IPlantable) drop.getItem();
        drop.shrink(1);
        if (drop.isEmpty()) {
          drops.remove(drop);
        }

        break;
      }
    }

    // if we have a valid seed, try to plant the crop
    boolean replanted = false;
    if (seed != null) {
      // make sure the plant is allowed here. should already be, mainly just covers the case of seeds from grass
      BlockState down = world.getBlockState(pos.down());

      if (down.getBlock().canSustainPlant(down, world, pos.down(), Direction.UP, seed)) {
        // success! place the plant and drop the rest of the items
        BlockState crop = seed.getPlant(world, pos);

        // only place the block/damage the tool if its a different state
        if (crop != state) {
          world.setBlockState(pos, seed.getPlant(world, pos));
          ToolInteractionUtil.damageTool(stack, 1, player);
        }

        // drop the remainder of the items
        for (ItemStack drop : drops) {
          Block.spawnAsEntity(world, pos, drop);
        }

        replanted = true;
      }
    }

    // can't plant? just break the block directly
    if (!replanted) {
      this.breakExtraBlock(stack, player.getEntityWorld(), player, pos, pos);
    }
  }

  @Override
  protected boolean breakBlock(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
    return !ToolData.isBroken(itemstack) && ToolInteractionUtil.shearBlock(itemstack, player.getEntityWorld(), player, pos);
  }

  @Override
  protected void breakExtraBlock(ItemStack tool, World world, PlayerEntity player, BlockPos pos, BlockPos refPos) {
    AoeToolInteractionUtil.shearExtraBlock(tool, world, player, pos, refPos);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
    ItemStack itemStackIn = playerIn.getHeldItem(handIn);

    if (ToolData.isBroken(itemStackIn)) {
      return ActionResult.resultFail(itemStackIn);
    }

    BlockRayTraceResult rayTraceResult = this.blockRayTrace(worldIn, playerIn, RayTraceContext.FluidMode.NONE);

    if (rayTraceResult == null) {
      return ActionResult.resultPass(itemStackIn);
    }

    int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, itemStackIn);
    BlockPos origin = rayTraceResult.getPos();

    boolean harvestedSomething = false;

    /*for(BlockPos pos : this.getAOEBlocks(itemStackIn, playerIn.getEntityWorld(), playerIn, origin)) {
      harvestedSomething |= harvestCrop(itemStackIn, worldIn, playerIn, pos, fortune);
    }*/

    //harvestedSomething |= this.harvestCrop(itemStackIn, worldIn, playerIn, origin, fortune);

    if (harvestedSomething) {
      playerIn.getEntityWorld().playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, playerIn.getSoundCategory(), 1.0F, 1.0F);
      this.swingTool(playerIn, handIn);
      return ActionResult.resultSuccess(itemStackIn);
    }

    return ActionResult.resultPass(itemStackIn);
  }
}
