package com.ashvinprajapati.skillconnect.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ashvinprajapati.skillconnect.fragments.MyBookingsFragment;
import com.ashvinprajapati.skillconnect.fragments.ServiceBookingsFragment;

public class BookingPagerAdapter extends FragmentStateAdapter {

    public BookingPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MyBookingsFragment();
            case 1:
                return new ServiceBookingsFragment();
            default:
                return new MyBookingsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Two tabs
    }
}
