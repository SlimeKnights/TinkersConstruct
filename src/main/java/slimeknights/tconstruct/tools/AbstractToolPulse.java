package slimeknights.tconstruct.tools;

import com.google.common.collect.Lists;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.tools.IPattern;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolPart;

/**
 * Parent pulse for all the pulses that add tinker tools.
 * So you don't forget anything and we can simplify a few tasks
 *
 * Attention Addon-Developers: If you're looking at this.. you can't use it.
 * All your stuff will run after TiC has already registered everything.
 */
public abstract class AbstractToolPulse extends TinkerPulse {

  // Helper stuff
  static List<ToolCore> tools = Lists.newLinkedList();      // contains all tools registered in this pulse
  static List<ToolPart> toolparts = Lists.newLinkedList();  // ^ all toolparts
  static List<IModifier> modifiers = Lists.newLinkedList(); // ^ all modifiers
  static List<Pair<Item, ToolPart>> toolPartPatterns = Lists.newLinkedList();

  // PRE-INITIALIZATION
  public void preInit(FMLPreInitializationEvent event) {
    registerToolParts();
    registerTools();
  }

  protected void registerToolParts() {}

  protected void registerTools() {}


  // INITIALIZATION
  public void init(FMLInitializationEvent event) {
    registerToolBuilding();
    registerRecipies();
  }

  protected void registerToolBuilding() {}

  protected void registerRecipies() {}


  // POST-INITIALIZATION
  public void postInit(FMLPostInitializationEvent event) {
    registerEventHandlers();
  }

  protected void registerEventHandlers() {}

  // HELPER FUNCTIONS

  protected static <T extends ToolCore> T registerTool(T item, String unlocName) {
    tools.add(item);
    return registerItem(item, unlocName);
  }

  protected ToolPart registerToolPart(ToolPart part, String name) {
    return registerToolPart(part, name, TinkerTools.pattern);
  }

  protected <T extends Item & IPattern> ToolPart registerToolPart(ToolPart part, String name, T pattern) {
    ToolPart ret = registerItem(part, name);

    if(pattern != null) {
      toolPartPatterns.add(Pair.<Item, ToolPart>of(pattern, ret));
    }

    toolparts.add(ret);

    return ret;
  }

  protected <T extends IModifier> T registerModifier(T modifier) {
    TinkerRegistry.registerModifier(modifier);
    modifiers.add(modifier);
    return modifier;
  }
}
