package com.example.binhasim.test;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.ServerValue;


public class EntryFragment extends Fragment {
    private static final String TEXT_TO_EDIT = "text_to_be_Edited";
    private static final String POSITION = "position";

    private String TextToEdit;
    private int position;

    public EntryFragment() {
        // Required empty public constructor
    }
    public static EntryFragment newInstance(String textToEdit, int positionOfText) {
        EntryFragment fragment = new EntryFragment();
        Bundle args = new Bundle();
        args.putString(TEXT_TO_EDIT, textToEdit);
        args.putInt(POSITION, positionOfText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            TextToEdit = getArguments().getString(TEXT_TO_EDIT);
            position = getArguments().getInt(POSITION);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit, container, false);
    }

    LinedEditText editTextView;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextView=view.findViewById(R.id.editText);
        editTextView.setText(TextToEdit);
        editTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textChanged=true;
            }
        });
    }
    boolean textChanged;


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.add(0,Constants.CANCEL_ID,Menu.NONE,R.string.cancel_item_id).setIcon(R.drawable.ic_action_cancel).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0,Constants.SAVE_ID,Menu.NONE,R.string.save_item_id).setIcon(R.drawable.ic_action_tick).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }
    public DataFormat giveThemData()
    {
      return  new DataFormat(editTextView.getText().toString(), ServerValue.TIMESTAMP)  ;
    }

    public Bundle passDataEntered()
    {
        Bundle bundle=new Bundle();
        bundle.putInt(Constants.KEY_FOR_POSITION_OF_ITEM_IN_VIEW,position);
        bundle.putString(Constants.KEY_TO_GET_TEXT_ENTERED,editTextView.getText().toString());
        return bundle;
    }

}
