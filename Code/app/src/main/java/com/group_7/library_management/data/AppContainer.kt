package com.group_7.library_management.data

import com.group_7.library_management.data.repository.LibraryRepository
import com.group_7.library_management.data.repository.MockLibraryRepository

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val libraryRepository: LibraryRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class DefaultAppContainer : AppContainer {
    override val libraryRepository: LibraryRepository by lazy {
        MockLibraryRepository()
    }
}
