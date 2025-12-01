package slimeknights.tconstruct.common.data.tags;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.mantle.data.BuiltinRegistryTagProvider;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class PotionTagProvider extends BuiltinRegistryTagProvider<Potion> {
  @SuppressWarnings("deprecation")
  public PotionTagProvider(PackOutput packOutput, CompletableFuture<Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
    super(packOutput, BuiltInRegistries.POTION, lookupProvider, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags(Provider provider) {
    tag(TinkerTags.Potions.HIDDEN_FLUID).addOptional(TinkerTags.HIDDEN_FROM_RECIPE_VIEWERS);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Potion Tags";
  }
}
