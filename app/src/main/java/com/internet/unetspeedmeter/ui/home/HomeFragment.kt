package com.internet.unetspeedmeter.ui.home

import CustomBroadcast
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.internet.unetspeedmeter.CUSTOM_BROADCAST
import com.internet.unetspeedmeter.R
import com.internet.unetspeedmeter.SUM_MOBILE_DOWNLOAD
import com.internet.unetspeedmeter.SUM_MOBILE_UPLOAD
import com.internet.unetspeedmeter.SUM_WIFI_DOWNLOAD
import com.internet.unetspeedmeter.SUM_WIFI_UPLOAD
import com.internet.unetspeedmeter.UNetSpeedMeterApplication
import com.internet.unetspeedmeter.adapter.HomeAdapter
import com.internet.unetspeedmeter.data.InternetDataItem
import com.internet.unetspeedmeter.databinding.FragmentHomeBinding
import com.internet.unetspeedmeter.math.kbToString
import com.internet.unetspeedmeter.service.NotificationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var application:UNetSpeedMeterApplication
    private lateinit var list: List<InternetDataItem>
    private lateinit var adapter:HomeAdapter

    private val customBroadcast = object : CustomBroadcast() {

        override fun onDataReceive(totalSpeed: String) {
            with(binding){
                textViewInternetSpeedText.text = getString(R.string.textview_internet_speed,totalSpeed)
                textViewTodayUpload.text = getString(R.string.textview_today_data_sent,
                    kbToString(requireContext().getSharedPreferences("DATA", Context.MODE_PRIVATE).getLong(
                        SUM_WIFI_UPLOAD, 0))
                )
                textViewTodayDownload.text = getString(R.string.textview_today_data_receive,
                    kbToString(requireContext().getSharedPreferences("DATA", Context.MODE_PRIVATE).getLong(
                        SUM_WIFI_DOWNLOAD, 0))
                )
                textViewTodayMobileUpload.text = getString(R.string.textview_today_mobile_data_sent,
                    kbToString(requireContext().getSharedPreferences("DATA", Context.MODE_PRIVATE).getLong(
                        SUM_MOBILE_UPLOAD, 0))
                )
                textViewTodayMobileDownload.text = getString(R.string.textview_today_mobile_data_receive,
                    kbToString(requireContext().getSharedPreferences("DATA", Context.MODE_PRIVATE).getLong(
                        SUM_MOBILE_DOWNLOAD, 0))
                )

            }


        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "UnspecifiedRegisterReceiverFlag")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().startService(Intent(activity, NotificationService::class.java))
        application = requireActivity().application as UNetSpeedMeterApplication
        adapter = HomeAdapter()


        adapter.context = requireContext()
        with(binding) {

            recyclerview.adapter = adapter

        }

        requireActivity().registerReceiver(customBroadcast, IntentFilter(CUSTOM_BROADCAST))


        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            list = application.appDataContainer.itemsRepository.getAllItemsStream()
            adapter.submitList(list)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        requireActivity().unregisterReceiver(customBroadcast)

        _binding = null
    }


}

