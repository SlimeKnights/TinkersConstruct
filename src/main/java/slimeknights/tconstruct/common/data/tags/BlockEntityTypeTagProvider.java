package slimeknights.tconstruct.common.data.tags;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;

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

  /** Creates a RL for iron chests */
  private static void ironchest(IntrinsicTagAppender<BlockEntityType<?>> appender, String name) {
    ResourceLocation chest = new ResourceLocation("ironchest", name + "_chest");
    appender.addOptional(chest).addOptional(chest.withPrefix("trapped_"));
    if (!"dirt".equals(name)) {
      appender.addOptional(new ResourceLocation("ironshulkerbox", name + "_shulker_box"));
    }
  }

  @Override
  protected void addTags(Provider provider) {
    IntrinsicTagAppender<BlockEntityType<?>> sideInventories = tag(TinkerTags.TileEntityTypes.SIDE_INVENTORIES);
    sideInventories.add(
      BlockEntityType.CHEST, BlockEntityType.TRAPPED_CHEST, BlockEntityType.BARREL, BlockEntityType.SHULKER_BOX,
      BlockEntityType.DISPENSER, BlockEntityType.DROPPER, BlockEntityType.HOPPER);
    // TODO 1.21: verify if BlockEntityType.CHISELED_BOOKSHELF has fixed the bug where setItem(ItemStack.EMPTY) doesn't work so it can be whitelisted.
    sideInventories.addOptional(new ResourceLocation("immersiveengineering", "woodencrate"));
    ironchest(sideInventories, "iron");
    ironchest(sideInventories, "gold");
    ironchest(sideInventories, "diamond");
    ironchest(sideInventories, "copper");
    ironchest(sideInventories, "crystal");
    ironchest(sideInventories, "obsidian");
    ironchest(sideInventories, "dirt");
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Block Entity Type Tags";
  }
}
