package slimeknights.tconstruct.tools.tools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
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
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.ForgeEventFactory;
import java.util.List;
import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.events.TinkerToolEvent;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.AoeToolCore;
import slimeknights.tconstruct.library.tools.IAoeTool;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

public class Scythe extends AoeToolCore {

  public static final String HARVEST_TAG = "harvesting";

  public static final ImmutableSet<net.minecraft.block.material.Material> effective_materials =
      ImmutableSet.of(net.minecraft.block.material.Material.WEB,
                      net.minecraft.block.material.Material.LEAVES,
                      net.minecraft.block.material.Material.PLANTS,
                      net.minecraft.block.material.Material.VINE,
                      net.minecraft.block.material.Material.GOURD,
                      net.minecraft.block.material.Material.CACTUS);


  public Scythe() {
    super(PartMaterialType.handle(TinkerTools.toolRod),
          PartMaterialType.head(TinkerTools.scytheHead),
          PartMaterialType.handle(TinkerTools.toolRod),
          PartMaterialType.extra(TinkerTools.toughBinding)); // todo

    addCategory(Category.HARVEST, Category.WEAPON);
  }

  @Override
  public float damagePotential() {
    return 0.8f;
  }

  @Override
  public double attackSpeed() {
    return 1f;
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
    if(!ToolHelper.isBroken(stack) && this instanceof IAoeTool && ((IAoeTool) this).isAoeHarvestTool()) {
      for(BlockPos extraPos : ((IAoeTool) this).getAOEBlocks(stack, player.worldObj, player, pos)) {
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
  public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {

    // only do AOE attack if the attack meter is charged
    if(player.getCooledAttackStrength(0.5F) <= 0.9f) {
      return super.onLeftClickEntity(stack, player, entity);
    }

    // increase the size based on the AOE stuffs
    TinkerToolEvent.ExtraBlockBreak event = TinkerToolEvent.ExtraBlockBreak.fireEvent(stack, player, player.worldObj.getBlockState(entity.getPosition()), 3, 3, 3, -1);
    if(event.isCanceled()) {
      return false;
    }

    // AOE attack!
    player.worldObj.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
    player.spawnSweepParticles();

    // subtract the default box and then half as this number is the amount to increase the box by
    return aoeAttack(stack, player, entity, event);
  }

  private boolean aoeAttack(ItemStack stack, EntityPlayer player, Entity target, TinkerToolEvent.ExtraBlockBreak event) {
    int width = (event.width - 1) / 2;
    int height = (event.width - 1) / 2;
    int distance = event.distance;
    boolean hit = false;
    AxisAlignedBB box = new AxisAlignedBB(target.posX, target.posY, target.posZ, target.posX + 1.0D, target.posY + 1.0D, target.posZ + 1.0D).expand(width, height, width);
    List<Entity> entities = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, box);

    for(Entity entity : entities) {
      if(distance < 0 || entity.getDistanceToEntity(target) < distance) {
        hit |= ToolHelper.attackEntity(stack, this, player, entity);
      }
    }

    return hit;
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
    if(ToolHelper.isBroken(stack)) {
      return ActionResult.newResult(EnumActionResult.FAIL, stack);
    }

    BlockPos origin = new BlockPos(player);

    // increase the size based on the AOE stuffs
    TinkerToolEvent.ExtraBlockBreak event = TinkerToolEvent.ExtraBlockBreak.fireEvent(stack, player, player.worldObj.getBlockState(player.getPosition()), 3, 3, 3, 2);
    if(event.isCanceled()) {
      return ActionResult.newResult(EnumActionResult.FAIL, stack);
    }

    // from this point out we succeeded, so set cooldowns
    player.getCooldownTracker().setCooldown(stack.getItem(), 10);
    player.resetCooldown();

    int width = event.width;
    int height = event.height;
    int distance = event.distance;

    // loop through AOE positions centered on the player
    for(BlockPos pos : ToolHelper.calcAOEBlocks(stack, world, origin, origin.add(-width/2, -height/2, -width/2), width, height, width, distance, false)) {
      IBlockState state = world.getBlockState(pos);

      // only work on blocks with a hardness of 0, as this is instant break otherwise
      if(ToolHelper.isToolEffective2(stack, state) && state.getBlockHardness(world, pos) <= 0) {
        int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);

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
            continue;
          }
        }

        // can't plant? just break the block directly
        breakBlock(stack, player, pos, pos);
      }
    }

    // AOE attack centered on the player, still applies same cooldown
    aoeAttack(stack, player, player, event);

    player.worldObj.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
    player.spawnSweepParticles();

    return ActionResult.newResult(player.worldObj.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS, stack);
  }

  @Override
  public int[] getRepairParts() {
    return new int[]{1, 2};
  }


  @Override
  public ToolNBT buildTagData(List<Material> materials) {
    return buildDefaultTag(materials);
  }
}
