package slimeknights.tconstruct.plugin.crt;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.impl.commands.CTCommandCollectionEvent;
import com.blamejared.crafttweaker.impl.commands.script_examples.ExampleCollectionEvent;
import com.blamejared.crafttweaker.impl.fluid.MCFluidStackMutable;
import com.blamejared.crafttweaker.impl_native.item.ExpandItem;
import com.google.gson.JsonElement;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.TinkerRegistries;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tools.item.ToolCore;

import java.util.stream.Collectors;

public class CRTHelper {
  
  public static MaterialId getMaterialId(String materialId) {
    MaterialId material = MaterialId.tryCreate(materialId);
    if(material == null) {
      throw new IllegalArgumentException("Invalid ResourceLocation provided! Provided: " + materialId);
    }
    IMaterial foundMaterial = MaterialRegistry.getMaterial(material);
    if(foundMaterial == Material.UNKNOWN) {
      throw new IllegalArgumentException("Material does not exist! Provided: " + materialId);
    }
    
    return material;
  }
  
  public static Modifier getModifier(String name) {
    ModifierId resultId = ModifierId.tryCreate(name);
    if(resultId == null) {
      throw new IllegalArgumentException("Invalid ResourceLocation provided! Provided: " + name);
    }
    Modifier resultModifier = TinkerRegistries.MODIFIERS.getValue(resultId);
    if(resultModifier == null) {
      throw new IllegalArgumentException("Modifier does not exist! Provided: " + resultId);
    }
    return resultModifier;
  }
  
  @SubscribeEvent
  public void onExampleCollection(ExampleCollectionEvent event) {
    event.addResource(location("alloying"));
    event.addResource(location("beheading"));
    event.addResource(location("casting_basin"));
    event.addResource(location("casting_table"));
    event.addResource(location("entity_melting"));
    event.addResource(location("fuel"));
    event.addResource(location("material"));
    event.addResource(location("melting"));
    event.addResource(location("molding_basin"));
    event.addResource(location("molding_table"));
    event.addResource(location("part_builder"));
    event.addResource(location("tinker_station"));
  }
  
  private ResourceLocation location(String location) {
    return new ResourceLocation(TConstruct.modID, TConstruct.modID + "/" + location);
  }
  
  @SubscribeEvent
  public void onCommandCollection(CTCommandCollectionEvent event) {
    
    event.registerDump("ticMaterials", "Lists the different Tinkers Construct Materials", commandContext -> {
      
      CraftTweakerAPI.logDump("List of all Tinkers Construct Materials: ");
      MaterialRegistry.getMaterials().forEach(iMaterial -> {
        StringBuilder builder = new StringBuilder();
        builder.append("Material: `").append(iMaterial.getIdentifier()).append("` {");
        builder.append("\n\tCraftable: ").append(iMaterial.isCraftable());
        builder.append("\n\tFluid: ").append(new MCFluidStackMutable(new FluidStack(iMaterial.getFluid(), 1)).getCommandString());
        builder.append("\n\tFluidPerUnit: ").append(iMaterial.getFluidPerUnit());
        builder.append("\n\tTranslation Key: `").append(iMaterial.getTranslationKey()).append("`");
        builder.append("\n\tColor: ").append(iMaterial.getColor().getHex());
        builder.append("\n\tTemperature: ").append(iMaterial.getTemperature());
        builder.append("\n\tTier: ").append(iMaterial.getTier());
        builder.append("\n\tSort Order: ").append(iMaterial.getSortOrder());
        builder.append("\n\tTraits: [");
        builder.append(iMaterial.getTraits().stream().map(ModifierEntry::toJson).map(JsonElement::toString).collect(Collectors.joining(", "))).append("]");
        builder.append("\n}");
        CraftTweakerAPI.logDump(builder.toString());
      });
      final StringTextComponent message = new StringTextComponent(TextFormatting.GREEN + "Material list written to the log" + TextFormatting.RESET);
      commandContext.getSource().sendFeedback(message, true);
      return 0;
    });
    
    event.registerDump("ticMaterialItems", "Lists the different items that are valid Material Items", commandContext -> {
      
      CraftTweakerAPI.logDump("List of all Items that can be used as a Material Item: ");
      ForgeRegistries.ITEMS.getValues().stream().filter(item -> item instanceof IMaterialItem).forEach(item -> {
        CraftTweakerAPI.logDump(ExpandItem.getDefaultInstance(item).getCommandString());
      });
      final StringTextComponent message = new StringTextComponent(TextFormatting.GREEN + "Material Items written to the log" + TextFormatting.RESET);
      commandContext.getSource().sendFeedback(message, true);
      return 0;
    });
    
    
    event.registerDump("ticModifiers", "Lists the different Tinkers Construct Modifiers", commandContext -> {
      
      CraftTweakerAPI.logDump("List of all Tinkers Construct Modifiers: ");
      TinkerRegistries.MODIFIERS.getValues().forEach(iMaterial -> {
        StringBuilder builder = new StringBuilder();
        builder.append("Modifier: `").append(iMaterial.getId()).append("` {");
        builder.append("\n\tDisplay Name: ").append(iMaterial.getDisplayName().getString());
        builder.append("\n\tTranslation Key: `").append(iMaterial.getTranslationKey()).append("`");
        builder.append("\n\tDescription: ```").append(iMaterial.getDescription().getString()).append("```");
        builder.append("\n\tColor: ").append(Color.fromInt(iMaterial.getColor()).getHex());
        builder.append("\n\tPriority: ").append(iMaterial.getPriority());
        builder.append("\n}");
        CraftTweakerAPI.logDump(builder.toString());
      });
      final StringTextComponent message = new StringTextComponent(TextFormatting.GREEN + "Modifier list written to the log" + TextFormatting.RESET);
      commandContext.getSource().sendFeedback(message, true);
      return 0;
    });
    
    event.registerDump("ticToolCores", "Lists the different items that are valid Tool Core Items", commandContext -> {
      
      // CraftTweaker currently doesn't have a nice way to print just the item registry name in our format without the nbt tag attached.
      CraftTweakerAPI.logDump("List of all Items that can be used as a Tool Core (remove the withTag, you just want the actual item): ");
      ForgeRegistries.ITEMS.getValues().stream().filter(item -> item instanceof ToolCore).forEach(item -> {
        CraftTweakerAPI.logDump(ExpandItem.getDefaultInstance(item).getCommandString());
      });
      final StringTextComponent message = new StringTextComponent(TextFormatting.GREEN + "Tool Core Items written to the log" + TextFormatting.RESET);
      commandContext.getSource().sendFeedback(message, true);
      return 0;
    });
    
  }
  
  
}
