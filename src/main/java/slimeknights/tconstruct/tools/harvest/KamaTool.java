package slimeknights.tconstruct.tools.harvest;

import com.google.common.collect.Sets;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.AOEToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class KamaTool extends HarvestTool {
  /** Tool harvest logic to damage when breaking instant break blocks */
  public static final AOEToolHarvestLogic HARVEST_LOGIC = new HarvestLogic(1, 1, 1);

  public KamaTool(Settings properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public AOEToolHarvestLogic getToolHarvestLogic() {
    return HARVEST_LOGIC;
  }

  @Override
  public ActionResult useOnEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
    // only run AOE on shearable entities
    if (target instanceof SheepEntity) {
      int fortune = EnchantmentHelper.getLevel(Enchantments.FORTUNE, stack);

      ToolStack tool = ToolStack.from(stack);
      if (!tool.isBroken() && this.shearEntity(stack, playerIn.getEntityWorld(), playerIn, target, fortune)) {
        ToolDamageUtil.damageAnimated(tool, 1, playerIn, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        this.swingTool(playerIn, hand);
        return ActionResult.SUCCESS;
      }
    }

    return ActionResult.PASS;
  }

  /**
   * Swings the given's player hand
   *
   * @param player the current player
   * @param hand the given hand the tool is in
   */
  protected void swingTool(PlayerEntity player, Hand hand) {
    player.swingHand(hand);
    player.spawnSweepAttackParticles();
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
    if (!(entity instanceof SheepEntity)) {
      return false;
    }

    SheepEntity target = (SheepEntity) entity;

    if (target.isShearable()) {
      if (!world.isClient) {
        target.sheared(SoundCategory.NEUTRAL);
      }
//        List<ItemStack> drops = target.shea(playerEntity, itemStack, world, entity.getBlockPos(), fortune);
//        Random rand = world.random;
//
//        drops.forEach(d -> {
//          ItemEntity ent = entity.dropStack(d, 1.0F);
//
//          if (ent != null) {
//            ent.setVelocity(ent.getVelocity().add((rand.nextFloat() - rand.nextFloat()) * 0.1F, rand.nextFloat() * 0.05F, (rand.nextFloat() - rand.nextFloat()) * 0.1F));
//          }
//        });
//      }
//      return true;
    }

    return false;
  }

  @Override
  public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
    ItemStack itemStackIn = playerIn.getStackInHand(handIn);
    if (ToolDamageUtil.isBroken(itemStackIn)) {
      return TypedActionResult.fail(itemStackIn);
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

    return TypedActionResult.pass(itemStackIn);
  }

  @Override
  public ActionResult useOnBlock(ItemUsageContext context) {
    return getToolHarvestLogic().transformBlocks(context, FabricToolTags.HOES, SoundEvents.ITEM_HOE_TILL, true);
  }

  public static class HarvestLogic extends AOEToolHarvestLogic {
    private static final Set<Material> EFFECTIVE_MATERIALS = Sets.newHashSet(
      Material.LEAVES, Material.COBWEB, Material.WOOL,
      Material.REPLACEABLE_PLANT, Material.NETHER_SHOOTS, Material.UNDERWATER_PLANT);

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
    public List<BlockPos> getAOEBlocks(ToolStack tool, PlayerEntity player, BlockPos origin, Direction sideHit, Vec3d hitVec, Predicate<BlockState> predicate) {
      // only works with modifiable harvest
      if (tool.isBroken()) {
        return Collections.emptyList();
      }

      // include depth in boost
      int expanded = tool.getModifierLevel(TinkerModifiers.expanded);
      return calculateAOEBlocks(player, origin, width + expanded, height + expanded, depth + expanded, sideHit, hitVec, predicate);
    }
  }
}
