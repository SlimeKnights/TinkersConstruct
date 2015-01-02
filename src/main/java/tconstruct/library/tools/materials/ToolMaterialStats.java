package tconstruct.library.tools.materials;


public class ToolMaterialStats extends AbstractMaterialStats {

  public final int durability;
  public final float handleModifier;
  public final float attack;
  public final float miningspeed;
  public final int harvestLevel;

  public ToolMaterialStats(int harvestLevel, int durability, float handleModifier,
                           float miningspeed, float attack) {
    super("tool");
    this.durability = durability;
    this.handleModifier = handleModifier;
    this.attack = attack;
    this.miningspeed = miningspeed;
    this.harvestLevel = harvestLevel;
  }
}
