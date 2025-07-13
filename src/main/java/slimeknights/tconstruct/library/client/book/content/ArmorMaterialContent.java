package slimeknights.tconstruct.library.client.book.content;

import com.google.common.collect.Lists;
import joptsimple.internal.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.TextComponentData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.TextComponentElement;
import slimeknights.mantle.client.screen.book.element.TextElement;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.book.elements.TinkerItemElement;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.json.variable.ToFloatFunction;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.stats.PlatingMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static slimeknights.tconstruct.tools.stats.PlatingMaterialStats.BOOTS;
import static slimeknights.tconstruct.tools.stats.PlatingMaterialStats.CHESTPLATE;
import static slimeknights.tconstruct.tools.stats.PlatingMaterialStats.HELMET;
import static slimeknights.tconstruct.tools.stats.PlatingMaterialStats.LEGGINGS;
import static slimeknights.tconstruct.tools.stats.PlatingMaterialStats.SHIELD;

/**
 * Content page for armor materials
 */
public class ArmorMaterialContent extends AbstractMaterialContent {
  /** Page ID for using this index directly */
  public static final ResourceLocation ID = TConstruct.getResource("armor_material");
  /** Supported stat type set */
  private static final Set<MaterialStatsId> SUPPORTED = Stream.concat(
    PlatingMaterialStats.TYPES.stream().map(MaterialStatType::getId),
    Stream.of(StatlessMaterialStats.MAILLE, StatlessMaterialStats.SHIELD_CORE, StatlessMaterialStats.CUIRASS).map(IMaterialStats::getIdentifier)
  ).collect(Collectors.toSet());
  /** Plating stat types in top down order */
  private static final List<MaterialStatsId> TOP_DOWN_STATS = List.of(HELMET.getId(), CHESTPLATE.getId(), LEGGINGS.getId(), BOOTS.getId(), SHIELD.getId());

  private static final Component PLATING_LABEL = TConstruct.makeTranslation("stat", "plating").withStyle(ChatFormatting.BOLD, ChatFormatting.UNDERLINE);
  private static final Component ARMOR_PLATING_LABEL = TConstruct.makeTranslation("stat", "plating_armor").withStyle(ChatFormatting.BOLD, ChatFormatting.UNDERLINE);
  private static final Component SHIELD_LABEL = TConstruct.makeTranslation("stat", "plating_shield").withStyle(ChatFormatting.BOLD, ChatFormatting.UNDERLINE);


  public ArmorMaterialContent(MaterialVariantId materialVariant, boolean detailed) {
    super(materialVariant, detailed);
  }

  @Override
  public ResourceLocation getId() {
    return ID;
  }

  @Nullable
  @Override
  protected MaterialStatsId getStatType(int index) {
    return switch (index) {
      case 0 -> StatlessMaterialStats.MAILLE.getIdentifier();
      case 1 -> StatlessMaterialStats.CUIRASS.getIdentifier();
      case 2 -> StatlessMaterialStats.SHIELD_CORE.getIdentifier();
      default -> null;
    };
  }

  @Override
  protected String getTextKey(MaterialId material) {
    if (detailed) {
      String primaryKey = String.format("material.%s.%s.armor", material.getNamespace(), material.getPath());
      if (Util.canTranslate(primaryKey)) {
        return primaryKey;
      }
      return String.format("material.%s.%s.encyclopedia", material.getNamespace(), material.getPath());
    }
    return String.format("material.%s.%s.flavor", material.getNamespace(), material.getPath());
  }

  @Override
  protected boolean supportsStatType(MaterialStatsId statsId) {
    return SUPPORTED.contains(statsId);
  }

  /** Gets the tool to display for the given stat type, just hardcoding to plate armor for simplicity */
  private static void addPlatingItem(MaterialStatsId statType, List<ItemStack> stacks, MaterialVariantId variant) {
    for (ArmorItem.Type slotType : ArmorItem.Type.values()) {
      if (statType.equals(PlatingMaterialStats.TYPES.get(slotType.ordinal()).getId())) {
        stacks.add(TinkerToolParts.plating.get(slotType).withMaterialForDisplay(variant));
        return;
      }
    }
    // gotta have something, so just fallback to shield
    ItemStack tool = ToolBuildHandler.createSingleMaterial(TinkerTools.plateShield.get(), MaterialVariant.of(variant));
    if (!tool.isEmpty()) {
      stacks.add(tool);
    } else {
      stacks.add(TinkerTools.plateShield.get().getRenderTool());
    }
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    MaterialVariantId materialVariant = getMaterialVariant();
    MaterialId material = materialVariant.getId();
    this.addTitle(list, getTitle(), true, MaterialTooltipCache.getColor(materialVariant).getValue());

    // the cool tools to the left/right
    this.addDisplayItems(list, rightSide ? BookScreen.PAGE_WIDTH - 18 : 0, materialVariant);

    int y = getTitleHeight();
    int x = (rightSide ? 0 : COLUMN_MARGIN) + 2;

    // we want a combined stats display for all 5 plating types
    IMaterialRegistry registry = MaterialRegistry.getInstance();
    List<PlatingMaterialStats> stats = TOP_DOWN_STATS.stream().flatMap(id -> registry.<PlatingMaterialStats>getMaterialStats(material, id).stream()).toList();
    if (!stats.isEmpty()) {
      List<ItemStack> plating = new ArrayList<>();
      for (PlatingMaterialStats stat : stats) {
        addPlatingItem(stat.getIdentifier(), plating, materialVariant);
      }
      int platingWidth = book.fontRenderer.width(PLATING_LABEL);
      list.add(new TextComponentElement(x, y, platingWidth, 10, PLATING_LABEL));
      for (int i = 0; i < plating.size(); i++) {
        if (i != 0) {
          list.add(new TextElement(platingWidth - 2 + x + i * 20, y, 15, 10, "/"));
        }
        list.add(new TinkerItemElement(platingWidth + 5 + x + i * 20, y, 0.5f, List.of(plating.get(i))));
      }
      y += 10;
      List<TextComponentData> lineData = new ArrayList<>();
      addStatLine(lineData, stats, ToolStats.DURABILITY, PlatingMaterialStats::durability);
      addStatLine(lineData, stats, ToolStats.ARMOR, PlatingMaterialStats::armor);
      addStatLine(lineData, stats, ToolStats.ARMOR_TOUGHNESS, PlatingMaterialStats::toughness);
      addStatLine(lineData, stats, ToolStats.KNOCKBACK_RESISTANCE, stat -> stat.knockbackResistance() * 10);
      list.add(new TextComponentElement(x - 2, y, BookScreen.PAGE_WIDTH - 20, BookScreen.PAGE_HEIGHT, lineData));
      y += lineData.size() * 5;
    }

    // material traits
    // note we don't add separate traits for each plating type, we take a shortcut adding just helmet and shield
    // while this may be inaccurate if someone does weird stuff, we just don't have space for more
    y = Math.max(
      this.addTraits(x - 3,          y, list, ARMOR_PLATING_LABEL, HELMET.getId()),
      this.addTraits(x + STAT_WIDTH, y, list, SHIELD_LABEL,        SHIELD.getId()));
    y = addAllMaterialStats(x, y, list, 2, false);

    // material description
    addDescription(x, y, list);
  }

  /** Adds a stat to the description */
  private static void addStatLine(List<TextComponentData> lineData, List<PlatingMaterialStats> plating, FloatToolStat stat, ToFloatFunction<PlatingMaterialStats> statGetter) {
    String[] values = plating.stream().map(s -> Util.COMMA_FORMAT.format(statGetter.apply(s))).toArray(String[]::new);
    boolean allMatch = true;
    for (int i = 1; i < values.length; i++) {
      if (!values[0].equals(values[i])) {
        allMatch = false;
        break;
      }
    }
    String stats = allMatch ? values[0] : Strings.join(values, " / ");
    TextComponentData data = new TextComponentData(stat.getPrefix().append(Component.literal(stats).withStyle(style -> style.withColor(stat.getColor()))));
    data.tooltips = new Component[] { stat.getDescription() };
    lineData.add(data);
    lineData.add(new TextComponentData("\n"));
  }

  /** Adds trait info to the listing */
  private int addTraits(int x, int y, List<BookElement> list, Component title, MaterialStatsId statsId) {
    IMaterialRegistry registry = MaterialRegistry.getInstance();
    MaterialVariantId material = getMaterialVariant();
    if (registry.getMaterialStats(material.getId(), statsId).isEmpty()) {
      return y;
    }

    // and the name itself
    list.add(new TextComponentElement(x, y, STAT_WIDTH, 10, title));
    y += 12;

    List<TextComponentData> lineData = Lists.newArrayList();
    addTraitLines(lineData, registry.getTraits(material.getId(), statsId));
    list.add(new TextComponentElement(x, y, STAT_WIDTH, BookScreen.PAGE_HEIGHT, lineData));

    return y + (lineData.size() * 5) + 3;
  }
}
