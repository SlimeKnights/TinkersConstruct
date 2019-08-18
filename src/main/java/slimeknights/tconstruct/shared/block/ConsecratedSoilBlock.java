package slimeknights.tconstruct.shared.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class ConsecratedSoilBlock extends Block {

  public ConsecratedSoilBlock() {
    super(Block.Properties.create(Material.SAND).hardnessAndResistance(3.0f).slipperiness(0.8F).sound(SoundType.SAND));
  }

  @Override
  public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
    this.processConsecratedSoil(entityIn);
  }

  // damage and set undead entities on fire
  private void processConsecratedSoil(Entity entity) {
    if (entity instanceof MobEntity) {
      LivingEntity entityLiving = (LivingEntity) entity;
      if (entityLiving.getCreatureAttribute() == CreatureAttribute.UNDEAD) {
        entityLiving.attackEntityFrom(DamageSource.MAGIC, 1);
        entityLiving.setFire(1);
      }
    }
  }

  @Nullable
  @Override
  //TODO: Replace when forge Re-Evaluates
  public net.minecraftforge.common.ToolType getHarvestTool(BlockState state) {
    return ToolType.SHOVEL;
  }

  @Override
  //TODO: Replace when forge Re-Evaluates
  public int getHarvestLevel(BlockState state) {
    return -1;
  }
}
