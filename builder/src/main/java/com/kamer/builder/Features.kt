package com.kamer.builder

import androidx.fragment.app.Fragment

//check https://github.com/damian-burke/android-dynamic-feature
object Features {

    val registries: MutableMap<Feature, FeatureRegistry> = mutableMapOf()

}

interface FeatureRegistry {
    val featureClass: Class<out Fragment>

    fun create(): Fragment
}

enum class Feature {
    FEED
}
