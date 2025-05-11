package com.blogspot.mido_mymall.ui.my_rewards

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.mido_mymall.databinding.FragmentMyRewardBinding
import com.blogspot.mido_mymall.domain.models.RewardModel
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Date

@AndroidEntryPoint
class MyRewardsFragment : Fragment() {

    companion object {
        private const val TAG = "MyRewardsFragment"
    }

    private var _binding: FragmentMyRewardBinding? = null
    private val binding get() = _binding!!

    private val myRewardsAdapter by  lazy {  MyRewardsAdapter(false) }

    private val rewardModelList = arrayListOf<RewardModel>()

    private val viewModel by viewModels<MyRewardsViewModel>()

    private var lastSeen: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyRewardBinding.inflate(inflater)



        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if(FirebaseAuth.getInstance().currentUser != null) {
            viewModel.getUserLastSeen()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userLastSeen.collect { response ->

                    when (response) {

                        is Resource.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            response.data?.let {
                                lastSeen = it.getDate("Last seen")
                            }.also {
                                viewModel.getRewards()
                            }
                        }

                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Log.e(TAG, "userLastSeen: ${response.message.toString()}")
                        }

                        else -> {}
                    }


                }
            }
        }

//        if(rewardModelList.isEmpty()){
//            viewModel.getRewards()
//        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.rewardList.collect { response ->

                    when (response) {

                        is Resource.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            response.data?.let { querySnapshot ->
                                querySnapshot.documents.forEach {

                                    if (it["type"].toString().equals("Discount")
                                        && lastSeen?.before((it.getDate("validity") as Date)) == true) {
                                        rewardModelList.add(
                                            RewardModel(
                                                it.id,
                                                it["type"].toString(),
                                                it["lower_limit"].toString(),
                                                it["upper_limit"].toString(),
                                                it["percentage"].toString(),
                                                it["body"].toString(),
                                                it["validity"] as Timestamp,
                                                it["already_used"] as Boolean

                                            )
                                        )

                                        Log.d(TAG, "rewardList: body ${it.get("body")}")

                                    } else if (it["type"].toString().equals("Flat Rs. * OFF")
                                        && lastSeen?.before((it.getDate("validity") as Date)) == true
                                    ) {
                                        rewardModelList.add(
                                            RewardModel(
                                                it.id,
                                                it["type"].toString(),
                                                it["lower_limit"].toString(),
                                                it["upper_limit"].toString(),
                                                it["amount"].toString(),
                                                it["body"].toString(),
                                                it["validity"] as Timestamp,
                                                it["already_used"] as Boolean

                                            )
                                        )
                                    }

                                    Log.d(TAG, "rewardList: body ${it.get("body")}")


                                }.also {
                                    binding.progressBar.visibility = View.GONE
                                    myRewardsAdapter.asyncListDiffer.submitList(rewardModelList)
                                }
                            }
                        }

                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Log.e(TAG, "rewardList: ${response.message.toString()}")
                        }

                        else -> {}
                    }

                }
            }
        }


        binding.myRewardsRV.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = myRewardsAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rewardModelList.clear()
        _binding = null
    }
}