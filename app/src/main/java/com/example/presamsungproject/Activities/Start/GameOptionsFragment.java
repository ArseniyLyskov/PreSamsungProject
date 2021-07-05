package com.example.presamsungproject.Activities.Start;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.example.presamsungproject.Activities.General.ProblemListener;
import com.example.presamsungproject.ConnectionObjects.Client;
import com.example.presamsungproject.Models.*;
import com.example.presamsungproject.R;

import java.util.HashMap;

public class GameOptionsFragment extends Fragment {
    private StartActivityFragmentListener SAFListener;
    private HashMap<String, String> players;
    private Spinner spinner;
    private CheckBox teamBattle, ricochets, debug;
    private TextView teamText, widthText, heightText;
    private EditText teamNumber, hpNumber;
    private EditText width, height, min_cells, empty_cell, creating_wall;

    public void setParams(StartActivityFragmentListener SAFListener, HashMap<String, String> players) {
        this.SAFListener = SAFListener;
        this.players = players;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_options, null);

        spinner = v.findViewById(R.id.fgo_spinner);
        teamBattle = v.findViewById(R.id.fgo_team_battle);
        ricochets = v.findViewById(R.id.fgo_ricochets);
        debug = v.findViewById(R.id.fgo_debug);
        teamText = v.findViewById(R.id.fgo_team_text);
        widthText = v.findViewById(R.id.fgo_width_text);
        heightText = v.findViewById(R.id.fgo_height_text);
        teamNumber = v.findViewById(R.id.fgo_team_number);
        hpNumber = v.findViewById(R.id.fgo_hp_number);
        width = v.findViewById(R.id.fgo_width);
        height = v.findViewById(R.id.fgo_height);
        min_cells = v.findViewById(R.id.fgo_min_cells);
        empty_cell = v.findViewById(R.id.fgo_empty_cell);
        creating_wall = v.findViewById(R.id.fgo_creating_wall);

        String[] spinnerData = {"Small", "Medium", "Big", "Huge", "Specific size"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.row, R.id.map_size_row, spinnerData);
        spinner.setAdapter(adapter);
        spinner.setSelection(1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 4) {
                    width.setVisibility(View.VISIBLE);
                    widthText.setVisibility(View.VISIBLE);
                    height.setVisibility(View.VISIBLE);
                    heightText.setVisibility(View.VISIBLE);
                } else {
                    width.setVisibility(View.INVISIBLE);
                    widthText.setVisibility(View.INVISIBLE);
                    height.setVisibility(View.INVISIBLE);
                    heightText.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        teamBattle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (teamBattle.isChecked()) {
                    teamText.setVisibility(View.VISIBLE);
                    teamNumber.setVisibility(View.VISIBLE);
                } else {
                    teamText.setVisibility(View.INVISIBLE);
                    teamNumber.setVisibility(View.INVISIBLE);
                }
            }
        });

        Button back = v.findViewById(R.id.fgo_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEffects.getInstance().executeEffect(SoundEffects.CLICK);
                Client.getInstance().stop();
                SAFListener.removeFragment(GameOptionsFragment.this);
            }
        });
        Button apply = v.findViewById(R.id.fgo_apply);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEffects.getInstance().executeEffect(SoundEffects.CLICK);
                if (!isParamsCorrect())
                    return;
                SAFListener.gameOptionsChanged(getEnteredGameOptions());
                SAFListener.removeFragment(GameOptionsFragment.this);
            }
        });

        return v;
    }

    private boolean isParamsCorrect() {
        ProblemListener PListener = Resources.getInstance().getPListener();

        if (teamBattle.isChecked()) {
            if (teamNumber.getText().toString().equals("")) {
                PListener.showProblem("You didn't enter number of teams");
                return false;
            }
            if (teamNumber.getText().toString().equals("0")) {
                PListener.showProblem("Number of teams can't be 0");
                return false;
            }
        }
        if (hpNumber.getText().toString().equals("")) {
            PListener.showProblem("You didn't enter tanks hp");
            return false;
        }
        if (hpNumber.getText().toString().equals("0")) {
            PListener.showProblem("Tanks hp can't be 0");
            return false;
        }
        if (spinner.getSelectedItemPosition() == 4) {
            if (width.getText().toString().equals("")) {
                PListener.showProblem("You didn't enter map width");
                return false;
            }
            if (height.getText().toString().equals("")) {
                PListener.showProblem("You didn't enter map height");
                return false;
            }
            if (Integer.parseInt(width.getText().toString()) < 8) {
                PListener.showProblem("Entered map width is too small");
                return false;
            }
            if (Integer.parseInt(height.getText().toString()) < 8) {
                PListener.showProblem("Entered map height is too small");
                return false;
            }
            if (Integer.parseInt(width.getText().toString()) > 40) {
                PListener.showProblem("Entered map width is too big");
                return false;
            }
            if (Integer.parseInt(height.getText().toString()) > 40) {
                PListener.showProblem("Entered map height is too big");
                return false;
            }
        }
        if (min_cells.getText().toString().equals("")) {
            PListener.showProblem("You didn't enter minimum of available cells");
            return false;
        }
        if (empty_cell.getText().toString().equals("")) {
            PListener.showProblem("You didn't enter chance of an empty cell");
            return false;
        }
        if (creating_wall.getText().toString().equals("")) {
            PListener.showProblem("You didn't enter chance of creating a wall");
            return false;
        }
        if (Integer.parseInt(empty_cell.getText().toString()) > 100
                || Integer.parseInt(creating_wall.getText().toString()) > 100) {
            PListener.showProblem("Percentage cannot be more than 100");
            return false;
        }
        return true;
    }

    private GameOptions getEnteredGameOptions() {
        boolean isTeamBattle = teamBattle.isChecked();
        int numberOfTeams = players.size();
        if (isTeamBattle)
            numberOfTeams = Integer.parseInt(teamNumber.getText().toString());
        boolean isRicochets = ricochets.isChecked();
        int tanksHP = Integer.parseInt(hpNumber.getText().toString());
        boolean debug = this.debug.isChecked();
        int min_width = -1, min_height = -1, max_width = -1, max_height = -1;
        if (spinner.getSelectedItemPosition() == 4) {
            min_width = Integer.parseInt(width.getText().toString());
            max_width = Integer.parseInt(width.getText().toString());
            min_height = Integer.parseInt(height.getText().toString());
            max_height = Integer.parseInt(height.getText().toString());
        } else if (spinner.getSelectedItemPosition() == 0) {
            min_width = 8;
            max_width = 13;
            min_height = 8;
            max_height = 13;
        } else if (spinner.getSelectedItemPosition() == 1) {
            min_width = 13;
            max_width = 20;
            min_height = 13;
            max_height = 20;
        } else if (spinner.getSelectedItemPosition() == 2) {
            min_width = 20;
            max_width = 30;
            min_height = 20;
            max_height = 30;
        } else if (spinner.getSelectedItemPosition() == 3) {
            min_width = 30;
            max_width = 40;
            min_height = 30;
            max_height = 40;
        }
        int min_cells = Integer.parseInt(this.min_cells.getText().toString());
        int empty_cell = Integer.parseInt(this.empty_cell.getText().toString());
        int creating_wall = Integer.parseInt(this.creating_wall.getText().toString());
        MapOptions mapOptions = new MapOptions(min_cells, min_width, min_height, max_width, max_height, empty_cell, creating_wall);
        Map map = new Map(players.size(), mapOptions);
        return new GameOptions(map, numberOfTeams, tanksHP, isRicochets, debug);
    }
}