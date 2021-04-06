package slimeknights.tconstruct.library.materials.json;

import lombok.Data;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

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
  private final ResourceLocation fluid;
  @Nullable
  private final Integer fluidPerUnit;
  @Nullable
  private final String textColor;
  @Nullable
  private final Integer temperature;
  @Nullable
  private final ModifierEntry[] traits;
}
