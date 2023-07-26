package com.internet.unetspeedmeter.ui.home

import CustomBroadcast
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.*
import androidx.fragment.app.Fragment
import com.internet.unetspeedmeter.CUSTOM_BROADCAST
import com.internet.unetspeedmeter.R
import com.internet.unetspeedmeter.UNetSpeedMeterApplication
import com.internet.unetspeedmeter.adapter.HomeAdapter
import com.internet.unetspeedmeter.data.InternetDataItem
import com.internet.unetspeedmeter.databinding.FragmentHomeBinding
import com.internet.unetspeedmeter.math.kbToString
import com.internet.unetspeedmeter.service.NotificationService
import com.internet.unetspeedmeter.singleton.DailyDataSingleton
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

        override fun onDataReceive(speedSend:String,speedReceiver: String) {
            with(binding){
                textViewInternetSpeedText.text = getString(R.string.textview_internet_speed,speedSend,speedReceiver)
                textViewTodayUpload.text = getString(R.string.textview_today_data_sent,
                    kbToString(DailyDataSingleton.wifiDataSend)
                )
                textViewTodayDownload.text = getString(R.string.textview_today_data_receive,
                    kbToString(DailyDataSingleton.wifiDataReceived)
                )
                textViewTodayMobileUpload.text = getString(R.string.textview_today_mobile_data_sent,
                    kbToString(DailyDataSingleton.mobileDataSend)
                )
                textViewTodayMobileDownload.text = getString(R.string.textview_today_mobile_data_receive,
                    kbToString(DailyDataSingleton.mobileDataReceived)
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().startService(Intent(activity, NotificationService::class.java))
        application = requireActivity().application as UNetSpeedMeterApplication
        adapter = HomeAdapter()

        adapter.context = requireContext()
        with(binding) {

            recyclerview.adapter = adapter

        }

        CoroutineScope(Dispatchers.IO).launch {
            list = application.appDataContainer.itemsRepository.getAllItemsStream()
            adapter.submitList(list)
            adapter.notifyDataSetChanged()

        }

        requireActivity().registerReceiver(customBroadcast, IntentFilter(CUSTOM_BROADCAST))

    }

    override fun onDestroy() {
        super.onDestroy()

        requireActivity().unregisterReceiver(customBroadcast)

        _binding = null
    }


}

