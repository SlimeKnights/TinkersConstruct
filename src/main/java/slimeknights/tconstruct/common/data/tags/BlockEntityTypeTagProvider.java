package slimeknights.tconstruct.common.data.tags;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class BlockEntityTypeTagProvider extends IntrinsicHolderTagsProvider<BlockEntityType<?>> {
  @SuppressWarnings("deprecation")
  public BlockEntityTypeTagProvider(PackOutput packOutput, CompletableFuture<Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
    super(packOutput, Registries.BLOCK_ENTITY_TYPE, lookupProvider,
          // not sure why fetching the resource key from the object is such a pain
          type -> BuiltInRegistries.BLOCK_ENTITY_TYPE.getHolder(BuiltInRegistries.BLOCK_ENTITY_TYPE.getId(type)).orElseThrow().key(),
          TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags(Provider provider) {
    this.tag(TinkerTags.TileEntityTypes.CRAFTING_STATION_BLACKLIST)
        .add(
          BlockEntityType.FURNACE, BlockEntityType.BLAST_FURNACE, BlockEntityType.SMOKER, BlockEntityType.BREWING_STAND,
          TinkerTables.craftingStationTile.get(), TinkerTables.tinkerStationTile.get(), TinkerTables.partBuilderTile.get(),
          TinkerTables.partChestTile.get(), TinkerTables.tinkersChestTile.get(), TinkerTables.castChestTile.get(),
          TinkerSmeltery.basin.get(), TinkerSmeltery.table.get(),
          TinkerSmeltery.melter.get(), TinkerSmeltery.smeltery.get(), TinkerSmeltery.foundry.get()
        );

  }

  @Override
  public String getName() {
    return "Tinkers' Construct Block Entity Type Tags";
  }
}
