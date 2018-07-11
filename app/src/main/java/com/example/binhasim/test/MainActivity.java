package com.example.binhasim.test;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;
import static java.text.DateFormat.getDateTimeInstance;

public class MainActivity extends AppCompatActivity implements ItemsAdapter.clickListener {
    private static final String ADD_BUTTON_TITLE = "add";
    private static final String ENTRY_FRAGMENT_TAG = "Entry_Fragment";
    private static final String SHOW_DETAILS_FRAGMENT_TAG = "show_details_fragment";
    private static final String TOOLBAR_TITLE_TEXT="back";
    private static final String SIGN_OUT = "Sign Out";

    private static final int ADD_BUTTON_ID=6787;
    private static final int SIGN_OUT_ID = 567788;

    private Logic Logic;
    ActionMode actionMode;
    callback CAB_Callback = new callback();
    HorizontalAdapter horizontalAdapter;
    ItemsAdapter adapter;
    ActionBar actionBar;
    Toolbar toolbar;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         mRecyclerView = findViewById(R.id.Test_Recycler_view);


        firebaseDatabase = FirebaseDatabase.getInstance();
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("userID", userid);
        databaseReference = firebaseDatabase.getReference().child("Users").child(userid).child("Items");

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator() {});
        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);




        Logic = new Logic();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        fragmentManager = getSupportFragmentManager();

    }

    @Override
    public void onItemClicked(View v, int position) {
        if (actionMode != null) {
            Logic.ToggleSelection(v, position);
            actionMode.setTitle(String.valueOf(Logic.getNumberOfSelectionsMade()));
        } else {

                    fragmentTransaction = fragmentManager.beginTransaction();
            DetailsFragment detailsFragment = new DetailsFragment();
            horizontalAdapter = new HorizontalAdapter();
            detailsFragment.setAdapter(horizontalAdapter, position);
            fragmentTransaction.replace(R.id.Details_Fragment, detailsFragment, SHOW_DETAILS_FRAGMENT_TAG).addToBackStack(SHOW_DETAILS_FRAGMENT_TAG).commit();
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            actionBar.setDisplayHomeAsUpEnabled(false);

        } else if(getSupportFragmentManager().getBackStackEntryCount()!=0){
            actionBar.setTitle(TOOLBAR_TITLE_TEXT);
        }

    }
    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                datafromFirebase=new ArrayList<>();entryKeys=new ArrayList<>();
                for(DataSnapshot entrySnapshot:dataSnapshot.getChildren())
                {
                    DataFormat data=entrySnapshot.getValue(DataFormat.class);
                    DataFormat information=new DataFormat(data.getMainText(),data.getDateTime());
                    datafromFirebase.add(information);
                    entryKeys.add(entrySnapshot.getKey());

                }
                mRecyclerView.setAdapter(new ItemsAdapter(MainActivity.this,datafromFirebase));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(mRecyclerView,"DataBase Error",Snackbar.LENGTH_LONG);
            }
        });

    }
ArrayList<DataFormat> datafromFirebase;
    ArrayList<String> entryKeys;
    @Override
    public boolean onItemLongClicked(View v, int position) {

        if (actionMode == null) {
            actionMode = startActionMode(CAB_Callback);
        }
        Logic.ToggleSelection(v, position);
        int count = Logic.getNumberOfSelectionsMade();
        actionMode.setTitle(String.valueOf(count));
        toolbar.setVisibility(View.INVISIBLE);
        return true;
    }

    public void shareButton(int currentPosition) {
Logic.ShareSelectedItems(currentPosition);
    }


    public void deleteButton(int currentPosition) {
        //Logic.deleteSelected(currentPosition);
deleteFromDataBase(entryKeys.get(currentPosition));
//        horizontalAdapter.notifyItemRemoved(currentPosition);
//        adapter.notifyItemRemoved(currentPosition);
    }


    public void FavouriteButton(int currentPosition) {
Logic.setFavourite(currentPosition);
    }
    EntryFragment entryFragment;

    public void EditButton(int currentPosition) {
        newEntry = false;
        fragmentTransaction = fragmentManager.beginTransaction();
        entryFragment = EntryFragment.newInstance(ItemsAdapter.list.get(currentPosition).getMainText(), currentPosition);
        fragmentTransaction.replace(R.id.Details_Fragment, entryFragment, ENTRY_FRAGMENT_TAG).addToBackStack(ENTRY_FRAGMENT_TAG).commit();
    }


    public void onBackButtonPressed() {
        onBackPressed();
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if(imm!=null)
            imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    void deleteFromDataBase(String itemKey)
    {
       final DatabaseReference itemToBeDeleted= databaseReference.child(itemKey);
       itemToBeDeleted.removeValue();
    }

    void EditFromDataBase(String itemKey,String newText)
    {
        final DatabaseReference itemToBeEdited= databaseReference.child(itemKey);
        itemToBeEdited.setValue(new DataFormat(newText,ServerValue.TIMESTAMP));
    }
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    String mUserId;
    public void onAddButtonPressed(DataFormat newData) {
        onBackButtonPressed();
      //  adapter.notifyDataSetChanged();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            Intent mainMenu=new Intent(this,LoginActivity.class);
            startActivity(mainMenu);
        }
        else
        {
            mUserId = mFirebaseUser.getUid();
        }
        save(newData);
    }

    private void save( DataFormat data)
    {
            databaseReference.push().setValue(data);
            Log.d("USER ID",mUserId);
            Toast.makeText(this, "Item Added", Toast.LENGTH_SHORT).show();
    }


    public void onUpdateData(int Position, String text) {
        onBackButtonPressed();
        EditFromDataBase(entryKeys.get(Position),text);
       // Logic.updateData( Position, text);
        //adapter.notifyDataSetChanged();
        horizontalAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0)
            menu.add(0, ADD_BUTTON_ID, 0, ADD_BUTTON_TITLE).setIcon(R.drawable.ic_action_add).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, SIGN_OUT_ID, 0, SIGN_OUT).setIcon(R.drawable.ic_action_sign_out).setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    private void openAddFragment() {
        newEntry = true;
        entryFragment = new EntryFragment();
                fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.Details_Fragment, entryFragment, ENTRY_FRAGMENT_TAG).addToBackStack(ENTRY_FRAGMENT_TAG).commit();
    }

   private boolean newEntry = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DetailsFragment detailsPage = (DetailsFragment) getSupportFragmentManager().findFragmentByTag(SHOW_DETAILS_FRAGMENT_TAG);
        EntryFragment EntryPage = (EntryFragment) getSupportFragmentManager().findFragmentByTag(ENTRY_FRAGMENT_TAG);
        switch (item.getItemId()) {
            case ADD_BUTTON_ID:
                openAddFragment();
                return true;
            case Constants.CANCEL_ID:
                onBackButtonPressed();
                return true;
            case Constants.SAVE_ID:

                if (newEntry) {
                    if (!(EntryPage.giveThemData().getMainText().isEmpty() || EntryPage.giveThemData().getMainText() == null)) {
                        onAddButtonPressed(EntryPage.giveThemData());
                        return true;
                    } else
                        onBackButtonPressed();
                    return true;

                } else {
                    if (EntryPage.textChanged) {
                        onUpdateData(EntryPage.passDataEntered().getInt(Constants.KEY_FOR_POSITION_OF_ITEM_IN_VIEW), EntryPage.passDataEntered().getString(Constants.KEY_TO_GET_TEXT_ENTERED));
                        return true;
                    } else {
                        onBackButtonPressed();
                    }
                }
            case R.id.delete:
                deleteButton(detailsPage.getPosition());

                return true;
            case R.id.edit:
                EditButton(detailsPage.getPosition());
                return true;
            case SIGN_OUT_ID:
                signOut();
                return true;
            default:
                return false;
        }
    }

    private class callback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.context_action_bar, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete: {
                    for (Integer itemSelected:Logic.getSelectedItems()) {
                        deleteFromDataBase(entryKeys.get(itemSelected));
                    }
                  //  Logic.DeleteSelectedItems();

                  //  adapter.notifyDataSetChanged();
                    if(toolbar.getVisibility()==View.INVISIBLE)
                    {
                        toolbar.setVisibility(View.VISIBLE);
                    }
                    mode.finish();

                    return true;
                }
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if(toolbar.getVisibility()==View.INVISIBLE)
            {
                toolbar.setVisibility(View.VISIBLE);
            }
            Logic.clearSelection();
            actionMode = null;
        }
    }


    public void signOut()
    {

        //firebase sign out
        FirebaseAuth.getInstance().signOut();
        Intent signOutInt;
startActivity(new Intent(this,LoginActivity.class).putExtra("signOut",Constants.signOutFlag));
            // Google sign out
    }
}