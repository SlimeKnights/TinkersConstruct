package slimeknights.tconstruct.common.data.tags;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerTools;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class MenuTypeTagProvider extends IntrinsicHolderTagsProvider<MenuType<?>> {
  @SuppressWarnings("deprecation")
  public MenuTypeTagProvider(PackOutput packOutput, CompletableFuture<Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
    super(packOutput, Registries.MENU, lookupProvider,
          // not sure why fetching the resource key from the object is such a pain
          type -> BuiltInRegistries.MENU.getHolder(BuiltInRegistries.MENU.getId(type)).orElseThrow().key(),
          TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags(Provider provider) {
    tag(TinkerTags.MenuTypes.TOOL_INVENTORY_REPLACEMENTS).add(
      // generic inventories are safe
      // anything with a notable UI component where you might lose progress (e.g. crafting table) is left out
      MenuType.GENERIC_9x1, MenuType.GENERIC_9x2, MenuType.GENERIC_9x3,
      MenuType.GENERIC_9x4, MenuType.GENERIC_9x5, MenuType.GENERIC_9x6,
      MenuType.SHULKER_BOX,
      MenuType.GENERIC_3x3, MenuType.HOPPER,
      MenuType.FURNACE, MenuType.BLAST_FURNACE, MenuType.SMOKER,
      MenuType.BREWING_STAND,
      // tool inventory allows really nice switching behavior
      TinkerTools.toolContainer.get(),
      TinkerTables.tinkerChestContainer.get(), TinkerSmeltery.singleItemContainer.get(),
      // unlike crafting table, our containers have persistent inventory; no progress loss\
      TinkerTables.craftingStationContainer.get(), TinkerTables.partBuilderContainer.get(), TinkerTables.tinkerStationContainer.get(), TinkerTables.modifierWorktableContainer.get(),
      TinkerSmeltery.melterContainer.get(), TinkerSmeltery.alloyerContainer.get(), TinkerSmeltery.smelteryContainer.get()
    );
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Menu Type Tags";
  }
}
