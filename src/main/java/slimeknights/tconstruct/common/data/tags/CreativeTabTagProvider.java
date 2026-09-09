package slimeknights.tconstruct.common.data.tags;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.mantle.data.BuiltinRegistryTagProvider;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class CreativeTabTagProvider extends BuiltinRegistryTagProvider<CreativeModeTab> {
  public CreativeTabTagProvider(PackOutput packOutput, CompletableFuture<Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
    super(packOutput, BuiltInRegistries.CREATIVE_MODE_TAB, lookupProvider, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags(Provider provider) {
    this.tag(TinkerTags.CreativeTabs.HIDDEN_IN_RECIPE_VIEWERS).add(TinkerTables.tabTables.get(), TinkerFluids.tabFluids.get());
  }
}
