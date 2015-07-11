package calculator;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

// listens to every button click in our calculator
public class ButtonsClickListener implements OnClickListener {

    boolean operationBtnClick = false;
    String displayData = "0";
    int operationClickCount = 0;
    String firstOperationClick;

    CalculatorContentView calculatorView;

    public ButtonsClickListener(CalculatorContentView calculatorView) {
        this.calculatorView = calculatorView;
    }

    @Override
    public void onClick(View view) {

        String tag = view.getTag().toString();

        String tempData = this.displayData;

        if (tag.equalsIgnoreCase(CalculatorContentView.ADD)) {

            displayData = this.operationClick(view, tag, tempData);

            this.calculatorView.display.setText(this.displayData);

        }

        else if (tag.equalsIgnoreCase(CalculatorContentView.SUBTRACT)) {

            displayData = this.operationClick(view, tag, tempData);
            this.calculatorView.display.setText(this.displayData);

        }

        else if (tag.equalsIgnoreCase(CalculatorContentView.MUTIPLY)) {
            displayData = this.operationClick(view, tag, tempData);
            this.calculatorView.display.setText(this.displayData);

        }

        else if (tag.equalsIgnoreCase(CalculatorContentView.DIVIDE)) {

            displayData = this.operationClick(view, tag, tempData);
            this.calculatorView.display.setText(this.displayData);

        }

        else if (tag.equalsIgnoreCase(CalculatorContentView.EQUAL)) {

            operationBtnClick = false;
            this.equalButtonClick(tempData);

        }

        else if (tag.equalsIgnoreCase(CalculatorContentView.DOT)) {

            tempData += ((TextView) view).getText().toString();
            displayData = tempData;
            calculatorView.display.setText(displayData);

        }

        else if (tag.equalsIgnoreCase(CalculatorContentView.CLEAR)) {

            operationBtnClick = false;

            // *** REMOVE LAST CHARACTER IN tempData
            tempData = this.removeLastChar(tempData);

            // *** IF LENGTH IS LESS THAN 0. SET displayData TO 0
            displayData = tempData.length() <= 0 ? "0" : tempData;

            // *** SET THE RESULT TO CALCULATOR DISPLAY
            calculatorView.display.setText(displayData);

        } else if (tag.equalsIgnoreCase(CalculatorContentView.OK)) {

            operationBtnClick = false;
            okButtonClick(tempData);
        }

        else {

            // *** NUMBER BUTTON CLICK
            operationBtnClick = false;

            if (tempData.equalsIgnoreCase("0")) {
                tempData = "";
            }

            tempData += tag;
            displayData = tempData;
            calculatorView.display.setText(displayData);
        }

    }

    private void equalButtonClick(String tempData) {
        try {

            String data = tempData.substring(1, tempData.length());
            // *** GET THE INDEX OF FIRST OPERATION. ADD 1 BECAUSE WE HAVE
            // REMOVE THE FIRST CHARACTER
            int indexOfFirstOperation = this.indexOfFirstOperation("[^0-9\\.]", data) + 1;

            // *** GET THE INDEX OF FIRST OPERATION
            String firstOperation = tempData.charAt(indexOfFirstOperation) + "";

            // *** COMPUTE THE RESULT
            double result = this.compute(firstOperation, tempData);

            // *** REMOVE UNWANTED DECIMAL PLACES
            this.displayData = this.removeUnwantedValueInDecimal(result + "");

            // *** SET THE RESULT TO CALCULATOR DISPLAY
            this.calculatorView.display.setText(this.displayData + "");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void okButtonClick(String tempData) {

        try {

            String data = tempData.length() > 2 ? tempData.substring(1,
                    tempData.length()) : tempData;

            // *** GET THE INDEX OF FIRST OPERATION
            int indexOfFirstOperation = this.indexOfFirstOperation("[^0-9\\.]",
                    data) + 1;

            if (isFirstOperationTextInBetweenNumber(indexOfFirstOperation, data)) {

                // *** COMPUTE
                // *** GET THE INDEX OF FIRST OPERATION
                String firstOperation = tempData.charAt(indexOfFirstOperation)
                        + "";

                // *** COMPUTE THE RESULT
                double result = this.compute(firstOperation, tempData);

                // *** REMOVE UNWANTED DECIMAL PLACES
                this.displayData = this.removeUnwantedValueInDecimal(result
                        + "");

                // *** UPDATE TEXT OF TEXTVIEW WHERE CLICK HAPPEN
                ((TextView) this.calculatorView.viewWhereClickHappen)
                        .setText(displayData + "");

                // *** DISMISS CALCULATOR POPUP
                this.calculatorView.calculatorPopup.dismiss();

            } else {

                // *** DATA IS A REAL MAY A REAL NUMBER
                tempData = (isLastTextOperation(tempData) || isLastTextDot(tempData))
                        && tempData.length() > 2 ? removeLastChar(tempData)
                        : tempData;

                if (isValidNumber(tempData)) {

                    tempData = Double.parseDouble(tempData) + "";
                    tempData = removeUnwantedValueInDecimal(tempData);

                    this.displayData = tempData;

                    // *** UPDATE TEXT OF TEXTVIEW WHERE CLICK HAPPEN
                    ((TextView) this.calculatorView.viewWhereClickHappen).setText(displayData + "");

                    // *** DISMISS CALCULATOR POPUP
                    this.calculatorView.calculatorPopup.dismiss();

                }

                else {
                    Toast.makeText(this.calculatorView.getContext(),
                            "Not a valid number " + tempData,
                            Toast.LENGTH_SHORT).show();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", "error");
        }
    }

    /**
     *
     * @param view
     * @param operation
     * @param tempData
     * @return
     */
    private String operationClick(View view, String operation, String tempData) {

        try {

            if (!this.allowToAppendOperationText(true, tempData, operation)) {
                Toast.makeText(view.getContext(), "Invalid", Toast.LENGTH_SHORT).show();
                return "0";
            }

            if (tempData.equalsIgnoreCase("0")
                    && operation.equalsIgnoreCase(CalculatorContentView.SUBTRACT)) {
                return operation;
            }

            if (this.isFirstTextNegativeSign(tempData) && tempData.length() <= 1) {
                return tempData;
            }

            if (!this.doDataContainOperationText(tempData)) {
                // no it doesn't
                tempData += operation;
                return tempData;
            }

            // *** APPEND THE operation TO tempData
            tempData += operation;

            // *** REMOVE FIRST CHARACTER IN tempData BECAUSE IT MAY A NEGATIVE value
            String data = tempData.substring(1, tempData.length());

            if (this.doComputeTotal("([^0-9\\.])", data)) {
                // *** COMPUTE TOTAL

                // *** GET THE INDEX OF FIRST OPERATION. ADD 1 BECAUSE WE HAVE REMOVED THE FIRST CHARACTER
                int indexOfFirstOperation = this.indexOfFirstOperation("[^0-9\\.]", data) + 1;

                // *** GET THE FIRST OPERATION
                String firstOperation = tempData.charAt(indexOfFirstOperation)
                        + "";

                // *** REMOVE LAST CHARACTER IN tempData
                tempData = this.removeLastChar(tempData);

                // *** COMPUTE THE RESULT
                double result = this.compute(firstOperation, tempData);

                // *** REMOVE UNWANTED DECIMAL VALUE
                tempData = this.removeUnwantedValueInDecimal(result + "") + operation;

            } else {
                // *** REPLACE THE OPERATION IN LAST CHAR IN tempData

                tempData = this.removePreviousOperationText(tempData);

            }

        } catch (Exception e) {

            e.printStackTrace();
            tempData = "";
            this.calculatorView.display.setText("Error");
        }

        return tempData;

    }

    /**
     *
     * @param indexOfFirstOperation
     * @param tempData
     * @return
     */
    private boolean isFirstOperationTextInBetweenNumber(
            int indexOfFirstOperation, String tempData) {
        int tempDataLength = tempData.length();

        if (indexOfFirstOperation > 1 && indexOfFirstOperation < (tempDataLength)) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param data
     * @return
     */
    private boolean isValidNumber(String data) {
        try {
            Double.parseDouble(data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     *
     * @param result
     * @return
     */
    private String removeUnwantedValueInDecimal(String result) {
        String resutlTmp = result.substring(result.lastIndexOf("."),
                result.length());

        if (this.containNumberNotZero("[1-9]", resutlTmp)) {
            // **** OF DECIMAL VALUES CONTAIN ANY NUMBER BETWEEN 1 - 9
            return result;

        } else {
            // *** IF DECIMAL VALUES CONTAIN ZEROS. REMOVE DECIMAL VALUES
            return result.substring(0, result.lastIndexOf("."));

        }

    }

    /**
     *
     * @param firstOperation
     * @param tempData
     * @return
     * @throws Exception
     */
    private double compute(String firstOperation, String tempData)
            throws Exception {

        double firstNumber = Double.parseDouble(tempData.substring(0,
                tempData.indexOf(firstOperation)));
        double secondNumber = Double.parseDouble(tempData.substring(
                tempData.indexOf(firstOperation) + 1, tempData.length()));
        double result = 0;

        if (firstOperation.equalsIgnoreCase(CalculatorContentView.ADD)) {

            result = firstNumber + secondNumber;

        } else if (firstOperation
                .equalsIgnoreCase(CalculatorContentView.SUBTRACT)) {

            result = firstNumber - secondNumber;

        } else if (firstOperation
                .equalsIgnoreCase(CalculatorContentView.MUTIPLY)) {

            result = firstNumber * secondNumber;

        } else if (firstOperation
                .equalsIgnoreCase(CalculatorContentView.DIVIDE)) {

            result = firstNumber / secondNumber;

        }

        return result;
    }

    /**
     * Check if need to compute data
     *
     * @param theRegex
     * @param stringToCheck
     * @return
     */
    private boolean doComputeTotal(String theRegex, String stringToCheck) {

        boolean computeTotal = false;
        Pattern pattern = Pattern.compile(theRegex);
        Matcher matcher = pattern.matcher(stringToCheck);

        int operationCount = 0;
        int firstOperationEndIndex = 0;
        int secondOperationStartIndex = 0;

        while (matcher.find()) {

            if (operationCount == 0) {

                firstOperationEndIndex = matcher.end();

            } else if (operationCount == 1) {

                secondOperationStartIndex = matcher.start();

            }

            operationCount++;
        }

        if (operationCount > 1
                && firstOperationEndIndex < secondOperationStartIndex) {

            computeTotal = true;

        } else if (operationCount > 1
                && firstOperationEndIndex >= secondOperationStartIndex) {

            computeTotal = false;

        }

        return computeTotal;

    }

    /**
     * Get the index of first operation
     *
     * @param theRegex
     * @param stringToCheck
     * @return
     */
    private int indexOfFirstOperation(String theRegex, String stringToCheck) {

        Pattern pattern = Pattern.compile(theRegex);
        Matcher matcher = pattern.matcher(stringToCheck);

        while (matcher.find()) {

            return matcher.start();

        }

        return 0;
    }

    /**
     * @param theRegex
     * @param stringToCheck
     * @return
     */
    private boolean containNumberNotZero(String theRegex, String stringToCheck) {

        Pattern pattern = Pattern.compile(theRegex);
        Matcher matcher = pattern.matcher(stringToCheck);

        while (matcher.find()) {
            return true;
        }

        return false;
    }

    /**
     *
     * @param isOperationButtonHasClicked
     * @param tempData
     * @return
     */
    private boolean allowToAppendOperationText(
            boolean isOperationButtonHasClicked, String tempData,
            String operation) {

        if (tempData.length() <= 0 && isOperationButtonHasClicked
                && !operation.equalsIgnoreCase(CalculatorContentView.SUBTRACT)) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param data
     * @return
     */
    private boolean isFirstTextNegativeSign(String data) {

        if (data.length() > 0) {

            if ((data.charAt(0) + "")
                    .equalsIgnoreCase(CalculatorContentView.SUBTRACT)) {
                return true;
            }

        }

        return false;
    }

    /**
     *
     * @param data
     * @return
     */
    private boolean isLastTextOperation(String data) {

        if (data.length() > 0) {

            String lastText = data.substring(data.length() - 1, data.length());
            if (lastText.equalsIgnoreCase(CalculatorContentView.DOT)) {

                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param data
     * @return
     */
    private boolean isLastTextDot(String data) {

        if (data.length() > 0) {
            String lastText = data.substring(data.length() - 1, data.length());
            if (lastText.equalsIgnoreCase(CalculatorContentView.ADD)
                    || lastText
                            .equalsIgnoreCase(CalculatorContentView.SUBTRACT)
                    || lastText
                            .equalsIgnoreCase(CalculatorContentView.SUBTRACT)
                    || lastText.equalsIgnoreCase(CalculatorContentView.MUTIPLY)) {

                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param data
     * @return
     */
    private String removePreviousOperationText(String data) {

        return data.substring(0, data.length() - 2)
                + data.substring(data.length() - 1, data.length());
    }

    /**
     *
     * @param data
     * @return
     */
    private String removeLastChar(String data) {
        if (data.length() > 0) {
            data = data.substring(0, data.length() - 1);
        }
        return data;
    }

    /**
     * Check if data contain operation
     *
     * @param data
     * @return
     */
    private boolean doDataContainOperationText(String data) {

        if (data.length() > 1) {
            String testData = new String(data.substring(1, data.length()));

            if (testData.contains(CalculatorContentView.ADD)
                    || testData.contains(CalculatorContentView.SUBTRACT)
                    || testData.contains(CalculatorContentView.MUTIPLY)
                    || testData.contains(CalculatorContentView.DIVIDE)) {
                return true;
            }

        }
        return false;
    }

}

