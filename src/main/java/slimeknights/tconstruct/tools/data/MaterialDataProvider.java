package slimeknights.tconstruct.tools.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.fluid.Fluids;
import slimeknights.tconstruct.library.data.GenericDataProvider;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialManager;
import slimeknights.tconstruct.library.materials.json.MaterialJson;
import slimeknights.tconstruct.library.materials.json.TraitJson;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

public class MaterialDataProvider extends GenericDataProvider {

  public MaterialDataProvider(DataGenerator gen) {
    super(gen, MaterialManager.FOLDER, MaterialManager.GSON);
  }

  @Override
  public void act(DirectoryCache cache) {
    Materials.allMaterials.forEach(material -> saveThing(cache, material.getIdentifier(), convert(material)));
  }

  private MaterialJson convert(IMaterial material) {
    TraitJson traitJson = null;
    ModifierEntry trait = material.getTrait();
    if (trait != null) {
      traitJson = new TraitJson(trait.getModifier().getId(), trait.getLevel());
    }

    // if empty, no fluid, no temperature
    String color = material.getColor().getName();
    if (material.getFluid() == Fluids.EMPTY) {
      return new MaterialJson(material.isCraftable(), null, null, color, null, traitJson);
    }
    return new MaterialJson(material.isCraftable(), material.getFluid().getRegistryName(), material.getFluidPerUnit(), color, material.getTemperature(), traitJson);
  }

  @Override
  public String getName() {
    return "TConstruct Materials";
  }
}
