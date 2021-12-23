package slimeknights.tconstruct;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.oredict.OreDictionary;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import slimeknights.tconstruct.library.MaterialIntegration;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public abstract class IMCIntegration {

  static final Logger log = Util.getLogger("IMC");
  private static List<FMLInterModComms.IMCMessage> earlyMessages = new LinkedList<>();

  private IMCIntegration() {}

  /**
   * Handles IMCs that must be done by the recipe register event
   */
  public static void integrateSmeltery() {
    for(FMLInterModComms.IMCMessage message : FMLInterModComms.fetchRuntimeMessages(TConstruct.instance)) {
      if(message.key.equals("integrateSmeltery")) {
        try {
          IMCIntegration.integrateSmeltery(message);
        } catch(ClassCastException e) {
          log.error("Got invalid integrateSmeltery IMC from {}", message.getSender());
        }
      } else {
        // store the message to process later
        earlyMessages.add(message);
      }
    }
  }

  /**
   * Handles main IMCs
   */
  public static void handleIMC(FMLInterModComms.IMCEvent event) {
    handleMessages(earlyMessages);
    earlyMessages.clear();
    handleMessages(event.getMessages());
  }

  /**
   * Handles main IMCs
   */
  private static void handleMessages(List<FMLInterModComms.IMCMessage> messages) {
    for(FMLInterModComms.IMCMessage message : messages) {
      try {
        switch(message.key) {
          case "integrateSmeltery":
            log.error("Received integrateSmetery IMC from {} too late, must be sent during Register<Item> at latest!", message.getSender());
            break;
          case "alloy":
            IMCIntegration.alloy(message);
            break;
          case "blacklistMelting":
            IMCIntegration.blacklistMelting(message);
            break;
          case "addDryingRecipe":
            IMCIntegration.addDryingRecipe(message);
            break;
          case "addHeadDrop":
            IMCIntegration.addHeadDrop(message);
            break;
          default:
            log.error("Got invalid IMC type {} from {}", message.key, message.getSender());
        }
      } catch(ClassCastException e) {
        log.error("Got invalid {} IMC from {}", message.key, message.getSender());
      }
    }
  }

  /**
   * Integrates oredictionary recipes for a material in the smeltery. Must be called by the item register event at lastest
   * @param message  NBT IMC message containing the following tags:
   * <ul>
   * <li><b>fluid:</b> String name of fluid for this material</li>
   * <li><b>ore:</b> String oredict suffix, e.g. for iron like ingotIron, use "Iron"</li>
   * <li><b>toolforge:</b> Boolean value, if true adds a toolforge for this metal</li>
   * <li><b>alloy:</b> List of fluids used in alloying this fluid, first fluid is output, the rest inputs
   * </ul>
   */
  protected static void integrateSmeltery(FMLInterModComms.IMCMessage message) {
    if(!message.isNBTMessage()) {
      log.error("Got invalid integrateSmeltery IMC from {}, expected NBT message", message.getSender());
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

    alloy(message);
  }

  /**
   * Adds an alloy recipe to the smeltery
   * @param message  NBT IMC message containing the following tags:
   * <ul>
   * <li><b>alloy:</b> List of fluids for an alloy, first tag in list is the output, any after are inputs
   * </ul>
   */
  protected static void alloy(FMLInterModComms.IMCMessage message) {
    if(!message.isNBTMessage()) {
      log.error("Got invalid alloy IMC from {}, expected NBT message", message.getSender());
      return;
    }

    // ensure it has the alloy tag
    NBTTagCompound tags = message.getNBTValue();
    if(tags.hasKey("alloy", 9)) {
      // load fluids from NBT
      NBTTagList list = tags.getTagList("alloy", 10);
      List<FluidStack> fluids = Lists.newLinkedList();
      for(int i = 0; i < list.tagCount(); i++) {
        NBTTagCompound tag = list.getCompoundTagAt(i);
        FluidStack fs = FluidStack.loadFluidStackFromNBT(tag);
        // fail if the fluid is invalid
        if(fs == null) {
          log.error("Invalid alloy fluid in {} IMC", message.key);
          return;
        }
        fluids.add(fs);
      }

      // needs at least 3 fluids
      if(fluids.size() > 2) {
        // first output, other two inputs
        FluidStack output = fluids.get(0);
        FluidStack[] input = new FluidStack[fluids.size() - 1];
        input = fluids.subList(1, fluids.size()).toArray(input);
        TinkerRegistry.registerAlloy(output, input);
        log.debug("Added integration alloy: " + output.getLocalizedName());
      } else {
        log.error("Not enough alloy fluids in {} IMC, expected at least 3, got {}", message.key, fluids.size());
      }
    // don't error if given an integrateSmeltery message, alloy is optional there
    } else if(message.key.equals("alloy")) {
      log.error("Missing alloys for alloy IMC message from {}", message.getSender());
    }
  }

  /* IMC Message types */

  /**
   * Blacklists an item from being melted in the smeltery during the automatic melting search
   * @param message  IMC message containing either a string (oredict name) or itemstack (normal item)
   */
  protected static void blacklistMelting(FMLInterModComms.IMCMessage message) {
    // oredict blacklist
    if(message.isStringMessage()) {
      TinkerSmeltery.meltingBlacklist.addAll(OreDictionary.getOres(message.getStringValue(), false));
      log.debug("Blacklisted oredictionary entry " + message.getStringValue() + " from melting");
    }
    else if(message.isItemStackMessage()) {
      TinkerSmeltery.meltingBlacklist.add(message.getItemStackValue());
      log.debug("Blacklisted " + message.getItemStackValue().getUnlocalizedName() + " from melting");
    } else {
      log.error("Got invalid blacklistMelting IMC from {}, expected string or ItemStack message", message.getSender());
    }
  }

  /**
   * Adds a drying rack recipe
   * @param message  NBT IMC message containing the following tags:
   * <ul>
   * <li><b>input:</b> Drying recipe input, can be either a string (oredict) or an NBTTagCompound ItemStack</li>
   * <li><b>output:</b> NBTTagCompound representing an itemstack for output</li>
   * <li><b>time:</b> Drying time in seconds</li>
   * </ul>
   */
  protected static void addDryingRecipe(FMLInterModComms.IMCMessage message) {
    if(!message.isNBTMessage()) {
      log.error("Got invalid addDryingRecipe IMC from {}, expected NBT message", message.getSender());
      return;
    }

    NBTTagCompound tag = message.getNBTValue();
    ItemStack output = new ItemStack(tag.getCompoundTag("output"));
    int time = tag.getInteger("time") * 20;

    // if two itemstacks
    if(!output.isEmpty() && time > 0) {
      if(tag.hasKey("input", 10)) {
        ItemStack input = new ItemStack(tag.getCompoundTag("input"));
        if(!input.isEmpty()) {
          TinkerRegistry.registerDryingRecipe(input, output, time);
          log.debug("Added drying rack recipe from " + input.getUnlocalizedName() + " to " + output.getUnlocalizedName());
        }
      } else {
        String ore = tag.getString("input");
        if(!ore.isEmpty()) {
          TinkerRegistry.registerDryingRecipe(ore, output, time);
          log.debug("Added drying rack recipe from oredictionary " + ore + " to " + output.getUnlocalizedName());
        } else {
          log.error("Got invalid addDryingRecipe IMC from {}, missing input, must be a string or ItemStack", message.getSender());
        }
      }
    } else {
      log.error("Got invalid addDryingRecipe IMC from {},output must not be empty and time greater than 0", message.getSender());
    }
  }

  /**
   * Adds a head drop for the cleaver
   * @param message  NBT IMC message containing the following tags:
   * <ul>
   * <li><b>entity:</b> Entity resource ID. For example, for creepers, the ID is "minecraft:creeper"</li>
   * <li><b>head:</b> NBTTagCompound representing an itemstack for the head</li>
   * </ul>
   */
  @SuppressWarnings("unchecked")
  protected static void addHeadDrop(FMLInterModComms.IMCMessage message) {
    if(!message.isNBTMessage()) {
      return;
    }

    // get head and entity
    NBTTagCompound tag = message.getNBTValue();
    Class<? extends Entity> clazz = EntityList.getClassFromName(tag.getString("entity"));
    ItemStack head = new ItemStack(tag.getCompoundTag("head"));

    // ensure its EntityLivingBase and we got a valid head
    if(clazz != null && EntityLivingBase.class.isAssignableFrom(clazz) && !head.isEmpty()) {
      TinkerRegistry.registerHeadDrop((Class<? extends EntityLivingBase>)clazz, (e) -> head);
    } else {
      log.error("Got invalid addHeadDrop IMC from {}, head must not be empty and entity must be EntityLivingBase", message.getSender());
    }
  }
}
