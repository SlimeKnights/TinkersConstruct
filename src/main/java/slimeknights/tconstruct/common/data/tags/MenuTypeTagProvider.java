package slimeknights.tconstruct.common.data.tags;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.mantle.data.BuiltinRegistryTagProvider;
import slimeknights.mantle.datagen.MantleTags;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerTools;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class MenuTypeTagProvider extends BuiltinRegistryTagProvider<MenuType<?>> {
  @SuppressWarnings("deprecation")
  public MenuTypeTagProvider(PackOutput packOutput, CompletableFuture<Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
    super(packOutput, BuiltInRegistries.MENU, lookupProvider, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags(Provider provider) {
    tag(MantleTags.MenuTypes.REPLACEABLE).add(
      // tool inventory allows really nice switching behavior
      TinkerTools.toolContainer.get(),
      TinkerTables.tinkerChestContainer.get(), TinkerSmeltery.singleItemContainer.get(),
      // unlike crafting table, our containers have persistent inventory; no progress loss\
      TinkerTables.craftingStationContainer.get(), TinkerTables.partBuilderContainer.get(), TinkerTables.tinkerStationContainer.get(), TinkerTables.modifierWorktableContainer.get(),
      TinkerSmeltery.melterContainer.get(), TinkerSmeltery.alloyerContainer.get(), TinkerSmeltery.smelteryContainer.get()
    );
    tag(TinkerTags.MenuTypes.TOOL_INVENTORY_REPLACEMENTS).addTag(MantleTags.MenuTypes.REPLACEABLE);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Menu Type Tags";
  }
}
