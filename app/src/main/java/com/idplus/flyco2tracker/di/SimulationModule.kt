package com.idplus.flyco2tracker.di

import com.idplus.flyco2tracker.data.remote.repository.SimulationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SimulationModule {

    @Provides
    @Singleton
    fun provideSimulationRepository(): SimulationRepository = SimulationRepository()
}