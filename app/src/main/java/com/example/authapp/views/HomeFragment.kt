package com.example.authapp.views

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.authapp.adapter.RVAdapter
import com.example.authapp.databinding.FragmentHomeBinding
import com.example.authapp.model.Image_Item
import com.example.authapp.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    val imageList = ArrayList<Image_Item>()
    var urlList = ArrayList<String>()
    var selectAll = false

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.layoutManager = GridLayoutManager(activity, 3)
        val adapter = RVAdapter()
        binding.recyclerview.adapter = adapter

        binding.fabDownload.visibility = View.GONE
        binding.tvStart.setOnClickListener {
            activity?.let { it1 -> homeViewModel.openDatePickerDialog("", it1) }
        }

        binding.tvEnd.setOnClickListener {
            if (binding.tvStart.text == "") {
                Toast.makeText(activity, "Please Select Start Date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            activity?.let { it1 ->
                homeViewModel.openDatePickerDialog(
                    binding.tvStart.text.toString(),
                    it1
                )
            }
        }

        homeViewModel.selectedStartDate.observe(viewLifecycleOwner) {
            binding.tvStart.setText(it)
        }

        homeViewModel.selectedEndDate.observe(viewLifecycleOwner) {
            binding.tvEnd.setText(it)
        }

        binding.ivFilter.setOnClickListener(View.OnClickListener {
            if (!binding.tvStart.text.toString().equals("") && !binding.tvEnd.text.toString()
                    .equals("")
            ) {
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val startDate: Date = sdf.parse(binding.tvStart.text.toString()) as Date
                val endDate: Date = sdf.parse(binding.tvEnd.text.toString()) as Date
                homeViewModel.filterData(startDate, endDate)
                urlList.clear()
                binding.fabDownload.visibility = View.GONE
                binding.tvClearFilter.visibility = View.VISIBLE
            } else {
                Toast.makeText(context, "Please Select Date", Toast.LENGTH_LONG).show()
            }
        })

        binding.tvSelectAll.setOnClickListener(View.OnClickListener {
            urlList.clear()
            for (i in imageList.indices) {
                if (!selectAll) {
                    imageList.get(i).selected = true
                    urlList.add(imageList.get(i).download_url.toString())
                } else {
                    imageList.get(i).selected = false
                }
            }
            selectAll = !selectAll

            if (urlList.size > 0) {
                binding.fabDownload.visibility = View.VISIBLE
            } else {
                binding.fabDownload.visibility = View.GONE
            }
            adapter.notifyDataSetChanged()
        })

        binding.tvClearFilter.setOnClickListener(View.OnClickListener {
            binding.tvStart.setText("")
            binding.tvEnd.setText("")
            urlList.clear()
            binding.fabDownload.visibility = View.GONE
            binding.tvClearFilter.visibility = View.GONE
            homeViewModel.getImageList()
        })

        binding.fabDownload.setOnClickListener(View.OnClickListener {
            if (urlList.size > 0) {
                activity?.let { it1 -> homeViewModel.downloadImages(urlList, it1) }
            }
        })

        adapter.addItemClickListener(object : RVAdapter.ItemClickListener {
            override fun onItemClick(position: Int, action: Int) {
                imageList.get(position).selected = action == 0
                if (urlList.contains(imageList.get(position).download_url)) {
                    urlList.remove(imageList.get(position).download_url)
                } else {
                    urlList.add(imageList.get(position).download_url.toString())
                }
                if (urlList.size > 0) {
                    binding.fabDownload.visibility = View.VISIBLE
                } else {
                    binding.fabDownload.visibility = View.GONE
                }
                adapter.notifyItemChanged(position)
            }
        })

        val mProgressDialog = ProgressDialog(activity)
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        homeViewModel.getImageList()
        homeViewModel.imageList.observe(viewLifecycleOwner) {
            mProgressDialog.dismiss()
            imageList.clear()
            urlList.clear()
            imageList.addAll(it)
            adapter.setImageList(imageList)
            if (imageList.size == 0) {
                binding.tvNoData.visibility = View.VISIBLE
            } else {
                binding.tvNoData.visibility = View.GONE
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}