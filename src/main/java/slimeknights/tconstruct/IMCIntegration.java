package slimeknights.tconstruct;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.logging.log4j.Logger;

import slimeknights.tconstruct.library.MaterialIntegration;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public abstract class IMCIntegration {

  static final Logger log = Util.getLogger("IMC");

  private IMCIntegration() {
  }

  public static void integrateSmeltery(FMLInterModComms.IMCMessage message) {
    if(!message.isNBTMessage()) {
      return;
    }
    NBTTagCompound tag = message.getNBTValue();
    String fluidName = tag.getString("fluid");
    String ore = tag.getString("ore");
    boolean toolforge = tag.getBoolean("toolforge");

    Fluid fluid = FluidRegistry.getFluid(fluidName);

    if(fluid != null && !ore.isEmpty()) {
      boolean isNew = true;
      for(MaterialIntegration mi : TinkerRegistry.getMaterialIntegrations()) {
        if(mi.fluid != null && mi.fluid.getName().equals(fluidName)) {
          isNew = false;
        }
      }
      // only integrate if not present already
      if(isNew) {
        MaterialIntegration materialIntegration = new MaterialIntegration(null, fluid, ore);
        if(toolforge) {
          materialIntegration.toolforge();
        }
        TinkerRegistry.integrate(materialIntegration);
        materialIntegration.preInit();
        log.debug("Added integration smelting for " + ore + " from " + message.getSender());
      }
    }
    if(tag.hasKey("alloy")) {
      alloy(tag.getTagList("alloy", 10));
    }
  }

  public static void alloy(FMLInterModComms.IMCMessage message) {
    if(!message.isNBTMessage()) {
      return;
    }
    alloy(message.getNBTValue().getTagList("alloy", 10));
  }

  private static void alloy(NBTTagList tagList) {
    TinkerIntegration.addAlloyNBTTag(tagList);
    // logging happens in TinkerIntegration when the alloys are handled
  }

  public static void blacklistMelting(FMLInterModComms.IMCMessage message) {
    if(!message.isStringMessage() && !message.isItemStackMessage()) {
      return;
    }
    // oredict blacklist
    if(message.getMessageType() == String.class) {
      TinkerSmeltery.meltingBlacklist.addAll(OreDictionary.getOres(message.getStringValue(), false));
      log.debug("Blacklisted oredictionary entry " + message.getStringValue() + " from melting");
    }
    else {
      TinkerSmeltery.meltingBlacklist.add(message.getItemStackValue());
      log.debug("Blacklisted " + message.getItemStackValue().getUnlocalizedName() + " from melting");
    }
  }

  public static void addDryingRecipe(FMLInterModComms.IMCMessage message) {
    if(!message.isNBTMessage()) {
      return;
    }

    NBTTagCompound tag = message.getNBTValue();
    ItemStack input = new ItemStack(tag.getCompoundTag("input"));
    ItemStack output = new ItemStack(tag.getCompoundTag("output"));
    int time = tag.getInteger("time") * 20;

    if(!input.isEmpty() && !output.isEmpty() && time > 0) {
      TinkerRegistry.registerDryingRecipe(input, output, time);
      log.debug("Added drying rack recipe from " + input.getUnlocalizedName() + " to " + output.getUnlocalizedName());
    }
    else if(input.isEmpty()) {
      // try oredict
      String ore = tag.getString("input");
      if(!ore.isEmpty()) {
        TinkerRegistry.registerDryingRecipe(ore, output, time);
        log.debug("Added drying rack recipe from oredictionary " + ore + " to " + output.getUnlocalizedName());
      }
    }
  }
}
