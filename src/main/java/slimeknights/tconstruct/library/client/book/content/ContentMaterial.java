package slimeknights.tconstruct.library.client.book.content;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.book.data.element.TextComponentData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.ItemElement;
import slimeknights.mantle.client.screen.book.element.TextComponentElement;
import slimeknights.mantle.client.screen.book.element.TextElement;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.client.book.elements.TinkerItemElement;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingLookup;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialFluidRecipe;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

public class ContentMaterial extends PageContent {
  private static final Component PART_BUILDER = TConstruct.makeTranslation("book", "material.part_builder");
  private static final String CAST_FROM = TConstruct.makeTranslationKey("book", "material.cast_from");
  private static final String COMPOSITE_FROM = TConstruct.makeTranslationKey("book", "material.composite_from");
  /** Page ID for using this index directly */
  public static final ResourceLocation ID = TConstruct.getResource("toolmaterial");

  // cached data
  private transient MaterialVariantId materialVariant;
  private transient List<ItemStack> repairStacks;
  private transient IMaterial material;

  @SerializedName("material")
  public String materialName;
  public boolean detailed;

  public ContentMaterial(MaterialVariantId materialVariant, boolean detailed) {
    this.materialName = materialVariant.toString();
    this.materialVariant = materialVariant;
    this.detailed = detailed;
  }

  /** Gets the material variant for this page */
  protected MaterialVariantId getMaterialVariant() {
    if (materialVariant == null) {
      materialVariant = MaterialVariantId.parse(materialName);
    }
    return materialVariant;
  }

  /** Gets the material for this page */
  protected IMaterial getMaterial() {
    if (material == null) {
      material = MaterialRegistry.getMaterial(getMaterialVariant().getId());
    }
    return material;
  }


  /** Gets a list of all repair items for the given material */
  protected List<ItemStack> getRepairStacks() {
    if (repairStacks == null) {
      Level world = Minecraft.getInstance().level;
      if (world == null) {
        return Collections.emptyList();
      }
      // simply combine all items from all recipes
      MaterialVariantId material = getMaterialVariant();
      repairStacks = RecipeHelper.getUIRecipes(world.getRecipeManager(), RecipeTypes.MATERIAL, MaterialRecipe.class, recipe -> material.matchesVariant(recipe.getMaterial()))
                                 .stream()
                                 .flatMap(recipe -> Arrays.stream(recipe.getIngredient().getItems()))
                                 .collect(Collectors.toList());
      // no repair items? use the repair kit
      if (repairStacks.isEmpty()) {
        TConstruct.LOG.debug("Material with id " + material + " has no representation items associated with it, using repair kit");
        // bypass the valid check, because we need to show something
        repairStacks = Collections.singletonList(TinkerToolParts.repairKit.get().withMaterialForDisplay(material));
      }
    }
    return repairStacks;
  }

  /** Gets the display stacks for this page */
  public List<ItemStack> getDisplayStacks() {
    return getRepairStacks();
  }

  @Nonnull
  @Override
  public String getTitle() {
    return getTitleComponent().getString();
  }

  /** Gets the title of this page to display in the index */
  public Component getTitleComponent() {
    return MaterialTooltipCache.getDisplayName(getMaterialVariant());
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    MaterialVariantId materialVariant = getMaterialVariant();
    this.addTitle(list, getTitle(), true, MaterialTooltipCache.getColor(materialVariant).getValue());

    // the cool tools to the left/right
    this.addDisplayItems(list, rightSide ? BookScreen.PAGE_WIDTH - 18 : 0, materialVariant);

    int col_margin = 22;
    int top = getTitleHeight();
    int left = rightSide ? 0 : col_margin;

    int y = top + 5;
    int x = left + 5;
    int w = BookScreen.PAGE_WIDTH / 2 - 5;

    // head stats
    MaterialId material = materialVariant.getId();
    int headTraits = this.addStatsDisplay(x, y, w, list, material, HeadMaterialStats.ID);
    // handle
    int handleTraits = this.addStatsDisplay(x + w, y, w - 10, list, material, HandleMaterialStats.ID);

    // extra
    y+= 65;
    this.addStatsDisplay(x, y + 10 * headTraits, w, list, material, ExtraMaterialStats.ID);

    // inspirational quote, or boring description text
    String textKey = String.format(detailed ? "material.%s.%s.encyclopedia" : "material.%s.%s.flavor", material.getNamespace(), material.getPath());
    if (I18n.exists(textKey)) {
      // using forge instead of I18n.format as that prevents % from being interpreted as a format key
      String translated = ForgeI18n.getPattern(textKey);
      if (!detailed) {
        translated = '"' + translated + '"';
      }
      TextData flavourData = new TextData(translated);
      flavourData.italic = !detailed;
      list.add(new TextElement(x + w - 16, y + 10 * handleTraits, w, 60, flavourData));
    }
  }

  /** Adds the stats for a stat type */
  protected int addStatsDisplay(int x, int y, int w, ArrayList<BookElement> list, MaterialId material, MaterialStatsId statsId) {
    Optional<IMaterialStats> stats = MaterialRegistry.getInstance().getMaterialStats(material, statsId);
    if (stats.isEmpty()) {
      return 0;
    }

    List<ModifierEntry> traits = MaterialRegistry.getInstance().getTraits(material, statsId);

    // create a list of all valid toolparts with the stats
    List<ItemStack> parts = Lists.newLinkedList();

    for (IToolPart part : getToolParts()) {
      if (part.getStatType() == statsId) {
        parts.add(part.withMaterial(material));
      }
    }

    // said parts next to the name
    int textOffset = 0;
    if (!parts.isEmpty()) {
      ItemElement display = new TinkerItemElement(x, y + 1, 0.5f, parts);
      list.add(display);
      textOffset = 10;
    }

    // and the name itself
    TextElement name = new TextElement(x + textOffset, y, w - textOffset, 10, stats.get().getLocalizedName().getString());
    name.text[0].bold = true;
    name.text[0].underlined = true;
    list.add(name);
    y += 12;

    List<TextComponentData> lineData = Lists.newArrayList();
    // add lines of tool information
    lineData.addAll(getStatLines(stats.get()));
    lineData.addAll(getTraitLines(traits));

    list.add(new TextComponentElement(x, y, w, BookScreen.PAGE_HEIGHT, lineData));

    return traits.size();
  }

  /** Gets all stat text data for the given stat instance */
  private static List<TextComponentData> getStatLines(IMaterialStats stats) {
    List<TextComponentData> lineData = new ArrayList<>();

    for (int i = 0; i < stats.getLocalizedInfo().size(); i++) {
      TextComponentData text = new TextComponentData(stats.getLocalizedInfo().get(i));
      if (stats.getLocalizedDescriptions().get(i).getString().isEmpty()) {
        text.tooltips = null;
      } else {
        text.tooltips = new Component[]{stats.getLocalizedDescriptions().get(i)};
      }

      lineData.add(text);
      lineData.add(new TextComponentData("\n"));
    }

    return lineData;
  }

  /** Gets all trait text data for the given stat instance */
  private static List<TextComponentData> getTraitLines(List<ModifierEntry> traits) {
    List<TextComponentData> lineData = new ArrayList<>();

    for (ModifierEntry trait : traits) {
      Modifier mod = trait.getModifier();
      TextComponentData textComponentData = new TextComponentData(mod.getDisplayName());

      List<Component> textComponents = mod.getDescriptionList(trait.getLevel());
      textComponentData.tooltips = textComponents.toArray(new Component[0]);
      textComponentData.text = textComponentData.text.copy().withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.UNDERLINE);

      lineData.add(textComponentData);
      lineData.add(new TextComponentData("\n"));
    }

    return lineData;
  }

  /** Checks if the given material has the given stat type */
  private static boolean hasStatType(MaterialId materialId, MaterialStatsId statsId) {
    return MaterialRegistry.getInstance().getMaterialStats(materialId, statsId).isPresent();
  }

  /** Gets the first material from the registry for the given stat type */
  private static IMaterial getFirstMaterialWithType(MaterialStatsId statsId) {
    for (IMaterial material : MaterialRegistry.getMaterials()) {
      if (hasStatType(material.getIdentifier(), statsId)) {
        return material;
      }
    }
    return IMaterial.UNKNOWN;
  }

  /** Returns true if this stat type is supported, anything unsupported is hidden from the tools list */
  protected boolean supportsStatType(MaterialStatsId statsId) {
    return statsId.equals(HeadMaterialStats.ID) || statsId.equals(HandleMaterialStats.ID) || statsId.equals(ExtraMaterialStats.ID);
  }

  /** Adds items to the display tools list for all relevant recipes */
  protected void addPrimaryDisplayItems(List<ItemElement> displayTools, MaterialVariantId materialId) {
    // part builder
    if (getMaterial().isCraftable()) {
      ItemStack partBuilder = new ItemStack(TinkerTables.partBuilder.asItem());
      ItemElement elementItem = new TinkerItemElement(partBuilder);
      elementItem.tooltip = ImmutableList.of(PART_BUILDER);
      displayTools.add(elementItem);
    }

    // regular casting recipes
    List<MaterialFluidRecipe> fluids = MaterialCastingLookup.getCastingFluids(materialId);
    if (!fluids.isEmpty()) {
      ItemElement elementItem = new TinkerItemElement(0, 0, 1, fluids.stream().flatMap(recipe -> recipe.getFluids().stream())
                                                                     .map(fluid -> new ItemStack(fluid.getFluid().getBucket()))
                                                                     .collect(Collectors.toList()));
      FluidStack firstFluid = fluids.stream()
                                    .flatMap(recipe -> recipe.getFluids().stream())
                                    .findFirst().orElse(FluidStack.EMPTY);
      elementItem.tooltip = ImmutableList.of(new TranslatableComponent(CAST_FROM, firstFluid.getFluid().getAttributes().getDisplayName(firstFluid)));
      displayTools.add(elementItem);
    }

    // composite casting
    List<MaterialFluidRecipe> composites = MaterialCastingLookup.getCompositeFluids(materialId);
    for (MaterialFluidRecipe composite : composites) {
      MaterialVariant input = composite.getInput();
      if (input != null) {
        MaterialVariantId inputId = input.getVariant();
        ItemElement elementItem = new TinkerItemElement(0, 0, 1, MaterialCastingLookup.getAllItemCosts().stream()
                                                                                      .map(Entry::getKey)
                                                                                      .filter(part -> part.canUseMaterial(inputId.getId()) && part.canUseMaterial(material))
                                                                                      .map(part -> part.withMaterial(inputId))
                                                                                      .collect(Collectors.toList()));
        FluidStack firstFluid = composite.getFluids().stream().findFirst().orElse(FluidStack.EMPTY);
        elementItem.tooltip = ImmutableList.of(new TranslatableComponent(COMPOSITE_FROM, firstFluid.getFluid().getAttributes().getDisplayName(firstFluid), MaterialTooltipCache.getDisplayName(inputId)));
        displayTools.add(elementItem);
      }
    }
  }

  /** Adds display items to the tool sidebars */
  protected void addDisplayItems(ArrayList<BookElement> list, int x, MaterialVariantId materialVariant) {
    List<ItemElement> displayTools = Lists.newArrayList();

    // add display items
    displayTools.add(new TinkerItemElement(0, 0, 1f, getRepairStacks()));
    addPrimaryDisplayItems(displayTools, materialVariant);

    // fill in leftover space
    if (displayTools.size() < 9) {
      MaterialId materialId = materialVariant.getId();
      toolLoop:
      for (Item item : TinkerTags.Items.MULTIPART_TOOL.getValues()) {
        if (item instanceof IModifiable tool) {
          List<PartRequirement> requirements = tool.getToolDefinition().getData().getParts();
          // start building the tool with the given material
          MaterialNBT.Builder materials = MaterialNBT.builder();
          boolean usedMaterial = false;
          for (PartRequirement part : requirements) {
            // if any stat type of the tool is not supported by this page, skip the whole tool
            if (!supportsStatType(part.getStatType())) {
              continue toolLoop;
            }
            // if the stat type is not supported by the material, substitute
            if (hasStatType(materialId, part.getStatType())) {
              materials.add(materialVariant);
              usedMaterial = true;
            } else {
              materials.add(getFirstMaterialWithType(part.getStatType()));
            }
          }

          // only add a stack if our material showed up
          if (usedMaterial) {
            ItemStack display = ToolBuildHandler.buildItemFromMaterials(tool, materials.build());
            displayTools.add(new TinkerItemElement(display));
            if (displayTools.size() == 9) {
              break;
            }
          }
        }
      }
    }

    // built tools
    if (!displayTools.isEmpty()) {
      int y = getTitleHeight() - 5;
      for (ItemElement element : displayTools) {
        element.x = x;
        element.y = y;
        element.scale = 1f;
        y += ItemElement.ITEM_SIZE_HARDCODED;

        list.add(element);
      }
    }
  }

  /** Gets a list of all tool parts */
  private List<IToolPart> getToolParts() {
    return TinkerTags.Items.TOOL_PARTS.getValues().stream()
                                      .filter(item -> item instanceof IToolPart)
                                      .map(item -> (IToolPart) item)
                                      .collect(Collectors.toList());
  }
}
