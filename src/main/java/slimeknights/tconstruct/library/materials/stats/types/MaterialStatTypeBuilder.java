package slimeknights.tconstruct.library.materials.stats.types;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;


public class MaterialStatTypeBuilder {

    private final ResourceLocation registryName;
    private List<DynamicStatField<?>> fields = new ArrayList<>();
    @Setter
    private boolean canRepair = false;
    @Getter
    @Setter
    @Accessors(fluent = true)
    private boolean shouldBuild = true;

    protected MaterialStatTypeBuilder(ResourceLocation registryName) {
        this.registryName = registryName;
    }

    /**
     * Creates a new material stat type builder.
     * 
     * @param registryName  The registry name of the material stat type.
     * @return  The created material stat type builder.
     */
    public static MaterialStatTypeBuilder begin(
            ResourceLocation registryName) {
        return new MaterialStatTypeBuilder(registryName);
    }

    /**
     * Adds a stat field to the material stat type.
     * 
     * @param field The stat field to add.
     */
    public void addField(DynamicStatField<?> field) {
        this.fields.add(field);
    }
    
    /**
     * Builds the material stat type.
     * 
     * @param resource The resource location to use for the material stat type.
     * @return The built material stat type.
     */
    public DynamicMaterialStatType build() {
        return new DynamicMaterialStatType(new MaterialStatsId(registryName), this.canRepair, fields);
    }
}
