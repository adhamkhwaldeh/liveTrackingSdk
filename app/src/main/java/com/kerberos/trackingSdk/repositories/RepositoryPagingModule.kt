//package com.aljawad.sons.gorestrepository
//
//import com.kerberos.livetrackingsdk.repositories.repositories.TripPagingRepository
//import com.aljawad.sons.gorestrepository.repositories.UserPagingRepositoryImpl
//import dagger.Binds
//import dagger.Module
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//
//@Module
//@InstallIn(SingletonComponent::class)
//abstract class RepositoryPagingModule {
//
//    @Binds
//    abstract fun bindUserPagingRepository(
//        repository: UserPagingRepositoryImpl
//    ): TripPagingRepository
//
//}