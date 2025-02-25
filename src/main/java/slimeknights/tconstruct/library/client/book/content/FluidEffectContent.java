package slimeknights.tconstruct.library.client.book.content;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import slimeknights.mantle.client.screen.book.element.TextComponentElement;
import slimeknights.mantle.client.screen.book.element.TextElement;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.book.elements.FluidItemElement;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffects;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/** Page type to display a fluid effect in the encyclopedia */
public class FluidEffectContent extends PageContent {
  public static final transient ResourceLocation ID = TConstruct.getResource("fluid_effect");
  private static final transient String KEY_BLOCK_EFFECTS = TConstruct.makeTranslationKey("book", "fluid_effects.block");
  private static final transient String KEY_ENTITY_EFFECTS = TConstruct.makeTranslationKey("book", "fluid_effects.entity");

  @Setter
  @Getter
  @Nonnull
  protected String title = "";
  @Getter
  protected String group = "";
  protected String text = "";
  @Nullable
  protected String[] entity = null;
  @Nullable
  protected String[] block = null;
  private transient List<Component> entityComponents = List.of();
  private transient List<Component> blockComponents = List.of();

  private transient List<FluidStack> fluids = List.of();
  private transient List<ItemStack> fluidItems = List.of();

  /** Updates the fluids from the page info */
  public void loadEffectData(ResourceLocation name, FluidEffects effects, List<FluidStack> fluids, List<ItemStack> fluidItems) {
    // set ingredient data
    this.fluids = fluids;
    this.fluidItems = fluidItems;
    // load in missing data
    String key = Util.makeTranslationKey("fluid", name);
    if (title.isBlank()) {
      title = ForgeI18n.getPattern(key);
    }
    if (text.isBlank()) {
      text = ForgeI18n.getPattern(key + ".fluid_effect");
    }
    // if we didn't set either effects list, fetch those
    if (effects.hasEffects()) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
        RegistryAccess access = level.registryAccess();
        if (effects.hasBlockEffects() && block == null) {
          blockComponents = effects.blockEffects().stream().map(effect -> effect.getDescription(access)).toList();
        }
        if (effects.hasEntityEffects() && entity == null) {
          entityComponents = effects.entityEffects().stream().map(effect -> effect.getDescription(access)).toList();
        }
      }
    }
  }

  /** Adds a list of effects to the page */
  private void addList(ArrayList<BookElement> list, int x, int y, int height, String key, @Nullable String[] texts, List<Component> components) {
    if (texts != null || !components.isEmpty()) {
      TextData head = new TextData(I18n.get(key));
      head.underlined = true;
      list.add(new TextElement(x, y, BookScreen.PAGE_WIDTH, height, head));

      // we use text components when autogenerating data from the effects list
      if (texts == null) {
        List<TextComponentData> effectData = new ArrayList<>();
        for (Component text : components) {
          effectData.add(new TextComponentData("\u25CF "));
          effectData.add(new TextComponentData(text));
          effectData.add(new TextComponentData("\n"));
        }
        list.add(new TextComponentElement(x, y + 14, BookScreen.PAGE_WIDTH, height, effectData));
      } else if (texts.length > 0) {
        List<TextData> effectData = new ArrayList<>();
        for (String text : texts) {
          effectData.add(new TextData("\u25CF "));
          effectData.add(new TextData(text));
          effectData.add(new TextData("\n"));
        }
        list.add(new TextElement(x, y + 14, BookScreen.PAGE_WIDTH, height, effectData));
      }
    }
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    this.addTitle(list, getTitle());

    // add the fluid element
    int y = getTitleHeight();
    FluidItemElement fluid = new FluidItemElement(-2, y, 2f, fluidItems, fluids);
    list.add(fluid);

    // description
    list.add(new TextElement(fluid.width, y, BookScreen.PAGE_WIDTH - fluid.width, fluid.height, text));
    y += fluid.height;

    // effects
    int group = (BookScreen.PAGE_HEIGHT - y) / 2;
    addList(list, 0, y,         group, KEY_ENTITY_EFFECTS, entity, entityComponents);
    addList(list, 0, y + group, group, KEY_BLOCK_EFFECTS,  block,  blockComponents);
  }
}
