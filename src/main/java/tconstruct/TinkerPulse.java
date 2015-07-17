package tconstruct;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import tconstruct.library.Util;
import tconstruct.library.tools.ToolPart;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.TinkerTools;

/**
 * Just a small helper class that provides some function for cleaner Pulses.
 *
 * Items should be registered during PreInit
 *
 * Models should be registered during Init
 */
// MANTLE
public abstract class TinkerPulse {

  protected static boolean isToolsLoaded() {
    return TConstruct.pulseManager.isPulseLoaded(TinkerTools.PulseId);
  }

  protected static boolean isSmelteryLoaded() {
    return TConstruct.pulseManager.isPulseLoaded(TinkerSmeltery.PulseId);
  }

  protected static ToolPart registerToolPart(String unlocName) {
    ToolPart part = new ToolPart();
    registerItem(part, unlocName);
    OreDictionary.registerOre(String.format("part%s", unlocName), part);

    return part;
  }

  /**
   * Sets the correct unlocalized name and registers the item.
   */
  protected static <T extends Item> T registerItem(T item, String unlocName) {
    if(!Character.isUpperCase(unlocName.charAt(0))) {
      TConstruct.log.warn("Unlocalized name {} should start with upper case!", unlocName);
    }

    item.setUnlocalizedName(Util.prefix(unlocName));
    GameRegistry.registerItem(item, unlocName);
    return item;
  }

  protected static <T extends Block> T registerBlock(T block, String unlocName) {
    if(!Character.isUpperCase(unlocName.charAt(0))) {
      TConstruct.log.warn("Unlocalized name {} should start with upper case!", unlocName);
    }

    block.setUnlocalizedName(Util.prefix(unlocName));
    GameRegistry.registerBlock(block, unlocName);
    return block;
  }

  protected static <T extends Block> T registerBlock(T block,
                                                     Class<? extends ItemBlock> itemBlockClazz,
                                                     String unlocName, Object... itemCtorArgs) {
    if(!Character.isUpperCase(unlocName.charAt(0))) {
      TConstruct.log.warn("Unlocalized name {} should start with upper case!", unlocName);
    }

    block.setUnlocalizedName(Util.prefix(unlocName));
    GameRegistry.registerBlock(block, itemBlockClazz, unlocName, itemCtorArgs);
    return block;
  }

  protected static void registerTE(Class<? extends TileEntity> teClazz, String name) {
    GameRegistry.registerTileEntity(teClazz, Util.prefix(name));
  }
}
