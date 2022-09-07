package com.debuggers.apna_tutor.Fragments;

import static com.debuggers.apna_tutor.App.QUEUE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.debuggers.apna_tutor.Adapters.CourseAdapter;
import com.debuggers.apna_tutor.Helpers.API;
import com.debuggers.apna_tutor.Models.Course;
import com.debuggers.apna_tutor.R;
import com.debuggers.apna_tutor.databinding.FragmentHomeBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class TeacherHomeFragment extends Fragment {
    FragmentHomeBinding binding;

    public TeacherHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        updateUi();
        binding.homeRefresher.setOnRefreshListener(this::updateUi);

        return binding.getRoot();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (binding.homeRV.getAdapter() != null) ((CourseAdapter) binding.homeRV.getAdapter()).getFilter().filter(newText);
                return false;
            }
        });
    }

    private void updateUi() {
        binding.homeRefresher.setRefreshing(true);
        QUEUE.add(new JsonObjectRequest(Request.Method.GET, API.COURSES_UPLOADED, null, response -> {
            List<Course> courses = new Gson().fromJson(response.toString(), new TypeToken<List<Course>>(){}.getType());
            binding.homeRV.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.homeRV.setAdapter(new CourseAdapter(courses, (course, position) -> {

            }));
            binding.homeRefresher.setRefreshing(false);
        }, error -> {
            binding.homeRefresher.setRefreshing(false);
            Toast.makeText(requireContext(), API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
        })).setRetryPolicy(new DefaultRetryPolicy());
    }

}