package slimeknights.tconstruct.common.data.tags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.nio.file.Path;

public class TileEntityTypeTagProvider extends TagsProvider<BlockEntityType<?>> {
  public TileEntityTypeTagProvider(DataGenerator generatorIn, @Nullable ExistingFileHelper existingFileHelper) {
    super(generatorIn, Registry.BLOCK_ENTITY_TYPE, TConstruct.MOD_ID, existingFileHelper, "tile_entity_types");
  }

  @Override
  protected void addTags() {
    this.tag(TinkerTags.TileEntityTypes.CRAFTING_STATION_BLACKLIST)
        .add(TinkerTables.craftingStationTile.get(), TinkerTables.tinkerStationTile.get(), TinkerTables.partBuilderTile.get(),
						 TinkerTables.partChestTile.get(), TinkerTables.tinkersChestTile.get(), TinkerTables.castChestTile.get(),
						 TinkerSmeltery.basin.get(), TinkerSmeltery.table.get(), TinkerSmeltery.smeltery.get());

  }

  @Override
  protected Path getPath(ResourceLocation id) {
    return this.generator.getOutputFolder().resolve("data/" + id.getNamespace() + "/tags/" + folder + "/" + id.getPath() + ".json");
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Tile Entity Type Tags";
  }
}
