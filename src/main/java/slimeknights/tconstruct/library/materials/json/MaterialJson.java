package slimeknights.tconstruct.library.materials.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

@AllArgsConstructor
@Getter
public class MaterialJson {
  @Nullable
  private final Boolean craftable;
  @Nullable
  private final ResourceLocation fluid;
  @Nullable
  private final Integer fluidPerUnit;
  @Nullable
  private final String textColor;
  @Nullable
  private final Integer temperature;
}
