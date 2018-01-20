package slimeknights.tconstruct.tools.tools;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import slimeknights.tconstruct.library.events.TinkerToolEvent;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

public class Scythe extends Kama {

  public static final float DURABILITY_MODIFIER = 2.2f;

  public Scythe() {
    super(PartMaterialType.handle(TinkerTools.toughToolRod),
        PartMaterialType.head(TinkerTools.scytheHead),
        PartMaterialType.extra(TinkerTools.toughBinding),
        PartMaterialType.handle(TinkerTools.toughToolRod));
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
  protected boolean breakBlock(ItemStack stack, BlockPos pos, EntityPlayer player) {
    // only allow shears with silktouch :D
    return isSilkTouch(stack) && super.breakBlock(stack, pos, player);
  }

  @Override
  protected void breakExtraBlock(ItemStack stack, World world, EntityPlayer player, BlockPos pos, BlockPos refPos) {
    // only allow shears with silktouch :D
    if(isSilkTouch(stack)) {
      ToolHelper.shearExtraBlock(stack, world, player, pos, refPos);
      return;
    }

    // can't be sheared or no silktouch. break it
    ToolHelper.breakExtraBlock(stack, world, player, pos, refPos);
  }

  @Override
  public Set<String> getToolClasses(ItemStack stack) {
    // probably should have two lists here if we ever add a tool class apart from shears
    if(!isSilkTouch(stack)) {
      return Collections.emptySet();
    }
    return super.getToolClasses(stack);
  }

  @Override
  public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState) {
    if("shears".equals(toolClass) && !isSilkTouch(stack)) {
      return -1;
    }

    return super.getHarvestLevel(stack, toolClass, player, blockState);
  }

  private static boolean isSilkTouch(ItemStack stack) {
    return EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0;
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
    TinkerToolEvent.ExtraBlockBreak event = TinkerToolEvent.ExtraBlockBreak.fireEvent(stack, player, player.getEntityWorld().getBlockState(target.getPosition()), 3, 3, 3, -1);
    if(event.isCanceled()) {
      return false;
    }

    // AOE attack!
    player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
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

    return player.getEntityWorld().getEntitiesWithinAABBExcludingEntity(player, box);
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
    TinkerToolEvent.ExtraBlockBreak event = TinkerToolEvent.ExtraBlockBreak.fireEvent(stack, player, player.getEntityWorld().getBlockState(target.getPosition()), 3, 3, 3, -1);
    if(event.isCanceled()) {
      return false;
    }

    int distance = event.distance;
    boolean shorn = false;

    int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
    for(Entity entity : getAoeEntities(player, target, event)) {
      if(distance < 0 || entity.getDistanceToEntity(target) <= distance) {
        shorn |= shearEntity(stack, player.getEntityWorld(), player, entity, fortune);
      }
    }

    if(shorn) {
      swingTool(player, hand);
    }

    return shorn;
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
