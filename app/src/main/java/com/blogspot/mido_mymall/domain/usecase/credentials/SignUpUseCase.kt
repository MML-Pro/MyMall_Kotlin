package com.blogspot.mido_mymall.domain.usecase.credentials

import com.blogspot.mido_mymall.domain.models.User
import com.blogspot.mido_mymall.domain.repo.CredentialsRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(private val credentialsRepository: CredentialsRepository) {

    operator fun invoke(user: User, password:String) =
        credentialsRepository.createUserWithEmailAndPassword(user, password)

}