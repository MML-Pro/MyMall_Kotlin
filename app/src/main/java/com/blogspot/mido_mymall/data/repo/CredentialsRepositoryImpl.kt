package com.blogspot.mido_mymall.data.repo

import android.content.Context
import android.content.Intent
import android.util.Log
import com.blogspot.mido_mymall.domain.models.User
import com.blogspot.mido_mymall.domain.repo.CredentialsRepository
import com.blogspot.mido_mymall.util.Constants.WEB_CLIENT_ID
import com.blogspot.mido_mymall.util.RegisterValidation
import com.blogspot.mido_mymall.util.Resource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "CredentialsRepositoryIm"

class CredentialsRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val context: Context
) : CredentialsRepository {

//    private var _validation = Channel<RegisterValidation.RegisterFailedStates>()
//    val validation get() = _validation.receiveAsFlow()


    override fun createUserWithEmailAndPassword(
        user: User,
        password: String
    ): Flow<Resource<FirebaseUser>> = flow {
        emit(Resource.Loading())
        val createUser = firebaseAuth.createUserWithEmailAndPassword(user.email, password).await()
        emit(Resource.Success(createUser.user))
        saveUserInfo(createUser.user!!.uid, user.userName, user.email)
    }.catch {
        emit(Resource.Error(it.message.toString()))
    }

    override fun saveUserInfo(
        userUID: String,
        userName: String,
        email: String
    ): Flow<Resource<Boolean>> {

        val saveUserInfo = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
        saveUserInfo.value = Resource.Loading()


        val userData: HashMap<String, Any> = HashMap()

        userData["userName"] = userName
        userData["email"] = email
        userData["profileImage"] = ""

        firestore.collection("USERS")
            .document(userUID)
            .set(userData)
            .addOnSuccessListener {
//                GlobalScope.launch {
//                    saveUserInfo.emit(Resource.Success(true))
//                }
                saveUserInfo.value = Resource.Success(true)

            }.addOnFailureListener {
                saveUserInfo.value = Resource.Error(it.message.toString())
            }

        return saveUserInfo
    }

    override fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<Resource<FirebaseUser>> = flow {

        emit(Resource.Loading())

        val loginUser = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        emit(Resource.Success(loginUser.user))

    }.catch { emit(Resource.Error(it.message.toString())) }

    override fun signInWithGoogle(): Flow<Resource<Intent>> = flow {

        emit(Resource.Loading())

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID).requestProfile()
            .requestEmail().build()

        val signInClient = GoogleSignIn.getClient(context, gso).signInIntent


        emit(Resource.Success(signInClient))

    }.catch {
        Log.e(TAG, "signInWithGoogle: ${it.message.toString()}")
        Log.e(TAG, "signInWithGoogle: ${it.cause.toString()}")
        emit(Resource.Error(it.message.toString())) }

    override fun signInWithCredential(credential: AuthCredential): Flow<Resource<AuthResult>> =
        flow {
            emit(Resource.Loading())
            emit(Resource.Success(firebaseAuth.signInWithCredential(credential).await()))
        }.catch {
            Log.e(TAG, "signInWithCredential: ${it.message.toString()}")
            Log.e(TAG, "signInWithCredential: ${it.cause.toString()}")
            emit(Resource.Error(it.message.toString()))
        }

    override fun signOut(googleSignInClient: GoogleSignInClient?): Flow<Resource<Boolean>> {

        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())

        result.value = Resource.Loading()


        if (googleSignInClient != null) {

            googleSignInClient.signOut()
                .addOnSuccessListener {

                    firebaseAuth.signOut()
                    result.value = Resource.Success(true)

                }.addOnFailureListener {

                    result.value = Resource.Error(it.message.toString())
                }

        } else {
            try {
                firebaseAuth.signOut()

                result.value = Resource.Success(true)
            } catch (th: Throwable) {
                result.value = Resource.Error(th.message.toString())
            }
        }

        return result
    }

    override fun resetPassword(email: String): Flow<Resource<Boolean>> {

        val resetPasswordState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())

        resetPasswordState.value = Resource.Loading()

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                resetPasswordState.value = Resource.Success(true)

            }.addOnFailureListener {
                resetPasswordState.value = Resource.Error(it.message.toString())
            }

        return resetPasswordState
    }

//    override suspend fun createUserData(): Flow<Resource<Boolean>> {
//        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
//
//        result.value = Resource.Loading()
//
//        val userDataReference =
//            firestore.collection("USERS").document(firebaseAuth.currentUser?.uid!!)
//                .collection("USER_DATA")
//
//
//        //MAPS
//        val wishListMap: HashMap<String, Any> = HashMap()
//        wishListMap["list_size"] = 0L
//
//        val ratingsMap: HashMap<String, Any> = HashMap()
//        ratingsMap["list_size"] = 0L
//
//        val cartMap: HashMap<String, Any> = HashMap()
//        cartMap["list_size"] = 0L
//
//        val addressMap: HashMap<String, Any> = HashMap()
//        addressMap["list_size"] = 0L
//
//        val notificationsMap: HashMap<String, Any> = HashMap()
//        notificationsMap["list_size"] = 0L
//
//
//        //MAPS
//
//        val documentNames: ArrayList<String> = ArrayList()
//        documentNames.add("MY_WISHLIST")
//        documentNames.add("MY_RATINGS")
//        documentNames.add("MY_CART")
//        documentNames.add("MY_ADDRESS")
//        documentNames.add("MY_NOTIFICATIONS")
//
//        val documentFields: ArrayList<Map<String, Any>> = ArrayList()
//
//        documentFields.add(wishListMap)
//        documentFields.add(ratingsMap)
//        documentFields.add(cartMap)
//        documentFields.add(addressMap)
//        documentFields.add(notificationsMap)
//
//
//        for (i in documentNames.indices) {
//            userDataReference.document(documentNames[i])
//                .set(documentFields[i])
//                .addOnSuccessListener {
//                    result.value = Resource.Success(true)
//                }.addOnFailureListener {
//                    result.value = Resource.Error(it.message.toString())
//
//                }
//
//        }
//
//        return result
//    }



    override suspend fun createUserData(): Flow<Resource<Boolean>> {
        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())

        result.value = Resource.Loading()

        val userDataReference =
            firestore.collection("USERS").document(firebaseAuth.currentUser?.uid!!)
                .collection("USER_DATA")

        // Check if the user data already exists
        val userDataSnapshot = userDataReference.get().await()
        if (userDataSnapshot.isEmpty) {
            // Data does not exist, proceed with creation

            // MAPS
            val wishListMap: HashMap<String, Any> = HashMap()
            wishListMap["list_size"] = 0L

            val ratingsMap: HashMap<String, Any> = HashMap()
            ratingsMap["list_size"] = 0L

            val cartMap: HashMap<String, Any> = HashMap()
            cartMap["list_size"] = 0L

            val addressMap: HashMap<String, Any> = HashMap()
            addressMap["list_size"] = 0L

            val notificationsMap: HashMap<String, Any> = HashMap()
            notificationsMap["list_size"] = 0L

            // MAPS

            val documentNames: ArrayList<String> = ArrayList()
            documentNames.add("MY_WISHLIST")
            documentNames.add("MY_RATINGS")
            documentNames.add("MY_CART")
            documentNames.add("MY_ADDRESS")
            documentNames.add("MY_NOTIFICATIONS")

            val documentFields: ArrayList<Map<String, Any>> = ArrayList()

            documentFields.add(wishListMap)
            documentFields.add(ratingsMap)
            documentFields.add(cartMap)
            documentFields.add(addressMap)
            documentFields.add(notificationsMap)

            for (i in documentNames.indices) {
                userDataReference.document(documentNames[i])
                    .set(documentFields[i])
                    .addOnSuccessListener {
                        result.value = Resource.Success(true)
                    }
                    .addOnFailureListener {
                        result.value = Resource.Error(it.message.toString())
                    }
            }
        } else {
            // Data already exists, return success without creating it again
            result.value = Resource.Success(true)
        }

        return result
    }
}