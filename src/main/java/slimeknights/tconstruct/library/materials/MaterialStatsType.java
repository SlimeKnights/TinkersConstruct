package slimeknights.tconstruct.library.materials;

import net.minecraft.util.IStringSerializable;

// todo: possibly switch this directly to a class and implement an interface that signifies the registry stuff?
public interface MaterialStatsType {

  MaterialStatsType HEAD = new MaterialStatsTypeImpl("head");
  MaterialStatsType HANDLE = new MaterialStatsTypeImpl("handle");
  MaterialStatsType EXTRA = new MaterialStatsTypeImpl("extra");

  MaterialStatsType BOW = new MaterialStatsTypeImpl("bow");
  MaterialStatsType BOWSTRING = new MaterialStatsTypeImpl("bowstring");

  MaterialStatsType PROJECTILE = new MaterialStatsTypeImpl("projectile");
  MaterialStatsType SHAFT = new MaterialStatsTypeImpl("shaft");
  MaterialStatsType FLETCHING = new MaterialStatsTypeImpl("fletching");

  String getName();

  class MaterialStatsTypeImpl implements MaterialStatsType, IStringSerializable {

    private final String name;

    public MaterialStatsTypeImpl(String name) {
      this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }
  }
}
