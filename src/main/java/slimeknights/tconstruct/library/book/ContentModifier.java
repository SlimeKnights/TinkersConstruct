package slimeknights.tconstruct.library.book;

import com.google.gson.annotations.SerializedName;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.gui.book.GuiBook;
import slimeknights.mantle.client.gui.book.element.BookElement;
import slimeknights.tconstruct.library.modifiers.IModifier;

@SideOnly(Side.CLIENT)
public class ContentModifier extends TinkerPage {

  private transient IModifier modifier;
  @SerializedName("modifier")
  public String modifierName;

  public ContentModifier() {}

  public ContentModifier(IModifier modifier) {
    this.modifier = modifier;
    this.modifierName = modifier.getIdentifier();
  }

  @Override
  public void load() {
    super.load();
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    addTitle(list, modifier.getLocalizedName(), null, null);

    int x = GuiBook.PAGE_WIDTH/2;
    int w = GuiBook.PAGE_WIDTH;
    int y = TITLE_HEIGHT;


  }
}
