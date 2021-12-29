package slimeknights.tconstruct.world.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;

public class EarthSlimeEntity extends Slime {
  public EarthSlimeEntity(EntityType<? extends Slime> type, Level worldIn) {
    super(type, worldIn);
  }

  @Override
  public ItemStack getPickedResult(HitResult target) {
    // this entity is a clone of the vanilla mob, so the vanilla egg is sufficient
    // only difference is spawning rules
    return new ItemStack(Items.SLIME_SPAWN_EGG);
  }
}
