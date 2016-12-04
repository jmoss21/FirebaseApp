package com.prestech.Trackit;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;


import TrackitDataObjects.Driver;


/**
 * The MainTabActivity is the central hub for the functions of the application.
 * After authentication the Main Screen provides the user with several tabs that
 * allow the user to alter their account, view previous trips, start new trip,
 * and logout.  All these features are tabs within the Main Screen. The Main
 * Screen will handle the transitions of the user from each of the tabs.
 */
public class MainTabActivity extends AppCompatActivity
{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;


    //database references
    private static FirebaseUser firebaseUser;
    private static DatabaseReference mDatabase;


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private static Intent mLoginIntent;

    /******************************************************************************************
     * This is an Activty call back method when the activity and its views are being created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);


        mLoginIntent = getIntent();

        //initialize firebaseAuth and firebaseUser
        firebaseUser = getFirebaseUser();


        //open the login activity if user is null
        if(firebaseUser == null)
        {
            //open the login activity
            startActivity(new Intent(MainTabActivity.this, LoginActivity.class));

        }




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



    }//onCreate() Ens


    /*******************************************************
     *
     * @param auth
     */
    public static void setFirebaseUser(FirebaseAuth auth)
    {
        firebaseUser = auth.getCurrentUser();

    }//setUserAuthentication() Ends


    /******************************************************
     *
     * @return
     */
    public static FirebaseUser getFirebaseUser()
    {
        return firebaseUser;
    }//getUserAuthentication Ends()






    /***********************************************************************
     * This is a menu call back method when the menu option is being created
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_tab, menu);
        return true;
    }//onCreateOptionMenu() Ends


    /**********************************************************
     * This is a menu call back method when an item is selected
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }
        else  if (id == R.id.action_logout)
        {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainTabActivity.this, LoginActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected() Ends




    /*=============================SUB-CLASS OF MainTabActivity==============================
     * This class is responsible for creating the fragments for each tabs
     *
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener
    {

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String FRAGMENT_RESOURCE_ID = "section_number";



        /***** Reference objects used in the "PAST TRIP" tap***/

        //reference to recycle view
        private RecyclerView mRecyclerView;
        //recycleView's adapter
        private RecyclerView.Adapter mAdapter;
        //layoutManager for recyclview
        private LinearLayoutManager llm;
        //holds PAST TRIP data
        private ArrayList<String> mDatabaseResource;

        /******reference to HOME PAGE tab*****/

        //reference to "New Trip" Button
        private Button newTripBtn;
        private String welcomeMessage;
        private TextView welcomeTextView;


        /*****Refernce to "MY ACCOUNT" tab******/

        private EditText firstNameEditView;
        private EditText lastNameEditView;
        private EditText emailEditView;
        private EditText passwordEditView;
        private EditText phoneNumberEditView;


        /**************************************************
         * This is a non-argument Constructor
         */
        public PlaceholderFragment()
        {

        }//constructor ends

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber)
        {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(FRAGMENT_RESOURCE_ID, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }//newInstance(int) Ends



        /************************************************************************************
         * This is a Fragment call back method when views are created
         * @param inflater
         * @param container
         * @param savedInstanceState
         * @return
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {


            mDatabaseResource = new ArrayList<>();
            mDatabaseResource.add("First Trip");
            mDatabaseResource.add("Second Trip");
            mDatabaseResource.add("Third Trip");
            mDatabaseResource.add("Fourth Trip");
            mDatabaseResource.add("Fifth Trip");
            mDatabaseResource.add("sixth Trip");
            mDatabaseResource.add("seventh Trip");


            View rootView = inflater.inflate(getArguments().getInt(FRAGMENT_RESOURCE_ID), container, false);



            /*******************Initialize "PAST TRIP" TAB views******************************/

            //initialize the RecycleView
            mRecyclerView = (RecyclerView)rootView.findViewById(R.id.past_trip_recycler);

            //set up the recycle View's layout and content in the 'PAST TRIP' tab
            setupRecycleViewEnvironment();


            /*******************initialize "HOME" TAB views**************************/
            newTripBtn =  (Button)rootView.findViewById(R.id.home_new_trip_btn);

            welcomeTextView = (TextView)rootView.findViewById(R.id.welcome_textview);

            if(welcomeTextView!=null)
            {
                welcomeMessage = "Welcome \n To \n Trackit \n "+ mLoginIntent.getStringExtra(LoginActivity.LAST_NAME) + "!";

                welcomeTextView.setText(welcomeMessage);
            }

            //if button is not null
            if(newTripBtn != null)
            {
                //set its onClickListener
                newTripBtn.setOnClickListener(this);
            }//if ends





            /**********************Initialize "MY ACCOUNT" TAB views********************/
            firstNameEditView = (EditText) rootView.findViewById(R.id.first_name_field);
            lastNameEditView = (EditText)rootView.findViewById(R.id.last_name_field);
            emailEditView = (EditText)rootView.findViewById(R.id.email_field);
            phoneNumberEditView = (EditText) rootView.findViewById(R.id.phone_field);
            passwordEditView = (EditText)rootView.findViewById(R.id.passsd_field);



            populateUserAccountInfo(mLoginIntent);


            //return the rootView
            return rootView;

        }//onCreateViews() Ends

        @Override
        public void onDestroy() {
            super.onDestroy();

            //mLoginIntent = null;
        }

        /***********************************************************************************
        *This method is the implementation of the OnClickListener; it register the views to
        * a clickable event.
        */
        @Override
        public void onClick(View view)
        {

            switch (view.getId())
            {
                case R.id.home_new_trip_btn:
                    startActivity(new Intent(getContext(),TripInfoActivity.class));
                    break;
            }//switch ends
        }//onClick ends


        /**
         *
         */
         private void setupRecycleViewEnvironment()
         {
             //check if the RecycleView object is available.
             if(mRecyclerView != null)
             {
                 //create a layoutManager
                 llm = new LinearLayoutManager(getContext());

                 //create an adapter object
                 mAdapter = new MyRecyclerAdapter(mDatabaseResource);

                 //set
                 mRecyclerView.setHasFixedSize(true);
                 mRecyclerView.setLayoutManager(llm);
                 mRecyclerView.setAdapter(mAdapter);

             }//if ends
         }//setupRecycleViewEnvironment() Ends


        /**
         *
         */
        private  void populateUserAccountInfo(Intent mLoginIntent)
        {
            if(mLoginIntent != null)
            {

                if(firstNameEditView != null )
                {
                    firstNameEditView.setEnabled(false);
                    firstNameEditView.setText(mLoginIntent.getStringExtra(LoginActivity.FIRST_NAME));
                }

                if(lastNameEditView != null )
                {
                    lastNameEditView.setEnabled(false);
                    lastNameEditView.setText(mLoginIntent.getStringExtra(LoginActivity.LAST_NAME));
                }

                if(passwordEditView != null )
                {
                    passwordEditView.setEnabled(false);

                }
                if(emailEditView != null )
                {
                    emailEditView.setEnabled(false);
                    emailEditView.setText(mLoginIntent.getStringExtra(LoginActivity.EMAIL));
                }

                if(phoneNumberEditView != null )
                {
                    phoneNumberEditView.setEnabled(false);
                    phoneNumberEditView.setText(mLoginIntent.getStringExtra(LoginActivity.PHONE_NUMBER));

                }
            }//populateUserAccountInfo()


        }//populateUserAccountInfo




    }//PlaceholderFragment Class Ends


    /*=========================SUB CLASS OF MainTabActivity=================================
     * This  class is a subclass of the FragmentPagerAdapter Class. It creates the Tabs, and
     * attaches the layouts of the tabs/pagers to their position
     *
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {


        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below)

            if(position==0)
            {
                return PlaceholderFragment.newInstance(R.layout.activity_home_page);

            }
            else if (position==1)
            {

                return PlaceholderFragment.newInstance(R.layout.activity_past_trip);
            }
            else
               return PlaceholderFragment.newInstance(R.layout.activity_my_account);


        }//getItem() Ends


        /*****************************************************************************
         *This is a FragmentPagerAdapter method which return the number TABS to created
         * For this project we are creating 3 tabs
         * @return
         */
        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }//getCount() Ends



        /***********************************************************
         *This method specify the HEADINGS for each of the 3 tabs
         * @param position
         * @return
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "HOME";
                case 1:
                    return "PAST TRIPS";
                case 2:
                    return "MY ACCOUNT";
            }
            return null;
        }//getPageTitle() Ends

    }//SectionsPagerAdapter Class Ends

}//MainTabActivity CLASS EDNS






/*==================================================================================================
 ***************************************************************************************************
 *=====================================INNER CLASS==================================================
 *MyRecyclerAdapter is used to create the Recycle view object needed by the "PAST TRIPS" Tabs to display
 * a the list of past trips
 */
class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder>
 {


     //this will hold the list of trips to be displayed
     private ArrayList<String> mDatabaseResources;


     /**
      *
      * @param mDatabaseResources
      */
     public MyRecyclerAdapter(ArrayList<String> mDatabaseResources)
      {
          this.mDatabaseResources = mDatabaseResources;

      }

     /**
      *
      * @param parent
      * @param viewType
      * @return
      */
     @Override
     public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
     {
         View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_main_tab, parent, false);

         MyViewHolder mViewHolder = new MyViewHolder(view);

         return mViewHolder;

     }//ViewHolder Ends

     /**
      *
      * @param holder
      * @param position
      */
     @Override
     public void onBindViewHolder(MyViewHolder holder, int position)
     {
         holder.dataTextView.setText(mDatabaseResources.get(position));

         //you can register the onClick listener too
     }

     /**
      *
      * @param dataResource
      */
     public void addItem(String dataResource)
     {
         mDatabaseResources.add(dataResource);

         notifyItemInserted(mDatabaseResources.size()-1);
     }
     /**
      *
      * @return
      */
     @Override
     public int getItemCount()
     {
         return mDatabaseResources.size();
     }//getItemCount() Ends

     @Override
     public void onAttachedToRecyclerView(RecyclerView recyclerView) {
         super.onAttachedToRecyclerView(recyclerView);
     }


     /**===================INNER Class To MyRecyclerAdapter=================
      *
      */
     public static class MyViewHolder extends RecyclerView.ViewHolder
     {

         TextView dataTextView;

         /**
          *
          * @param itemView
          */
         public MyViewHolder(View itemView)
         {
             super(itemView);
             dataTextView = (TextView) itemView.findViewById(R.id.recycler_textview);

         }//ViewHolder constructor ends

     }//ViewHolder Ends


 }//MyRecycler Adapter Ends

