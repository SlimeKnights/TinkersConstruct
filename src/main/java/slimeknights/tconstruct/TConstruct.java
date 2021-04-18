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
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.shared.TinkerClient;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.TinkerWorld;

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
    TinkerNetwork.setup();

    MaterialRegistry.init();
  }

  public TConstruct() {
    instance = this;
  }
}
