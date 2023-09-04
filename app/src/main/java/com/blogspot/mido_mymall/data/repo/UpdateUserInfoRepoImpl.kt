package com.blogspot.mido_mymall.data.repo

import android.util.Log
import com.blogspot.mido_mymall.domain.repo.UpdateUserInfoRepo
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "UpdateUserInfoRepoImpl"

class UpdateUserInfoRepoImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) : UpdateUserInfoRepo {


    override suspend fun updateUserName(userName: String): Resource<Boolean> {
        return try {
            firestore.collection("USERS")
                .document(firebaseAuth.currentUser?.uid!!)
                .update("userName", userName)
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive() // see https://stackoverflow.com/a/76261124/506796
            Resource.Error(e.message.toString())
        }
    }


    override suspend fun updateUserProfileImage(imageDataByteArray: ByteArray): MutableSharedFlow<Resource<Boolean>> {

        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())

        result.value = Resource.Loading()

        val storageReference = firebaseStorage.reference

        val reference =
            storageReference.child("users/${firebaseAuth.currentUser!!.uid}/profile_picture.jpg")

        val uploadTask = reference.putBytes(imageDataByteArray)

        uploadTask.addOnSuccessListener {

            reference.downloadUrl.addOnSuccessListener { imageUri ->


                firestore.collection("USERS")
                    .document(firebaseAuth.currentUser?.uid!!)
                    .update("profileImage", imageUri.toString())
                    .addOnSuccessListener {
                        result.value = Resource.Success(true)
                    }
                    .addOnFailureListener {
                        result.value = Resource.Error(it.message.toString())
                    }


            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }


        }.addOnFailureListener {
            result.value = Resource.Error(it.message.toString())
        }

        return result


    }


    override suspend fun updateEmail(
        oldEmail: String?,
        newEmail: String,
        password: String?,
        idToken: String?
    ): Flow<Resource<Boolean>> {
        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())

        result.value = Resource.Loading()

        if (idToken != null) {

            val authCredential = GoogleAuthProvider.getCredential(idToken, null)

            firebaseAuth.currentUser?.reauthenticate(authCredential)
                ?.addOnSuccessListener {

                    firebaseAuth.currentUser?.updateEmail(newEmail)
                        ?.addOnSuccessListener {


                            firestore.collection("USERS")
                                .document(firebaseAuth.currentUser?.uid!!)
                                .update("email", newEmail)
                                .addOnSuccessListener {
                                    result.value = Resource.Success(true)

                                }.addOnFailureListener {
                                    result.value = Resource.Error(it.message.toString())
                                }


                        }?.addOnFailureListener {
                            result.value = Resource.Error(it.message.toString())
                        }

                }?.addOnFailureListener {
                    result.value = Resource.Error(it.message.toString())

                }
        } else {
            val authCredential = EmailAuthProvider.getCredential(oldEmail!!, password!!)

            firebaseAuth.currentUser?.reauthenticate(authCredential)
                ?.addOnSuccessListener {
                    firebaseAuth.currentUser?.updateEmail(newEmail)
                        ?.addOnSuccessListener {
                            firestore.collection("USERS")
                                .document(firebaseAuth.currentUser?.uid!!)
                                .update("email", newEmail)
                                .addOnSuccessListener {
                                    result.value = Resource.Success(true)

                                }.addOnFailureListener {
                                    Log.d(TAG, "updateEmail: ${it.message.toString()}")
                                    result.value = Resource.Error(it.message.toString())
                                }
                        }?.addOnFailureListener {
                            Log.d(TAG, "updateEmail: ${it.message.toString()}")
                            result.value = Resource.Error(it.message.toString())

                        }
                }?.addOnFailureListener {
                    result.value = Resource.Error(it.message.toString())
                }

        }
        return result
    }

    override suspend fun updatePassword(
        currentEmail: String,
        oldPassword: String,
        newPassword: String
    ): MutableSharedFlow<Resource<Boolean>> {

        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())

        result.value = Resource.Loading()

        if (!oldPassword.equals(newPassword)) {

            val authCredential = EmailAuthProvider.getCredential(currentEmail, oldPassword)

            firebaseAuth.currentUser?.reauthenticate(authCredential)
                ?.addOnSuccessListener {
                    firebaseAuth.currentUser?.updatePassword(newPassword)
                        ?.addOnSuccessListener {
                            result.value = Resource.Success(true)
                        }?.addOnFailureListener {
                            result.value = Resource.Error(it.message.toString())
                        }
                }?.addOnFailureListener {
                    result.value = Resource.Error(it.message.toString())
                }
        } else {
            result.value = Resource.Error("old password is the same new one")
        }

        return result


    }

}