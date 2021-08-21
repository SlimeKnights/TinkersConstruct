package slimeknights.tconstruct.world.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EarthSlimeEntity extends SlimeEntity {
  public EarthSlimeEntity(EntityType<? extends SlimeEntity> type, World worldIn) {
    super(type, worldIn);
  }

  @Override
  public ItemStack getPickedResult(RayTraceResult target) {
    // this entity is a clone of the vanilla mob, so the vanilla egg is sufficient
    // only difference is spawning rules
    return new ItemStack(Items.SLIME_SPAWN_EGG);
  }
}
