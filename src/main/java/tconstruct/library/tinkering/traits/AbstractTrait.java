package tconstruct.library.tinkering.traits;

import net.minecraft.util.StatCollector;

import tconstruct.library.Util;

public abstract class AbstractTrait implements ITrait {
  public static final String LOCALIZATION_STRING = "trait.%s.name";
  private final String identifier;

  public AbstractTrait(String identifier) {
    this.identifier = identifier;
  }

  @Override
  public String getIdentifier() {
    return identifier;
  }

  @Override
  public String getLocalizedName() {
    String locString = Util.sanitizeLocalizationString(getIdentifier());
    return StatCollector.translateToLocal(String.format(LOCALIZATION_STRING, locString));
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
