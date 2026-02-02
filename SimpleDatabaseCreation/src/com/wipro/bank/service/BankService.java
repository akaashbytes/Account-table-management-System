package com.wipro.bank.service;

import com.wipro.bank.bean.TransferBean;
import com.wipro.bank.dao.BankDAO;
import com.wipro.bank.util.INsufficientFundsException;

public class BankService {

    BankDAO obj = new BankDAO();

    public String checkBalance(String accountNumber) {
        boolean valid = obj.validateAccount(accountNumber);
        if (valid) {
            float balance = obj.findBalance(accountNumber);
            return "BALANCE : " + balance;
        }
        return "ACCOUNT NUMBER INVALID";
    }

    public boolean transfer(TransferBean transferBean)
            throws INsufficientFundsException {

        BankDAO dao = new BankDAO();

        String fromAcc = transferBean.getFromAccountNumber();
        String toAcc = transferBean.getToAccountNumber();
        float amount = transferBean.getAmount();

        // 1. Validate accounts
        if (!dao.validateAccount(fromAcc) || !dao.validateAccount(toAcc)) {
            return false;
        }

        // 2. Check balance
        float fromBalance = dao.findBalance(fromAcc);

        if (fromBalance < amount) {
            throw new INsufficientFundsException();
        }

        // 3. Update balances
        boolean debit = dao.updateBalance(fromAcc, fromBalance - amount);

        float toBalance = dao.findBalance(toAcc);
        boolean credit = dao.updateBalance(toAcc, toBalance + amount);

        // 4. Insert transfer record
        boolean record = dao.transferMoney(transferBean);

        return debit && credit && record;
    }

}