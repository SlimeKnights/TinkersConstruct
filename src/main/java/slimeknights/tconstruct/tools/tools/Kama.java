package slimeknights.tconstruct.tools.tools;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockReed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.events.TinkerToolEvent;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.AoeToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

public class Kama extends AoeToolCore {

  public static final ImmutableSet<net.minecraft.block.material.Material> effective_materials =
      ImmutableSet.of(net.minecraft.block.material.Material.WEB,
          net.minecraft.block.material.Material.LEAVES,
          net.minecraft.block.material.Material.PLANTS,
          net.minecraft.block.material.Material.VINE,
          net.minecraft.block.material.Material.GOURD,
          net.minecraft.block.material.Material.CACTUS);


  public Kama(PartMaterialType... requiredComponents) {
    super(requiredComponents);

    addCategory(Category.HARVEST, Category.WEAPON);
    setHarvestLevel("shears", 0);
  }

  public Kama() {
    this(PartMaterialType.handle(TinkerTools.toolRod),
        PartMaterialType.head(TinkerTools.kamaHead),
        PartMaterialType.extra(TinkerTools.binding));
  }

  @Override
  public float damagePotential() {
    return 1f;
  }

  @Override
  public double attackSpeed() {
    return 1.3f;
  }

  @Override
  public boolean isEffective(IBlockState state) {
    return effective_materials.contains(state.getMaterial());
  }

  @Override
  protected boolean breakBlock(ItemStack stack, BlockPos pos, EntityPlayer player) {
    return !ToolHelper.isBroken(stack) && ToolHelper.shearBlock(stack, player.getEntityWorld(), player, pos);
  }

  @Override
  protected void breakExtraBlock(ItemStack stack, World world, EntityPlayer player, BlockPos pos, BlockPos refPos) {
    ToolHelper.shearExtraBlock(stack, world, player, pos, refPos);
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
    ItemStack itemStackIn = playerIn.getHeldItem(hand);
    if(ToolHelper.isBroken(itemStackIn)) {
      return ActionResult.newResult(EnumActionResult.FAIL, itemStackIn);
    }

    RayTraceResult trace = this.rayTrace(worldIn, playerIn, true);
    if(trace == null || trace.typeOfHit != RayTraceResult.Type.BLOCK) {
      return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
    }

    int fortune = ToolHelper.getFortuneLevel(itemStackIn);

    BlockPos origin = trace.getBlockPos();

    boolean harvestedSomething = false;
    for(BlockPos pos : this.getAOEBlocks(itemStackIn, playerIn.getEntityWorld(), playerIn, origin)) {
      harvestedSomething |= harvestCrop(itemStackIn, worldIn, playerIn, pos, fortune);
    }

    // center space done after the loop to prevent from changing the hitbox before AOE runs
    harvestedSomething |= harvestCrop(itemStackIn, worldIn, playerIn, origin, fortune);

    if(harvestedSomething) {
      playerIn.getEntityWorld().playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, playerIn.getSoundCategory(), 1.0F, 1.0F);
      swingTool(playerIn, hand);
      return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
    }

    return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
  }

  protected boolean canHarvestCrop(IBlockState state) {
    boolean canHarvest = state.getBlock() instanceof BlockReed;

    if(state.getBlock() instanceof BlockCrops && ((BlockCrops) state.getBlock()).isMaxAge(state)) {
      canHarvest = true;
    }
    if(state.getBlock() instanceof BlockNetherWart && state.getValue(BlockNetherWart.AGE) == 3) {
      canHarvest = true;
    }
    return canHarvest;
  }

  /**
   * Returns true if the item can be used on the given entity, e.g. shears on sheep.
   */
  @Override
  public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
    // only run AOE on shearable entities
    if(target instanceof IShearable) {
      int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
      if(shearEntity(stack, player.getEntityWorld(), player, target, fortune)) {
        swingTool(player, hand);
        return true;
      }
    }

    return false;
  }

  protected void swingTool(EntityPlayer player, EnumHand hand) {
    player.swingArm(hand);
    player.spawnSweepParticles();
  }

  public boolean harvestCrop(ItemStack stack, World world, EntityPlayer player, BlockPos pos, int fortune) {
    IBlockState state = world.getBlockState(pos);

    boolean canHarvest = canHarvestCrop(state);

    // do not harvest bottom row reeds
    if(state.getBlock() instanceof BlockReed && !(world.getBlockState(pos.down()).getBlock() instanceof BlockReed)) {
      canHarvest = false;
    }

    TinkerToolEvent.OnScytheHarvest event = TinkerToolEvent.OnScytheHarvest.fireEvent(stack, player, world, pos, state, canHarvest);

    // can't harvest
    if(event.isCanceled()) {
      return false;
    }

    // harvest handled by event
    if(event.getResult() == Event.Result.DENY) {
      return true;
    }
    // should harwest block nontheless
    else if(event.getResult() == Event.Result.ALLOW) {
      canHarvest = true;
    }

    if(!canHarvest) {
      return false;
    }

    // can be harvested, always just return true clientside for the animation stuff
    if(!world.isRemote) {
      doHarvestCrop(stack, world, player, pos, fortune, state);
    }

    return true;
  }

  protected void doHarvestCrop(ItemStack stack, World world, EntityPlayer player, BlockPos pos, int fortune, IBlockState state) {
    // first, try getting a seed from the drops, if we don't have one we don't replant
    float chance = 1.0f;
    NonNullList<ItemStack> drops = NonNullList.create();
    state.getBlock().getDrops(drops, world, pos, state, fortune);
    chance = ForgeEventFactory.fireBlockHarvesting(drops, world, pos, state, fortune, chance, false, player);

    IPlantable seed = null;
    for(ItemStack drop : drops) {
      if(drop != null && drop.getItem() instanceof IPlantable) {
        seed = (IPlantable) drop.getItem();
        drop.shrink(1);
        if(drop.isEmpty()) {
          drops.remove(drop);
        }

        break;
      }
    }

    // if we have a valid seed, try to plant the crop
    boolean replanted = false;
    if(seed != null) {
      // make sure the plant is allowed here. should already be, mainly just covers the case of seeds from grass
      IBlockState down = world.getBlockState(pos.down());
      if(down.getBlock().canSustainPlant(down, world, pos.down(), EnumFacing.UP, seed)) {
        // success! place the plant and drop the rest of the items
        IBlockState crop = seed.getPlant(world, pos);

        // only place the block/damage the tool if its a different state
        if(crop != state) {
          world.setBlockState(pos, seed.getPlant(world, pos));
          ToolHelper.damageTool(stack, 1, player);
        }

        // drop the remainder of the items
        for(ItemStack drop : drops) {
          if(world.rand.nextFloat() <= chance) {
            Block.spawnAsEntity(world, pos, drop);
          }
        }
        replanted = true;
      }
    }

    // can't plant? just break the block directly
    if(!replanted) {
      breakExtraBlock(stack, player.getEntityWorld(), player, pos, pos);
    }
  }

  public boolean shearEntity(ItemStack stack, World world, EntityPlayer player, Entity entity, int fortune) {
    if(!(entity instanceof IShearable)) {
      return false;
    }

    IShearable shearable = (IShearable) entity;
    if(shearable.isShearable(stack, world, entity.getPosition())) {
      if(!world.isRemote) {
        List<ItemStack> drops = shearable.onSheared(stack, world, entity.getPosition(), fortune);
        Random rand = world.rand;
        for(ItemStack drop : drops) {
          EntityItem entityItem = entity.entityDropItem(drop, 1.0F);
          if(entityItem != null) {
            entityItem.motionY += rand.nextFloat() * 0.05F;
            entityItem.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
            entityItem.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
          }
        }
      }
      ToolHelper.damageTool(stack, 1, player);

      return true;
    }

    return false;
  }

  @Override
  protected ToolNBT buildTagData(List<Material> materials) {
    return buildDefaultTag(materials);
  }
}
