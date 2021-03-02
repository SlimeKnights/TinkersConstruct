package slimeknights.tconstruct.tools.modifiers.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import slimeknights.tconstruct.library.effect.TinkerEffect;

import java.util.List;

public class MagneticEffect extends TinkerEffect {
  public MagneticEffect() {
    super(EffectType.BENEFICIAL, 0x720000, false);
  }

  @Override
  public boolean isReady(int duration, int amplifier) {
    return (duration & 1) == 0;
  }

  @Override
  public void performEffect(LivingEntity entity, int amplifier) {
    // super magnetic - inspired by botanias code
    double x = entity.getPosX();
    double y = entity.getPosY();
    double z = entity.getPosZ();
    float range = 3f + 1f * amplifier;
    List<ItemEntity> items = entity.getEntityWorld().getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(x - range, y - range, z - range, x + range, y + range, z + range));

    // only pull up to 200 items
    int pulled = 0;
    for (ItemEntity item : items) {
      if (item.getItem().isEmpty() || !item.isAlive()) {
        continue;
      }

      // calculate direction: item -> player
      Vector3d vec = entity.getPositionVec()
                           .subtract(item.getPosX(), item.getPosY(), item.getPosZ())
                           .normalize()
                           .scale(0.05f + amplifier * 0.05f);

      // we calculated the movement vector and set it to the correct strength.. now we apply it \o/
      item.setMotion(item.getMotion().add(vec));

      // use stack size as limiting factor
      pulled += item.getItem().getCount();
      if (pulled > 200) {
        break;
      }
    }
  }
}
