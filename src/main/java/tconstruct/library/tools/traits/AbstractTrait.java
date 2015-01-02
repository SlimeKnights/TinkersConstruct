package tconstruct.library.tools.traits;

import net.minecraft.util.StatCollector;

import tconstruct.Util;

public abstract class AbstractTrait implements IMaterialTrait {

  @Override
  public String getLocalizedName() {
    String locString = "material.trait." + getIdentifier();
    locString = Util.sanitizeLocalizationString(locString);
    return StatCollector.translateToLocal(locString);
  }

  /* Updating */
  @Override
  public void onUpdate() {

  }


  /* Harvesting */
  @Override
  public float miningSpeed(float speed, float currentSpeed, boolean isEffective) {
    return currentSpeed;
  }

  @Override
  public boolean beforeBlockBreak() {
    return false;
  }

  @Override
  public void afterBlockBreak() {

  }

  /* Attacking */
  @Override
  public float onHit(float damage, float currentDamage) {
    return currentDamage;
  }

  @Override
  public boolean doesCriticalHit() {
    return false;
  }

  /* Damage tool */
  @Override
  public int onDamage(int damage, int currentDamage) {
    return currentDamage;
  }
}
