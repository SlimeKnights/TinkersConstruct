package slimeknights.tconstruct.shared.block;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GraveyardSoilBlock extends Block {

  public GraveyardSoilBlock(Settings properties) {
    super(properties);
  }

  @Override
  public void onSteppedOn(World worldIn, BlockPos pos, Entity entityIn) {
    this.processGraveyardSoil(entityIn);
  }

  // heal undead entities
  private void processGraveyardSoil(Entity entity) {
    if (entity instanceof MobEntity) {
      LivingEntity entityLiving = (LivingEntity) entity;
      if (entityLiving.getGroup() == EntityGroup.UNDEAD) {
        entityLiving.heal(1);
      }
    }
  }
}
