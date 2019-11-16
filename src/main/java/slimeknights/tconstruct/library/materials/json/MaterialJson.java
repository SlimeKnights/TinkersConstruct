package slimeknights.tconstruct.library.materials.json;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class MaterialJson {

  private final Boolean craftable;
  private final ResourceLocation fluid;
  private final ResourceLocation shardItem;

  public MaterialJson(Boolean craftable, ResourceLocation fluid, ResourceLocation shardItem) {
    this.craftable = craftable;
    this.fluid = fluid;
    this.shardItem = shardItem;
  }

  @Nullable
  public Boolean getCraftable() {
    return craftable;
  }

  @Nullable
  public ResourceLocation getFluid() {
    return fluid;
  }

  @Nullable
  public ResourceLocation getShardItem() {
    return shardItem;
  }
}
