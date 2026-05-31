package slimeknights.tconstruct.common.data.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;

public class MaterialModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {
  private JsonArray offset = null;
  public MaterialModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
    super(TConstruct.getResource("material"), parent, existingFileHelper);
  }

  /** Sets the offset */
  public MaterialModelBuilder<T> offset(int x, int y) {
    this.offset = new JsonArray(2);
    this.offset.add(x);
    this.offset.add(y);
    return this;
  }

  @Override
  public JsonObject toJson(JsonObject json) {
    super.toJson(json);
    if (offset != null) {
      json.add("offset", offset);
    }
    return json;
  }
}
