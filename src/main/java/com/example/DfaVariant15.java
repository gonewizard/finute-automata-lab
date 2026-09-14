package com.example;

import java.util.Scanner;

/**
 * Детерминированный конечный автомат (ДКА) для варианта 15а.
 *
 * Задание: построить ДКА в алфавите {a, b}, допускающий
 * все строки, длина которых НЕ кратна 5.
 *
 * Идея: состояние автомата кодирует остаток от деления длины
 * прочитанной части строки на 5. Поскольку всего возможно 5 остатков
 * (0, 1, 2, 3, 4), автомат имеет ровно 5 состояний.
 *
 * Состояния:
 *   q0 — остаток 0 (длина кратна 5). Не принимающее.
 *   q1 — остаток 1. Принимающее.
 *   q2 — остаток 2. Принимающее.
 *   q3 — остаток 3. Принимающее.
 *   q4 — остаток 4. Принимающее.
 *
 * Табличное представление (используется в коде вместо
 * строковых операций — согласно требованию задания):
 *
 * Важно: обработка входной строки ведётся через массив
 * char[] и таблицу переходов. Функции обработки строк
 * (length(), charAt(), substring() и т.п.)
 * в логике распознавания не используются.
 */
public class DfaVariant15 {

    /**
     * Алфавит автомата.
     * Индекс 0 соответствует символу 'a', индекс 1 - символу 'b'.
     */
    private static final char[] ALPHABET = {'a', 'b'};

    /**
     * Таблица переходов ДКА.
     *
     * Формат: TRANSITIONS[текущееСостояние][индексСимвола] = следующееСостояние.
     *
     * Переходы одинаковы для символов 'a' и 'b':
     * из состояния с остатком k автомат переходит в состояние
     * с остатком (k+1) mod 5.
     */
    private static final int[][] TRANSITIONS = {
            //  'a'  'b'   — индекс символа
            {    1,   1 },  // q0 -> q1
            {    2,   2 },  // q1 -> q2
            {    3,   3 },  // q2 -> q3
            {    4,   4 },  // q3 -> q4
            {    0,   0 }   // q4 -> q0
    };

    /**
     * Массив принимающих состояний.
     *
     * ACCEPTING[state] == true, если состояние является принимающим.
     * Принимающими являются все состояния, кроме q0 (остаток 0).
     */
    private static final boolean[] ACCEPTING = {
            false,  // q0 — длина кратна 5, reject
            true,   // q1 — accept
            true,   // q2 — accept
            true,   // q3 — accept
            true    // q4 — accept
    };

    /** Начальное состояние ДКА — q0 (длина 0). */
    private static final int INITIAL_STATE = 0;

    /**
     * Определяет индекс символа в алфавите.
     *
     * @param c входной символ
     * @return индекс символа в массиве ALPHABET,
     *         или -1, если символ не входит в алфавит
     */
    private static int symbolIndex(char c) {
        for (int i = 0; i < ALPHABET.length; i++) {
            if (ALPHABET[i] == c) return i;
        }
        return -1;
    }

    /**
     * Запускает ДКА на массиве символов.
     *
     * Алгоритм:
     *   1. Установить текущее состояние в INITIAL_STATE.
     *   2. Для каждого символа входного массива:
     *      - найти индекс символа в алфавите;
     *      - если символа нет — вывести ошибку и вернуть false;
     *      - перейти в следующее состояние по TRANSITIONS.
     *   3. Вернуть ACCEPTING[текущее состояние].
     *
     * @param input строка в виде массива char
     * @return true, если строка принята (длина не кратна 5),
     *         иначе false
     */
    public static boolean run(char[] input) {
        int state = INITIAL_STATE;
        for (int i = 0; i < input.length; i++) {
            int idx = symbolIndex(input[i]);
            if (idx == -1) {
                System.out.println("Ошибка: символ '" + input[i] + "' не входит в алфавит {a, b}");
                return false;
            }
            state = TRANSITIONS[state][idx];
        }
        return ACCEPTING[state];
    }

    /**
     * Печатает таблицу переходов автомата в консоль.
     *
     * Формат вывода повторяет внешний вид таблицы из JFLAP:
     * символ "->" обозначает начальное состояние,
     * "*" — принимающее.
     */
    private static void printTable() {
        System.out.println("Таблица переходов ДКА");
        System.out.println("Состояние | a | b");
        System.out.println("----------+---+---");
        for (int s = 0; s < TRANSITIONS.length; s++) {
            String mark = (s == INITIAL_STATE ? "->" : "  ")
                    + (ACCEPTING[s] ? "*" : " ");
            System.out.printf("%s q%d    | q%d | q%d%n",
                    mark, s,
                    TRANSITIONS[s][0], TRANSITIONS[s][1]);
        }
        System.out.println();
    }

    /**
     * Точка входа. Запускает цикл чтения строк из консоли и проверки их
     * автоматом. Для выхода введите "exit".
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        printTable();

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("Введите строку из {a, b} (или 'exit'): ");
            String line = sc.nextLine();
            if (line.equals("exit")) break;
            char[] chars = new char[line.length()];
            for (int i = 0; i < line.length(); i++) chars[i] = line.charAt(i);

            boolean accepted = run(chars);
            System.out.println("Результат: " + (accepted ? "ACCEPT" : "REJECT"));
            System.out.println();
        }
    }
}