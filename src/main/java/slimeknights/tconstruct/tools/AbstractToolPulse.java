package slimeknights.tconstruct.tools;

import com.google.common.collect.Lists;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.registries.IForgeRegistry;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

import slimeknights.tconstruct.common.TinkerPulse;
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
  static List<ToolCore> tools = Lists.newLinkedList(); // contains all tools registered in this pulse
  static List<ToolPart> toolparts = Lists.newLinkedList(); // ^ all toolparts
  static List<IModifier> modifiers = Lists.newLinkedList(); // ^ all modifiers
  static List<Pair<Item, ToolPart>> toolPartPatterns = Lists.newLinkedList();

  public void registerItems(Register<Item> event) {
    IForgeRegistry<Item> registry = event.getRegistry();

    registerToolParts(registry);
    registerTools(registry);
  }

  protected void registerToolParts(IForgeRegistry<Item> registry) {
  }

  protected void registerTools(IForgeRegistry<Item> registry) {
  }

  // INITIALIZATION
  public void init(FMLInitializationEvent event) {
    registerToolBuilding();
  }

  protected void registerToolBuilding() {
  }

  // POST-INITIALIZATION
  public void postInit(FMLPostInitializationEvent event) {
    registerEventHandlers();
  }

  protected void registerEventHandlers() {
  }

  // HELPER FUNCTIONS

  protected static <T extends ToolCore> T registerTool(IForgeRegistry<Item> registry, T item, String unlocName) {
    tools.add(item);
    return registerItem(registry, item, unlocName);
  }

  protected ToolPart registerToolPart(IForgeRegistry<Item> registry, ToolPart part, String name) {
    return registerToolPart(registry, part, name, TinkerTools.pattern);
  }

  protected <T extends Item & IPattern> ToolPart registerToolPart(IForgeRegistry<Item> registry, ToolPart part, String name, T pattern) {
    ToolPart ret = registerItem(registry, part, name);

    if(pattern != null) {
      toolPartPatterns.add(Pair.of(pattern, ret));
    }

    toolparts.add(ret);

    return ret;
  }

  protected <T extends IModifier> T registerModifier(T modifier) {
    modifiers.add(modifier);
    return modifier;
  }
}
