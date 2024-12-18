package slimeknights.tconstruct.library;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.command.BookTestCommand;
import slimeknights.tconstruct.TConstruct;

/** This class can safely be accessed serverside for book IDs */
public class TinkerBookIDs {
  public static final ResourceLocation MATERIALS_BOOK_ID = TConstruct.getResource("materials_and_you");
  public static final ResourceLocation MIGHTY_SMELTING_ID = TConstruct.getResource("mighty_smelting");
  public static final ResourceLocation PUNY_SMELTING_ID = TConstruct.getResource("puny_smelting");
  public static final ResourceLocation TINKERS_GADGETRY_ID = TConstruct.getResource("tinkers_gadgetry");
  public static final ResourceLocation FANTASTIC_FOUNDRY_ID = TConstruct.getResource("fantastic_foundry");
  public static final ResourceLocation ENCYCLOPEDIA_ID = TConstruct.getResource("encyclopedia");

  /** Registers suggestions with the mantle command */
  @SuppressWarnings({"removal", "DeprecatedIsStillUsed"})
  @Deprecated(forRemoval = true) // TODO 1.20: remove
  public static void registerCommandSuggestion() {
    BookTestCommand.addBookSuggestion(MATERIALS_BOOK_ID);
    BookTestCommand.addBookSuggestion(MIGHTY_SMELTING_ID);
    BookTestCommand.addBookSuggestion(PUNY_SMELTING_ID);
    BookTestCommand.addBookSuggestion(TINKERS_GADGETRY_ID);
    BookTestCommand.addBookSuggestion(FANTASTIC_FOUNDRY_ID);
    BookTestCommand.addBookSuggestion(ENCYCLOPEDIA_ID);
  }
}
