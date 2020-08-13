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
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;
import net.minecraftforge.common.IForgeShearable;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.AoeToolInteractionUtil;
import slimeknights.tconstruct.library.tools.helper.ToolInteractionUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolData;

import java.util.List;
import java.util.Random;

public class KamaTool extends ToolCore {

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

  protected void swingTool(PlayerEntity player, Hand hand) {
    player.swingArm(hand);
    player.spawnSweepParticles();
  }

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

      ToolInteractionUtil.damageTool(itemStack, 1, playerEntity);

      return true;
    }

    return false;
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
