package com.example.authapp.views

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.authapp.R
import com.example.authapp.databinding.FragmentSignUpBinding
import com.example.authapp.viewmodel.AuthViewModel

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: AuthViewModel
    val IMAGE_PICK_CODE = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mProgressDialog = ProgressDialog(activity)
        mProgressDialog.setMessage("Loading...")
        viewModel =
            ViewModelProvider(this).get(
                AuthViewModel::class.java
            )

        viewModel.registerStatus.observe(viewLifecycleOwner, Observer {
            mProgressDialog.dismiss()
            if (it == true) {
                Toast.makeText(
                    activity,
                    "You are Register Successfully",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
            } else {
                Toast.makeText(
                    activity,
                    viewModel.error,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        binding.profileImage.setOnClickListener {
            pickImageFromGallery()
        }
        binding.signInText.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
        binding.signUpBtn.setOnClickListener {
            val email: String = binding.emailEditSignUp.getText().toString()
            val pass: String = binding.passEditSignUp.getText().toString()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(
                    activity,
                    "Please Enter Email & Password",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (pass.length < 6) {
                Toast.makeText(
                    activity,
                    "Please Enter Minimum 6 Digit Password",
                    Toast.LENGTH_SHORT
                ).show()
            } else{
                mProgressDialog.show()
                viewModel.register(email, pass)
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            binding.profileImage.setImageURI(data?.data)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}