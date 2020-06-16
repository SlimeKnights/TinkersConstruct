package slimeknights.tconstruct.library.materials.json;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class MaterialJson {

  private final Boolean craftable;
  private final ResourceLocation fluid;
  private final String textColor;

  public MaterialJson(Boolean craftable, ResourceLocation fluid, String textColor) {
    this.craftable = craftable;
    this.fluid = fluid;
    this.textColor = textColor;
  }

  @Nullable
  public Boolean getCraftable() {
    return this.craftable;
  }

  @Nullable
  public ResourceLocation getFluid() {
    return this.fluid;
  }

  public String getTextColor() {
    return this.textColor;
  }
}
