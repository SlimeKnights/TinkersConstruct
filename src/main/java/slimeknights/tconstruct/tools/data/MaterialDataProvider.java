package slimeknights.tconstruct.tools.data;

import net.minecraft.data.DirectoryCache;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.data.GenericDataProvider;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialManager;
import slimeknights.tconstruct.library.materials.json.MaterialJson;

public class MaterialDataProvider extends GenericDataProvider {

  public MaterialDataProvider() {
    super(MaterialManager.FOLDER, MaterialManager.GSON);
  }

  @Override
  public void act(DirectoryCache cache) {
    Materials.allMaterials.forEach(material -> saveThing(cache, material.getIdentifier(), convert(material)));
  }

  private MaterialJson convert(IMaterial material) {
    ResourceLocation fluid = material.getFluid() == Fluids.EMPTY ? null : material.getFluid().getRegistryName();
    // todo: implement shard if needed
    return new MaterialJson(material.isCraftable(), fluid, null);
  }


  @Override
  public String getName() {
    return "TConstruct Materials";
  }
}
