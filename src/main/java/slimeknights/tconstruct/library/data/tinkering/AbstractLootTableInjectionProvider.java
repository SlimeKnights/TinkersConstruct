package slimeknights.tconstruct.library.data.tinkering;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.tconstruct.library.loot.LootTableInjection;
import slimeknights.tconstruct.library.loot.LootTableInjector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Data provider for adding new loot table injections */
public abstract class AbstractLootTableInjectionProvider extends GenericDataProvider {
  private final List<Builder> builders = new ArrayList<>();
  private final String domain;

  public AbstractLootTableInjectionProvider(DataGenerator generator, String domain) {
    super(generator, PackType.SERVER_DATA, LootTableInjector.FOLDER);
    this.domain = domain;
  }

  /** Method to add all relevant tables */
  protected abstract void addTables();

  @Override
  public final void run(CachedOutput output) throws IOException {
    addTables();
    // add all builders to the output
    for (Builder builder : builders) {
      JsonObject json = LootTableInjection.LOADABLE.serialize(builder.build()).getAsJsonObject();
      if (builder.conditions.length > 0) {
        json.add("conditions", CraftingHelper.serialize(builder.conditions));
      }
      saveJson(output, new ResourceLocation(domain, builder.path), json);
    }
  }

  /** Creates a new injection */
  protected LootTableInjection.Builder inject(String path, ResourceLocation name, ICondition... conditions) {
    LootTableInjection.Builder builder = new LootTableInjection.Builder();
    builders.add(new Builder(path, name, builder, conditions));
    return builder;
  }

  /** Creates a new injection for the Minecraft domain */
  protected LootTableInjection.Builder inject(String path, String name, ICondition... conditions) {
    return inject(path, new ResourceLocation(name), conditions);
  }

  /** Internal builder tuple */
  private record Builder(String path, ResourceLocation name, LootTableInjection.Builder builder, ICondition[] conditions) {
    public LootTableInjection build() {
      return builder.build(name);
    }
  }
}
