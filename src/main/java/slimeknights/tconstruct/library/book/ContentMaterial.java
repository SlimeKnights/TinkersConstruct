package slimeknights.tconstruct.library.book;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.gui.book.GuiBook;
import slimeknights.mantle.client.gui.book.element.BookElement;
import slimeknights.mantle.client.gui.book.element.ElementItem;
import slimeknights.mantle.client.gui.book.element.ElementText;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.BlockCasting;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.block.BlockToolTable;

@SideOnly(Side.CLIENT)
public class ContentMaterial extends TinkerPage {

  private transient Material material;
  public String[] types;
  @SerializedName("material")
  public String materialName;

  public ContentMaterial() {}

  public ContentMaterial(Material material, String... types) {
    this.material = material;
    this.types = types;
    this.materialName = material.getIdentifier();
  }

  @Override
  public void load() {
    if(material == null) {
      material = TinkerRegistry.getMaterial(materialName);
    }
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list) {
    addTitle(list, material.getLocalizedNameColored(), material.getRepresentativeItem(), material.getLocalizedName());

    List<ItemStack> displayTools = Lists.newArrayList();

    if(TinkerTools.pickaxe != null) {
      displayTools.add(TinkerTools.pickaxe.buildItem(ImmutableList.of(material, material, material)));
    }
    if(TinkerTools.broadSword != null) {
      displayTools.add(TinkerTools.broadSword.buildItem(ImmutableList.of(material, material, material)));
    }
    if(TinkerTools.hammer != null) {
      displayTools.add(TinkerTools.hammer.buildItem(ImmutableList.of(material, material, material, material)));
    }
    if(TinkerTools.cleaver != null) {
      displayTools.add(TinkerTools.cleaver.buildItem(ImmutableList.of(material, material, material, material)));
    }

    int y = 28;
    int x = 18;
    int w = GuiBook.PAGE_WIDTH/2 - 20 - 18;
    int x2 = x + w + 20;

    // built tools
    if(!displayTools.isEmpty()) {
      x = GuiBook.PAGE_WIDTH/2;
      x -= displayTools.size()*18/2;
      for(ItemStack stack : displayTools) {
        list.add(new ElementItem(x, y, 1f, stack));
        x += 18;
      }
    }

    y = 48;
    x = 18;

    LinkedHashSet<ITrait> allTraits = new LinkedHashSet<ITrait>();

    // left column
    TextData textStats = new TextData("Stats");
    textStats.underlined = true;
    textStats.scale = 1.2f;
    list.add(new ElementText(x+25, y, w, 15, textStats));
    y += 15;

    for(String type : types) {
      IMaterialStats stats = material.getStats(type);
      if(stats == null) {
        continue;
      }

      // save traits for processing later
      List<ITrait> traits = material.getAllTraitsForStats(type);
      allTraits.addAll(traits);

      List<ItemStack> parts = Lists.newLinkedList();
      for(IToolPart part : TinkerRegistry.getToolParts()) {
        if(part.hasUseForStat(stats.getIdentifier())) {
          parts.add(part.getItemstackWithMaterial(material));
        }
      }

      if(parts.size() > 0) {
        ElementItem display = new ElementItem(0, y, 1f, parts);
        list.add(display);
      }

      y += 5;
      ElementText name = new ElementText(x, y, w, 10, stats.getLocalizedName());
      name.text[0].underlined = true;
      list.add(name);
      y+= 12;
      for(int i = 0; i < stats.getLocalizedInfo().size(); i++) {
        TextData text = new TextData(stats.getLocalizedInfo().get(i));
        text.tooltip = new String[] {stats.getLocalizedDesc().get(i)};
        list.add(new ElementText(x, y, w, 10, text));
        y += 10;
      }

      if(traits.size() > 0) {
        y += 3;
      }
      List<TextData> traitText = Lists.newLinkedList();
      for(ITrait trait : traits) {
        if(!traitText.isEmpty()) {
          traitText.add(new TextData(", "));
        }
        TextData text = new TextData(trait.getLocalizedName());
        text.tooltip = Util.convertNewlines(trait.getLocalizedDesc()).split("\n");
        traitText.add(text);
      }
      if(!traitText.isEmpty()) {
        traitText.add(0, new TextData("Traits: "));
        list.add(new ElementText(x, y, w, 10, traitText));
        y += 10;
      }
      y += 7;
    }

    // right column
    y = 48;

    TextData textCrafting = new TextData("Crafting");
    textCrafting.underlined = true;
    textCrafting.scale = 1.2f;
    list.add(new ElementText(x2+35, y, w, 15, textCrafting));
    y += 15;

    // craftable/castable
    x = x2 + w/2;
    if(material.isCraftable()) {
      x -= 18/2;
    }
    if(material.isCastable()) {
      x -= 18/2;
    }
    if(material.isCraftable()) {
      ItemStack partbuilder = new ItemStack(TinkerTools.toolTables, 1, BlockToolTable.TableTypes.PartBuilder.meta);
      ElementItem elementItem = new ElementItem(x, y, 1.2f, partbuilder);
      elementItem.tooltip = ImmutableList.of("Can be crafted in the Part Builder");
      list.add(elementItem);
      x += 18;
    }
    if(material.isCastable()) {
      ItemStack basin = new ItemStack(TinkerSmeltery.castingBlock, 1, BlockCasting.CastingType.BASIN.getMeta());
      ElementItem elementItem = new ElementItem(x, y, 1.2f, basin);
      elementItem.tooltip = ImmutableList.of(String.format("Can be cast from %s", material.getFluid().getLocalizedName(new FluidStack(material.getFluid(), 0))));
      list.add(elementItem);
    }

    y += 25;
    TextData textTraits = new TextData("Traits");
    textTraits.underlined = true;
    textTraits.scale = 1.2f;
    list.add(new ElementText(x2+40, y, w, 15, textTraits));
    //y += 15;

    ElementText traitsElement = new ElementText(x2, y, w, GuiBook.PAGE_HEIGHT);
    List<TextData> textDatas = Lists.newArrayList();
    for(ITrait trait : allTraits) {
      TextData name = new TextData(trait.getLocalizedName());
      name.underlined = true;
      name.paragraph = true;
      textDatas.add(name);

      for(String s : Util.convertNewlines(trait.getLocalizedDesc()).split("\n")) {
        TextData desc = new TextData(s);
        desc.paragraph = true;
        textDatas.add(desc);
      }
    }

    traitsElement.text = textDatas.toArray(new TextData[textDatas.size()]);

    if(traitsElement.text.length > 0) {
      list.add(traitsElement);
    }
  }

  public static class Tool extends ContentMaterial {
    public static final String ID = "toolmaterial";

    public Tool(Material material) {
      super(material, HeadMaterialStats.TYPE, HandleMaterialStats.TYPE, ExtraMaterialStats.TYPE);
    }
  }
}
