package slimeknights.tconstruct.tools;

import com.google.common.eventbus.Subscribe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.ModelRegisterUtil;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.tools.modifiers.ModExtraTraitDisplay;
import slimeknights.tconstruct.tools.modifiers.ModFortifyDisplay;

import static slimeknights.tconstruct.tools.TinkerModifiers.modCreative;
import static slimeknights.tconstruct.tools.TinkerModifiers.modHarvestHeight;
import static slimeknights.tconstruct.tools.TinkerModifiers.modHarvestWidth;

// this class is called after all other pulses that add stuff have been called and registers all the tools, modifiers
// and more in one swoop
@Pulse(
    id = AggregateModelRegistrar.PulseId,
    description = "Registers tool models and co",
    pulsesRequired = TinkerTools.PulseId,
    forced = true)
public class AggregateModelRegistrar extends AbstractToolPulse {

  public static final String PulseId = "TinkerModelRegister";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.tools.AggregateModelRegistrar$AggregateClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  @Override
  @SubscribeEvent
  public void registerItems(Register<Item> event) {
    for(Pair<Item, ToolPart> toolPartPattern : toolPartPatterns) {
      registerStencil(toolPartPattern.getLeft(), toolPartPattern.getRight());
    }
  }

  @SubscribeEvent
  public void registerModels(ModelRegistryEvent event) {
    proxy.registerModels();
  }

  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    proxy.preInit();
  }

  private void registerStencil(Item pattern, ToolPart toolPart) {
    for(ToolCore toolCore : TinkerRegistry.getTools()) {
      for(PartMaterialType partMaterialType : toolCore.getRequiredComponents()) {
        if(partMaterialType.getPossibleParts().contains(toolPart)) {
          ItemStack stencil = new ItemStack(pattern);
          Pattern.setTagForPart(stencil, toolPart);
          TinkerRegistry.registerStencilTableCrafting(stencil);
          return;
        }
      }
    }
  }


  public static class AggregateClientProxy extends ClientProxy {

    @Override
    public void registerModels() {
      super.registerModels();

      // toolparts
      for(ToolPart part : toolparts) {
        ModelRegisterUtil.registerPartModel(part);
      }

      // tools
      for(ToolCore tool : tools) {
        ModelRegisterUtil.registerToolModel(tool);
      }

      registerModifierModels();
    }

    private void registerModifierModels() {
      for(IModifier modifier : modifiers) {
        if(modifier == modCreative || modifier == modHarvestWidth || modifier == modHarvestHeight) {
          // modifiers without model are blacklisted
          continue;
        }
        ModelRegisterUtil.registerModifierModel(modifier, Util.getModifierResource(modifier.getIdentifier()));
      }

      // we add a temporary modifier that does nothing to work around the model restrictions for the fortify modifier
      ModelRegisterUtil.registerModifierModel(new ModFortifyDisplay(), Util.getResource("models/item/modifiers/fortify"));
      new ModExtraTraitDisplay();
    }
  }
}
