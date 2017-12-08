package slimeknights.tconstruct.library;

import net.minecraft.block.Block;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

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
  private boolean preInit;
  private boolean toolforge = false;
  private boolean addedFluidBlock;

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
    this.preInit = false;
    this.addedFluidBlock = false;
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

  /**
   * Contains all registration as those need to be done regardless of oredict entries
   */
  public void preInit() {
    // prevent running twice, mainly to prevent it from not getting registered as the mod may load after we register
    if(preInit) {
      return;
    }

    preInit = true;

    // decativate fluids if smeltery isn't loaded
    if(!TConstruct.pulseManager.isPulseLoaded(TinkerSmeltery.PulseId)) {
      fluid = null;
    }

    // fluid first.
    if(fluid != null) {
      Fluid registeredFluid = FluidRegistry.getFluid(fluid.getName());
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
  }

  /**
   * Called to integrate the recipes based on oredictionary recipes
   */
  public void integrate() {
    if(integrated) {
      return;
    }

    if(oreRequirement != null && oreRequirement.length > 0 && !Config.forceRegisterAll) {
      // loop through each ore string ensuring it is used
      for(String ore : oreRequirement) {
        // this is much more efficient then iterating through all entries and ensures we do not create entries
        if(OreDictionary.getOres(ore, false).isEmpty()) {
          return;
        }
      }
    }

    integrated = true;

    // register melting and casting
    if(fluid != null && oreSuffix != null) {
      TinkerSmeltery.registerOredictMeltingCasting(fluid, oreSuffix);
    }
    if(material != null) {
      material.setVisible();
      TinkerSmeltery.registerToolpartMeltingCasting(material);
      registerRepresentativeItem();
    }
  }

  private void registerRepresentativeItem() {
    // also set the representative item
    if(material.getRepresentativeItem().isEmpty() && representativeItem != null && !representativeItem.isEmpty()) {
      material.setRepresentativeItem(representativeItem);
    }
  }

  /**
   * Called during the register recipes event to add a variant to the tool forge for this material
   * @param registry  IRecipe Registry
   */
  public void registerToolForgeRecipe(IForgeRegistry<IRecipe> registry) {
    if(toolforge && oreSuffix != null && !oreSuffix.isEmpty()) {
      TinkerTools.registerToolForgeBlock(registry, "block" + oreSuffix);
    }
  }

  /**
   * Called during the register blocks event to register fluid blocks. If no fluid is defined it does nothing for simplicity
   * @param registry  Block Registry
   */
  public void registerFluidBlock(IForgeRegistry<Block> registry) {
    // ensure the fluid block is not already registered
    if(fluid != null && fluid.getBlock() == null) {
      addedFluidBlock = true;
      TinkerFluids.registerMoltenBlock(registry, fluid);
    }
  }

  /**
   * Called during the register event to register fluid models. If no fluid is defined it does nothing for simplicity
   */
  public void registerFluidModel() {
    if(addedFluidBlock) {
      TinkerFluids.proxy.registerFluidModels(fluid);
    }
  }
}
