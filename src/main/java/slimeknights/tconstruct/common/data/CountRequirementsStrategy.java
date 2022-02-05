package slimeknights.tconstruct.common.data;

import net.minecraft.advancements.RequirementsStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CountRequirementsStrategy implements RequirementsStrategy {
  private final int[] sizes;
  public CountRequirementsStrategy(int... sizes) {
    this.sizes = sizes;
  }

  @Override
  public String[][] createRequirements(Collection<String> strings) {
    String[][] requirements = new String[sizes.length][];
    List<String> list = new ArrayList<>(strings);
    int nextIndex = 0;
    for (int i = 0; i < sizes.length; i++) {
      requirements[i] = new String[sizes[i]];
      for (int j = 0; j < sizes[i]; j++) {
        requirements[i][j] = list.get(nextIndex);
        nextIndex++;
      }
    }
    return requirements;
  }
}
