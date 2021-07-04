package slimeknights.tconstruct.common.data.tags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.TagsProvider;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.nio.file.Path;

public class TileEntityTypeTagProvider extends TagsProvider<TileEntityType<?>> {
  public TileEntityTypeTagProvider(DataGenerator generatorIn, @Nullable ExistingFileHelper existingFileHelper) {
    super(generatorIn, Registry.BLOCK_ENTITY_TYPE, TConstruct.MOD_ID, existingFileHelper, "tile_entity_types");
  }

  @Override
  protected void registerTags() {
    this.getOrCreateBuilder(TinkerTags.TileEntityTypes.CRAFTING_STATION_BLACKLIST)
        .add(TinkerTables.craftingStationTile.get(), TinkerTables.tinkerStationTile.get(), TinkerTables.partBuilderTile.get(),
             TinkerTables.partChestTile.get(), TinkerTables.modifierChestTile.get(), TinkerTables.castChestTile.get(),
             TinkerSmeltery.basin.get(), TinkerSmeltery.table.get(), TinkerSmeltery.smeltery.get());

  }

  @Override
  protected Path makePath(ResourceLocation id) {
    return this.generator.getOutputFolder().resolve("data/" + id.getNamespace() + "/tags/" + folder + "/" + id.getPath() + ".json");
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Tile Entity Type Tags";
  }
}
