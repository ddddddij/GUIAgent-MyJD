package com.example.jd_sim.di

import android.content.Context
import com.example.jd_sim.domain.repository.DataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt依赖注入模块
 * 提供应用级别的单例依赖
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * 提供DataRepository单例
     * 使用@Singleton确保全局唯一实例
     */
    @Provides
    @Singleton
    fun provideDataRepository(
        @ApplicationContext context: Context
    ): DataRepository {
        return DataRepository.getInstance(context)
    }
}
