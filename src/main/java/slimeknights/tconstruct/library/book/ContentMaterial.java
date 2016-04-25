package slimeknights.tconstruct.library.book;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.gui.book.GuiBook;
import slimeknights.mantle.client.gui.book.element.BookElement;
import slimeknights.mantle.client.gui.book.element.ElementImage;
import slimeknights.mantle.client.gui.book.element.ElementItem;
import slimeknights.mantle.client.gui.book.element.ElementText;
import slimeknights.mantle.client.gui.book.element.SizedBookElement;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolCore;
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
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    //GuiBook.PAGE_PADDING_LEFT = 10;
    addTitle(list, material.getLocalizedNameColored(), null, material.getLocalizedName());
/*
    list.add(new SizedBookElement(0, 0, GuiBook.PAGE_WIDTH, GuiBook.PAGE_HEIGHT) {
      @Override
      public void draw(int mouseX, int mouseY, float partialTicks, FontRenderer fontRenderer) {
        drawRect(x, y, width, height, 0xffff0000);
      }
    });*/

    // the cool tools to the left/right
    addDisplayItems(list, rightSide ? GuiBook.PAGE_WIDTH - 18 : 0);
    //addDisplayItems(book, list, !rightSide ? GuiBook.PAGE_WIDTH - 18 : 0);

    int col_margin = 22;
    int top = 15;
    int left = rightSide ? 0 : col_margin;

    int y = top + 10;
    int x = left + 10;
    int w = GuiBook.PAGE_WIDTH/2 - col_margin;

    LinkedHashSet<ITrait> allTraits = new LinkedHashSet<ITrait>();

    // head stats
    addStatsDisplay(x, y, w, list, allTraits, HeadMaterialStats.TYPE);
    addStatsDisplay(x+w+col_margin, y, w, list, allTraits, HandleMaterialStats.TYPE);

    y += 65 + 10 * material.getAllTraitsForStats(HeadMaterialStats.TYPE).size();
    addStatsDisplay(x, y, w, list, allTraits, ExtraMaterialStats.TYPE);
/*

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

      y += 5;
      if(parts.size() > 0) {
        ElementItem display = new ElementItem(x, y+1, 0.5f, parts);
        list.add(display);
      }

      ElementText name = new ElementText(x + 10, y, w, 10, stats.getLocalizedName());
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
    }*/
  }

  private void addStatsDisplay(int x, int y, int w, ArrayList<BookElement> list, LinkedHashSet<ITrait> allTraits, String stattype) {
    IMaterialStats stats = material.getStats(stattype);
    if(stats == null) {
      return;
    }

    List<ITrait> traits = material.getAllTraitsForStats(stats.getIdentifier());
    allTraits.addAll(traits);

    // create a list of all valid toolparts with the stats
    List<ItemStack> parts = Lists.newLinkedList();
    for(IToolPart part : TinkerRegistry.getToolParts()) {
      if(part.hasUseForStat(stats.getIdentifier())) {
        parts.add(part.getItemstackWithMaterial(material));
      }
    }

    // said parts next to the name
    if(parts.size() > 0) {
      ElementItem display = new ElementItem(x, y+1, 0.5f, parts);
      list.add(display);
    }

    // and the name itself
    ElementText name = new ElementText(x + 10, y, w-10, 10, stats.getLocalizedName());
    name.text[0].underlined = true;
    list.add(name);
    y+= 12;

    List<TextData> lineData = Lists.newArrayList();
    // add lines of tool information
    for(int i = 0; i < stats.getLocalizedInfo().size(); i++) {
      TextData text = new TextData(stats.getLocalizedInfo().get(i));
      text.tooltip = Util.convertNewlines(stats.getLocalizedDesc().get(i)).split("\n");
      lineData.add(text);
      lineData.add(new TextData("\n"));
      //list.add(new ElementText(x, y, w, 10, text));
      //y += 10;
    }

    if(traits.size() > 0) {
      //y += 3;
    }
    List<TextData> traitText = Lists.newLinkedList();
    for(ITrait trait : traits) {
      if(!traitText.isEmpty()) {
        //traitText.add(new TextData(", "));
      }
      TextData text = new TextData(trait.getLocalizedName());
      text.tooltip = Util.convertNewlines(trait.getLocalizedDesc()).split("\n");
      text.italic = true;
      traitText.add(text);
    }
    if(!traitText.isEmpty()) {
      //traitText.add(0, new TextData("Traits: "));
      //lineData.add(new TextData("Traits: "));
      //list.add(new ElementText(x, y, w, 35, traitText));
      for(TextData data : traitText) {
        lineData.add(data);
        lineData.add(new TextData("\n"));
      }
    }

    list.add(new ElementText(x, y, w, GuiBook.PAGE_HEIGHT, lineData));
  }

  private void addDisplayItems(ArrayList<BookElement> list, int x) {
    List<ElementItem> displayTools = Lists.newArrayList();

    int y = 10;

    // representative item first
    if(material.getRepresentativeItem() != null) {
      displayTools.add(new ElementItem(0, 0, 1, material.getRepresentativeItem()));
    }
    // then "craftability"
    if(material.isCraftable()) {
      ItemStack partbuilder = new ItemStack(TinkerTools.toolTables, 1, BlockToolTable.TableTypes.PartBuilder.meta);
      ElementItem elementItem = new ElementItem(0, 0, 1, partbuilder);
      elementItem.tooltip = ImmutableList.of("Can be crafted in the Part Builder");
      displayTools.add(elementItem);
    }
    if(material.isCastable()) {
      ItemStack basin = new ItemStack(TinkerSmeltery.castingBlock, 1, BlockCasting.CastingType.BASIN.getMeta());
      ElementItem elementItem = new ElementItem(0, 0, 1, basin);
      elementItem.tooltip = ImmutableList.of(String.format("Can be cast from %s", material.getFluid().getLocalizedName(new FluidStack(material.getFluid(), 0))));
      displayTools.add(elementItem);
    }

    // build a range of tools to fill the "bar" at the side
    ToolCore[] tools = new ToolCore[] {TinkerTools.pickaxe, TinkerTools.mattock, TinkerTools.broadSword,
                                       TinkerTools.hammer, TinkerTools.cleaver, TinkerTools.shuriken,
                                       TinkerTools.fryPan, TinkerTools.lumberAxe, TinkerTools.battleSign};

    for(ToolCore tool : tools) {
      if(tool == null) {
        continue;
      }
      ImmutableList.Builder<Material> builder = ImmutableList.builder();
      for(int i = 0; i < tool.getRequiredComponents().size(); i++) {
        builder.add(material);
      }
      ItemStack builtTool = tool.buildItem(builder.build());
      if(tool.hasValidMaterials(builtTool)) {
        displayTools.add(new ElementItem(0, 0, 1, builtTool));
      }

      if(displayTools.size() == 9) {
        break;
      }
    }

    // built tools
    if(!displayTools.isEmpty()) {
      for(ElementItem element : displayTools) {
        element.x = x;
        element.y = y;
        element.scale = 1f;
        y += ElementItem.ITEM_SIZE_HARDCODED;
        list.add(element);
      }
    }
  }

  public static class Tool extends ContentMaterial {
    public static final String ID = "toolmaterial";

    public Tool(Material material) {
      super(material, HeadMaterialStats.TYPE, HandleMaterialStats.TYPE, ExtraMaterialStats.TYPE);
    }
  }
}
