package slimeknights.tconstruct.library.client.modifiers.block.model;

import com.google.gson.JsonObject;

public interface ParentModel extends BlockModifierModel {
  /**
   * Merges the child modifier model with this one
   * 
   * @param override The override object
   * @return The merged model
   * 
   * TODO: A rough implementation, might need to be redesigned
   */
  ParentModel mergeChild(JsonObject override);
}