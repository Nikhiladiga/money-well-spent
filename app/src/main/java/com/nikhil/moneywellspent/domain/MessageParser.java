package com.nikhil.moneywellspent.domain;

import com.fasterxml.uuid.Generators;
import com.nikhil.moneywellspent.entity.Transaction;

public class MessageParser {

    public static Transaction parseMessage(String bank, String message, Long createdAt) {
        if (bank.toLowerCase().contains("axis")) {
            return handleAxisBankTransactionMessage(message, createdAt);
        } else if (bank.toLowerCase().contains("hdfc")) {
            return handleHdfcBankTransactionMessage(message, createdAt);
        } else if (bank.toLowerCase().contains("sbi")) {
            return handleSbiTransactionMessage(message, createdAt);
        } else if (bank.toLowerCase().contains("icici")) {
            return handleIciciTransactionMessage(message, createdAt);
        } else {
            return null;
        }
    }

    private static Transaction handleAxisBankTransactionMessage(String message, Long createdAt) {
        try {
            Transaction transaction = new Transaction();

            //Set bank name
            transaction.setBank("Axis Bank");

            //Set transaction id
            transaction.setId(Generators.timeBasedGenerator().generate().toString());

            //Get transaction type
            if (message.contains("Debit")) {
                transaction.setIsCredit(0);

                //Get transaction amount
                String transactionSeparator = "INR ";
                int transSepPos = message.indexOf(transactionSeparator);
                transaction.setAmount(Double.parseDouble(message.substring(transSepPos + transactionSeparator.length()).split("\n")[0]));

                //Fill transaction date and time
                transaction.setCreatedAt(createdAt);

                //Get payee name
                String payeeString = message.split("\n")[4];
                String[] payeeNameSplitString;
                String payeeName;

                try {
                    payeeNameSplitString = payeeString.split("/");
                    if (payeeNameSplitString.length >= 4) {
                        payeeName = payeeString.split("/")[3];
                    } else if (payeeNameSplitString.length >= 3) {
                        payeeName = payeeString.split("/")[2];
                    } else if (payeeNameSplitString.length >= 2) {
                        payeeName = payeeString.split("/")[1];
                    } else {
                        payeeName = "Unknown";
                    }
                    transaction.setName(payeeName);
                } catch (Exception e) {
                    e.printStackTrace();
                    transaction.setName("Unknown");
                }

                //Set category
                transaction.setCategory("General");

            } else if (message.contains("credited to")) {
                transaction.setIsCredit(1);

                //Get transaction amount
                String transactionAmtPrefixSeparator = "INR";
                String transactionAmtSuffixSeparator = "credited";
                int transactionAmtSepPrefixPos = message.indexOf(transactionAmtPrefixSeparator);
                int transactionAmtSepSuffixPos = message.indexOf(transactionAmtSuffixSeparator);
                transaction.setAmount(Double.valueOf(message.substring(transactionAmtSepPrefixPos + 3, transactionAmtSepSuffixPos).trim()));

                //Fill transaction date and time
                transaction.setCreatedAt(createdAt);

                //Get payee name
                String payeeNamePrefixSeparator = "Info-";
                int payeeNameSepPrefixPos = message.indexOf(payeeNamePrefixSeparator);
                transaction.setName(message.substring(payeeNameSepPrefixPos + 5).split("/")[0].trim());

                //Check if company and set category (Only for me)
                if (message.contains("ACCESS RESEARCH")) {
                    transaction.setCategory("Salary");
                } else {
                    transaction.setCategory("General");
                }
            }

            return transaction;

        } catch (Exception ignored) {
        }
        return null;
    }

    private static Transaction handleSbiTransactionMessage(String message, Long createdAt) {
        try {
            Transaction transaction = new Transaction();

            //Set bank name
            transaction.setBank("SBI");

            //Set transaction id
            transaction.setId(Generators.timeBasedGenerator().generate().toString());

            //Get transaction type
            if (message.contains("debited by")) {
                transaction.setIsCredit(0);
                //Get payee name
                String payeeNamePrefixSeparator = "transfer to ";
                String payeeNameSuffixSeparator = "Ref No ";
                int payeeNameSepPrefixPos = message.indexOf(payeeNamePrefixSeparator);
                int payeeNameSepSuffixPos = message.indexOf(payeeNameSuffixSeparator);
                transaction.setName(message.substring(payeeNameSepPrefixPos + 12, payeeNameSepSuffixPos).trim());

                //Get amount debited
                String debitedAmountSeparatorPrefix = "debited by";
                String debitedAmountSeparatorSuffix = "on ";
                int debitedAmountSepPrefixPos = message.indexOf(debitedAmountSeparatorPrefix);
                int debitedAmountSepSuffixPos = message.indexOf(debitedAmountSeparatorSuffix);
                Double amount = Double.parseDouble((message.substring(debitedAmountSepPrefixPos + 10, debitedAmountSepSuffixPos)).split("Rs")[1]);
                transaction.setAmount(amount);

            } else if (message.contains("credited by")) {
                transaction.setIsCredit(1);

                //Set name as general
                transaction.setName("Unknown");

                //Get amount credited
                String creditedAmountSeparatorPrefix = "credited by";
                String creditedAmountSeparatorSuffix = "on ";
                int creditedAmountSepPrefixPos = message.indexOf(creditedAmountSeparatorPrefix);
                int creditedAmountSepSuffixPos = message.indexOf(creditedAmountSeparatorSuffix);
                Double amount = Double.parseDouble((message.substring(creditedAmountSepPrefixPos + 10, creditedAmountSepSuffixPos)).split("Rs")[1]);
                transaction.setAmount(amount);
            }

            //Fill transaction date and time
            transaction.setCreatedAt(createdAt);
            transaction.setCategory("General");

            return transaction;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Transaction handleHdfcBankTransactionMessage(String message, Long createdAt) {
        try {
            Transaction transaction = new Transaction();

            //Set bank name
            transaction.setBank("HDFC Bank");

            //Set transaction id
            transaction.setId(Generators.timeBasedGenerator().generate().toString());

            if (message.contains("debited from")) {
                transaction.setIsCredit(0);

                //Get payee name
                String payeeNamePrefixSeparator = "VPA";
                String payeeNameSuffixSeparator = "@";
                int payeeNameSepPrefixPos = message.indexOf(payeeNamePrefixSeparator);
                int payeeNameSepSuffixPos = message.indexOf(payeeNameSuffixSeparator);
                try {
                    transaction.setName(message.substring(payeeNameSepPrefixPos + 3, payeeNameSepSuffixPos).trim());
                } catch (StringIndexOutOfBoundsException e) {
                    transaction.setName("Unknown");
                }

                //Get amount debited
                if (message.contains("Rs.")) {
                    String amountDebitedPrefixSeparator = "Rs. ";
                    String amountDebitedSuffixSeparator = "debited";
                    int amountDebitedSepPrefixPos = message.indexOf(amountDebitedPrefixSeparator);
                    int amountDebitedSepSuffixPos = message.indexOf(amountDebitedSuffixSeparator);
                    transaction.setAmount(Double.valueOf(message.substring(amountDebitedSepPrefixPos + 3, amountDebitedSepSuffixPos)));
                } else if (message.contains("Rs")) {
                    String amountDebitedPrefixSeparator = "Rs ";
                    String amountDebitedSuffixSeparator = "debited";
                    int amountDebitedSepPrefixPos = message.indexOf(amountDebitedPrefixSeparator);
                    int amountDebitedSepSuffixPos = message.indexOf(amountDebitedSuffixSeparator);
                    transaction.setAmount(Double.valueOf(message.substring(amountDebitedSepPrefixPos + 3, amountDebitedSepSuffixPos)));
                } else if (message.contains("INR")) {
                    String amountDebitedPrefixSeparator = "INR ";
                    String amountDebitedSuffixSeparator = "debited";
                    int amountDebitedSepPrefixPos = message.indexOf(amountDebitedPrefixSeparator);
                    int amountDebitedSepSuffixPos = message.indexOf(amountDebitedSuffixSeparator);
                    StringBuilder stringBuilder = new StringBuilder(message.substring(amountDebitedSepPrefixPos + 4, amountDebitedSepSuffixPos));
                    stringBuilder.deleteCharAt(stringBuilder.indexOf(","));
                    transaction.setAmount(Double.valueOf(stringBuilder.toString()));
                }


            } else if (message.contains("credited to")) {
                transaction.setIsCredit(1);

                //Set payee name
                String payeeNamePrefixSeparator = "VPA";
                String payeeNameSuffixSeparator = "@";
                int payeeNameSepPrefixPos = message.indexOf(payeeNamePrefixSeparator);
                int payeeNameSepSuffixPos = message.indexOf(payeeNameSuffixSeparator);
                try {
                    transaction.setName(message.substring(payeeNameSepPrefixPos + 3, payeeNameSepSuffixPos).trim());
                } catch (StringIndexOutOfBoundsException e) {
                    System.out.println(message);
                }

                //Get amount credited
                String amountCreditedPrefixSeparator = "Rs. ";
                String amountCreditedSuffixSeparator = "credited";
                int amountCreditedSepPrefixPos = message.indexOf(amountCreditedPrefixSeparator);
                int amountCreditedSepSuffixPos = message.indexOf(amountCreditedSuffixSeparator);
                transaction.setAmount(Double.valueOf(message.substring(amountCreditedSepPrefixPos + 4, amountCreditedSepSuffixPos)));
            }

            //Fill transaction date and time
            transaction.setCreatedAt(createdAt);
            transaction.setCategory("General");

            return transaction;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Transaction handleIciciTransactionMessage(String message, Long createdAt) {
        try {
            Transaction transaction = new Transaction();

            //Set bank name
            transaction.setBank("ICICI Bank");

            //Set transaction id
            transaction.setId(Generators.timeBasedGenerator().generate().toString());

            if (message.contains("debited for")) {
                transaction.setIsCredit(0);

                //Get payee name
                String payeeNamePrefixSeparator = ";";
                String payeeNameSuffixSeparator = "credited";
                int payeeNameSepPrefixPos = message.indexOf(payeeNamePrefixSeparator);
                int payeeNameSepSuffixPos = message.indexOf(payeeNameSuffixSeparator);
                transaction.setName(message.substring(payeeNameSepPrefixPos + 1, payeeNameSepSuffixPos).trim());

                //Get amount debited
                String amountDebitedPrefixSeparator = "Rs ";
                String amountDebitedSuffixSeparator = "on";
                int amountDebitedSepPrefixPos = message.indexOf(amountDebitedPrefixSeparator);
                int amountDebitedSepSuffixPos = message.indexOf(amountDebitedSuffixSeparator);
                transaction.setAmount(Double.valueOf(message.substring(amountDebitedSepPrefixPos + 3, amountDebitedSepSuffixPos)));


            } else if (message.contains("credited with")) {
                transaction.setIsCredit(1);

                //Set name as unknown
                String payeeNamePrefixSeparator = "from ";
                String payeeNameSuffixSeparator = ". ";
                int payeeNameSepPrefixPos = message.indexOf(payeeNamePrefixSeparator);
                int payeeNameSepSuffixPos = message.indexOf(payeeNameSuffixSeparator);
                transaction.setName(message.substring(payeeNameSepPrefixPos + 5, payeeNameSepSuffixPos).trim());

                //Get amount credited
                String amountCreditedPrefixSeparator = "Rs ";
                String amountCreditedSuffixSeparator = "on";
                int amountCreditedSepPrefixPos = message.indexOf(amountCreditedPrefixSeparator);
                int amountCreditedSepSuffixPos = message.indexOf(amountCreditedSuffixSeparator);
                transaction.setAmount(Double.valueOf(message.substring(amountCreditedSepPrefixPos + 4, amountCreditedSepSuffixPos)));
            }

            //Fill transaction date and time
            transaction.setCreatedAt(createdAt);
            transaction.setCategory("General");

            return transaction;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
