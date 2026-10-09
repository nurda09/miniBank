package org.example;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;
import java.util.Scanner;

public class BankingCLI {

    private final Service service;
    private final Scanner in = new Scanner(System.in);

    public BankingCLI(Service service) {
        this.service = service;
    }



    public void run(){
        boolean is_running = true;
        printMenu();
        String choice = in.next().trim();

        try{
            switch (choice){
                case "1" -> openAccount();
                case "2" -> checkBalance();
                case "3" -> depositBalance();
                case "4" -> withdraw();
                case "5" -> transfer();
                case "6" -> showHistory();
                case "0" -> is_running = false;
                default -> System.out.println("Неизвестная команда");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


    public void printMenu(){
        System.out.println("""
            
            === Банк ===
            1. Открыть счёт
            2. Проверить баланс
            3. Пополнить
            4. Снять
            5. Перевести
            6. История операций
            0. Выход
            Выбери действие:""");
    }

    private void openAccount(){
        System.out.println("Какое имя владельца выберете?");
        String ownerName = in.next();
        System.out.println("Баланс вашего аккаунта?");
        double balance = in.nextInt();
        System.out.println("---------------");
        Account account = service.openAccount(ownerName, balance);
        System.out.println("Создан аккаунт: " + ownerName);
    }

    private void checkBalance() throws AccountNotFoundException {
        int id = readAccount();
        System.out.println("Идет проверка аккаунта");
        System.out.println("Ваш баланс = " + service.getAccount(id).getBalance());
    }

    private void depositBalance() throws  AccountNotFoundException{
        int id = readAccount(); // запрашивает айдишку без наших усилий
        System.out.println("Сумма: ");
        double amount = in.nextDouble();
        service.deposit(id, amount);
        System.out.println("Успешно пополнено");
    }

    private void withdraw() throws AccountNotFoundException{
        int id = readAccount();
        System.out.println("Сумма: ");
        double amount = in.nextDouble();
        service.withdraw(id, amount);
        System.out.println("Успешно выведено");
    }

    private void transfer(){
        System.out.println("Счет отправителя: ");
        int fromId = readAccount();
        System.out.println("Счет получателя: ");
        int toId = readAccount();
        System.out.println("Сумма: ");
        double amount = in.nextDouble();

        service.transfer(fromId,toId,amount);
        System.out.println("Перевод выполнен");
    }

    public void showHistory() throws AccountNotFoundException {
        int id = readAccount();
        List<Transactions> history = service.getHistoryById(id);
        System.out.println(history);
    }


    private int readAccount(){
        System.out.println("Id счета: ");
        return in.nextInt();
    }

}
