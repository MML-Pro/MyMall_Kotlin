package com.blogspot.mido_mymall.di

import android.app.Application
import android.content.Context
import com.blogspot.mido_mymall.data.remote.PaymentApiService
import com.blogspot.mido_mymall.data.repo.AddressRepoImpl
import com.blogspot.mido_mymall.data.repo.CredentialsRepositoryImpl
import com.blogspot.mido_mymall.data.repo.DeliveryRepoImpl
import com.blogspot.mido_mymall.data.repo.HomeRepositoryImpl
import com.blogspot.mido_mymall.data.repo.MainActivityRepoImpl
import com.blogspot.mido_mymall.data.repo.MyCartRepoImpl
import com.blogspot.mido_mymall.data.repo.MyOrdersRepoImpl
import com.blogspot.mido_mymall.data.repo.NotificationsRepoImpl
import com.blogspot.mido_mymall.data.repo.ProductDetailsRepoImpl
import com.blogspot.mido_mymall.data.repo.RazorpayRepoImpl
import com.blogspot.mido_mymall.data.repo.RewardRepoImpl
import com.blogspot.mido_mymall.data.repo.SearchRepoImpl
import com.blogspot.mido_mymall.data.repo.UpdateUserInfoRepoImpl
import com.blogspot.mido_mymall.data.repo.WishListRepoImpl
import com.blogspot.mido_mymall.domain.repo.AddressRepo
import com.blogspot.mido_mymall.domain.repo.CredentialsRepository
import com.blogspot.mido_mymall.domain.repo.DeliveryRepo
import com.blogspot.mido_mymall.domain.repo.HomeRepository
import com.blogspot.mido_mymall.domain.repo.MainActivityRepo
import com.blogspot.mido_mymall.domain.repo.MyCartRepo
import com.blogspot.mido_mymall.domain.repo.MyOrdersRepo
import com.blogspot.mido_mymall.domain.repo.NotificationRepo
import com.blogspot.mido_mymall.domain.repo.ProductDetailsRepo
import com.blogspot.mido_mymall.domain.repo.RazorpayRepo
import com.blogspot.mido_mymall.domain.repo.RewardRepo
import com.blogspot.mido_mymall.domain.repo.SearchRepo
import com.blogspot.mido_mymall.domain.repo.UpdateUserInfoRepo
import com.blogspot.mido_mymall.domain.repo.WishListRepo
import com.blogspot.mido_mymall.domain.usecase.credentials.SignInEmailAndPwUseCase
import com.blogspot.mido_mymall.domain.usecase.credentials.SignInGoogleUseCase
import com.blogspot.mido_mymall.domain.usecase.credentials.SignInCredentialUseCase
import com.blogspot.mido_mymall.domain.usecase.credentials.SignOutUseCase
import com.blogspot.mido_mymall.domain.usecase.credentials.SignUpUseCase
import com.blogspot.mido_mymall.util.Constants
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