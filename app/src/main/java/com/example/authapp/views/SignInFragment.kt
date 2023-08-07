package com.example.authapp.views

import android.app.ProgressDialog
import android.content.Context.MODE_PRIVATE
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
import com.example.authapp.databinding.FragmentSignInBinding
import com.example.authapp.viewmodel.AuthViewModel
import com.example.authapp.views.HomeActivity

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
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

        viewModel.userData.observe(viewLifecycleOwner, Observer {
            val sharedPreferences = activity?.getSharedPreferences("MySharedPref", MODE_PRIVATE)
            val myEdit = sharedPreferences?.edit()
            myEdit?.putString("email",  viewModel.userData.value?.email)
            myEdit?.apply()
        })

        viewModel.loggedStatus.observe(viewLifecycleOwner, Observer {
            mProgressDialog.dismiss()
            if (it == true) {
                Toast.makeText(
                    activity,
                    "You are Login Successfully",
                    Toast.LENGTH_SHORT
                ).show()

                activity?.let {
                    requireActivity().finish()
                    val intent = Intent(it, HomeActivity::class.java)
                    it.startActivity(intent)
                }
            } else {
                Toast.makeText(
                    activity,
                    viewModel.error,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        binding.signUpText.setOnClickListener {
            binding.emailEditSignIn.setText("");
            binding.passEditSignIn.setText("");
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.signInBtn.setOnClickListener {
            val email: String = binding.emailEditSignIn.getText().toString()
            val pass: String = binding.passEditSignIn.getText().toString()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(
                    activity,
                    "Please Enter Email & Password",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                mProgressDialog.show()
                viewModel.signIn(email, pass)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}