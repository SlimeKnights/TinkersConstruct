package slimeknights.tconstruct;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.config.TConfig;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeGrassBlock.FoliageType;

import java.util.Random;

/**
 * TConstruct, the tool mod. Craft your tools with style, then modify until the original is gone!
 *
 * @author mDiyo
 */

public class TConstruct implements ModInitializer {

  public static final String modID = Util.MODID;

  public static final Logger log = LogManager.getLogger(modID);
  public static final Random random = new Random();

  /* Instance of this mod, used for grabbing prototype fields */
  public static TConstruct instance;

  @Override
  public void onInitialize() {
    AutoConfig.register(TConfig.Common.class, GsonConfigSerializer::new);
    AutoConfig.register(TConfig.Client.class, GsonConfigSerializer::new);

    TConfig.common = AutoConfig.getConfigHolder(TConfig.Common.class).getConfig();
    TConfig.client = AutoConfig.getConfigHolder(TConfig.Client.class).getConfig();

    // init deferred registers
    TinkerModule.initRegisters();

    MaterialRegistry.init();
  }

  public TConstruct() {
    instance = this;
  }

  @Nullable
  private static Block missingBlock(String name) {
    switch (name) {
      // slimy mud
      case "slimy_mud_green": return TinkerWorld.congealedSlime.get(SlimeType.EARTH);
      case "slimy_mud_blue": return TinkerWorld.congealedSlime.get(SlimeType.SKY);
      case "slimy_mud_magma": return TinkerWorld.congealedSlime.get(SlimeType.ICHOR);
      // ardite
      case "ardite_ore": return TinkerWorld.cobaltOre.get();
      case "ardite_block": return TinkerMaterials.cobalt.get();
      case "molten_ardite_fluid": return TinkerFluids.moltenCobalt.getBlock();
      // slime vine rename
      case "blue_slime_vine": return TinkerWorld.skySlimeVine.get();
      case "purple_slime_vine": return TinkerWorld.enderSlimeVine.get();
      case "green_slime_fluid": return TinkerFluids.earthSlime.getBlock();
      case "blue_slime_fluid": return TinkerFluids.skySlime.getBlock();
      case "purple_slime_fluid": return TinkerFluids.enderSlime.getBlock();
      // pig iron underscore
      case "pigiron_block": return TinkerMaterials.pigIron.get();
      // soils
      case "graveyard_soil": case "consecrated_soil": return Blocks.DIRT;
      // firewood to blazewood
      case "firewood": return TinkerCommons.blazewood.get();
      case "firewood_slab": return TinkerCommons.blazewood.getSlab();
      case "firewood_stairs": return TinkerCommons.blazewood.getStairs();
    }
    // other slime changes:
    // green -> earth
    // blue -> sky
    // magma -> ichor
    // purple -> ender
    for (SlimeType type : SlimeType.TRUE_SLIME) {
      String typeName = type.getOriginalName();
      if (name.equals(typeName + "_slime")) return TinkerWorld.slime.get(type);
      if (name.equals(typeName + "_congealed_slime")) return TinkerWorld.congealedSlime.get(type);
      if (name.equals(typeName + "_slime_dirt")) return TinkerWorld.allDirt.get(type);
    }
    for (FoliageType foliage : FoliageType.ORIGINAL) {
      String foliageName = foliage.getOriginalName();
      if (name.equals(foliageName + "_slime_fern")) return TinkerWorld.slimeFern.get(foliage);
      if (name.equals(foliageName + "_slime_tall_grass")) return TinkerWorld.slimeTallGrass.get(foliage);
      if (name.equals(foliageName + "_slime_sapling")) return TinkerWorld.slimeSapling.get(foliage);
      if (name.equals(foliageName + "_slime_leaves")) return TinkerWorld.slimeLeaves.get(foliage);
      // note blood is included in the loop, blood = vanilla here
      for (SlimeType type : SlimeType.values()) {
        if (name.equals(foliageName + "_" + type.getOriginalName() + "_slime_grass")) {
          return TinkerWorld.slimeGrass.get(type).get(foliage);
        }
      }
    }
    return null;
  }
}
