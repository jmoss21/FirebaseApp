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


    private static FirebaseUser firebaseUser;
    private DatabaseReference mDatabase;
    private String driverId;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    /******************************************************************************************
     * This is an Activty call back method when the activity and its views are being created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);


        //initialize firebaseAuth and firebaseUser
        firebaseUser = getFirebaseUser();

        //open the login activity if user is null
        if(firebaseUser == null)
        {
            startActivity(new Intent(MainTabActivity.this, LoginActivity.class));

        }//if ends
        else {

            driverId = firebaseUser.getUid();

            //Initializes the database (Firebase)
            mDatabase = FirebaseDatabase.getInstance().getReference("trips");

           // mDatabase.setValue(true);

            mDatabase.addValueEventListener(databaseValueEventListener);

            // mDatabase.addChildEventListener(childEventListener);

            querryFirebase();

        }//else ends



        //register the database reference with an even listener
       // mDatabase.addChildEventListener(databaseValueEventListener);


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


    /**
     *
     */
     private void querryFirebase()
     {
         Query query = mDatabase;
         query.addValueEventListener(databaseValueEventListener);
     }//querryFirebase ends


    /**
     * ValueEventListener listens for changes in database. It implements two
     * methods
     */
    ValueEventListener databaseValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {


            Iterable<DataSnapshot> iterableDataSnapshots = dataSnapshot.getChildren();
            Iterator<DataSnapshot> dataSnapshotIterator = iterableDataSnapshots.iterator();

            Log.i("c DATABASE_TRIGERRING",dataSnapshotIterator.next().toString());

            int count = 0;
            while (dataSnapshotIterator.hasNext())
            {
                count++;

                Log.i("c "+count+" DATABASE_TRIGERRING",dataSnapshotIterator.next().toString());

            }//while ends

        }//onDataChange() //Ends

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }//onCancelled() Ends
    };

    /*ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            Log.i("2: DATABASE_TRIGER",dataSnapshot.toString()+ "   "+ s);

        }//onChildAdded Ends

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
*/
    /**
     *
     * @param auth
     */
    public static void setFirebaseUser(FirebaseAuth auth)
    {
        firebaseUser = auth.getCurrentUser();
    }//setUserAuthentication Ends()


    /**
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
        private static final String ARG_SECTION_NUMBER = "section_number";


        RecyclerView mRecyclerView;
        private RecyclerView.Adapter mAdapter;
        LinearLayoutManager llm;

        private ArrayList<String> mDatabaseResource;

        //reference to HOME PAGE view
        private Button newTripBtn;


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
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }



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


            View rootView = inflater.inflate(getArguments().getInt(ARG_SECTION_NUMBER), container, false);

            //initialize homepage views
            newTripBtn =  (Button)rootView.findViewById(R.id.home_new_trip_btn);

            if(newTripBtn != null)
            {
                newTripBtn.setOnClickListener(this);
            }//if ends

            mRecyclerView = (RecyclerView)rootView.findViewById(R.id.past_trip_recycler);


            if(mRecyclerView != null)
            {
                llm = new LinearLayoutManager(getContext());
                mRecyclerView.setHasFixedSize(true);
                mAdapter = new MyRecyclerAdapter(mDatabaseResource);
                mRecyclerView.setLayoutManager(llm);
                mRecyclerView.setAdapter(mAdapter);

            }



            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
           // textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            return rootView;
        }//onCreateViews() Ends


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
 *==================================================================================================
 *
 */
class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder>
 {


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

