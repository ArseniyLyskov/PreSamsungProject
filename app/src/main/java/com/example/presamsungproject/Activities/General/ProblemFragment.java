package com.example.presamsungproject.Activities.General;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.presamsungproject.Models.SoundEffects;
import com.example.presamsungproject.Activities.Start.StartActivityFragmentListener;
import com.example.presamsungproject.R;

public class ProblemFragment extends Fragment {
    private final FragmentListener FListener;
    private final String message;

    public ProblemFragment(FragmentListener FListener, String message) {
        this.FListener = FListener;
        this.message = message;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_problem, null);

        TextView textView = v.findViewById(R.id.fp_textview);
        textView.setText(message);
        Button button = v.findViewById(R.id.fp_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEffects.getInstance().executeEffect(SoundEffects.CLICK);
                FListener.removeFragment(ProblemFragment.this);
            }
        });

        return v;
    }
}
