package slimeknights.tconstruct.library;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerTools;

/**
 * Encapsulates the standard actions for integrating a material/item into tcon
 */
public class MaterialIntegration {

  public Material material; // TCon material
  public Fluid fluid;
  public String oreSuffix; // oredict suffix, e.g. "Iron" -> "ingotIron", "blockIron",...
  public String oreRequirement; // required oredict entry for this integration
  private boolean integrated;
  private boolean toolforge = false;

  public MaterialIntegration(Material material) {
    this(material, null);
  }

  public MaterialIntegration(Material material, Fluid fluid) {
    this(null, material, fluid, null);
  }

  public MaterialIntegration(Material material, Fluid fluid, String oreSuffix) {
    this("ingot" + oreSuffix, material, fluid, oreSuffix);
  }

  public MaterialIntegration(String oreRequirement, Material material, Fluid fluid, String oreSuffix) {
    this.material = material;
    this.fluid = fluid;
    this.oreSuffix = oreSuffix;
    this.oreRequirement = oreRequirement;
    this.oreRequirement = null;

    this.integrated = false;
  }

  public MaterialIntegration toolforge() {
    this.toolforge = true;
    return this;
  }

  public void integrate() {
    if(integrated) {
      return;
    }

    if(oreRequirement != null) {
      boolean found = false;
      // we use this method because it doesn't add empty entries to the oredict, even though it is less performant
      for(String ore : OreDictionary.getOreNames()) {
        if(ore.equals(oreRequirement)) {
          found = true;
          break;
        }
      }
      // prerequisite not fulfilled
      if(!found) {
        return;
      }
    }

    integrated = true;

    // decativate fluids if smeltery isn't loaded
    if(!TConstruct.pulseManager.isPulseLoaded(TinkerSmeltery.PulseId)) {
      fluid = null;
    }

    // fluid first
    if(fluid != null) {
      FluidRegistry.registerFluid(fluid);
      TinkerFluids.registerFluid(fluid);
      registerFluidBlock();
    }

    // register material
    if(material != null) {
      TinkerRegistry.addMaterial(material);
      if(fluid != null) {
        material.setFluid(fluid);
        material.setCastable(true);
      }
      else {
        material.setCraftable(true);
      }
    }

    // add toolforge recipe
    if(toolforge && oreSuffix != null && !oreSuffix.isEmpty()) {
      TinkerTools.registerToolForgeBlock("block" + oreSuffix);
    }
  }

  public void integrateRecipes() {
    if(!integrated) {
      return;
    }
    // register melting and casting
    if(fluid != null && oreSuffix != null) {
      TinkerSmeltery.registerOredictMeltingCasting(fluid, oreSuffix);
    }
    if(material != null) {
      TinkerSmeltery.registerToolpartMeltingCasting(material);
    }
  }

  public void registerFluidBlock() {
    TinkerFluids.registerMoltenBlock(fluid);
    TinkerFluids.proxy.registerFluidModels(fluid);
  }
}
