package com.example.firebasetrain

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_feed.*


class feed : AppCompatActivity() {


    private lateinit var feedRecyclerAdapter: FeedRecyclerAdapter
    private lateinit var layoutManager: LinearLayoutManager


    private lateinit var auth :FirebaseAuth
    private lateinit var database:FirebaseFirestore
    private lateinit var postList: ArrayList<post>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        postList = ArrayList()
        database = FirebaseFirestore.getInstance()

        pullData()

        refreshLayout.setOnRefreshListener {
            val intent = Intent(this, feed::class.java)
            startActivity(intent)
            refreshLayout.isRefreshing = false
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
    }

    fun pullData() {
        database.collection("Post").orderBy("post_date", Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
            } else {
                if (snapshot != null) {
                    if (!snapshot.isEmpty) {
                        val documents = snapshot.documents

                        postList.clear()
                        for (document in documents) {
                            val userMail = document.get("user_mail") as String
                            val postComment = document.get("post_comment") as String
                            val imageUrl = document.get("image_url") as String

                            val downloadedPost = post(userMail, postComment, imageUrl)

                            postList.add(downloadedPost)
                        }

                        // Set up RecyclerView and adapter here after populating postList
                        layoutManager = LinearLayoutManager(this)
                        feedRecyclerView.layoutManager = layoutManager

                        feedRecyclerAdapter = FeedRecyclerAdapter(postList)
                        feedRecyclerView.adapter = feedRecyclerAdapter
                    }
                }
            }
        }
    }

    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        postList =ArrayList()
        database= FirebaseFirestore.getInstance()
        pullData()

        refreshLayout.setOnRefreshListener {
            val intent = Intent(this,feed::class.java)
            startActivity(intent)
            refreshLayout.isRefreshing = false
        }

        layoutManager=LinearLayoutManager(this)
        feedRecyclerView.layoutManager=layoutManager

        feedRecyclerAdapter= FeedRecyclerAdapter(postList)
        feedRecyclerView.adapter=feedRecyclerAdapter

        auth = FirebaseAuth.getInstance()
        database= FirebaseFirestore.getInstance()

    }

    fun pullData(){
        database.collection("Post").orderBy("post_date",Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception !=null){
                Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
            }
            else{
                if(snapshot !=null){
                    if (!snapshot.isEmpty){
                        val documents =snapshot.documents

                        postList.clear()
                        for(document in documents){
                            val userMail =document.get("user_mail") as String
                            val postComment=document.get("post_comment") as String
                            val imageUrl=document.get("image_url") as String

                            val downloadedPost=post(userMail,postComment,imageUrl)

                            postList.add(downloadedPost)
                        }
                    }
                }
            }
        }
    }
     */

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.share->{
                shareAction()
            }
            R.id.exit ->{
                exitAction()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun exitAction() {
        auth.signOut()
        val intent = Intent(this,welcome::class.java)
        startActivity(intent)
        finish()
    }

    private fun shareAction() {

    val intent = Intent(this,shareActivity::class.java)
        startActivity(intent)
    }





}