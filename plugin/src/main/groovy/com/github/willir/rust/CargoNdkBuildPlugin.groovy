package com.github.willir.rust

import org.gradle.api.Plugin
import org.gradle.api.Project

class CargoNdkBuildPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        def ext = project.extensions.create("cargoNdk", CargoNdkBuildPluginExtension, project)

        def variants = project.android.hasProperty("applicationVariants")
                ? project.android.applicationVariants
                : project.android.libraryVariants

        variants.all { variant ->
            def variantUpper = variant.name.capitalize()
            project.task(type: CargoNdkBuildTask, "buildCargoNdk" + variantUpper) {
                group = "Build"
                description = "Build rust library for variant " + variant.name
                setVariant(variant.name)
                extension = ext
            }
        }

        project.tasks.whenTaskAdded { task ->
            variants.all{ variant ->
                def variantName = variant.name
                def variantUpper = variantName.capitalize()
                def preTasks = ["compile" + variantUpper + "Sources" ,
                                "merge" + variantUpper + "JniLibFolders"]
                if (task.name in preTasks) {
                    task.dependsOn "buildCargoNdk" + variantUpper
                }
            }
        }
    }
}

