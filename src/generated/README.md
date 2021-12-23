This directory (as the name suggests) contains resources that are generated. Do not directly add, modifiy, or remove resources in this directory as they will automatically be reverted the next time datagen is run.

If you need to make changes to these as part of a PR, modify the appropriate data generator in code and then run the data generators, then include both your changes to the data generators and the new generated resources as part of the commit.

Alternatively, if you are just adding resources, you can add them under `src/main/resources`, which contains non-generated resources. Note the following types of are currently generated, any PRs adding files of those types without using a data generator will be rejected:

* Resource Packs
  * Material Render Info
* Data Packs
  * Advancements
  * Loot tables
  * Material JSONs (definition, stats, traits)
  * Recipes
  * Tags
  * Tool definitions and Tinker Station slot layouts