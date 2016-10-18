package slimeknights.tconstruct.tools.ranged.item;

import java.util.List;

import slimeknights.tconstruct.library.materials.BowMaterialStats;
import slimeknights.tconstruct.library.materials.BowStringMaterialStats;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ProjectileLauncherNBT;
import slimeknights.tconstruct.tools.TinkerTools;

public class LongBow extends ShortBow {

  // little more durability due to the plate
  public static final float DURABILITY_MODIFIER = 1.4f;

  public LongBow() {
    super(PartMaterialType.bowstring(TinkerTools.bowString),
          PartMaterialType.bow(TinkerTools.bowLimb),
          PartMaterialType.bow(TinkerTools.bowLimb),
          PartMaterialType.extra(TinkerTools.largePlate));
  }

  /* Tic Tool Stuff */

  @Override
  public float baseProjectileDamage() {
    return 6.5f; // shortbow 2f
  }

  @Override
  protected float baseProjectileSpeed() {
    return 6f; // shortbow 3f
  }

  @Override
  protected float baseInaccuracy() {
    return 3.2f; // shortbow 1f
  }

  @Override
  public int getDrawTime() {
    return 35; // shortbow 20
  }

  /* Data Stuff */

  @Override
  public ProjectileLauncherNBT buildTagData(List<Material> materials) {
    ProjectileLauncherNBT data = new ProjectileLauncherNBT();
    HeadMaterialStats head1 = materials.get(1).getStatsOrUnknown(MaterialTypes.HEAD);
    HeadMaterialStats head2 = materials.get(2).getStatsOrUnknown(MaterialTypes.HEAD);
    ExtraMaterialStats grip = materials.get(3).getStatsOrUnknown(MaterialTypes.EXTRA);
    BowMaterialStats limb1 = materials.get(1).getStatsOrUnknown(MaterialTypes.BOW);
    BowMaterialStats limb2 = materials.get(2).getStatsOrUnknown(MaterialTypes.BOW);
    BowStringMaterialStats bowstring = materials.get(0).getStatsOrUnknown(MaterialTypes.BOWSTRING);


    data.head(head1, head2);
    data.limb(limb1, limb2);
    data.extra(grip);
    data.bowstring(bowstring);
    //data.handle();

    data.durability *= DURABILITY_MODIFIER;

    return data;
  }

}
