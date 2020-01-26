package slimeknights.tconstruct.library;

import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.client.CreativeTab;
import slimeknights.tconstruct.library.materials.MaterialManager;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsManager;
import slimeknights.tconstruct.library.traits.MaterialTraitsManager;

public final class TinkerRegistry {

  // the logger for the library
  public static final Logger log = Util.getLogger("API");

  public static void init() {
    MaterialRegistry.init(new MaterialRegistryImpl(new MaterialManager(), new MaterialStatsManager(), new MaterialTraitsManager()));
  }

  private TinkerRegistry() {
  }


  /*---------------------------------------------------------------------------
  | ITEM GROUPS                                                               |
  ---------------------------------------------------------------------------*/
  public static CreativeTab tabGeneral = new CreativeTab("tinkers_general", new ItemStack(Items.SLIME_BALL));
  public static CreativeTab tabTools = new CreativeTab("tinkers_tools", new ItemStack(Items.IRON_PICKAXE));
  public static CreativeTab tabParts = new CreativeTab("tinkers_tool_parts", new ItemStack(Items.STICK));
  public static CreativeTab tabSmeltery = new CreativeTab("tinkers_smeltery", new ItemStack(Item.getItemFromBlock(Blocks.STONE_BRICKS)));
  public static CreativeTab tabWorld = new CreativeTab("tinkers_world", new ItemStack(Item.getItemFromBlock(Blocks.SLIME_BLOCK)));
  public static CreativeTab tabGadgets = new CreativeTab("tinkers_gadgets", new ItemStack(Blocks.TNT));

  /*---------------------------------------------------------------------------
  | Traceability & Internal stuff                                             |
  ---------------------------------------------------------------------------*/
  private static void error(String message, Object... params) {
    throw new TinkerAPIException(String.format(message, params));
  }
}
