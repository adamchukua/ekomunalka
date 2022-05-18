package com.example.ekomunalka.ui.home;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ekomunalka.DatabaseHelper;
import com.example.ekomunalka.R;
import com.example.ekomunalka.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    DatabaseHelper db;
    Button addData;
    EditText editText;
    ListView listView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = new DatabaseHelper(getContext());
        editText = (EditText) view.findViewById(R.id.editText);
        addData = (Button) view.findViewById(R.id.addData);
        listView = (ListView) view.findViewById(R.id.listView);

        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEntry = editText.getText().toString();
                if (editText.length() != 0){
                    AddData(newEntry);
                    editText.setText("");
                    RefreshList();
                } else {
                    Toast.makeText(getActivity(), "You must put something in the text field!", Toast.LENGTH_LONG).show();
                }
            }
        });

        RefreshList();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    public void AddData(String newEntry) {
        boolean insertData = db.addData(newEntry);

        if (insertData) {
            Toast.makeText(getActivity(), "Data Successfully Inserted!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "Something went wrong :(.", Toast.LENGTH_LONG).show();
        }
    }

    public void RefreshList() {
        ArrayList<String> arrayList = new ArrayList<>();
        Cursor data = db.getListContents();
        if (data.getCount() != 0) {
            while (data.moveToNext()) {
                arrayList.add(data.getString(1));
                ListAdapter listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(listAdapter);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}