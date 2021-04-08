package slimeknights.tconstruct.tools.modifiers.effect;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import slimeknights.tconstruct.library.effect.TinkerEffect;

import java.util.List;

public class MagneticEffect extends TinkerEffect {
  public MagneticEffect() {
    super(StatusEffectType.BENEFICIAL, 0x720000, false);
  }

  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    return (duration & 1) == 0;
  }

  @Override
  public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    // super magnetic - inspired by botanias code
    double x = entity.getX();
    double y = entity.getY();
    double z = entity.getZ();
    float range = 3f + 1f * amplifier;
    List<ItemEntity> items = entity.getEntityWorld().getNonSpectatingEntities(ItemEntity.class, new Box(x - range, y - range, z - range, x + range, y + range, z + range));

    // only pull up to 200 items
    int pulled = 0;
    for (ItemEntity item : items) {
      if (item.getStack().isEmpty() || !item.isAlive()) {
        continue;
      }

      // calculate direction: item -> player
      Vec3d vec = entity.getPos()
                           .subtract(item.getX(), item.getY(), item.getZ())
                           .normalize()
                           .multiply(0.05f + amplifier * 0.05f);

      // we calculated the movement vector and set it to the correct strength.. now we apply it \o/
      item.setVelocity(item.getVelocity().add(vec));

      // use stack size as limiting factor
      pulled += item.getStack().getCount();
      if (pulled > 200) {
        break;
      }
    }
  }
}
