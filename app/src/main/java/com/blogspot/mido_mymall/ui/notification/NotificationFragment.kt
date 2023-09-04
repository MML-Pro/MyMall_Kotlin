package com.blogspot.mido_mymall.ui.notification

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.mido_mymall.databinding.FragmentNotificationBinding
import com.blogspot.mido_mymall.domain.models.NotificationModel
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "NotificationFragment"

@AndroidEntryPoint
class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!


    private val notificationViewModel by viewModels<NotificationViewModel>()

    private val notificationAdapter = NotificationAdapter()

    private val notificationList = arrayListOf<NotificationModel>()

    private var runQuery = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.notificationRV.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = notificationAdapter
        }

        notificationViewModel.getNotifications(false)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                notificationViewModel.notification.collect { response ->

                    when (response) {

                        is Resource.Loading -> {}

                        is Resource.Success -> {
                            if(notificationList.isNotEmpty()) notificationList.clear()

                            response.data?.let {
                                val listSize = it["list_size"] as Long


                                for (i in 0 until listSize ) {
                                    notificationList.add(
                                        NotificationModel(
                                            image = it.get("Image_$i").toString(),
                                            body = it.get("Body_$i").toString(),
                                            beenRead = it.get("been_read_$i") as Boolean
                                        )
                                    )
                                }

                                val readMap = hashMapOf<String, Any>()

                                for (i in 0 until listSize.toInt()) {

                                    if (!notificationList[i].beenRead) {

                                        Log.d(TAG, "notificationList been read: ${notificationList[i].beenRead}")

                                        runQuery = true
                                    }

                                    readMap["been_read_$i"] = true
                                }

                                if (runQuery) {
                                    FirebaseFirestore.getInstance().collection("USERS")
                                        .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                                        .collection("USER_DATA")
                                        .document("MY_NOTIFICATIONS")
                                        .update(readMap)
                                        .addOnSuccessListener {
                                            Log.d(TAG, "update been read success ")
                                        }.addOnFailureListener {
                                            Log.e(TAG, "update been read failed: ${it.message.toString()}",)
                                            Log.e(TAG, "update been read failed: ${it.cause.toString()}",)
                                        }
                                }

                            }.also {
                                notificationAdapter.asyncListDiffer.submitList(notificationList)

                            }
                        }

                        is Resource.Error -> {
                            Log.e(TAG, "onViewCreated: ${response.message.toString()}")
                        }

                        else -> {

                        }
                    }
                }
            }
        }


    }




    override fun onStop() {
        super.onStop()

        for (i in 0 until notificationList.size ) {
            notificationList[i].beenRead = true
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        notificationViewModel.getNotifications(true)
        notificationList.clear()
        _binding = null
    }

}