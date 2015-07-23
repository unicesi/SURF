package co.icesi.i2t.example.calculator.impl;

import co.icesi.i2t.example.calculator.Calculator;

public class CalculatorImpl implements Calculator {

	@Override
	public int add(int a, int b) {
		int result = a + b;
		return result;
	}

}