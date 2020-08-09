package com.example.kshitijverma.beat_my_recipie;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExploreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExploreFragment extends Fragment {

    //ListView lv;
   // FirebaseListAdapter adapter;
    /*
    private RecyclerView explore_recycler_view;
    private List<explore_class>explore_list;
    private FirebaseFirestore firebaseFirestore;
    private explore_recycler_adapter EXPLORE_RECYCLER_ADAPTER;

     */

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public ExploreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExploreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExploreFragment newInstance(String param1, String param2) {
        ExploreFragment fragment = new ExploreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //lv=(ListView)getView().findViewById(R.id.explore_listview);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Intent toExploreIntent=new Intent(getActivity(),Explore_Activity.class);
        startActivity(toExploreIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_explore,container,false);
        /*
        explore_list=new ArrayList<>();

        explore_recycler_view=view.findViewById(R.id.explore_recycler_view_xml);
        EXPLORE_RECYCLER_ADAPTER=new explore_recycler_adapter(explore_list);
        explore_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        explore_recycler_view.setHasFixedSize(true);
        explore_recycler_view.setAdapter(EXPLORE_RECYCLER_ADAPTER);
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseFirestore.collection("explore_adapter_class").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges())
                {
                    if(doc.getType()==DocumentChange.Type.ADDED)
                    {
                        explore_class explore_class=doc.getDocument().toObject(explore_class.class);
                        explore_list.add(explore_class);
                        EXPLORE_RECYCLER_ADAPTER.notifyDataSetChanged();
                    }
                }
            }
        });


        /*
        adapter = new FirebaseListAdapter<explore_adapter_class>(options) {
            @Override
            protected void populateView(View v, explore_adapter_class model, int position) {
                TextView country_name=(TextView)v.findViewById(R.id.country_name_xml);
                TextView dish_name=(TextView)v.findViewById(R.id.dish_name_xml);
                explore_adapter_class epf=(explore_adapter_class) model;
                country_name.setText(epf.getCountryName().toString());
                dish_name.setText(epf.getDishName().toString());
                // Bind the Chat to the view
                // ...
            }
        };

         */



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }
    // or  (ImageView) view.findViewById(R.id.foo);
}
