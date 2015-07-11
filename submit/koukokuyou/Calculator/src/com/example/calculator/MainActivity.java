package com.example.calculator;



import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import calculator.CalculatorPopup;
import calculator.ViewSize;

public class MainActivity extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the content as 5 text view
        setContentView(this.contentView());
    }

    LinearLayout contentView(){

        // container of 5 text views
        LinearLayout contentView = new LinearLayout(this);
        contentView.setOrientation(LinearLayout.VERTICAL);

        // generate 5 sample text views
        for(int x = 0 ; x < 5 ; x++){

            // just some params for the text view
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) ViewSize.computeWidth(100,this),(int) ViewSize.computeHeight(50,this));
            params.setMargins(5, 5, 0, 0);

            // a text view
            TextView textView = new TextView(this);
            textView.setText(x+"");
            textView.setPadding(2, 0, 2, 0);
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            textView.setLayoutParams(params);

            // set the onClick() method of this class
            textView.setOnClickListener(this);

            // add to linear layout
            contentView.addView(textView);
        }

        return contentView;
    }

    @Override
    public void onClick(View view) {
        // our calculator instance
        CalculatorPopup calculatorPopup = new CalculatorPopup(this);

        // set the text view where click happened
        calculatorPopup.getCalculator().setViewWhereClickHappen(view);

        // show the calculator pop up
        calculatorPopup.show();
    }
}
