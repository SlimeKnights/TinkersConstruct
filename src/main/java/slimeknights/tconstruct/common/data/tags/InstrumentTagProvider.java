package slimeknights.tconstruct.common.data.tags;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.InstrumentTagsProvider;
import net.minecraft.world.item.Instruments;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;

import java.util.concurrent.CompletableFuture;

public class InstrumentTagProvider extends InstrumentTagsProvider {
  public InstrumentTagProvider(PackOutput output, CompletableFuture<Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
    super(output, provider, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags(Provider pProvider) {
    this.tag(TinkerTags.Instruments.VARIANT_HORNS).add(
      Instruments.PONDER_GOAT_HORN, Instruments.SING_GOAT_HORN, Instruments.SEEK_GOAT_HORN, Instruments.FEEL_GOAT_HORN,
      Instruments.ADMIRE_GOAT_HORN, Instruments.CALL_GOAT_HORN, Instruments.YEARN_GOAT_HORN, Instruments.DREAM_GOAT_HORN
    );
  }
}
