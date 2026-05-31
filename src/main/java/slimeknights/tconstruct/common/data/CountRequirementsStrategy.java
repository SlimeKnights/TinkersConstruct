package slimeknights.tconstruct.common.data;

import net.minecraft.advancements.AdvancementRequirements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CountRequirementsStrategy implements AdvancementRequirements.Strategy {
  private final int[] sizes;
  public CountRequirementsStrategy(int... sizes) {
    this.sizes = sizes;
  }

  @Override
  public AdvancementRequirements create(Collection<String> strings) {
    List<List<String>> requirements = new ArrayList<>(sizes.length);
    List<String> list = new ArrayList<>(strings);
    int nextIndex = 0;
    for (int size : sizes) {
      List<String> group = new ArrayList<>(size);
      for (int j = 0; j < size; j++) {
        group.add(list.get(nextIndex));
        nextIndex++;
      }
      requirements.add(group);
    }
    return new AdvancementRequirements(requirements);
  }
}
