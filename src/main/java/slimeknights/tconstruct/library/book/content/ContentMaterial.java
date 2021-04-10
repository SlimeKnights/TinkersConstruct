package slimeknights.tconstruct.library.book.content;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.TextComponentData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.ItemElement;
import slimeknights.mantle.client.screen.book.element.TextComponentElement;
import slimeknights.mantle.client.screen.book.element.TextElement;
import slimeknights.mantle.util.ItemStackList;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.book.TinkerPage;
import slimeknights.tconstruct.library.book.elements.TinkerItemElement;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ContentMaterial extends TinkerPage {

  public static final String ID = "toolmaterial";

  private transient Lazy<IMaterial> material;
  @SerializedName("material")
  public String materialName;

  public ContentMaterial(IMaterial material) {
    this.material = Lazy.of(() -> material);
    this.materialName = material.getIdentifier().toString();
  }

  @Override
  public void load() {
    if (this.material == null) {
      this.material = Lazy.of(() -> MaterialRegistry.getMaterial(new MaterialId(this.materialName)));
    }
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    IMaterial material = this.material.get();

    this.addTitle(list, new TranslationTextComponent(material.getTranslationKey()).getString(), true, material.getColor().getColor());

    // the cool tools to the left/right
    this.addDisplayItems(list, rightSide ? BookScreen.PAGE_WIDTH - 18 : 0, material.getIdentifier());

    int col_margin = 22;
    int top = 15;
    int left = rightSide ? 0 : col_margin;

    int y = top + 10;
    int x = left + 10;
    int w = BookScreen.PAGE_WIDTH / 2 - 10;

    // head stats
    this.addStatsDisplay(x, y, w, list, material, HeadMaterialStats.ID);
    // handle
    this.addStatsDisplay(x + w, y, w - 10, list, material, HandleMaterialStats.ID);

    // extra
    y += 65 + 10 * material.getTraits().size();
    this.addStatsDisplay(x, y, w, list, material, ExtraMaterialStats.ID);

    // inspirational quote
    String flavour = parent.parent.parent.strings.get(String.format("%s.flavour", material.getIdentifier().getPath()));
    //flavour = "How much wood could a woodchuck chuck if a woodchuck could chuck wood?";
    if (flavour != null) {
      TextData flavourData = new TextData("\"" + flavour + "\"");
      flavourData.italic = true;
      list.add(new TextElement(x + w, y, w - 16, 60, flavourData));
    }
  }

  private void addStatsDisplay(int x, int y, int w, ArrayList<BookElement> list, IMaterial material, MaterialStatsId statsId) {
    Optional<IMaterialStats> stats = MaterialRegistry.getInstance().getMaterialStats(material.getIdentifier(), statsId);

    if (!stats.isPresent()) {
      return;
    }

    List<ModifierEntry> traits = material.getTraits();

    // create a list of all valid toolparts with the stats
    List<ItemStack> parts = Lists.newLinkedList();

    for (IToolPart part : getToolParts()) {
      if (part.getStatType() == statsId) {
        parts.add(part.getItemstackWithMaterial(material));
      }
    }

    // said parts next to the name
    if (parts.size() > 0) {
      ItemElement display = new TinkerItemElement(x, y + 1, 0.5f, parts);
      list.add(display);
    }

    // and the name itself
    TextElement name = new TextElement(x + 10, y, w - 10, 10, stats.get().getLocalizedName().getString());
    name.text[0].underlined = true;
    list.add(name);
    y += 12;

    List<TextComponentData> lineData = Lists.newArrayList();
    // add lines of tool information
    lineData.addAll(getStatLines(stats.get()));
    lineData.addAll(getTraitLines(traits, material));

    list.add(new TextComponentElement(x, y, w, BookScreen.PAGE_HEIGHT, lineData));
  }

  public static List<TextComponentData> getStatLines(IMaterialStats stats) {
    List<TextComponentData> lineData = new ArrayList<>();

    for (int i = 0; i < stats.getLocalizedInfo().size(); i++) {
      TextComponentData text = new TextComponentData(stats.getLocalizedInfo().get(i));
      text.tooltips = new ITextComponent[]{stats.getLocalizedDescriptions().get(i)};
      text.scale = 0.95F;
      lineData.add(text);
      lineData.add(new TextComponentData("\n"));
    }

    return lineData;
  }

  public static List<TextComponentData> getTraitLines(List<ModifierEntry> traits, IMaterial material) {
    List<TextComponentData> lineData = new ArrayList<>();

    for (ModifierEntry trait : traits) {
      Modifier mod = trait.getModifier();
      TextComponentData textComponentData = new TextComponentData(mod.getDisplayName());

      List<ITextComponent> textComponents = mod.getDescriptionList();
      List<IFormattableTextComponent> formatted = new ArrayList<>();

      for (ITextComponent textComponent : textComponents) {
        formatted.add(((IFormattableTextComponent) textComponent).modifyStyle(style -> style.setColor(material.getColor())));
      }

      textComponentData.tooltips = formatted.toArray(new ITextComponent[0]);
      textComponentData.text = textComponentData.text.deepCopy().mergeStyle(TextFormatting.GRAY).mergeStyle(TextFormatting.UNDERLINE);

      lineData.add(textComponentData);
      lineData.add(new TextComponentData("\n"));
    }

    return lineData;
  }

  private void addDisplayItems(ArrayList<BookElement> list, int x, MaterialId materialId) {
    List<ItemElement> displayTools = Lists.newArrayList();

    // representative item first
    displayTools.add(new TinkerItemElement(new ItemStack(Items.CHAIN)));

    if (material.get().isCraftable()) {
      ItemStack partBuilder = new ItemStack(TinkerTables.partBuilder.asItem());
      ItemElement elementItem = new TinkerItemElement(partBuilder);
      elementItem.tooltip = ImmutableList.of(new StringTextComponent(parent.translate("material.craft_partbuilder")));
      displayTools.add(elementItem);
    } else {
      ItemStack castingBasin = new ItemStack(TinkerSmeltery.castingBasin.asItem());
      ItemStack castingTable = new ItemStack(TinkerSmeltery.castingTable.asItem());

      ItemStackList stacks = ItemStackList.of(castingBasin, castingTable);

      ItemElement elementItem = new TinkerItemElement(0, 0, 1, stacks);
      String text = parent.translate("material.craft_casting");
      elementItem.tooltip = ImmutableList.of(new StringTextComponent(String.format(text, new TranslationTextComponent(material.get().getFluid().getRegistryName().toString()).getString())));
      displayTools.add(elementItem);
    }

    int y = 10;
    for (Item tool : TinkerTags.Items.MULTIPART_TOOL.getAllElements()) {
      if (tool instanceof ToolCore) {
        List<IToolPart> requirements = ((ToolCore)tool).getToolDefinition().getRequiredComponents();
        int size = requirements.size();
        List<MaterialId> toolMaterials = new ArrayList<>(size);

        for (int i = 0; i < requirements.size(); i++) {
          toolMaterials.add(i, materialId);
        }

        ItemStack stack = new MaterialIdNBT(toolMaterials).updateStack(new ItemStack(tool));
        displayTools.add(new TinkerItemElement(stack));

        if (displayTools.size() == 9) {
          break;
        }
      }
    }

    // built tools
    if (!displayTools.isEmpty()) {
      for (ItemElement element : displayTools) {
        element.x = x;
        element.y = y;
        element.scale = 1f;
        y += ItemElement.ITEM_SIZE_HARDCODED;

        list.add(element);
      }
    }
  }

  public List<IToolPart> getToolParts() {
    return ForgeRegistries.ITEMS.getValues().stream().filter(item -> item instanceof IToolPart).map(item -> (IToolPart) item).collect(Collectors.toList());
  }

}
