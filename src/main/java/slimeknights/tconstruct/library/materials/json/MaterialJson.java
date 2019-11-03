package slimeknights.tconstruct.library.materials.json;

import net.minecraft.util.ResourceLocation;

import java.util.List;

public class MaterialJson {

  private final ResourceLocation id;
  private final Boolean craftable;
  private final ResourceLocation fluid;
  private final ResourceLocation shardItem;
  private final List<MaterialStatJson> stats;

  public MaterialJson(ResourceLocation id, Boolean craftable, ResourceLocation fluid, ResourceLocation shardItem, List<MaterialStatJson> stats) {
    this.id = id;
    this.craftable = craftable;
    this.fluid = fluid;
    this.shardItem = shardItem;
    this.stats = stats;
  }

  public ResourceLocation getId() {
    return id;
  }

  public Boolean getCraftable() {
    return craftable;
  }

  public ResourceLocation getFluid() {
    return fluid;
  }

  public List<MaterialStatJson> getStats() {
    return stats;
  }

  public ResourceLocation getShardItem() {
    return shardItem;
  }
}
