package slimeknights.tconstruct.plugin.crt;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.impl.commands.CTCommandCollectionEvent;
import com.blamejared.crafttweaker.impl.fluid.MCFluidStackMutable;
import com.blamejared.crafttweaker.impl_native.fluid.ExpandFluid;
import com.blamejared.crafttweaker.impl_native.item.ExpandItem;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

import java.util.stream.Collectors;

public class CRTEvents {

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


  }


}
