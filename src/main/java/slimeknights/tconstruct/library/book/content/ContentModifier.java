package slimeknights.tconstruct.library.book.content;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.ArrowButton;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.ImageElement;
import slimeknights.mantle.client.screen.book.element.TextElement;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.util.ItemStackList;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerRegistries;
import slimeknights.tconstruct.library.book.elements.CycleRecipeElement;
import slimeknights.tconstruct.library.book.elements.TinkerItemElement;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import slimeknights.tconstruct.tools.modifiers.EmptyModifier;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ContentModifier extends PageContent {
  public static final transient String ID = "modifier";
  public static final transient int TEX_SIZE = 256;
  public static final ResourceLocation BOOK_MODIFY = TConstruct.getResource("textures/gui/book/modify.png");
  private static final transient String KEY_EFFECTS = TConstruct.makeTranslationKey("book", "modifiers.effect");

  public static final transient ImageData IMG_SLOT_1 = new ImageData(BOOK_MODIFY, 0, 75, 22, 22, TEX_SIZE, TEX_SIZE);
  public static final transient ImageData IMG_SLOT_2 = new ImageData(BOOK_MODIFY, 0, 97, 40, 22, TEX_SIZE, TEX_SIZE);
  public static final transient ImageData IMG_SLOT_3 = new ImageData(BOOK_MODIFY, 0, 119, 58, 22, TEX_SIZE, TEX_SIZE);
  public static final transient ImageData IMG_SLOT_4 = new ImageData(BOOK_MODIFY, 0, 141, 40, 40, TEX_SIZE, TEX_SIZE);
  public static final transient ImageData IMG_SLOT_5 = new ImageData(BOOK_MODIFY, 0, 181, 58, 41, TEX_SIZE, TEX_SIZE);
  public static final transient ImageData IMG_TABLE = new ImageData(BOOK_MODIFY, 214, 0, 42, 46, TEX_SIZE, TEX_SIZE);
  public static final transient ImageData[] IMG_SLOTS = new ImageData[]{IMG_SLOT_1, IMG_SLOT_2, IMG_SLOT_3, IMG_SLOT_4, IMG_SLOT_5};

  public static final transient int[] SLOTS_X = new int[]{3, 21, 39, 12, 30};
  public static final transient int[] SLOTS_Y = new int[]{3, 3, 3, 22, 22};
  public static final transient int[] SLOTS_X_4 = new int[]{3, 21, 3, 21};
  public static final transient int[] SLOTS_Y_4 = new int[]{3, 3, 22, 22};

  private transient Modifier modifier;
  private transient List<IDisplayModifierRecipe> recipes;

  private transient int currentRecipe = 0;
  private final transient List<BookElement> parts = new ArrayList<>();

  public TextData[] text;
  public String[] effects;
  public boolean more_text_space = false;

  @SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
  @SerializedName("required_mod")
  private String requiredMod = "";

  @SerializedName("modifier_id")
  public String modifierID;

  /** Checks if this modifier has the required mods to load */
  public boolean hasRequiredMod() {
    return requiredMod == null || requiredMod.isEmpty() || ModList.get().isLoaded(requiredMod);
  }

  @Override
  public void load() {
    if (this.modifierID == null) {
      this.modifierID = this.parent.name;
    }

    if (this.modifier == null) {
      this.modifier = TinkerRegistries.MODIFIERS.getValue(new ModifierId(this.modifierID));
    }

    if (this.recipes == null) {
      assert Minecraft.getInstance().world != null;
      this.recipes = RecipeHelper.getJEIRecipes(Minecraft.getInstance().world.getRecipeManager(), RecipeTypes.TINKER_STATION, IDisplayModifierRecipe.class).stream().filter(recipe -> recipe.getDisplayResult().getModifier() == this.modifier).collect(Collectors.toList());
    }
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean brightSide) {
    if (this.modifier == null || this.modifier instanceof EmptyModifier || this.recipes.isEmpty()) {
      list.add(new ImageElement(0, 0, 32, 32, ImageData.MISSING));
      System.out.println("Modifier with id " + modifierID + " not found");
      return;
    }
    this.addTitle(list, this.modifier.getDisplayName().getString(), true, this.modifier.getColor());

    // description
    int y = getTitleHeight();
    int h = more_text_space ? BookScreen.PAGE_HEIGHT * 2 / 5 : BookScreen.PAGE_HEIGHT * 2 / 7;
    list.add(new TextElement(5, y, BookScreen.PAGE_WIDTH - 10, h, text));

    if (this.effects.length > 0) {
      TextData head = new TextData(I18n.format(KEY_EFFECTS));
      head.underlined = true;

      list.add(new TextElement(5, y + h, BookScreen.PAGE_WIDTH / 2 - 5, BookScreen.PAGE_HEIGHT - h - 20, head));

      List<TextData> effectData = Lists.newArrayList();

      for (String e : this.effects) {
        effectData.add(new TextData("\u25CF "));
        effectData.add(new TextData(e));
        effectData.add(new TextData("\n"));
      }

      list.add(new TextElement(5, y + 14 + h, BookScreen.PAGE_WIDTH / 2 + 5, BookScreen.PAGE_HEIGHT - h - 20, effectData));
    }

    if (recipes.size() > 1) {
      int col = book.appearance.structureButtonColor;
      int colHover = book.appearance.structureButtonColorHovered;
      list.add(new CycleRecipeElement(BookScreen.PAGE_WIDTH - ArrowButton.ArrowType.RIGHT.w - 32, 160,
        ArrowButton.ArrowType.RIGHT, col, colHover, this, book, list));
    }

    this.buildAndAddRecipeDisplay(book, list, this.recipes.get(this.currentRecipe), null);
  }

  /**
   * Builds the recipe display to show in the book's gui and adds it so that the user can see the recipes.
   *
   * @param book   the book data
   * @param list   the list of book elements
   * @param recipe recipe to display
   * @param parent the parent book screen, only used when there is multiple recipes
   */
  public void buildAndAddRecipeDisplay(BookData book, ArrayList<BookElement> list, @Nullable IDisplayModifierRecipe recipe, @Nullable BookScreen parent) {
    if (recipe != null) {
      List<List<ItemStack>> inputs = recipe.getDisplayItems();
      ImageData img = IMG_SLOTS[Math.min(inputs.size() - 2, 4)];
      if (inputs.size() > 6) {
        TConstruct.LOG.warn("Too many inputs in recipe {}, size {}", recipe, inputs.size() - 2);
      }
      int[] slotsX = SLOTS_X;
      int[] slotsY = SLOTS_Y;

      if (inputs.size() == 5) {
        slotsX = SLOTS_X_4;
        slotsY = SLOTS_Y_4;
      }

      int imgX = BookScreen.PAGE_WIDTH / 2 + 20;
      int imgY = BookScreen.PAGE_HEIGHT / 2 + 30;

      imgX = imgX + 29 - img.width / 2;
      imgY = imgY + 20 - img.height / 2;

      ImageElement table = new ImageElement(imgX + (img.width - IMG_TABLE.width) / 2, imgY - 24, -1, -1, IMG_TABLE);

      if (parent != null)
        table.parent = parent;

      this.parts.add(table);
      list.add(table); // TODO ADD TABLE TO TEXTURE?

      ImageElement slot = new ImageElement(imgX, imgY, -1, -1, img, book.appearance.slotColor);

      if (parent != null)
        slot.parent = parent;

      this.parts.add(slot);
      list.add(slot);

      ItemStackList demo = getDemoTools(recipe.getToolWithModifier());

      TinkerItemElement demoTools = new TinkerItemElement(imgX + (img.width - 16) / 2, imgY - 24, 1f, demo);

      if (parent != null)
        demoTools.parent = parent;

      this.parts.add(demoTools);
      list.add(demoTools);

      ImageElement image = new ImageElement(imgX + (img.width - 22) / 2, imgY - 27, -1, -1, IMG_SLOT_1, 0xffffff);

      if (parent != null)
        image.parent = parent;

      this.parts.add(image);
      list.add(image);

      for (int i = 1; i < Math.min(inputs.size(), 6); i++) {
        TinkerItemElement part = new TinkerItemElement(imgX + slotsX[i - 1], imgY + slotsY[i - 1], 1f, inputs.get(i));

        if (parent != null)
          part.parent = parent;

        this.parts.add(part);
        list.add(part);
      }
    }
  }

  /**
   * Creates an ItemStackList from a list of itemstacks
   * Used for rendering the tools in the book's modifier screen
   *
   * @param stacks The items to use.
   * @return A itemStackList containing the tools to show with the modifier applied
   */
  protected ItemStackList getDemoTools(List<ItemStack> stacks) {
    ItemStackList demo = ItemStackList.withSize(stacks.size());

    for (int i = 0; i < stacks.size(); i++) {
      demo.set(i, stacks.get(i));
    }

    return demo;
  }

  /**
   * Cycles to the next recipe by deleting the old elements and adding them again.
   *
   * @param book The book data
   * @param list The list of book elements
   */
  public void nextRecipe(BookData book, ArrayList<BookElement> list) {
    this.currentRecipe++;

    if (this.currentRecipe >= this.recipes.size()) {
      this.currentRecipe = 0;
    }

    BookScreen parent = this.parts.get(0).parent;

    for (BookElement element : this.parts) {
      list.remove(element);
    }

    this.parts.clear();

    this.buildAndAddRecipeDisplay(book, list, this.recipes.get(this.currentRecipe), parent);
  }
}
