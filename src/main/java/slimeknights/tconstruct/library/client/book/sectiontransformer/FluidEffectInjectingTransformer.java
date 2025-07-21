package slimeknights.tconstruct.library.client.book.sectiontransformer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.content.ContentPageIconList;
import slimeknights.mantle.client.book.data.content.ContentPageIconList.PageWithIcon;
import slimeknights.mantle.client.book.transformer.BookTransformer;
import slimeknights.mantle.client.screen.book.element.ItemElement;
import slimeknights.mantle.client.screen.book.element.SizedBookElement;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.book.content.FluidEffectContent;
import slimeknights.tconstruct.library.client.book.elements.FluidItemElement;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectManager;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffects;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Transformer adding fluid effect pages */
public class FluidEffectInjectingTransformer extends BookTransformer {
  public static final FluidEffectInjectingTransformer INSTANCE = new FluidEffectInjectingTransformer();
  private static final ResourceLocation KEY = TConstruct.getResource("fluid_effects");

  private FluidEffectInjectingTransformer() {}

  /** Populates the given section with the data */
  private void addPages(SectionData section, JsonElement element) {
    try {
      JsonObject json = GsonHelper.convertToJsonObject(element, KEY.toString());
      String path = GsonHelper.getAsString(json, "path");
      // so, originally I considered designing a super fancy setup for sorting all the fluids
      // however, such system either requires additional serverside data keys for one time use sort, or creating pages for every fluid
      // list in presorted order is just simpler; as addon stuff makes the most sense at the end anyway
      List<String> presorted = StringLoadable.DEFAULT.list(0).getOrDefault(json, "order", List.of());

      // setup the index
      List<FluidEffects.Entry> effects = FluidEffectManager.INSTANCE.getFluids();
      if (effects.isEmpty()) {
        return;
      }

      // sort the fluid effects
      Map<ResourceLocation, PageWithIcon> newPages = new HashMap<>();
      for (FluidEffects.Entry entry : effects) {
        FluidEffects effect = entry.effects();
        // skip hidden effects
        if (effect.hidden()) {
          continue;
        }
        // skip effects with no fluids - usually means empty tag for compat
        List<FluidStack> fluids = effect.ingredient().getFluids();
        if (fluids.isEmpty()) {
          return;
        }

        // start building the page
        ResourceLocation name = entry.name();
        PageData newPage = new PageData(true);
        newPage.parent = section;
        newPage.source = section.source;
        newPage.type = FluidEffectContent.ID;
        newPage.name = name.getNamespace() + "." + name.getPath();
        String data = path + "/" + name.getNamespace() + "_" + name.getPath() + ".json";

        // if the path exists load the page, otherwise use a fallback option
        if (section.source.resourceExists(section.source.getResourceLocation(data))) {
          newPage.data = data;
        } else {
          newPage.content = new FluidEffectContent();
        }
        newPage.load();

        // set fluid effect properties into the page
        List<ItemStack> displayStacks = FluidItemElement.createItemList(fluids);
        if (newPage.content instanceof FluidEffectContent fluidEffectContent) {
          fluidEffectContent.loadEffectData(name, effect, fluids, displayStacks);
        }

        // build the icon
        SizedBookElement icon = new ItemElement(0, 0, 1f, displayStacks);
        newPages.put(name, new PageWithIcon(icon, newPage));
      }

      // add the pages and the indexes
      List<ContentPageIconList> listPages = ContentPageIconList.getPagesNeededForItemCount(
        newPages.size(), section,
        section.parent.translate(section.name),
        section.parent.strings.get(String.format("%s.subtext", section.name)));


      // add each page from the requested sorted order
      List<PageWithIcon> sortedPages = new ArrayList<>();
      for (String name : presorted) {
        // since this feature is just for us, automatically prefix IDs
        // though for the sake of pack makers we allow other domains if they contain :
        ResourceLocation id = null;
        if (name.contains(":")) {
          id = ResourceLocation.tryParse(name);
        } else if (ResourceLocation.isValidPath(name)) {
          id = TConstruct.getResource(name);
        }
        if (id == null) {
          continue;
        }
        PageWithIcon page = newPages.get(id);
        if (page != null) {
          sortedPages.add(page);
          newPages.remove(id);
        }
      }
      // sort remaining pages by title
      sortedPages.addAll(newPages.values().stream().sorted(Comparator.comparing(pair -> pair.page().getTitle())).toList());

      // add all sorted pages
      ContentPageIconList.addPages(section, listPages, sortedPages);
    } catch (JsonParseException e) {
      TConstruct.LOG.error("Failed to parse tag for book page injecting", e);
    }
  }

  @Override
  public void transform(BookData book) {
    for (SectionData section : book.sections) {
      JsonElement element = section.extraData.get(KEY);
      if (element != null) {
        addPages(section, element);
      }
    }
  }
}
