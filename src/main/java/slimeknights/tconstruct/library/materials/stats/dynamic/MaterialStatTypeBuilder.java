package slimeknights.tconstruct.library.materials.stats.dynamic;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

@RequiredArgsConstructor(staticName="name")
public class MaterialStatTypeBuilder {

    private final MaterialStatsId registryName;
    private List<DynamicStatField<?>> fields = new ArrayList<>();
    @Setter
    @Accessors(chain=true)
    private String durabilityField = "";

    /**
     * Adds a stat field to the material stat type.
     * 
     * @param field The stat field to add.
     * @return This builder.
     */
    public MaterialStatTypeBuilder addField(DynamicStatField<?> field) {
        this.fields.add(field);
        return this;
    }
    
    /**
     * Builds the material stat type.
     * 
     * @param resource The resource location to use for the material stat type.
     * @return The built material stat type.
     */
    public DynamicMaterialStatType build() {
        return new DynamicMaterialStatType(registryName, durabilityField, fields);
    }
}
