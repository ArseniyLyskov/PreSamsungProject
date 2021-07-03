package com.example.presamsungproject.Activities.Start;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import com.example.presamsungproject.Models.SoundEffects;
import com.example.presamsungproject.R;

public class AboutFragment extends Fragment {
    private StartActivityFragmentListener SAFListener;

    public void setParams(StartActivityFragmentListener SAFListener) {
        this.SAFListener = SAFListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, null);

        Button button = v.findViewById(R.id.fa_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEffects.getInstance().executeEffect(SoundEffects.CLICK);
                SAFListener.removeFragment(AboutFragment.this);
            }
        });

        return v;
    }
}
