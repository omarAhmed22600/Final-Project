package com.udacity.project4.locationreminders.reminderslist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.authentication.FirebaseUserLiveData
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentRemindersBinding
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import com.udacity.project4.utils.setTitle
import com.udacity.project4.utils.setup
import org.koin.android.ext.android.bind
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReminderListFragment : BaseFragment() {

    companion object {
        const val TAG = "ReminderListFragment"
        const val SIGN_IN_RESULT_CODE = 1001
    }

    //use Koin to retrieve the ViewModel instance
    override val _viewModel: RemindersListViewModel by viewModel()
    private lateinit var binding: FragmentRemindersBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_reminders, container, false
            )
        _viewModel.authenticationState.observe(viewLifecycleOwner, Observer {
            if (_viewModel.authenticationState.value == RemindersListViewModel.AuthenticationState.AUTHENTICATED) {
                setTitle("Welcome : " + FirebaseAuth.getInstance().currentUser?.displayName)
            } else {
                Log.i(TAG,"aUTHENTICATED")
                _viewModel.navigationCommand.postValue(
                    NavigationCommand.To(
                        ReminderListFragmentDirections.actionReminderListFragmentToAuthenticationActivity()
                    )
                )
            }
        })
        binding.viewModel = _viewModel
        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(false)
        binding.refreshLayout.setOnRefreshListener { _viewModel.loadReminders() }
        _viewModel.authenticationState.observe(viewLifecycleOwner, Observer {
            activity?.invalidateOptionsMenu()
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        setupRecyclerView()
        binding.addReminderFAB.setOnClickListener {
            navigateToAddReminder()
        }
    }

    override fun onResume() {
        super.onResume()
        //load the reminders list on the ui
        _viewModel.loadReminders()
    }

    private fun navigateToAddReminder() {
        //use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                ReminderListFragmentDirections.toSaveReminder()
            )
        )
    }

    private fun setupRecyclerView() {
        val adapter = RemindersListAdapter {
        }
//        setup the recycler view using the extension function
        binding.reminderssRecyclerView.setup(adapter)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                AuthUI.getInstance().signOut(requireContext())
            }
        }
        return super.onOptionsItemSelected(item)
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
//        display logout as menu item
        inflater.inflate(R.menu.main_menu, menu)
        var logoutBtn = menu.findItem(R.id.logout)
        logoutBtn.title = getString(R.string.logout)
    }

}
