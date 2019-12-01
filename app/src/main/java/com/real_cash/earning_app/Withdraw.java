package com.real_cash.earning_app;

public class Withdraw {

    String paymentMethodName;
    String phoneNumber;
    String money;

    public Withdraw() {

    }

    public Withdraw(String paymentMethodName, String phoneNumber, String money) {
        this.paymentMethodName = paymentMethodName;
        this.phoneNumber = phoneNumber;
        this.money = money;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getPaymentMethodName() {
        return paymentMethodName;
    }

    public void setPaymentMethodName(String paymentMethodName) {
        this.paymentMethodName = paymentMethodName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
