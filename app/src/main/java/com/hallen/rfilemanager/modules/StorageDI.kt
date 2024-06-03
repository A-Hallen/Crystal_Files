package com.hallen.rfilemanager.modules

import android.content.Context
import com.hallen.rfilemanager.infraestructure.FileLister
import com.hallen.rfilemanager.infraestructure.Storages
import com.hallen.rfilemanager.infraestructure.persistance.Prefs
import com.hallen.rfilemanager.infraestructure.utils.IconsManager
import com.hallen.rfilemanager.infraestructure.utils.ImageController
import com.hallen.rfilemanager.ui.view.adapters.main.MainAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/*
@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @ActivityScoped
    @Provides
    fun providesNavListAdapter(@ActivityContext context: Context) = NavListAdapter(context)
}
*/


@Module
@InstallIn(SingletonComponent::class)
object StorageDI {

    @Provides
    @Singleton
    fun provideStorages(@ApplicationContext context: Context): Storages = Storages(context)

    @Provides
    @Singleton
    fun providesFileLister(): FileLister = FileLister()

    @Provides
    @Singleton
    fun provideIconsManager(@ApplicationContext context: Context, prefs: Prefs): IconsManager =
        IconsManager(context, prefs)

    @Provides
    @Singleton
    fun provideImageController(@ApplicationContext context: Context): ImageController =
        ImageController(context)

    @Provides
    @Singleton
    fun providesMainAdapter(
        imageController: ImageController,
    ): MainAdapter =
        MainAdapter(imageController)

    @Provides
    @Singleton
    fun providesPreferences(@ApplicationContext context: Context): Prefs = Prefs(context)
}