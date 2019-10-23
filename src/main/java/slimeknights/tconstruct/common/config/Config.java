package slimeknights.tconstruct.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import slimeknights.mantle.pulsar.config.PulsarConfig;

public class Config {

  public static PulsarConfig pulseConfig = new PulsarConfig("TinkerModules", "Modules");

  /**
   * Server specific configuration
   */
  public static class Server {

    public final ForgeConfigSpec.BooleanValue shouldSpawnWithTinkersBook;

    public final ForgeConfigSpec.BooleanValue addGravelToFlintRecipe;

    public final ForgeConfigSpec.BooleanValue requireSlimeballsToMatchInVanillaRecipe;

    public final ForgeConfigSpec.BooleanValue registerAllRecipes;

    public final ForgeConfigSpec.BooleanValue generateCobalt;
    public final ForgeConfigSpec.ConfigValue<Integer> veinCountCobalt;

    public final ForgeConfigSpec.BooleanValue generateArdite;
    public final ForgeConfigSpec.ConfigValue<Integer> veinCountArdite;

    public final ForgeConfigSpec.BooleanValue generateSlimeIslands;

    Server(ForgeConfigSpec.Builder builder) {
      builder.comment("Everything to do with gameplay").push("gameplay");

      this.shouldSpawnWithTinkersBook = builder
              .comment("Set this to false to disable new players spawning with the Tinkers' Book.")
              .translation("tconstruct.configgui.shouldSpawnWithTinkersBook")
              .worldRestart()
              .define("shouldSpawnWithTinkersBook", true);

      this.addGravelToFlintRecipe = builder
              .comment("Add a recipe that allows you to craft a piece of flint using 3 gravel")
              .translation("tconstruct.configgui.addGravelToFlintRecipe")
              .worldRestart()
              .define("addGravelToFlintRecipe", true);

      this.requireSlimeballsToMatchInVanillaRecipe = builder
              .comment("If true, requires slimeballs in the vanilla slimeblock recipe to match in color, otherwise gives a pink slimeblock")
              .translation("tconstruct.configgui.requireSlimeballsToMatchInVanillaRecipe")
              .worldRestart()
              .define("requireSlimeballsToMatchInVanillaRecipe", true);

      this.registerAllRecipes = builder
              .comment("If true, all recipes will be added even if the modules required are disabled.")
              .translation("tconstruct.configgui.registerAllRecipes")
              .worldRestart()
              .define("registerAllRecipes", false);

      builder.pop();

      builder.comment("Everything to do with world generation").push("worldgen");

      this.generateCobalt = builder
              .comment("Generate Cobalt")
              .translation("tconstruct.configgui.generateCobalt")
              .worldRestart()
              .define("generateCobalt", true);

      this.veinCountCobalt = builder
              .comment("Approx Ores per Chunk")
              .translation("tconstruct.configgui.veinCountCobalt")
              .worldRestart()
              .define("veinCountCobalt", 20);

      this.generateArdite = builder
              .comment("Generate Ardite")
              .translation("tconstruct.configgui.generateArdite")
              .worldRestart()
              .define("generateArdite", true);

      this.veinCountArdite = builder
              .comment("Approx Ores per Chunk")
              .translation("tconstruct.configgui.veinCountArdite")
              .worldRestart()
              .define("veinCountArdite", 20);

      this.generateSlimeIslands = builder
              .comment("Set this to false to disable slime islands spawning in the world")
              .translation("tconstruct.configgui.generateSlimeIslands")
              .worldRestart()
              .define("generateSlimeIslands", true);

      builder.pop();
    }
  }

  /**
   * Client specific configuration - only loaded clientside from tconstruct-client.toml
   */
  public static class Client {

    public final ForgeConfigSpec.BooleanValue renderInventoryInWorld;
    public final ForgeConfigSpec.BooleanValue temperatureInCelsius;

    Client(ForgeConfigSpec.Builder builder) {
      builder.comment("Client only settings").push("client");

      this.renderInventoryInWorld = builder
              .comment("If true all of Tinkers' blocks with contents (tables, basin, drying racks,...) will render their contents in the world")
              .translation("tconstruct.configgui.renderInventoryInWorld")
              .define("renderInventoryInWorld", true);

      this.temperatureInCelsius = builder
              .comment("If true, temperatures in the smeltery and in JEI will display in celsius. If false they will use the internal units of Kelvin, which may be better for developers")
              .translation("tconstruct.configgui.temperatureInCelsius")
              .define("temperatureInCelsius", true);

      builder.pop();
    }
  }

  public static final ForgeConfigSpec clientSpec;
  public static final Client CLIENT;

  static {
    final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
    clientSpec = specPair.getRight();
    CLIENT = specPair.getLeft();
  }

  public static final ForgeConfigSpec serverSpec;
  public static final Server SERVER;

  static {
    final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
    serverSpec = specPair.getRight();
    SERVER = specPair.getLeft();
  }

}
