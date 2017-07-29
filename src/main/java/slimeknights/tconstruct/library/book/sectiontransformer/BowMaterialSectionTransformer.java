package slimeknights.tconstruct.library.book.sectiontransformer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.gui.book.element.ElementImage;
import slimeknights.mantle.client.gui.book.element.ElementItem;
import slimeknights.mantle.client.gui.book.element.SizedBookElement;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.book.content.ContentListing;
import slimeknights.tconstruct.library.book.content.ContentPageIconList;
import slimeknights.tconstruct.library.book.content.ContentSingleStatMultMaterial;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;

/** Populates the materials section for tool materials with content */
@SideOnly(Side.CLIENT)
public class BowMaterialSectionTransformer extends SectionTransformer {

  private static final List<String> MATERIAL_TYPES_ON_DISPLAY = ImmutableList.of(
      MaterialTypes.BOW, MaterialTypes.BOWSTRING, MaterialTypes.SHAFT, MaterialTypes.FLETCHING
  );

  public BowMaterialSectionTransformer() {
    super("bowmaterials");
  }

  @Override
  public void transform(BookData book, SectionData data) {
    ContentListing listing = new ContentListing();
    listing.title = book.translate(sectionName);

    addPage(data, sectionName, "", listing);

    // don't do stuff during preinit etc, we only want to fill it once everything is added
    if(!Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION)) {
      return;
    }

    MATERIAL_TYPES_ON_DISPLAY.forEach(type -> {
      int pageIndex = data.pages.size();
      generateContent(type, data);
      if(pageIndex < data.pages.size()) {
        listing.addEntry(getStatName(type), data.pages.get(pageIndex));
      }
    });
  }

  protected String getStatName(String type) {
    return Material.UNKNOWN.getStats(type).getLocalizedName();
  }

  protected List<ContentPageIconList> generateContent(String materialType, SectionData data) {
    List<Material> materialList = TinkerRegistry.getAllMaterials().stream()
                                                .filter(m -> !m.isHidden())
                                                .filter(Material::hasItems)
                                                .filter(material -> material.hasStats(materialType))
                                                .collect(Collectors.toList());

    if(materialList.size() == 0) {
      return ImmutableList.of();
    }

    List<ContentPageIconList> contentPages = ContentPageIconList.getPagesNeededForItemCount(materialList.size(), data, getStatName(materialType));
    ListIterator<ContentPageIconList> iter = contentPages.listIterator();
    ContentPageIconList currentOverview = iter.next();

    // we want all the same, because it looks really weird otherwise :I
    contentPages.forEach(p -> p.maxScale = 1f);

    for(List<Material> materials : Lists.partition(materialList, 3)) {
      ContentSingleStatMultMaterial content = new ContentSingleStatMultMaterial(materials, materialType);
      String id = materialType + "_" + materials.stream().map(Material::getIdentifier).collect(Collectors.joining("_"));
      PageData page = addPage(data, id, ContentSingleStatMultMaterial.ID, content);

      for(Material material : materials) {
        SizedBookElement icon;
        if(material.getRepresentativeItem() != null) {
          icon = new ElementItem(0, 0, 1f, material.getRepresentativeItem());
        }
        else {
          icon = new ElementImage(ImageData.MISSING);
        }

        if(!currentOverview.addLink(icon, material.getLocalizedNameColored(), page)) {
          currentOverview = iter.next();
        }
      }
    }

    return contentPages;
  }
}
