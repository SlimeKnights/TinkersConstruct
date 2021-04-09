package slimeknights.tconstruct.library.materials.json;

import lombok.Data;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.recipe.ICondition;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import net.minecraft.util.Identifier;

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
  private final Identifier fluid;
  @Nullable
  private final Integer fluidPerUnit;
  @Nullable
  private final String textColor;
  @Nullable
  private final Integer temperature;
  @Nullable
  private final ModifierEntry[] traits;
}
