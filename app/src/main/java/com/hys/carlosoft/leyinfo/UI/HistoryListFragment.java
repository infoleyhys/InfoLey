/*
 * InfoLey 4.2 - 2024 
 * Copyright (C) 2017-2024 Carlos A. Martínez 
 * 
 * email: infoleyhys@gmail.com
 * web: consultoramartinez.com.ar
 * 
 * Rosario, Argentina.
 */
package com.hys.carlosoft.leyinfo.UI;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hys.carlosoft.leyinfo.Model.MenuItemViewModel;
import com.hys.carlosoft.leyinfo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnHistory_Listener}
 * interface.
 */
public class HistoryListFragment extends Fragment {

    private static final String ST_URL_CHILD = "ST_URL_CHILD";
    private static final String ST_URL_HEADER = "ST_URL_HEADER";
    private OnHistory_Listener mListener;
    private List<MenuItem> mHistoryItems;
    private RecyclerView mHistoryList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HistoryListFragment() {
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (!mHistoryItems.isEmpty()) {
            ArrayList<Integer> groupList = new ArrayList<>();
            ArrayList<Integer> childList = new ArrayList<>();

            for (MenuItem item : mHistoryItems) {

                groupList.add(item.getGroupID());
                childList.add(item.getTextID());
            }
            //guardo el historial de items abiertos
            outState.putIntegerArrayList(ST_URL_CHILD, childList);
            outState.putIntegerArrayList(ST_URL_HEADER, groupList);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_selectedItem_list, container, false);
        mHistoryList = (RecyclerView) view;

        mHistoryItems = new ArrayList<>();
        MenuItemViewModel viewModel = new ViewModelProvider(requireActivity()).get(MenuItemViewModel.class);
      // Observo el ViewModel para recibir los items seleccionados
        viewModel.getItem().observe(getViewLifecycleOwner(), selectedItem -> {
            if (!mHistoryItems.contains(selectedItem))
                mHistoryItems.add(selectedItem);
        });
        // Si hay un historial guardado, lo recupero
        if (savedInstanceState != null) {
            ArrayList<Integer> childlist = savedInstanceState.getIntegerArrayList(ST_URL_CHILD);
            ArrayList<Integer> headerlist = savedInstanceState.getIntegerArrayList(ST_URL_HEADER);
          
            if (childlist != null && headerlist != null)
                mHistoryItems = viewModel.getItemsFromIds(headerlist, childlist);
        }
        initRecyclerView();

        return view;
    }



    private void initRecyclerView() {
        HistoryListAdapter mAdapter = new HistoryListAdapter(getContext(), mHistoryItems, mListener);
        mHistoryList.setLayoutManager(new LinearLayoutManager(mHistoryList.getContext()));
        mHistoryList.setAdapter(mAdapter);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnHistory_Listener) {
            mListener = (OnHistory_Listener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnHistory_Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public boolean isHistoryOpen() {
        return mHistoryList != null && mHistoryList.getVisibility() == View.VISIBLE;
    }

    public void showHistory() {
        if (mHistoryList != null)
            mHistoryList.setVisibility(View.VISIBLE);
    }

    public void hideHistory() {
        if (mHistoryList != null)
            mHistoryList.setVisibility(View.GONE);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnHistory_Listener {

        void onHistoryItemSelect(MenuItem item);
    }
}
