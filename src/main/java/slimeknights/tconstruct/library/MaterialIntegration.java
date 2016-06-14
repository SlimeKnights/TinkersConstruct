package slimeknights.tconstruct.library;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
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
  public String[] oreRequirement; // required oredict entry for this integration
  public String representativeItem; // oredict entry for the representative item
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
    this(material, fluid, oreSuffix, oreRequirement);
  }

  public MaterialIntegration(Material material, Fluid fluid, String oreSuffix, String... oreRequirement) {
    this.material = material;
    this.fluid = fluid;
    this.oreSuffix = oreSuffix;
    this.representativeItem = "ingot" + oreSuffix;
    this.oreRequirement = oreRequirement[0] == null ? new String[0] : oreRequirement; // API backwards compatibility

    this.integrated = false;
  }

  public MaterialIntegration toolforge() {
    this.toolforge = true;
    return this;
  }

  public MaterialIntegration setRepresentativeItem(String representativeItem) {
    this.representativeItem = representativeItem;
    return this;
  }

  public boolean isIntegrated() {
    return integrated;
  }

  public void integrate() {
    if(integrated) {
      return;
    }

    if(oreRequirement != null && oreRequirement.length > 0 && !Config.forceRegisterAll) {
      int found = 0;
      // we use this method because it doesn't add empty entries to the oredict, even though it is less performant
      for(String ore : OreDictionary.getOreNames()) {
        for(int i = 0; i < oreRequirement.length; i++) {
          if(oreRequirement[i].equals(ore)) {
            if(++found == oreRequirement.length) {
              break;
            }
          }
        }
      }
      // prerequisite not fulfilled
      if(found < oreRequirement.length) {
        return;
      }
    }

    integrated = true;

    // decativate fluids if smeltery isn't loaded
    if(!TConstruct.pulseManager.isPulseLoaded(TinkerSmeltery.PulseId)) {
      fluid = null;
    }

    // fluid first.
    if(fluid != null) {
      Fluid registeredFluid = FluidRegistry.getFluid(fluid.getName());
      // we only register blocks and buckets if it's our own fluid
      if(registeredFluid == fluid && fluid.getBlock() == null) {
        registerFluidBlock();
      }

      // we register a bucket for the fluid if it's not done because we need it
      if(!FluidRegistry.getBucketFluids().contains(registeredFluid)) {
        FluidRegistry.addBucketForFluid(registeredFluid);
      }
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

  public void registerRepresentativeItem() {
    // also set the representative item
    if(material != null && material.getRepresentativeItem() == null && representativeItem != null && !representativeItem.isEmpty()) {
      List<ItemStack> ore = OreDictionary.getOres(representativeItem, false);
      if(!ore.isEmpty()) {
        ItemStack itemStack = ore.get(0).copy();
        if(itemStack.getMetadata() == OreDictionary.WILDCARD_VALUE) {
          itemStack.setItemDamage(0);
        }
        material.setRepresentativeItem(itemStack);
      }
    }
  }

  public void registerFluidBlock() {
    TinkerFluids.registerMoltenBlock(fluid);
    TinkerFluids.proxy.registerFluidModels(fluid);
  }
}
