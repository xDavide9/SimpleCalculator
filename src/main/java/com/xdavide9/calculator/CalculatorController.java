package com.xdavide9.calculator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

public class CalculatorController {

    @FXML
    private Label label;

    @FXML
    private Button fnButton;

    //the members are static because otherwise when injecting the 2 fxml files would use different objects

    private static BigDecimal count, temp;
    private static char operation;
    private static boolean hasCount, hasTemp;
    private static StringBuilder builder = new StringBuilder();
    private static String onLabel;
    private static boolean isOnSimple = true;
    private String id;

    private static final int maxDigits = 11;
    private static final String errorString = "err";

    /**
     * Appends to the label the number of the corresponding button
     * respecting the limit of maximum digits allowed.
     */
    @FXML
    protected void OnNumberPressed(ActionEvent e) {
        id = updateId(e);
        System.out.println("Pressed: " + id);

        onLabel = label.getText();
        if (status(onLabel) == -1) return;

        if (onLabel.equals("0"))
            builder = new StringBuilder();

        switch (id) {
            case "zeroButton" ->  {
                if (onLabel.startsWith("0") && (!onLabel.contains("."))) {
                    System.err.println("Can't append 0");
                    return;
                }
                builder.append(0);
            }
            case "oneButton" -> builder.append(1);
            case "twoButton" -> builder.append(2);
            case "threeButton" -> builder.append(3);
            case "fourButton" -> builder.append(4);
            case "fiveButton" -> builder.append(5);
            case "sixButton" -> builder.append(6);
            case "sevenButton" -> builder.append(7);
            case "eightButton" -> builder.append(8);
            case "nineButton" -> builder.append(9);
            case "piButton" -> builder.append(round(BigDecimal.valueOf(Math.PI)));
            case "eButton" -> builder.append(round(BigDecimal.valueOf(Math.E)));
        }

        onLabel = builder.toString();
        if (hasTooManyDigits(onLabel.length())) {
            System.err.println("Too many digits");
            onLabel = onLabel.substring(0, maxDigits);
        }

        label.setText(onLabel);
    }

    /**
     * Performs an operation using memory:
     * stores the number in the label either in count or temp,
     * performs an operation if it was stored in memory and
     * stores the operation to be performed next.
     * Currently performed operations:
     * Addition,
     * Subtraction,
     * Multiplication,
     * Division,
     * Exponentiation of N power,
     * Root Extraction of N index,
     * Logarithm of N base.
     */
    @FXML
    protected void onMemoryOperationPressed(ActionEvent e) {
        id = updateId(e);
        System.out.println("Pressed: " + id);

        onLabel = label.getText();
        if (status(onLabel) == -1) return;

        if (onLabel.equals(""))
            return;

        if (!hasCount) {
            count = new BigDecimal(label.getText());
            hasCount = true;
        } else {
            temp = new BigDecimal(label.getText());
            hasTemp = true;
        }

        builder = new StringBuilder();

        if (hasTemp && operation != 0) {
            switch (operation) {
                case '+' -> count = addition(count, temp);
                case '-' -> count = subtraction(count, temp);
                case '*' -> count = multiplication(count, temp);
                case '/' ->  {
                    if (temp.doubleValue() == 0.0) {
                        System.err.println("Can't divide by 0");
                        displayErr();
                        return;
                    }
                    count = division(count, temp);
                }
                case 'n' ->  {
                    if (count.doubleValue() <= 0) {
                        System.err.println("Can't extract power");
                        displayErr();
                        return;
                    }
                    count = exponentiation(count, temp);
                }
                case 'm' -> {
                    if (temp.doubleValue() == 0.0 || !String.valueOf(temp.doubleValue()).endsWith(".0") || count.doubleValue() < 0.0) {
                        System.err.println("Can't extract root");
                        displayErr();
                        return;
                    }
                    count = rootExtraction(count, temp);
                }
                case 'l' -> {
                    if (temp.doubleValue() == 1.0 || temp.doubleValue() <= 0.0 || count.doubleValue() < 0.0) {
                        System.err.println("Can't perform this log");
                        displayErr();
                        return;
                    }
                    count = logBaseN(count, temp);
                }
            }
            hasTemp = false;
            temp = new BigDecimal("0");
        } else {
            System.err.println("No temp in memory");
        }

        onLabel = count.toString();

        if (hasTooManyDigits(onLabel.length())) {
            System.err.println("Too many digits");
            displayErr();
            return;
        }

        label.setText(onLabel);

        switch (id) {
            case "plusButton" -> operation = '+';
            case "minusButton" -> operation = '-';
            case "timesButton" -> operation = '*';
            case "divideButton" -> operation = '/';
            case "toThePowerOfNButton" -> operation = 'n';
            case "radicalNButton" -> operation = 'm';
            case "logBaseNButton" -> operation = 'l';
        }
    }

    /**
     * Performs an operation if it was stored in memory
     * and displays the final value of the expression evaluated so far.
     */
    @FXML
    protected void onEqualsPressed(ActionEvent e) {
        id = updateId(e);
        System.out.println("Pressed: " + id);

        onLabel = label.getText();
        if (status(onLabel) == -1) return;

        if (operation != 0) {
            if (label.getText().equals("")) return;

            temp = new BigDecimal(label.getText());

            switch (operation) {
                case '+' -> count = addition(count, temp);
                case '-' -> count = subtraction(count, temp);
                case '*' -> count = multiplication(count, temp);
                case '/' ->  {
                    if (temp.doubleValue() == 0.0) {
                        System.err.println("Can't divide by 0");
                        displayErr();
                        return;
                    }
                    count = division(count, temp);
                }
                case 'n' ->  {
                    if (count.doubleValue() <= 0) {
                        System.err.println("Can't extract power");
                        displayErr();
                        return;
                    }
                    count = exponentiation(count, temp);
                }
                case 'm' -> {
                    if (temp.doubleValue() == 0.0 || !String.valueOf(temp.doubleValue()).endsWith(".0") || count.doubleValue() < 0.0) {
                        System.err.println("Can't extract root");
                        displayErr();
                        return;
                    }
                    count = rootExtraction(count, temp);
                }
                case 'l' -> {
                    if (temp.doubleValue() == 1.0 || temp.doubleValue() <= 0.0 || count.doubleValue() < 0.0) {
                        System.err.println("Can't perform this log");
                        displayErr();
                        return;
                    }
                    count = logBaseN(count, temp);
                }
            }
            operation = 0;
            temp = new BigDecimal("0");
            hasTemp = false;
        } else {
            return;
        }

        builder = new StringBuilder();

        onLabel = String.valueOf(count);
        if (hasTooManyDigits(onLabel.length())) {
            System.out.println("Too many digits");
            displayErr();
            return;
        }

        label.setText(onLabel);
        builder.append(onLabel);

        count = new BigDecimal("0");
        hasCount = false;
    }

    /**
     * Appends a point to the label.
     */
    @FXML
    protected void onPointPressed(ActionEvent e) {
        id = updateId(e);
        System.out.println("Pressed: " + id);

        onLabel = label.getText();
        if (status(onLabel) == -1) return;

        if (onLabel.contains(".")) return;

        builder = new StringBuilder();
        builder.append(onLabel);
        builder.append(".");
        onLabel = builder.toString();
        label.setText(onLabel);
    }

    /**
     * Deletes a single digit in the label
     * without affecting anything in memory.
     */
    @FXML
    protected void onDelPressed(ActionEvent e) {
        id = updateId(e);
        System.out.println("Pressed: " + id);

        onLabel = label.getText();
        if (status(onLabel) == -1) return;

        if (onLabel.equals("0")) return;

        builder = new StringBuilder();
        onLabel = onLabel.substring(0, onLabel.length() - 1);
        if (onLabel.equals(""))
            onLabel = "0";
        builder.append(onLabel);
        label.setText(onLabel);
    }

    /**
     * Deletes everything in the label
     * and removes any previous operation in memory.
     */
    @FXML
    protected void onAcPressed(ActionEvent e) {
        id = updateId(e);
        System.out.println("Pressed: " + id);

        setDefaults();
    }

    /**
     * Changes the sign of the number in label.
     */
    @FXML
    protected void onPlusMinusPressed(ActionEvent e) {
        id = updateId(e);
        System.out.println("Pressed: " + id);

        onLabel = label.getText();
        if (status(onLabel) == -1) return;

        if (onLabel.equals("0"))
            return;

        BigDecimal value = new BigDecimal(onLabel);
        value = value.multiply(new BigDecimal("-1"));
        value = stripDecimalTrailingZeros(value);

        onLabel = String.valueOf(value);
        builder = new StringBuilder();
        builder.append(onLabel);

        if (hasTooManyDigits(onLabel.length())) {
            System.err.println("Too many digits");
            displayErr();
            return;
        }

        label.setText(onLabel);
    }

    /**
     * Switches to a different scene where more
     * complex functions are present.
     */
    @FXML
    protected void onFnPressed(ActionEvent e) throws IOException {
        id = updateId(e);
        System.out.println("Pressed: " + id);

        onLabel = label.getText();
        if (status(onLabel) == -1) return;

        FXMLLoader fxmlLoader;
        onLabel = label.getText();

        if (isOnSimple) {
            isOnSimple = false;
            fxmlLoader = new FXMLLoader(CalculatorApplication.class.getResource("calculatorComplex.fxml"));
        } else {
            isOnSimple = true;
            fxmlLoader = new FXMLLoader(CalculatorApplication.class.getResource("calculatorSimple.fxml"));
        }

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(CalculatorApplication.class.getResource("darcula.css")).toExternalForm());
        CalculatorController controller = fxmlLoader.getController();
        //injecting the label's text through the controller each time you switch scene because it cannot be made static
        controller.label.setText(onLabel);
        //injecting focus on the fn button to prevent an annoying effect otherwise occurring
        controller.fnButton.requestFocus();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Performs operation on the value being displayed without the need to use memory
     * Currently performed operations:
     * Exponentiation of the second power,
     * Square root extraction,
     * Percentage calculation,
     * Logarithm of base 10,
     * Logarithm of base e,
     * sine function,
     * cosine function ,
     * tangent function,
     * arc-sine function,
     * arc-cosine function,
     * arc-tangent function,
     * root extraction of index 3,
     * reciprocal calculation,
     * factorial function.
     */
    @FXML
    protected void onInstantOperationPressed(ActionEvent e) {
        id = updateId(e);
        System.out.println("Pressed: " + id);

        //error checking
        onLabel = label.getText();
        if (status(onLabel) == -1) return;

        BigDecimal value = new BigDecimal(onLabel);

        switch (id) {
            case "toThePowerOfTwoButton" -> value = value.pow(2);
            case "radicalTwoButton" ->  {
                if (value.doubleValue() < 0.0) {
                    System.err.println("Can't extract square root of a negative number");
                    displayErr();
                    return;
                }
                value = value.sqrt(new MathContext(15));
            }
            case "percentButton" ->  {
                if (value.doubleValue() < 0.0) {
                    System.err.println("Can't calculate percentage off a negative number");
                    displayErr();
                    return;
                }
                value = division(value, BigDecimal.valueOf(100)); //divide by 100
            }
            case "logButton" ->  {
                if (value.doubleValue() <= 0.0 || value.doubleValue() == 1.0) {
                    System.err.println("Can't log a negative number or 0 or 1");
                    displayErr();
                    return;
                }
                value = BigDecimal.valueOf(Math.log10(value.doubleValue()));
            }
            case "lnButton" ->  {
                if (value.doubleValue() <= 0.0 || value.doubleValue() == 1.0) {
                    System.err.println("Can't ln a negative number or 0 or 1");
                    displayErr();
                    return;
                }
                value = BigDecimal.valueOf(Math.log(value.doubleValue()));
            }
            case "sinButton" -> value = BigDecimal.valueOf(Math.sin(value.doubleValue()));
            case "cosButton" -> value = BigDecimal.valueOf(Math.cos(value.doubleValue()));
            case "tanButton" -> value = BigDecimal.valueOf(Math.tan(value.doubleValue()));
            case "asinButton" -> {
                if (value.doubleValue() < -1.0 || value.doubleValue() > 1.0) {
                    System.err.println("Out of asin domain");
                    displayErr();
                    return;
                }
                value = BigDecimal.valueOf(Math.asin(value.doubleValue()));
            }
            case "acosButton" -> {
                if (value.doubleValue() < -1.0 || value.doubleValue() > 1.0) {
                    System.err.println("Out of acos domain");
                    displayErr();
                    return;
                }
                value = BigDecimal.valueOf(Math.acos(value.doubleValue()));
            }
            case "atanButton" -> value = BigDecimal.valueOf(Math.atan(value.doubleValue()));
            case "radicalThreeButton" -> value = BigDecimal.valueOf(Math.cbrt(value.doubleValue()));
            case "reciprocalButton" -> {
                if (value.doubleValue() == 0.0) {
                    System.err.println("Reciprocal of 0 does not exist");
                    displayErr();
                    return;
                }
                value = division(new BigDecimal("1"), value);
            }
            case "factorialButton" -> {
                if (value.toString().contains(".") || value.doubleValue() < 0.0) {
                    System.err.println("Factorial must be a positive integer");
                    displayErr();
                    return;
                }
                value = BigDecimal.valueOf(factorial(value.doubleValue()));
            }
        }

        if (value.toString().contains(".")) {
            value = round(value);
        }

        onLabel = String.valueOf(value);
        if (hasTooManyDigits(onLabel.length())) {
            System.err.println("too big");
            displayErr();
            return;
        }

        builder = new StringBuilder();
        builder.append(onLabel);
        label.setText(onLabel);
    }

    /**
     * Resets all the variables of the program to default values.
     */
    private void setDefaults() {
        count = new BigDecimal("0");
        temp = new BigDecimal("0");
        operation = 0;
        hasCount = false;
        hasTemp = false;
        onLabel = "0";
        label.setText(onLabel);
        builder = new StringBuilder();
    }

    /**
     * Checks whether the application needs to be reset or not.
     */
    private int status(String onLabel) {
        if (onLabel.equals("err")) {
            System.err.println("resetting");
            setDefaults();
            return -1;
        }
        return 0;
    }

    /**
     * Updates the id to map the current function to be performed.
     */
    private String updateId(ActionEvent e) {
        return ((Node) e.getSource()).getId();
    }

    /**
     * Rounds the number depending on the max digits that are possible on the label
     * in order to show the max precision.
     *
     * @throws IllegalArgumentException if it doesn't have a point
     */
    private BigDecimal round(BigDecimal d) {
        if (!d.toString().contains("."))
            throw new IllegalArgumentException();
        int leftDigits = d.toString().substring(0, d.toString().indexOf('.')).length();
        d = d.setScale(maxDigits - leftDigits - 2, RoundingMode.HALF_UP);
        d = stripDecimalTrailingZeros(d);
        return d;
    }

    /**
     * BigDecimal.stripTrailingZeros removes not only the 0s that are decimal
     * but also the 0s in the integer part of the number.
     * For example: 100 would be 1e+2 where the scale is negative.
     * To avoid this set the scale 0 so no scientific notation happens.
     */
    private BigDecimal stripDecimalTrailingZeros(BigDecimal d) {
        d = d.stripTrailingZeros();
        if (d.scale() < 0)
            d = d.setScale(0, RoundingMode.HALF_UP);
        return d;
    }

    /**
     * Displays "err" on the label which is recognized as error condition.
     * When this condition is met all the other methods will call setDefaults()
     * and return.
     */
    private void displayErr() {
        onLabel = errorString;
        label.setText(onLabel);
    }

    /**
     * Checks if the number has too many digits for the label.
     */
    private boolean hasTooManyDigits(int i) {
        return i > maxDigits;
    }

    /**
     * Updates a with the sum of a and b.
     * @return a
     */
    private BigDecimal addition(BigDecimal a, BigDecimal b) {
        a = a.add(b);
        a = stripDecimalTrailingZeros(a);
        System.out.println("Result: " + a);
        return a;
    }

    /**
     * Updates a with the difference of a and b.
     * @return a
     */
    private BigDecimal subtraction(BigDecimal a, BigDecimal b) {
        a = a.subtract(b);
        a = stripDecimalTrailingZeros(a);
        System.out.println("Result: " + a);
        return a;
    }

    /**
     * Updates a with the product of a and b.
     * @return a
     */
    private BigDecimal multiplication(BigDecimal a, BigDecimal b) {
        a = a.multiply(b);
        if (a.toString().contains("."))
            a = round(a);
        System.out.println("Result: " + a);
        return a;
    }

    /**
     * Updates a with the quotient of a and b.
     * @return a
     */
    private BigDecimal division(BigDecimal a, BigDecimal b) {
        a = a.setScale(15, RoundingMode.HALF_UP);
        a = a.divide(b, RoundingMode.HALF_UP);
        if (a.toString().contains("."))
            a = round(a);
        //dividing 0 by something is a special case that keeps the scale of 15 because
        //it does not contain a point so round is not run on it
        if (a.toString().contains("E") && a.toString().contains("0"))
            a = new BigDecimal("0");
        System.out.println("Result: " + a);
        return a;
    }

    /**
     * Updates a with the power of a and b.
     * @return a
     */
    private BigDecimal exponentiation(BigDecimal a, BigDecimal b) {
        a = a.setScale(15, RoundingMode.HALF_UP);
        a = BigDecimal.valueOf(Math.pow(a.doubleValue(), b.doubleValue()));
        if (a.toString().contains("."))
            a = round(a);
        System.out.println("Result: " + a);
        return a;
    }

    /**
     * Updates a with the bth root of a.
     * @return a
     */
    private BigDecimal rootExtraction(BigDecimal a, BigDecimal b) {
        a = a.setScale(15, RoundingMode.HALF_UP);
        a = BigDecimal.valueOf(Math.pow(a.doubleValue(), 1 / b.doubleValue()));
        if (a.toString().contains("."))
            a = round(a);
        System.out.println("Result: " + a);
        return a;
    }

    /**
     * Updates a with the value of log base b of a.
     * @return a
     */
    private BigDecimal logBaseN(BigDecimal a, BigDecimal b) {
        a = a.setScale(15, RoundingMode.HALF_UP);
        a = BigDecimal.valueOf(Math.log(a.doubleValue()) / Math.log(b.doubleValue()));
        if (a.toString().contains("."))
            a = round(a);
        System.out.println("Result: " + a);
        return a;
    }

    /**
     * Performs factorial using recursion.
     */
    private double factorial(double a) {
        if (a == 0.0)
            return 1.0;
        else
            return (a * factorial(a - 1));
    }
}