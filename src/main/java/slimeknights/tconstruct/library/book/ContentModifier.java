package slimeknights.tconstruct.library.book;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.gui.book.GuiBook;
import slimeknights.mantle.client.gui.book.element.BookElement;
import slimeknights.mantle.client.gui.book.element.ElementItem;
import slimeknights.mantle.client.gui.book.element.ElementText;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.traits.ITrait;

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
  public void build(BookData book, ArrayList<BookElement> list) {
    addTitle(list, modifier.getLocalizedName(), null, null);

    int x = GuiBook.PAGE_WIDTH/2;
    int w = GuiBook.PAGE_WIDTH;
    int y = TITLE_HEIGHT;


  }
}
