package com.np.suprimpoudel.daythreeassignment.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.np.suprimpoudel.daythreeassignment.utils.FirebaseConstants.Companion.DATA_REFERENCE_URL

class FirebaseService {
    companion object {
        private var firebaseAuth: FirebaseAuth? = null
        private var firebaseStorage: FirebaseStorage? = null
        private var firebaseDatabase: FirebaseDatabase? = null

        @JvmName("getFirebaseAuth1")
        fun getFirebaseAuth(): FirebaseAuth {
            if(firebaseAuth == null) {
                firebaseAuth = FirebaseAuth.getInstance()
                return firebaseAuth as FirebaseAuth
            } else {
                return firebaseAuth as FirebaseAuth
            }
        }

        @JvmName("getFirebaseStorage1")
        fun getFirebaseStorage(): FirebaseStorage {
            if(firebaseStorage == null) {
                firebaseStorage = FirebaseStorage.getInstance()
                return firebaseStorage as FirebaseStorage
            } else {
                return firebaseStorage as FirebaseStorage
            }
        }

        fun getFirebaseDatabase(): FirebaseDatabase {
            if(firebaseDatabase == null) {
                firebaseDatabase = FirebaseDatabase.getInstance(DATA_REFERENCE_URL)
                return firebaseDatabase as FirebaseDatabase
            } else {
                return firebaseDatabase as FirebaseDatabase
            }
        }
    }
}