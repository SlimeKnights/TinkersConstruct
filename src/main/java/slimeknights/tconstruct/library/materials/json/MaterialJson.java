package slimeknights.tconstruct.library.materials.json;

import lombok.Data;
import net.minecraftforge.common.crafting.conditions.ICondition;

import javax.annotation.Nullable;

@Data
public class MaterialJson {
  @Nullable
  private final ICondition condition;
  @Nullable
  private final Boolean craftable;
  @Nullable
  private final Integer tier;
  @Nullable
  private final Integer sortOrder;
  @Nullable
  private final String textColor;
  @Nullable
  private final Boolean hidden;
}
