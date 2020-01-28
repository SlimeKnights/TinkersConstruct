package slimeknights.tconstruct.tools.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommonMaterialStats extends BaseMaterialStats {

  private int durability;
  private float attack;
}
