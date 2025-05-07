package Fragment;

import static Utel.UtelsDB.SHAREDPREFERNCES_FILENAME_EMAIL;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nouroeddinne.sweetsstore.HomeActivity;
import com.nouroeddinne.sweetsstore.R;
import com.nouroeddinne.sweetsstore.SignupActivity;
import com.nouroeddinne.sweetsstore.SplashScreenActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Controlar.Adapter;
import Model.Model;
import Utel.PaginationScrollListener;

public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private ArrayList<Model> mDessertList;
    Adapter adapter;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;
    private static final int PAGE_SIZE = 9;
    GridLayoutManager gridLayoutManager;


    public HomeFragment() {}

    public static HomeFragment newInstance(String param1, ArrayList<Model> list) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putParcelableArrayList(ARG_PARAM2, list);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mDessertList = getArguments().getParcelableArrayList(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        // Set refresh indicator colors
        swipeRefreshLayout.setColorSchemeResources(
                R.color.blue,
                R.color.lithPink2,
                R.color.Baje);

        // Configure the refresh listener
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your refresh code here
                refreshData();
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new Adapter(getContext(), new ArrayList<>());

        //adapter = new Adapter(getActivity(), mDessertList);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                HomeActivity.textNotif();
            }
        });
        recyclerView.setAdapter(adapter);

        // Load initial data
        loadFirstPage();

        // Setup scroll listener
        recyclerView.addOnScrollListener(new PaginationScrollListener(gridLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                loadNextPage();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

    }




    private void loadFirstPage() {
        // Show a progress bar if needed
        // progressBar.setVisibility(View.VISIBLE);

        // Make your API call or load data from database
        fetchData(currentPage, new OnDataLoadedListener() {
            @Override
            public void onDataLoaded(List<Model> data) {
                // Hide progress bar
                // progressBar.setVisibility(View.GONE);

                // Add fetched data to the adapter
                adapter.addItems(data);

                if (currentPage < getTotalPages()) {
                    adapter.addLoadingFooter();
                } else {
                    isLastPage = true;
                }
            }
        });
    }

    private void loadNextPage() {
        fetchData(currentPage, new OnDataLoadedListener() {
            @Override
            public void onDataLoaded(List<Model> data) {
                adapter.removeLoadingFooter();
                isLoading = false;

                adapter.addItems(data);

                if (currentPage < getTotalPages()) {
                    adapter.addLoadingFooter();
                } else {
                    isLastPage = true;
                }
            }
        });
    }

    private void fetchData(int page, final OnDataLoadedListener listener) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                List<Model> items = new ArrayList<>();
                for (int i = 0; i < PAGE_SIZE; i++) {
                    int position = (page - 1) * PAGE_SIZE + i;
                    items.add(mDessertList.get(position));
                }
                listener.onDataLoaded(items);

            }
        },1000);

    }

    private int getTotalPages() {
        // This would normally come from your API
        return 5; // Example: 5 pages total
    }

    public interface OnDataLoadedListener {
        void onDataLoaded(List<Model> data);
    }






    private void refreshData() {
        // Fetch new data (network call, database query, etc.)
        swipeRefreshLayout.setRefreshing(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Collections.shuffle(mDessertList);
                adapter = new Adapter(getActivity(), new ArrayList<>());
                adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onChanged() {
                        super.onChanged();
                        HomeActivity.textNotif();
                    }
                });
                recyclerView.setAdapter(adapter);

                // Load initial data
                loadFirstPage();

                // Once data is loaded, hide the refresh indicator
                swipeRefreshLayout.setRefreshing(false);

            }
        }, 1500);

    }




}





















