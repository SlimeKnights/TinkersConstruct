package slimeknights.tconstruct.library.book.content;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.gui.book.GuiBook;
import slimeknights.mantle.client.gui.book.element.BookElement;
import slimeknights.mantle.client.gui.book.element.ElementItem;
import slimeknights.mantle.client.gui.book.element.ElementText;
import slimeknights.mantle.util.LocUtils;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.book.TinkerPage;
import slimeknights.tconstruct.library.book.elements.ElementTinkerItem;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.BlockCasting;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.common.block.BlockToolTable;
import slimeknights.tconstruct.tools.harvest.TinkerHarvestTools;
import slimeknights.tconstruct.tools.melee.TinkerMeleeWeapons;
import slimeknights.tconstruct.tools.ranged.TinkerRangedWeapons;

@SideOnly(Side.CLIENT)
public class ContentSingleStatMultMaterial extends TinkerPage {

  public static final String ID = "single_stat_material";

  private transient List<Material> materials;
  @SerializedName("materials")
  public String[] materialNames;
  public String materialType;

  public ContentSingleStatMultMaterial(List<Material> materials, String materialType) {
    this.materials = ImmutableList.copyOf(materials);
    this.materialNames = materials.stream().map(Material::getIdentifier).toArray(String[]::new);
    this.materialType = materialType;
  }

  @Override
  public void load() {
    if(materials == null) {
      materials = Stream.of(materialNames).map(TinkerRegistry::getMaterial).collect(Collectors.toList());
    }
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    int yStep = GuiBook.PAGE_HEIGHT/3;

    for(int i = 0; i < materials.size(); i++) {
      Material material = materials.get(i);
      int y = yStep * i;

      addTitle(list, material.getLocalizedNameColored(), true, y);
      /*
      // the cool tools to the left/right
      addDisplayItems(list, material, rightSide ? GuiBook.PAGE_WIDTH - 18 : 0);
*/
      int col_margin = 22;
      int top = 15;
      int left = rightSide ? 0 : col_margin;

      y += 20;
      int x = left + 10;
      int w = GuiBook.PAGE_WIDTH / 2 - 10;

      LinkedHashSet<ITrait> allTraits = new LinkedHashSet<>();

      // head stats
      addStatsDisplay(x, y, w, list, material, allTraits, materialType);
    }
  }



  private void addStatsDisplay(int x, int y, int w, ArrayList<BookElement> list, Material material, LinkedHashSet<ITrait> allTraits, String stattype) {
    IMaterialStats stats = material.getStats(stattype);
    if(stats == null) {
      return;
    }

    int x1 = 10;
    int x2 = 30;
    int x3 = 120;


    List<ITrait> traits = material.getAllTraitsForStats(stats.getIdentifier());

    // create a list of all valid toolparts with the stats
    List<ItemStack> parts = Lists.newLinkedList();
    parts.addAll(TinkerRegistry.getToolParts().stream()
                               .filter(part -> part.hasUseForStat(stats.getIdentifier()))
                               .map(part -> part.getItemstackWithMaterial(material))
                               .collect(Collectors.toList()));

    // said parts next to the name
    if(parts.size() > 0) {
      ElementItem display = new ElementTinkerItem(x1, y + 1, 1f, parts);
      list.add(display);
    }

    List<TextData> lineData = ContentMaterial.getStatLines(stats);
    List<TextData> traitLineData = ContentMaterial.getTraitLines(traits, material);

    list.add(new ElementText(x2, y, w, GuiBook.PAGE_HEIGHT, lineData));
    if(!traitLineData.isEmpty()) {
      list.add(new ElementText(x3, y, w, GuiBook.PAGE_HEIGHT, traitLineData));
    }
  }
}
