package org.example;

import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/inf.informatik.haw-hamburg.de",
                "db_2676928",
                "FM7SATRAX1PGMV"
        );
        menu(connection);
    }

    /**
     * Opens a menu for the user
     * @param connection to Oracle
     * @throws SQLException Fehler
     */
    public static void menu(Connection connection) throws SQLException {
        Scanner input = new Scanner(System.in);
        System.out.println("\nWas möchten Sie tun? \nEinen Ehemaligen hinzufügen [1] \nAlle Ehemaligen anzeigen [2]\nAlle Ehemaligen einer bestimmten Hochschule mit eine bestimmten Fach [3]\nDas Programm schließen [q]");
        String answer = checkMenuInput(input);
        if (answer.equals("1")){
            addEhemaliger(connection);
        }
        if (answer.equals("2")){
            showEhemalige(connection);
        }
        if (answer.equals("3")){
            showEhemalige(connection, "HAW", "IT");
        }
        if (answer.equals("q")) {
            connection.close();
            System.out.println("\nAuf wiedersehen :)");
        }
    }

    public static String checkMenuInput(Scanner input){
        String answer = input.nextLine();
        if (answer.matches("[123q]")){
            return answer;
        }
        else  {
            System.out.println("\nBitte geben Sie eine gültige Auswahl ein");
            return checkMenuInput(input);
        }
    }

    public static void showEhemalige(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM EHEMALIGE");
        ResultSet ehemalige = preparedStatement.executeQuery();
        while (ehemalige.next()){
            String ehemaliger = String.format("" +
                            "\nMatrikelnummer: %s " +
                            "\nVorname: %s " +
                            "\nNachname: %s " +
                            "\nGeburtsdatum: %s " +
                            "\nGeschlecht: %s " +
                            "\nE-Mail: %s " +
                            "\nTelefonnummer: %s",
                    ehemalige.getString("MATRIKELNUMMER"),
                    ehemalige.getString("VORNAME"),
                    ehemalige.getString("NACHNAME"),
                    ehemalige.getString("GEBURTSDATUM"),
                    ehemalige.getString("GESCHLECHT"),
                    ehemalige.getString("EMAIL"),
                    ehemalige.getString("TELEFONNUMMER"));
            System.out.printf("\n\t%s ", ehemaliger);
        }
        System.out.println("\n\n");
        menu(connection);
    }

    public static void showEhemalige(Connection connection, String hochschulenname, String fach) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement
                ("SELECT * FROM EHEMALIGE e INNER JOIN HAT_BELEGT_STUDIENGAENGE hbs ON e.MATRIKELNUMMER = hbs.MATRIKELNUMMER INNER JOIN STUDIENGAENGE s ON hbs.KUERZEL = s.KUERZEL WHERE e.MATRIKELNUMMER IN (SELECT MATRIKELNUMMER FROM HAT_BESUCHT_HOCHSCHULE hbh WHERE hbh.HOCHSCHULNAME = ?) AND s.KUERZEL = ?");
        preparedStatement.setString(1, hochschulenname);
        preparedStatement.setString(2, fach);
        ResultSet ehemalige = preparedStatement.executeQuery();

        while (ehemalige.next()){
            String ehemaliger = String.format("" +
                            "\nMatrikelnummer: %s " +
                            "\nVorname: %s " +
                            "\nNachname: %s " +
                            "\nGeburtsdatum: %s " +
                            "\nGeschlecht: %s " +
                            "\nE-Mail: %s " +
                            "\nTelefonnummer: %s",
                    ehemalige.getString("MATRIKELNUMMER"),
                    ehemalige.getString("VORNAME"),
                    ehemalige.getString("NACHNAME"),
                    ehemalige.getString("GEBURTSDATUM"),
                    ehemalige.getString("GESCHLECHT"),
                    ehemalige.getString("EMAIL"),
                    ehemalige.getString("TELEFONNUMMER"));
            System.out.printf("\n\t%s ", ehemaliger);
        }
        System.out.println("\n\n");
        menu(connection);
    }

    /**
     * Fügt mit Hilfe von Nutzereingaben neue Ehemalige in die Datenbank ein
     * @param connection Verbindung zu Oracle
     * @throws SQLException Fehlermeldung
     */
    public static void addEhemaliger(Connection connection) throws SQLException {

        //GET USER INPUT
        Scanner input = new Scanner(System.in);
        System.out.println("\nVorname: \n");
        String vorname = name(input);
        System.out.println("\nNachname: \n");
        String nachname = name(input);
        String geburtsdatum = geburtsdatum(input);
        String geschlecht = geschlecht(input);
        String email = email(input);
        String telefonnummer = telefonnummer(input);
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT into EHEMALIGE (VORNAME, NACHNAME, GEBURTSDATUM, GESCHLECHT, EMAIL, TELEFONNUMMER) VALUES (?, ?, ?, ?, ?, ?) ");
        preparedStatement.setString(1, vorname);
        preparedStatement.setString(2, nachname);
        preparedStatement.setString(3, geburtsdatum);
        preparedStatement.setString(4, geschlecht);
        preparedStatement.setString(5, email);
        preparedStatement.setString(6, telefonnummer);
        preparedStatement.executeQuery();

        menu(connection);
    }



// HILFSMETHODEN ZUM ÜBERPRÜFEN DER NUTZEREINGABEN

    public static String name(Scanner input){
        String name = input.nextLine();
        if (name.matches("[a-z, A-Z]+")){
            return name;
        }
        else {
            System.out.println("\nBitte geben Sie einen Namen ein!");
            return name(input);
        }
    }

    public static String geburtsdatum(Scanner input){
        System.out.println("\nDatum [TT-MM-JJ]: \n");
        String geburtsdatum = input.nextLine();
        if (geburtsdatum.matches("\\d{2}-1[0-2]-\\d{2}")||geburtsdatum.matches("\\d{2}-0[1-9]-\\d{2}")){
            return geburtsdatum;
        }
        else {
            System.out.println("\nBitte bachten Sie das Format TT-MM-JJ!");
            return geburtsdatum(input);
        }
    }

    public static String geschlecht(Scanner input){
        System.out.println("\nGeschlecht [m|w|d]");
        String geschlecht = input.nextLine();
        if (geschlecht.matches("[mwd]?")){
            return geschlecht;
        }
        else {
            System.out.println("\nBitte geben Sie ein gültige Antwort oder überspringen Sie die Eingabe!");
            return email(input);
        }
    }

    public static String email(Scanner input){
        System.out.println("\nEmail: \n");
        String email = input.nextLine();
        if (email.matches("[0-9a-zA-Z.]+@[0-9a-zA-Z]+\\.[0-9a-zA-Z]+")){
            return email;
        }
        else {
            System.out.println("\nBitte geben Sie eine gültige Email ein!");
            return email(input);
        }
    }

    public static String telefonnummer(Scanner input){
        System.out.println("\nTelefonnummer: \n");
        String telefonnummer = input.nextLine();
        if (telefonnummer.matches("[0-9]+")){
            return telefonnummer;
        }
        else {
            System.out.println("\nBitte geben Sie eine gültige Telefonnummer ein!");
            return telefonnummer(input);
        }
    }

//       public static String notNull(Scanner input){
//        String info = input.nextLine();
//        if(!(info.trim().equals(""))){
//            return info.trim();
//        }
//        else {
//            System.out.println("\nIhre Eingabe war leer! Bitte geben Sie etwas ein!\n");
//            return notNull(input);
//        }
//    }

}