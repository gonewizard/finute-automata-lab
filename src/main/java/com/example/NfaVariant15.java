package com.example;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Недетерминированный конечный автомат (НКА) для варианта 15б.
 *
 * Задание: построить НКА, допускающий язык имён переменных в языке Java.
 * Зарезервированные ключевые слова игнорируются.
 *
 * Алфавит (группы символов, для удобства в JFLAP и в коде):
 *   'L' - любая буква (a-z, A-Z)
 *   'D' - любая цифра (0-9)
 *   '_' - символ подчёркивания
 *   '$' - символ доллара
 *
 * Правила языка (имя переменной Java):
 *   - Первый символ - буква, '_' или '$'. Цифра первой быть не может.
 *   - Последующие символы - буквы, цифры, '_' или '$'.
 *
 * Состояния:
 *   q0 - начальное, ожидаем первый символ.
 *   q1 - прочитан первый символ (был буквой / '_' / '$').
 *   q1' - дубль q1, обеспечивает недетерминизм.
 *   qerr - ловушка: первый символ был цифрой, ошибка.
 *
 * Принимающие состояния: q1, q1'.
 * Непринимающие: q0, qerr.
 *
 * Табличное представление НКА:
 *   Каждая ячейка таблицы - массив возможных следующих состояний.
 *   Пустой массив означает отсутствие перехода.
 * Важно: обработка строки ведётся через массив char[] и таблицу переходов.
 * Функции обработки строк в логике распознавания не используются.
 */
public class NfaVariant15 {

    /**
     * Алфавит автомата (4 группы символов).
     * Индекс 0 = 'L', 1 = 'D', 2 = '_', 3 = '$'.
     */
    private static final char[] ALPHABET = {'L', 'D', '_', '$'};

    /**
     * Таблица переходов НКА.
     *
     * Формат: TRANSITIONS[текущееСостояние][индексСимвола] -
     * массив возможных следующих состояний.
     * Пустой массив = перехода нет.
     */
    private static final int[][][] TRANSITIONS = {
            // q0
            {
                    {1, 2},   // по 'L'  -> q1 или q1'
                    {3},      // по 'D'  -> qerr
                    {1, 2},   // по '_'  -> q1 или q1'
                    {1, 2}    // по '$'  -> q1 или q1'
            },
            // q1
            {
                    {1}, {1}, {1}, {1}   // петли на q1 по всем символам
            },
            // q1'
            {
                    {2}, {2}, {2}, {2}   // петли на q1' по всем символам
            },
            // qerr
            {
                    {3}, {3}, {3}, {3}   // петли на qerr по всем символам
            }
    };

    /**
     * Принимающие состояния.
     * Индекс совпадает с номером состояния: 0=q0, 1=q1, 2=q1', 3=qerr.
     */
    private static final boolean[] ACCEPTING = {
            false,  // q0
            true,   // q1
            true,   // q1'
            false   // qerr
    };

    /** Начальное состояние НКА - q0. */
    private static final int INITIAL_STATE = 0;

    /**
     * Определяет индекс символа в алфавите с учётом групп L и D.
     *
     * @param c входной символ
     * @return индекс в ALPHABET или -1, если символ недопустим
     */
    private static int symbolIndex(char c) {
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) return 0;
        if (c >= '0' && c <= '9') return 1;
        if (c == '_') return 2;
        if (c == '$') return 3;
        return -1;
    }

    /**
     * Эмулирует работу НКА на массиве символов.
     *
     * Алгоритм:
     *   1. current = {INITIAL_STATE}.
     *   2. Для каждого символа:
     *      - определить индекс символа;
     *      - если символ недопустим - вернуть false;
     *      - собрать все возможные следующие состояния
     *        из всех текущих состояний.
     *   3. Если хотя бы одно из текущих состояний принимающее - true.
     *
     * @param input строка в виде массива char
     * @return true, если строка принята хотя бы одним путём
     */
    public static boolean run(char[] input) {
        Set<Integer> current = new HashSet<>();
        current.add(INITIAL_STATE);

        for (int i = 0; i < input.length; i++) {
            int idx = symbolIndex(input[i]);
            if (idx == -1) {
                return false;
            }
            Set<Integer> next = new HashSet<>();
            for (int state : current) {
                for (int ns : TRANSITIONS[state][idx]) {
                    next.add(ns);
                }
            }
            current = next;
            if (current.isEmpty()) return false;
        }

        for (int state : current) {
            if (ACCEPTING[state]) return true;
        }
        return false;
    }

    /**
     * Печатает таблицу переходов НКА в консоль.
     *
     * Каждая ячейка содержит список состояний, в которые можно попасть.
     */
    private static void printTable() {
        System.out.println("Таблица переходов НКА");
        System.out.println("Состояние | L       | D       | _       | $");
        System.out.println("----------+---------+---------+---------+--------");
        for (int s = 0; s < TRANSITIONS.length; s++) {
            StringBuilder head = new StringBuilder();
            head.append(ACCEPTING[s] ? "*" : " ");
            head.append(s == INITIAL_STATE ? "->" : "  ");
            head.append(" q").append(s);
            System.out.printf("%-10s| ", head);
            for (int sym = 0; sym < ALPHABET.length; sym++) {
                int[] targets = TRANSITIONS[s][sym];
                if (targets.length == 0) {
                    System.out.print("--      | ");
                } else {
                    StringBuilder cell = new StringBuilder();
                    for (int k = 0; k < targets.length; k++) {
                        if (k > 0) cell.append(",");
                        cell.append("q").append(targets[k]);
                    }
                    System.out.printf("%-8s| ", cell);
                }
            }
            System.out.println();
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
            System.out.print("Введите имя переменной (или 'exit'): ");
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