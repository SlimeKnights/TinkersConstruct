package slimeknights.tconstruct.common.data.tags;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
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
    tag(TinkerTags.MenuTypes.TOOL_INVENTORY_REPLACEMENTS).add(TinkerTools.toolContainer.get());
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Menu Type Tags";
  }
}
