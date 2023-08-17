package com.example.mymall_kotlin.di

import android.app.Application
import android.content.Context
import com.example.mymall_kotlin.data.remote.PaymentApiService
import com.example.mymall_kotlin.data.repo.AddressRepoImpl
import com.example.mymall_kotlin.data.repo.CredentialsRepositoryImpl
import com.example.mymall_kotlin.data.repo.DeliveryRepoImpl
import com.example.mymall_kotlin.data.repo.HomeRepositoryImpl
import com.example.mymall_kotlin.data.repo.MainActivityRepoImpl
import com.example.mymall_kotlin.data.repo.MyCartRepoImpl
import com.example.mymall_kotlin.data.repo.MyOrdersRepoImpl
import com.example.mymall_kotlin.data.repo.NotificationsRepoImpl
import com.example.mymall_kotlin.data.repo.ProductDetailsRepoImpl
import com.example.mymall_kotlin.data.repo.RazorpayRepoImpl
import com.example.mymall_kotlin.data.repo.RewardRepoImpl
import com.example.mymall_kotlin.data.repo.SearchRepoImpl
import com.example.mymall_kotlin.data.repo.UpdateUserInfoRepoImpl
import com.example.mymall_kotlin.data.repo.WishListRepoImpl
import com.example.mymall_kotlin.domain.repo.AddressRepo
import com.example.mymall_kotlin.domain.repo.CredentialsRepository
import com.example.mymall_kotlin.domain.repo.DeliveryRepo
import com.example.mymall_kotlin.domain.repo.HomeRepository
import com.example.mymall_kotlin.domain.repo.MainActivityRepo
import com.example.mymall_kotlin.domain.repo.MyCartRepo
import com.example.mymall_kotlin.domain.repo.MyOrdersRepo
import com.example.mymall_kotlin.domain.repo.NotificationRepo
import com.example.mymall_kotlin.domain.repo.ProductDetailsRepo
import com.example.mymall_kotlin.domain.repo.RazorpayRepo
import com.example.mymall_kotlin.domain.repo.RewardRepo
import com.example.mymall_kotlin.domain.repo.SearchRepo
import com.example.mymall_kotlin.domain.repo.UpdateUserInfoRepo
import com.example.mymall_kotlin.domain.repo.WishListRepo
import com.example.mymall_kotlin.domain.usecase.credentials.SignInEmailAndPwUseCase
import com.example.mymall_kotlin.domain.usecase.credentials.SignInGoogleUseCase
import com.example.mymall_kotlin.domain.usecase.credentials.SignInCredentialUseCase
import com.example.mymall_kotlin.domain.usecase.credentials.SignOutUseCase
import com.example.mymall_kotlin.domain.usecase.credentials.SignUpUseCase
import com.example.mymall_kotlin.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage() = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideCredentialsRepo(
        firebaseAuth: FirebaseAuth, firebaseFirestore: FirebaseFirestore,
        context: Context
    ): CredentialsRepository {
        return CredentialsRepositoryImpl(firebaseAuth, firebaseFirestore, context)
    }

    @Provides
    @Singleton
    fun provideSignUpUseCase(credentialsRepository: CredentialsRepository): SignUpUseCase {
        return SignUpUseCase(credentialsRepository)
    }

    @Provides
    @Singleton
    fun provideSignInUseCase(credentialsRepository: CredentialsRepository): SignInEmailAndPwUseCase {
        return SignInEmailAndPwUseCase(credentialsRepository)
    }

    @Provides
    @Singleton
    fun provideSignInGoogleUseCase(credentialsRepository: CredentialsRepository): SignInGoogleUseCase {
        return SignInGoogleUseCase(credentialsRepository)
    }

    @Provides
    @Singleton
    fun provideSignInCredentialUseCase(credentialsRepository: CredentialsRepository): SignInCredentialUseCase {
        return SignInCredentialUseCase(credentialsRepository)
    }

    @Provides
    @Singleton
    fun provideSignOutUseCase(credentialsRepository: CredentialsRepository): SignOutUseCase {
        return SignOutUseCase(credentialsRepository)
    }

    @Provides
    @Singleton
    fun provideHomeRepo(firebaseFirestore: FirebaseFirestore): HomeRepository {
        return HomeRepositoryImpl(firebaseFirestore)
    }

    @Provides
    @Singleton
    fun provideProductDetailsRepo(
        firebaseFirestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): ProductDetailsRepo {
        return ProductDetailsRepoImpl(firebaseFirestore, firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideWishListRepo(
        firebaseFirestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): WishListRepo {
        return WishListRepoImpl(firebaseFirestore, firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideMyCartRepo(
        firebaseFirestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): MyCartRepo {
        return MyCartRepoImpl(firebaseFirestore, firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideAddressRepo(
        firebaseFirestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): AddressRepo {
        return AddressRepoImpl(firebaseAuth, firebaseFirestore)
    }

    @Provides
    @Singleton
    fun provideRewardsRepo(
        firebaseFirestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): RewardRepo {
        return RewardRepoImpl(firebaseFirestore, firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideMainActivityRepo(
        firebaseFirestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): MainActivityRepo {
        return MainActivityRepoImpl(firebaseFirestore, firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideDeliveryRepo(
        firebaseFirestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): DeliveryRepo {
        return DeliveryRepoImpl(firebaseFirestore, firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideMyOrdersRepo(
        firebaseFirestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): MyOrdersRepo {
        return MyOrdersRepoImpl(firebaseFirestore, firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideUpdateUserInfoRepo(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore,
        firebaseStorage: FirebaseStorage
    ): UpdateUserInfoRepo {
        return UpdateUserInfoRepoImpl(firebaseAuth,firebaseFirestore,firebaseStorage)
    }

    @Provides
    @Singleton
    fun provideSearchRepo(
        firebaseFirestore: FirebaseFirestore
    ): SearchRepo {
        return SearchRepoImpl(firebaseFirestore)
    }

    @Provides
    @Singleton
    fun provideNotificationRepo(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore
    ): NotificationRepo {
        return NotificationsRepoImpl(firebaseAuth,firebaseFirestore)
    }


    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    @Singleton
    @Provides
    fun provideHTTPClient(): OkHttpClient {
        return OkHttpClient.Builder().readTimeout(
            20, TimeUnit.SECONDS
        ).connectTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(provideLoggingInterceptor())
            .build()

    }

    @Singleton
    @Provides
    fun postAPIService(): PaymentApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.RAZORPAY_BASE_URL)
            .client(provideHTTPClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(PaymentApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRazorRepo(): RazorpayRepo {
        return RazorpayRepoImpl(postAPIService())
    }

}