package slimeknights.tconstruct.library.book;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.BookTransformer;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.repository.ModuleFileRepository;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.book.content.ContentImageText2;
import slimeknights.tconstruct.library.book.content.ContentMaterial;
import slimeknights.tconstruct.library.book.content.ContentModifier;
import slimeknights.tconstruct.library.book.content.ContentModifierFortify;
import slimeknights.tconstruct.library.book.content.ContentSingleStatMultMaterial;
import slimeknights.tconstruct.library.book.content.ContentTool;
import slimeknights.tconstruct.library.book.sectiontransformer.BowMaterialSectionTransformer;
import slimeknights.tconstruct.library.book.sectiontransformer.MaterialSectionTransformer;
import slimeknights.tconstruct.library.book.sectiontransformer.ModifierSectionTransformer;
import slimeknights.tconstruct.library.book.sectiontransformer.ToolSectionTransformer;

@SideOnly(Side.CLIENT)
public class TinkerBook extends BookData {

  public final static BookData INSTANCE = BookLoader.registerBook(Util.RESOURCE, false, false);

  public static void init() {
    BookLoader.registerPageType(ContentMaterial.ID, ContentMaterial.class);
    BookLoader.registerPageType(ContentModifier.ID, ContentModifier.class);
    BookLoader.registerPageType(ContentModifierFortify.ID, ContentModifierFortify.class);
    BookLoader.registerPageType(ContentTool.ID, ContentTool.class);
    BookLoader.registerPageType(ContentSingleStatMultMaterial.ID, ContentSingleStatMultMaterial.class);
    BookLoader.registerPageType(ContentImageText2.ID, ContentImageText2.class);
    INSTANCE.addRepository(new ModuleFileRepository(TConstruct.pulseManager, Util.resource("book")));
    INSTANCE.addTransformer(new ToolSectionTransformer());
    INSTANCE.addTransformer(new MaterialSectionTransformer());
    INSTANCE.addTransformer(new ModifierSectionTransformer());
    INSTANCE.addTransformer(new BowMaterialSectionTransformer());
    INSTANCE.addTransformer(BookTransformer.IndexTranformer());
  }
}
