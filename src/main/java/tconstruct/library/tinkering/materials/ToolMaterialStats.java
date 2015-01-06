package tconstruct.library.tinkering.materials;


public class ToolMaterialStats extends AbstractMaterialStats {

  public final static String TYPE = "tool";

  public final int durability;
  public final float durabilityModifier;
  public final float attack;
  public final float miningspeed;
  public final int harvestLevel;

  public ToolMaterialStats(int harvestLevel, int durability, float durabilityModifier,
                           float miningspeed, float attack) {
    super(TYPE);
    this.durability = durability;
    this.durabilityModifier = durabilityModifier;
    this.attack = attack;
    this.miningspeed = miningspeed;
    this.harvestLevel = harvestLevel;
  }
}
