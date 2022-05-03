package com.np.suprimpoudel.daythreeassignment.features.fragment.user_dashboard

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.np.suprimpoudel.daythreeassignment.R
import com.np.suprimpoudel.daythreeassignment.databinding.FragmentHomeBinding
import com.np.suprimpoudel.daythreeassignment.features.shared.adapter.PostAdapter
import com.np.suprimpoudel.daythreeassignment.features.shared.model.PostDetails
import com.np.suprimpoudel.daythreeassignment.features.shared.model.User
import com.np.suprimpoudel.daythreeassignment.network.FirebaseService
import com.np.suprimpoudel.daythreeassignment.utils.FirebaseConstants
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userData: DatabaseReference
    private lateinit var postDatabaseReference: DatabaseReference
    private lateinit var dialog: Dialog
    private lateinit var user: User

    private var postAdapter: PostAdapter = PostAdapter(ArrayList())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)

        initDialog()
        initFirebase()
        initListener()

        dialog.show()

        initDBReference()

        populateData(userData)

        populatePost()

        postAdapter = PostAdapter(ArrayList())
        binding.recyclerPosts.apply {
            setHasFixedSize(false)
            adapter = postAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initListener() {
        binding.btnPost.setOnClickListener {
            postContent()
        }
    }

    private fun initDialog() {
        this.dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_dialog)
        dialog.setCancelable(false)
    }

    private fun initFirebase() {
        firebaseDatabase = FirebaseService.getFirebaseDatabase()
        firebaseAuth = FirebaseService.getFirebaseAuth()
    }

    private fun initDBReference() {
        userData = firebaseDatabase
            .reference.child(FirebaseConstants.DATA_USERS)

        postDatabaseReference = firebaseDatabase
            .reference.child(FirebaseConstants.DATA_POSTS)
    }

    private fun populateData(userData: DatabaseReference) {
        userData.child(firebaseAuth.currentUser?.uid!!).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)!!

                dialog.dismiss()

                Glide.with(requireView())
                    .load(user.profileImageUrl)
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .error(R.drawable.ic_baseline_person_24)
                    .into(binding.imgUserProfilePicture)
            }

            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
                Toast.makeText(context, "Error Fetching User Info", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun postContent() {
        val messageByUser = binding.edtPostDetails.text.toString().trim()
        if (messageByUser.isNotEmpty()) {
            dialog.show()

            val currentDate: String =
                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

            val message = PostDetails(
                postBy = user.fullName,
                postUserPhoto = user.profileImageUrl,
                postContent = messageByUser,
                time = currentDate
            )

            val key = postDatabaseReference.push().key

            postDatabaseReference.child(key.toString()).child(FirebaseConstants.POST_DETAIL).setValue(message)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        dialog.dismiss()
                        Toast.makeText(context, "Post Added. Refresh to see changes", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    dialog.dismiss()
                    Toast.makeText(context, "Error sending message", Toast.LENGTH_SHORT).show()
                }

            binding.edtPostDetails.setText("", TextView.BufferType.EDITABLE)
        }
    }

    private fun populatePost() {
        postDatabaseReference
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChildren()) {
                        binding.txtNoMorePost.visibility = View.GONE
                        snapshot.children.forEach { dataSnapshot ->
                            val postId = dataSnapshot.key
                            if(postId != null) {
                                postDatabaseReference.child(postId).child(FirebaseConstants.POST_DETAIL)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(singleData: DataSnapshot) {
                                            val post = singleData.getValue(PostDetails::class.java)
                                            if (post != null) {
                                                val posts = PostDetails(
                                                    postBy = post.postBy,
                                                    postUserPhoto = post.postUserPhoto,
                                                    postContent = post.postContent,
                                                    time = post.time
                                                )
                                                postAdapter.addMessage(posts)
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                        }

                                    })
                            }
                        }
                    } else {
                        binding.txtNoMorePost.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }
}