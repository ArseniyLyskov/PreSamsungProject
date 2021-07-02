package com.example.presamsungproject.Activities.General;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.presamsungproject.Models.MySingletons;
import com.example.presamsungproject.Models.MySoundEffects;
import com.example.presamsungproject.MyInterfaces.StartActivityFragmentListener;
import com.example.presamsungproject.R;

public class ProblemFragment extends Fragment {
    private final StartActivityFragmentListener SAFListener;
    private final String message;

    public ProblemFragment(StartActivityFragmentListener SAFListener, String message) {
        this.SAFListener = SAFListener;
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
                MySingletons.getMyResources().getSFXInterface().executeEffect(MySoundEffects.CLICK);
                SAFListener.removeFragment(ProblemFragment.this);
            }
        });

        return v;
    }
}
