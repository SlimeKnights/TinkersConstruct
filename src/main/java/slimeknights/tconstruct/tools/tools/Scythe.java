package slimeknights.tconstruct.tools.tools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockReed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
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
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.AoeToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

public class Scythe extends AoeToolCore {

  public static final float DURABILITY_MODIFIER = 2.2f;

  public static final ImmutableSet<net.minecraft.block.material.Material> effective_materials =
      ImmutableSet.of(net.minecraft.block.material.Material.WEB,
                      net.minecraft.block.material.Material.LEAVES,
                      net.minecraft.block.material.Material.PLANTS,
                      net.minecraft.block.material.Material.VINE,
                      net.minecraft.block.material.Material.GOURD,
                      net.minecraft.block.material.Material.CACTUS);


  public Scythe() {
    super(PartMaterialType.handle(TinkerTools.toughToolRod),
          PartMaterialType.head(TinkerTools.scytheHead),
          PartMaterialType.extra(TinkerTools.toughBinding),
          PartMaterialType.handle(TinkerTools.toughToolRod));

    addCategory(Category.HARVEST, Category.WEAPON);
  }

  @Override
  public float damagePotential() {
    return 0.75f;
  }

  @Override
  public double attackSpeed() {
    return 0.9f;
  }

  @Override
  public boolean isEffective(IBlockState state) {
    return effective_materials.contains(state.getMaterial());
  }

  protected void breakBlock(ItemStack stack, EntityPlayer player, BlockPos pos, BlockPos refPos) {
    // silktouch gives us shears :D
    if(EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0) {
      if(ToolHelper.shearBlock(stack, player.worldObj, player, pos)) {
        return;
      }
    }

    // can't be sheared or no silktouch. break it
    ToolHelper.breakExtraBlock(stack, player.worldObj, player, pos, refPos);
  }


  @Override
  public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
    if(!ToolHelper.isBroken(stack) && this.isAoeHarvestTool()) {
      for(BlockPos extraPos : this.getAOEBlocks(stack, player.worldObj, player, pos)) {
        breakBlock(stack, player, extraPos, pos);
      }
    }

    return super.onBlockStartBreak(stack, pos, player);
  }

  @Override
  public ImmutableList<BlockPos> getAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin) {
    return ToolHelper.calcAOEBlocks(stack, world, player, origin, 3, 3, 3);
  }

  @Override
  public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity target) {

    // only do AOE attack if the attack meter is charged
    if(player.getCooledAttackStrength(0.5F) <= 0.9f) {
      return super.onLeftClickEntity(stack, player, target);
    }

    // increase the size based on the AOE stuffs
    TinkerToolEvent.ExtraBlockBreak event = TinkerToolEvent.ExtraBlockBreak.fireEvent(stack, player, player.worldObj.getBlockState(target.getPosition()), 3, 3, 3, -1);
    if(event.isCanceled()) {
      return false;
    }

    // AOE attack!
    player.worldObj.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
    player.spawnSweepParticles();

    int distance = event.distance;
    boolean hit = false;
    // we cache the cooldown here since it resets as soon as the first entity is hit
    for(Entity entity : getAoeEntities(player, target, event)) {
      if(distance < 0 || entity.getDistanceToEntity(target) <= distance) {
        hit |= ToolHelper.attackEntity(stack, this, player, entity, null, false);
      }
    }

    if(hit) {
      player.resetCooldown();
    }

    // subtract the default box and then half as this number is the amount to increase the box by
    return hit;
  }

  private List<Entity> getAoeEntities(EntityPlayer player, Entity target, TinkerToolEvent.ExtraBlockBreak event) {
    int width = (event.width - 1) / 2;
    int height = (event.width - 1) / 2;
    AxisAlignedBB box = new AxisAlignedBB(target.posX, target.posY, target.posZ, target.posX + 1.0D, target.posY + 1.0D, target.posZ + 1.0D).expand(width, height, width);

    return player.worldObj.getEntitiesWithinAABBExcludingEntity(player, box);
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
    if(ToolHelper.isBroken(stack)) {
      return ActionResult.newResult(EnumActionResult.FAIL, stack);
    }

    RayTraceResult trace = this.rayTrace(world, player, true);
    if(trace == null || trace.typeOfHit != RayTraceResult.Type.BLOCK) {
      return ActionResult.newResult(EnumActionResult.PASS, stack);
    }

    int fortune = ToolHelper.getFortuneLevel(stack);

    BlockPos origin = trace.getBlockPos();

    boolean harvestedSomething = false;
    for(BlockPos pos : this.getAOEBlocks(stack, player.worldObj, player, origin)) {
      harvestedSomething |= harvestCrop(stack, world, player, pos, fortune);
    }

    // center space done after the loop to prevent from changing the hitbox before AOE runs
    harvestedSomething |= harvestCrop(stack, world, player, origin, fortune);

    if(harvestedSomething) {
      player.swingArm(hand);
      player.worldObj.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
      player.spawnSweepParticles();
      return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    return ActionResult.newResult(EnumActionResult.PASS, stack);
  }

  protected boolean canHarvestCrop(IBlockState state) {
    boolean canHarvest = state.getBlock() instanceof BlockReed;

    if(state.getBlock() instanceof BlockCrops && ((BlockCrops) state.getBlock()).isMaxAge(state)) {
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
    if(!(target instanceof IShearable)) {
      return false;
    }

    // increase the size based on the AOE stuffs
    TinkerToolEvent.ExtraBlockBreak event = TinkerToolEvent.ExtraBlockBreak.fireEvent(stack, player, player.worldObj.getBlockState(target.getPosition()), 3, 3, 3, -1);
    if(event.isCanceled()) {
      return false;
    }

    int distance = event.distance;
    boolean shorn = false;

    int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
    for(Entity entity : getAoeEntities(player, target, event)) {
      if(distance < 0 || entity.getDistanceToEntity(target) <= distance) {
        shorn |= shearEntity(stack, player.worldObj, player, entity, fortune);
      }
    }

    if(shorn) {
      player.worldObj.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
      player.spawnSweepParticles();
    }

    return shorn;
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
    List<ItemStack> drops = state.getBlock().getDrops(world, pos, state, fortune);
    chance = ForgeEventFactory.fireBlockHarvesting(drops, world, pos, state, fortune, chance, false, player);

    IPlantable seed = null;
    for(ItemStack drop : drops) {
      if(drop != null && drop.getItem() instanceof IPlantable) {
        seed = (IPlantable) drop.getItem();
        drop.stackSize--;
        if(drop.stackSize <= 0) {
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
      breakBlock(stack, player, pos, pos);
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
          net.minecraft.entity.item.EntityItem ent = entity.entityDropItem(drop, 1.0F);
          ent.motionY += rand.nextFloat() * 0.05F;
          ent.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
          ent.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
        }
      }
      ToolHelper.damageTool(stack, 1, player);

      return true;
    }

    return false;
  }

  @Override
  public int[] getRepairParts() {
    return new int[]{1, 2};
  }

  @Override
  public ToolNBT buildTagData(List<Material> materials) {
    HandleMaterialStats handle = materials.get(0).getStatsOrUnknown(MaterialTypes.HANDLE);
    HeadMaterialStats head = materials.get(1).getStatsOrUnknown(MaterialTypes.HEAD);
    ExtraMaterialStats extra = materials.get(2).getStatsOrUnknown(MaterialTypes.EXTRA);
    HandleMaterialStats handle2 = materials.get(3).getStatsOrUnknown(MaterialTypes.HANDLE);

    ToolNBT data = new ToolNBT();
    data.head(head);
    data.extra(extra);
    data.handle(handle, handle2);

    data.durability *= DURABILITY_MODIFIER;

    return data;
  }
}
