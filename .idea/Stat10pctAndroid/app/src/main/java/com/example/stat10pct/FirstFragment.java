package com.example.stat10pct;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.HashSet;
import java.util.Random;

public class FirstFragment extends Fragment {
    TextView graphTextView;
    EditText popNumEditText;
    EditText propNumEditText;
    EditText nEditText;
    EditText sampleNumEditText;
    EditText bucketEditText;
    EditText factorEditText;
    Switch withReplacementSwitch;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View fragmentFirstLayout = inflater.inflate(R.layout.fragment_first, container, false);
        // Get the graph text view
        graphTextView = fragmentFirstLayout.findViewById(R.id.textGraph);
        popNumEditText = fragmentFirstLayout.findViewById(R.id.editPopNum);
        propNumEditText = fragmentFirstLayout.findViewById(R.id.editPropNum);
        nEditText = fragmentFirstLayout.findViewById(R.id.editN);
        sampleNumEditText = fragmentFirstLayout.findViewById(R.id.editSampleNum);
        bucketEditText = fragmentFirstLayout.findViewById(R.id.editBucket);
        factorEditText = fragmentFirstLayout.findViewById(R.id.editFactor);
        withReplacementSwitch = fragmentFirstLayout.findViewById(R.id.switch1);
        popNumEditText.setText("1000");
        propNumEditText.setText("500");
        nEditText.setText("600");
        sampleNumEditText.setText("2000");
        bucketEditText.setText("0.025");
        factorEditText.setText("20");
        graphTextView.setText(execute());

        return fragmentFirstLayout;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.Run).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                graphTextView.setText(execute());
            }
        });
    }

    public String execute() {
        int popNum = Integer.parseInt(popNumEditText.getText().toString()); // Population size
        int propNum = Integer.parseInt(propNumEditText.getText().toString()); // Number of successes
        int n = Integer.parseInt(nEditText.getText().toString()); // Sample size
        int sampleNum = Integer.parseInt(sampleNumEditText.getText().toString()); // Number of samples taken
        double bucket = Double.parseDouble(bucketEditText.getText().toString()); // Bucket width for the proportion
        int factor = Integer.parseInt(factorEditText.getText().toString()); // Factor to decrease the histogram by
        boolean withReplacement = withReplacementSwitch.isChecked(); // Indicates if the sample is w/ or w/o replacement

        boolean[] popVals = new boolean[popNum];

        for (int x = 0; x < propNum; x++) {
            popVals[x] = true;
        }
        for (int x = propNum; x < popNum; x++) {
            popVals[x] = false;
        }

        double[] sampleVals = new double[sampleNum];
        Random random = new Random();

        if (!withReplacement) {
            for (int sampleX = 0; sampleX < sampleNum; sampleX++) {
                double tempSample = 0;
                HashSet<Integer> alreadyTaken = new HashSet<Integer>(sampleNum);
                for (int x = 0; x < n; x++) {
                    int index = (int) (random.nextDouble() * popNum);
                    if (!alreadyTaken.contains(index)) {
                        if (popVals[index]) {
                            tempSample++;
                        }
                        alreadyTaken.add(index);
                    } else {
                        x--;
                    }
                }
                tempSample /= n;
                sampleVals[sampleX] = tempSample;
            }
        } else {
            for (int sampleX = 0; sampleX < sampleNum; sampleX++) {
                double tempSample = 0;
                for (int x = 0; x < n; x++) {
                    int index = (int) (random.nextDouble() * popNum);
                    if (popVals[index]) {
                        tempSample++;
                    }
                }
                tempSample /= n;
                sampleVals[sampleX] = tempSample;
            }
        }

        int[] histogram = new int[(int) (1 / bucket) + 1];
        for (double val : sampleVals) {
            histogram[(int) (val / bucket + .5)]++;
        }

        boolean[] extras = new boolean[(int) (1 / bucket) + 1];
        for (int x = 0; x < histogram.length; x++) {
            if (histogram[x] % factor != 0) {
                extras[x] = true;
            }
            histogram[x] /= factor;
        }

        String output = new String();
        for (int index = 0; index < histogram.length; index++) {
            for (int x = 0; x < histogram[index]; x++) {
                output += "x";
            }
            if (extras[index] == true) {
                output += "o\n";
            } else if (histogram[index] > 0) {
                output += "\n";
            }
        }

        return output;
    }
}