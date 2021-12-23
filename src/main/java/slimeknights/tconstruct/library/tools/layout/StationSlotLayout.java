package slimeknights.tconstruct.library.tools.layout;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.mantle.util.LogicHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A full layout for the tinker station
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class StationSlotLayout {
  private static final ResourceLocation EMPTY_NAME = TConstruct.getResource("empty");
  public static final StationSlotLayout EMPTY = new StationSlotLayout("", LayoutIcon.EMPTY, null, LayoutSlot.EMPTY, Collections.emptyList());

  @Getter @Setter(AccessLevel.PROTECTED)
  private transient ResourceLocation name = EMPTY_NAME;
  private final String translation_key;
  private final LayoutIcon icon;
  @Nullable
  private final Integer sortIndex;
  private final LayoutSlot tool_slot;
  private final List<LayoutSlot> input_slots;

  /** Creates a new builder instance */
  public static Builder builder() {
    return new Builder();
  }

  /** If true, this layout is the primary layout for a station */
  public boolean isMain() {
    return sortIndex == null;
  }

  /** Gets the sort index for the given layout */
  public int getSortIndex() {
    return LogicHelper.defaultIfNull(sortIndex, 255);
  }

  /** Gets the icon for this layout */
  public LayoutIcon getIcon() {
    return LogicHelper.defaultIfNull(icon, LayoutIcon.EMPTY);
  }

  /* Slots */

  /** Gets the contents of the tool slot */
  public LayoutSlot getToolSlot() {
    return LogicHelper.defaultIfNull(tool_slot, LayoutSlot.EMPTY);
  }

  /** Gets positions for all input slots */
  public List<LayoutSlot> getInputSlots() {
    return LogicHelper.defaultIfNull(input_slots, Collections.emptyList());
  }

  /** Gets the number of input slots */
  public int getInputCount() {
    return getInputSlots().size();
  }

  /** Gets the slot for the given index, includes the tool slot */
  public LayoutSlot getSlot(int index) {
    if (index == 0) {
      return getToolSlot();
    }
    List<LayoutSlot> inputs = getInputSlots();
    if (index < 0 || index > inputs.size()) {
      return LayoutSlot.EMPTY;
    }
    return inputs.get(index - 1);
  }


  /* Buffers */

  /** Reads a slot from the packet buffer */
  public static StationSlotLayout read(PacketBuffer buffer) {
    ResourceLocation name = buffer.readResourceLocation();
    String translationKey = buffer.readString(Short.MAX_VALUE);
    LayoutIcon icon = LayoutIcon.read(buffer);
    Integer sortIndex = null;
    if (buffer.readBoolean()) {
      sortIndex = buffer.readVarInt();
    }
    LayoutSlot toolSlot = LayoutSlot.read(buffer);
    int max = buffer.readVarInt();
    ImmutableList.Builder<LayoutSlot> inputs = ImmutableList.builder();
    for (int i = 0; i < max; i++) {
      inputs.add(LayoutSlot.read(buffer));
    }
    StationSlotLayout layout = new StationSlotLayout(translationKey, icon, sortIndex, toolSlot, inputs.build());
    layout.setName(name);
    return layout;
  }

  /** Writes a slot to the packet buffer */
  public void write(PacketBuffer buffer) {
    buffer.writeResourceLocation(name);
    buffer.writeString(getTranslationKey());
    icon.write(buffer);
    if (sortIndex != null) {
      buffer.writeBoolean(true);
      buffer.writeVarInt(sortIndex);
    } else {
      buffer.writeBoolean(false);
    }
    getToolSlot().write(buffer);
    List<LayoutSlot> inputs = getInputSlots();
    buffer.writeVarInt(inputs.size());
    for (LayoutSlot slot : inputs) {
      slot.write(buffer);
    }
  }


  /* Text */

  /** Gets the translation key for this slot, suffixing description at the end forms the full description */
  public String getTranslationKey() {
    return LogicHelper.defaultIfNull(translation_key, "");
  }

  /** Cache of display name */
  private transient ITextComponent displayName = null;
  /** Cache of display name */
  private transient ITextComponent description = null;

  /** Gets the display name from the unlocalized name of {@link #getTranslationKey()} */
  public ITextComponent getDisplayName() {
    if (displayName == null) {
      displayName = new TranslationTextComponent(getTranslationKey());
    }
    return displayName;
  }

  /** Gets the description from the unlocalized name of {@link #getTranslationKey()} */
  public ITextComponent getDescription() {
    if (description == null) {
      description = new TranslationTextComponent(getTranslationKey() + ".description");
    }
    return description;
  }

  @Accessors(fluent = true)
  public static class Builder {
    private static final Pattern PICKAXE = new Pattern(TConstruct.MOD_ID, "pickaxe");

    @Setter
    private String translationKey = null;
    private LayoutIcon icon = LayoutIcon.EMPTY;
    private Integer sortIndex = null;
    private LayoutSlot toolSlot = null;
    private final ImmutableList.Builder<LayoutSlot> inputSlots = ImmutableList.builder();

    private Builder() {}

    /** Sets the sort index of this layout, unused for non-main layouts */
    public Builder sortIndex(int index) {
      sortIndex = index;
      return this;
    }

    /* Icons */

    /** Sets the given item as both the name and icon */
    public Builder item(ItemStack stack) {
      icon(stack);
      translationKey = stack.getTranslationKey();
      return this;
    }

    /** Sets the icon of this layout to a stack */
    public Builder icon(ItemStack stack) {
      icon = LayoutIcon.ofItem(stack);
      return this;
    }

    /** Sets the icon of this layout to a pattern */
    public Builder icon(Pattern pattern) {
      icon = LayoutIcon.ofPattern(pattern);
      return this;
    }


    /* Slots */

    /** Sets the tool slot properties */
    public Builder toolSlot(Pattern icon, @Nullable String name, int x, int y, @Nullable Ingredient filter) {
      toolSlot = new LayoutSlot(icon, name, x, y, filter);
      return this;
    }

    /** Sets the tool slot properties */
    public Builder toolSlot(int x, int y, @Nullable Ingredient filter) {
      return toolSlot(PICKAXE, null, x, y, filter);
    }

    /** Sets the tool slot properties */
    public Builder toolSlot(int x, int y) {
      return toolSlot(x, y, null);
    }

    /** Adds an input slot with the given properties */
    public Builder addInputSlot(@Nullable Pattern icon, @Nullable String name, int x, int y, @Nullable Ingredient filter) {
      inputSlots.add(new LayoutSlot(icon, name, x, y, filter));
      return this;
    }

    /** Adds an input slot with the given properties */
    public Builder addInputSlot(@Nullable Pattern icon, @Nullable String name, int x, int y) {
      return addInputSlot(icon, name, x, y, null);
    }

    /** Adds an input slot with the given properties */
    public Builder addInputSlot(@Nullable Pattern icon, int x, int y) {
      return addInputSlot(icon, null, x, y);
    }

    /** Adds an input as the given item */
    public Builder addInputItem(Pattern icon, IItemProvider item, int x, int y) {
      return addInputSlot(icon, item.asItem().getTranslationKey(), x, y, Ingredient.fromItems(item));
    }

    /** Adds an input as the given item */
    public Builder addInputItem(IItemProvider item, int x, int y) {
      return addInputItem(new Pattern(Objects.requireNonNull(item.asItem().getRegistryName())), item, x, y);
    }

    /** Builds a station slot layout */
    public StationSlotLayout build() {
      return new StationSlotLayout(translationKey, icon, sortIndex, toolSlot, inputSlots.build());
    }
  }
}
