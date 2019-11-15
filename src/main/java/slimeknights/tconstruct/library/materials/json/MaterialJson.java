package slimeknights.tconstruct.library.materials.json;

import net.minecraft.util.ResourceLocation;

public class MaterialJson {

  private final Boolean craftable;
  private final ResourceLocation fluid;
  private final ResourceLocation shardItem;

  public MaterialJson(Boolean craftable, ResourceLocation fluid, ResourceLocation shardItem) {
    this.craftable = craftable;
    this.fluid = fluid;
    this.shardItem = shardItem;
  }

  public Boolean getCraftable() {
    return craftable;
  }

  public ResourceLocation getFluid() {
    return fluid;
  }

  public ResourceLocation getShardItem() {
    return shardItem;
  }
}
