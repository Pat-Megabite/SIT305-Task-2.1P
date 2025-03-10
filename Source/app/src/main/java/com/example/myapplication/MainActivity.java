package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //Global Variables
    EditText inputValue;
    TextView outputValue;
    Button convertButton;
    Spinner inputUnit, outputUnit;

    TextInputLayout inputLayout;

    //Conversion factors as functions
public class UnitConverter {
        private Map<String, Double> lengthConversionMap = new HashMap<>();
        private Map<String, Double> weightConversionMap = new HashMap<>();
        private Map<String, String> categoryMap = new HashMap<>();
         public UnitConverter() {
             // Assign units to categories
             categoryMap.put("inch", "Length");
             categoryMap.put("foot", "Length");
             categoryMap.put("yard", "Length");
             categoryMap.put("mile", "Length");
             categoryMap.put("cm", "Length");
             categoryMap.put("m", "Length");
             categoryMap.put("km", "Length");

             categoryMap.put("pound", "Weight");
             categoryMap.put("ounce", "Weight");
             categoryMap.put("ton", "Weight");
             categoryMap.put("gram", "Weight");
             categoryMap.put("kg", "Weight");

             categoryMap.put("C", "Temperature");
             categoryMap.put("F", "Temperature");
             categoryMap.put("K", "Temperature");

             //Length Conversions to cm
             lengthConversionMap.put("inch", 2.54);
             lengthConversionMap.put("foot", 30.48);
             lengthConversionMap.put("yard", 91.44);
             lengthConversionMap.put("mile", 160934.0);
             lengthConversionMap.put("cm", 1.0);
             lengthConversionMap.put("m", 100.0);
             lengthConversionMap.put("km", 100000.0);

             // Weight conversion factors to gram
             weightConversionMap.put("pound", 453.592);
             weightConversionMap.put("ounce", 28.3495);
             weightConversionMap.put("ton", 907185.0);
             weightConversionMap.put("gram", 1.0);
             weightConversionMap.put("kg", 1000.0);
        }

        public double convertLength(double value, String fromUnit, String toUnit) {
            double cmValue = value * lengthConversionMap.get(fromUnit); // Convert to cm
            return cmValue / lengthConversionMap.get(toUnit); // Convert to target unit
        }

        public double convertWeight(double value, String fromUnit, String toUnit) {
            double gramValue = value * weightConversionMap.get(fromUnit); // Convert to gram
            return gramValue / weightConversionMap.get(toUnit); // Convert to target unit
        }

        public double convertTemperature(double value, String fromUnit, String toUnit) {
             Double outputValue = value;
             if(fromUnit.equals("K")&&!toUnit.equals("K")){
                 outputValue  =- 273.15; //Convert to C
                 if(toUnit.equals("F")){
                     return (outputValue*1.8)+32;
                 }
                 else {
                     return outputValue;
                 }
             }
             else if (fromUnit.equals("C")&&!toUnit.equals("C")) {
                 if(toUnit.equals("F")){
                     return (outputValue*1.8)+32;
                 }
                 else {
                     return (outputValue+273.15); //Return K
                 }
             }
             else if (fromUnit.equals("F")&&!toUnit.equals("F")){
                 outputValue = (outputValue -32)/1.8; //Convert to C
                 if(toUnit.equals("K")){
                     return outputValue+273.15;
                 }
                 else {
                     return outputValue; //Return C
                 }
             }
             return outputValue; //Same units were selected
        }


        public String getCategory(String unit) {
            return categoryMap.getOrDefault(unit, "Unknown");
        }

        public double convert(double value, String fromUnit, String toUnit) {
            String category = getCategory(fromUnit);
            if (!category.equals(getCategory(toUnit))) {
                throw new IllegalArgumentException("Cannot convert between different categories!");
            }

            switch (category) {
                case "Length":
                    return convertLength(value, fromUnit, toUnit);
                case "Weight":
                    return convertWeight(value, fromUnit, toUnit);
                case "Temperature":
                    return convertTemperature(value, fromUnit, toUnit);
                default:
                    throw new IllegalArgumentException("Invalid category");
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inputValue = findViewById(R.id.srcvalue);
        inputLayout = findViewById(R.id.inputLayout);
        outputValue = findViewById(R.id.output);
        convertButton = findViewById(R.id.convert_button);
        inputUnit = findViewById(R.id.input_unit);
        outputUnit = findViewById(R.id.output_unit);
        UnitConverter unitConverter = new UnitConverter();

        //Setup spinners
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.units,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputUnit.setAdapter(adapter);
        outputUnit.setAdapter(adapter);

        convertButton.setOnClickListener(v -> {
            String inputDropdown = inputUnit.getSelectedItem().toString();
            String outputDropdown = outputUnit.getSelectedItem().toString();
            try {
                Double value = Double.parseDouble(inputValue.getText().toString());
                if(value <= 0 && !unitConverter.getCategory(inputDropdown).equals("Temperature")) { throw new NumberFormatException();}
                else    {
                    Double output = unitConverter.convert(value, inputDropdown, outputDropdown);
                    outputValue.setText(output.toString());
                }
            }
            catch (NumberFormatException | NullPointerException e) {
                inputLayout.setErrorEnabled(true);
                inputLayout.setError("Please enter a valid value!");
            }
            catch (IllegalArgumentException e) {
                //Occurs due to different categories selected
                inputLayout.setErrorEnabled(true);
                inputLayout.setError("Please enter a valid category to convert to!");
            }


        });
    }
}