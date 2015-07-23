package co.icesi.i2t.example.consumer;

import co.icesi.i2t.example.calculator.Calculator;

public class CalculatorConsumer {

    Calculator calcService;

    public synchronized void onBind(Calculator calcService) {
        this.calcService = calcService;
        System.out.println(this.add(13, 29));
    }

    public synchronized void onUnbind(Calculator calcService) {
        if (this.calcService == calcService) {
            this.calcService = null;
            }
    }

    public int add(int a, int b) {
        return this.calcService.add(a, b);
    }

}