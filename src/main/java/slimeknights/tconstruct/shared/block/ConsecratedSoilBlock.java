package slimeknights.tconstruct.shared.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class ConsecratedSoilBlock extends Block {

  public ConsecratedSoilBlock(Settings properties) {
    super(properties);
  }

  @Override
  public void onSteppedOn(World worldIn, BlockPos pos, Entity entityIn) {
    this.processConsecratedSoil(entityIn);
  }

  // damage and set undead entities on fire
  private void processConsecratedSoil(Entity entity) {
    if (entity instanceof MobEntity) {
      LivingEntity entityLiving = (LivingEntity) entity;
      if (entityLiving.getGroup() == EntityGroup.UNDEAD) {
        entityLiving.damage(DamageSource.MAGIC, 1);
        entityLiving.setOnFireFor(1);
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
