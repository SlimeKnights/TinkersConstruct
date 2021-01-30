package slimeknights.tconstruct.tools.harvest;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.library.tinkering.IAoeTool;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.AoeToolInteractionUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.helper.ToolInteractionUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.List;
import java.util.Random;

public class KamaTool extends ToolCore implements IAoeTool {

  public static final ImmutableSet<Material> EFFECTIVE_MATERIALS =
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
    return EFFECTIVE_MATERIALS.contains(state.getMaterial());
  }

  @Override
  public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
    // only run AOE on shearable entities
    if (target instanceof IForgeShearable) {
      int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);

      if (this.shearEntity(stack, playerIn.getEntityWorld(), playerIn, target, fortune)) {
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
   * @param itemStack the current tool stack
   * @param world the current world
   * @param playerEntity the current player
   * @param entity the entity to try to shear
   * @param fortune the fortune to apply to the sheared entity
   * @return if the sheering of the entity was performed or not
   */
  public boolean shearEntity(ItemStack itemStack, World world, PlayerEntity playerEntity, Entity entity, int fortune) {
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

      ToolStack.from(itemStack).damage(1, playerEntity, itemStack);
      return true;
    }

    return false;
  }


  @Override
  protected boolean breakBlock(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
    return !ToolDamageUtil.isBroken(itemstack) && ToolInteractionUtil.shearBlock(itemstack, player.getEntityWorld(), player, pos);
  }

  @Override
  protected void breakExtraBlock(ItemStack tool, World world, PlayerEntity player, BlockPos pos, BlockPos refPos) {
    AoeToolInteractionUtil.shearExtraBlock(tool, world, player, pos, refPos);
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
    return AoeToolInteractionUtil.tillBlocks(context, ToolType.HOE, SoundEvents.ITEM_HOE_TILL);
  }
}
