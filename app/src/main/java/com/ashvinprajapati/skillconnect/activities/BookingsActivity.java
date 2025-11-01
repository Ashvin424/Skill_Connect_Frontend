package com.ashvinprajapati.skillconnect.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.fragments.MyBookingsFragment;
import com.ashvinprajapati.skillconnect.fragments.ServiceBookingsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class BookingsActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bookings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Set adapter
        viewPager.setAdapter(new BookingPagerAdapter(this));

        // Attach TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("My Bookings");
                    } else {
                        tab.setText("Booking Requests");
                    }
                }
        ).attach();

    }

    // Adapter class
    private static class BookingPagerAdapter extends FragmentStateAdapter {
        public BookingPagerAdapter(@NonNull AppCompatActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new MyBookingsFragment();  // fragment for user's bookings
            } else {
                return new ServiceBookingsFragment(); // fragment for provider’s bookings
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

}