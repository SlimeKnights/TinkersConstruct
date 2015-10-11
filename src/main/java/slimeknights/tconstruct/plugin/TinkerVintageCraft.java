package slimeknights.tconstruct.plugin;

import com.google.common.eventbus.Subscribe;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.tools.TableRecipe;
import slimeknights.tconstruct.tools.block.BlockToolTable;

import static slimeknights.tconstruct.tools.TinkerMaterials.*;
import static slimeknights.tconstruct.tools.TinkerTools.*;

@Pulse(id = TinkerVintageCraft.PulseId, modsRequired = TinkerVintageCraft.modid)
public class TinkerVintageCraft {

  public static final String modid = "vintagecraft";
  public static final String PulseId = modid + "Integration";

  @Subscribe
  public void integrate(FMLServerStartingEvent event) {
    integrateMaterials();
    integrateTools();
  }

  private void integrateMaterials() {
    wood.addItem("vcraft-stickWood", 1, Material.VALUE_Shard);
    wood.addItem("vcraft-plankWood", 1, Material.VALUE_Ingot);
    wood.addItem("vcraft-logWood", 1, Material.VALUE_Ingot * 4);

    stone.addItem("vcraft-stone", 1, Material.VALUE_Ingot);
    stone.addItem("vcraft-stoneAny", 1, Material.VALUE_Fragment);
  }

  private void integrateTools() {
    // pattern
    GameRegistry.addRecipe(new ShapedOreRecipe(pattern, "PS", "SP", 'P', "vcraft-plankWood", 'S', "stickWood"));
    GameRegistry.addRecipe(new ShapedOreRecipe(pattern, "SP", "PS", 'P', "vcraft-plankWood", 'S', "stickWood"));

    // stenciltable
    GameRegistry.addRecipe(
        new TableRecipe(OreDictionary.getOres("vcraft-plankWood"), toolTables, BlockToolTable.TableTypes.StencilTable.meta,
                        "P", "B", 'P', pattern, 'B', "vcraft-plankWood"));
    // partbouilder
    GameRegistry.addRecipe(
        new TableRecipe(OreDictionary.getOres("vcraft-logWood"), toolTables, BlockToolTable.TableTypes.PartBuilder.meta, "P",
                        "B", 'P', pattern, 'B', "vcraft-logWood"));
  }
}
