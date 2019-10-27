package slimeknights.tconstruct.library.materials.stats;

import net.minecraft.util.IStringSerializable;

// todo: possibly switch this directly to a class and implement an interface that signifies the registry stuff?
public interface PartType {

  PartType HEAD = new PartTypeImpl("head");
  PartType HANDLE = new PartTypeImpl("handle");
  PartType EXTRA = new PartTypeImpl("extra");

  PartType BOW = new PartTypeImpl("bow");
  PartType BOWSTRING = new PartTypeImpl("bowstring");

  PartType PROJECTILE = new PartTypeImpl("projectile");
  PartType SHAFT = new PartTypeImpl("shaft");
  PartType FLETCHING = new PartTypeImpl("fletching");

  String getName();

  class PartTypeImpl implements PartType, IStringSerializable {

    private final String name;

    public PartTypeImpl(String name) {
      this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }
  }
}
