package slimeknights.tconstruct.tools.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.fluid.Fluids;
import net.minecraftforge.common.crafting.conditions.ICondition;
import slimeknights.tconstruct.library.data.GenericDataProvider;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialManager;
import slimeknights.tconstruct.library.materials.json.MaterialJson;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import javax.annotation.Nullable;
import java.util.List;

public class MaterialDataProvider extends GenericDataProvider {

  public MaterialDataProvider(DataGenerator gen) {
    super(gen, MaterialManager.FOLDER, MaterialManager.GSON);
  }

  @Override
  public void act(DirectoryCache cache) {
    Materials.allMaterials.forEach(pair -> saveThing(cache, pair.getFirst().getIdentifier(), convert(pair.getFirst(), pair.getSecond())));
  }

  private MaterialJson convert(IMaterial material, @Nullable ICondition condition) {
    List<ModifierEntry> list = material.getTraits();
    ModifierEntry[] traits = list.isEmpty() ? null : list.toArray(new ModifierEntry[0]);

    // if empty, no fluid, no temperature
    String color = material.getColor().getName();
    if (material.getFluid() == Fluids.EMPTY) {
      return new MaterialJson(condition, material.isCraftable(), material.getTier(), material.getSortOrder(), null, null, color, null, traits);
    }
    return new MaterialJson(condition, material.isCraftable(), material.getTier(), material.getSortOrder(), material.getFluid().getRegistryName(), material.getFluidPerUnit(), color, material.getTemperature(), traits);
  }

  @Override
  public String getName() {
    return "TConstruct Materials";
  }
}
