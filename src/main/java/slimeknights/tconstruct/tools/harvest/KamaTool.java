package slimeknights.tconstruct.tools.harvest;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.AOEToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.Collections;
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

  @Override
  public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
    // only run AOE on shearable entities
    if (target instanceof IForgeShearable) {
      int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);

      ToolStack tool = ToolStack.from(stack);
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

  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
    ItemStack itemStackIn = playerIn.getHeldItem(handIn);
    if (ToolDamageUtil.isBroken(itemStackIn)) {
      return ActionResult.resultFail(itemStackIn);
    }

//    BlockRayTraceResult rayTraceResult = blockRayTrace(worldIn, playerIn, RayTraceContext.FluidMode.NONE);
//
//    int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, itemStackIn);
//    BlockPos origin = rayTraceResult.getPos();

//    boolean harvestedSomething = false;
//
//    /*for(BlockPos pos : this.getAOEBlocks(itemStackIn, playerIn.getEntityWorld(), playerIn, origin)) {
//      harvestedSomething |= harvestCrop(itemStackIn, worldIn, playerIn, pos, fortune);
//    }*/
//
//    //harvestedSomething |= this.harvestCrop(itemStackIn, worldIn, playerIn, origin, fortune);
//
//    if (harvestedSomething) {
//      playerIn.getEntityWorld().playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, playerIn.getSoundCategory(), 1.0F, 1.0F);
//      this.swingTool(playerIn, handIn);
//      return ActionResult.resultSuccess(itemStackIn);
//    }

    return ActionResult.resultPass(itemStackIn);
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    return getToolHarvestLogic().transformBlocks(context, ToolType.HOE, SoundEvents.ITEM_HOE_TILL, true);
  }

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
