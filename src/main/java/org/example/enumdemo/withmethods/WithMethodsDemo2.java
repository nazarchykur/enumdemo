package org.example.enumdemo.withmethods;

import java.util.EnumMap;
import java.util.Map;

/**
 * Приклад №6: Enum із приватними, публічними та статичними методами.
 *
 * Бізнес-кейс: транспортні тарифи.
 *
 * Кожен вид транспорту має:
 *  - baseRate (базова ціна за км)
 *  - multiplier (множник, що враховує особливості: години пік, комфорт і т.д.)
 *
 * Методи:
 *  - приватний: внутрішній розрахунок реального тарифу (baseRate * multiplier)
 *  - публічний: API calculateCost() для підрахунку ціни поїздки
 *  - статичний: getTariffMap() для побудови EnumMap {TransportType → тариф за км}
 */
public class WithMethodsDemo2 {

    public static void main(String[] args) {
        double distance = 10.0; // км

        // 1️⃣ Використання публічного методу
        System.out.println("1. Trip cost examples:");
        System.out.println("- BUS cost for " + distance + " km = " + TransportType2.BUS.calculateCost(distance));
        System.out.println("- TRAIN cost for " + distance + " km = " + TransportType2.TRAIN.calculateCost(distance));
        System.out.println("- TAXI cost for " + distance + " km = " + TransportType2.TAXI.calculateCost(distance));

        // 2️⃣ Доступ до базових тарифів напряму
        System.out.println("\n2. Base rates:");
        for (TransportType2 type : TransportType2.values()) {
            System.out.println("- " + type.name() + " base rate = " + type.getBaseRate());
        }

        // 3️⃣ Використання статичного методу для отримання EnumMap
        Map<TransportType2, Double> tariffMap = TransportType2.getTariffMap();
        System.out.println("\n3. Final tariff map (baseRate * multiplier):");
        tariffMap.forEach((k, v) -> System.out.println("- " + k.name() + " = " + v + " per km"));

        // 4️⃣ Використовуємо name
        System.out.println("\n4. All available transport types:");
        for (TransportType2 type : TransportType2.values()) {
            System.out.println("- " + type.name());
        }
    }
}

/**
 * Enum TransportType
 *
 * ✅ Поля:
 *   - baseRate: базова ціна за км
 *   - multiplier: коефіцієнт (години пік, комфорт, швидкість)
 *
 * ✅ Методи:
 *   - private double calculateTariff(): інкапсулює формулу (baseRate * multiplier)
 *   - public double calculateCost(distance): API для користувача (використовує приватний метод)
 *   - static Map getTariffMap(): утиліта для швидкого отримання тарифів
 */
enum TransportType2 {
    BUS(2.5, 1.0),
    TRAIN(1.8, 0.9),
    TAXI(10.0, 1.5);

    private final double baseRate;
    private final double multiplier;

    // Конструктор завжди private, викликається JVM для кожної константи
    TransportType2(double baseRate, double multiplier) {
        this.baseRate = baseRate;
        this.multiplier = multiplier;
    }

    // Getter для базової ціни
    public double getBaseRate() {
        return baseRate;
    }

    // Приватний метод — інкапсульована формула
    private double calculateTariff() {
        return baseRate * multiplier;
    }

    // Публічний метод — API для користувача
    public double calculateCost(double distanceKm) {
        return calculateTariff() * distanceKm;
    }

    // Статичний метод — утиліта для побудови EnumMap (швидкий доступ до тарифів)
    public static Map<TransportType2, Double> getTariffMap() {
        EnumMap<TransportType2, Double> map = new EnumMap<>(TransportType2.class);
        for (TransportType2 type : values()) {
            map.put(type, type.calculateTariff());
        }
        return map;
    }
}

/*
📘 Теоретичне пояснення (детальне)
    🔹 Поля
        baseRate → потрібне, щоб мати базову ціну (наприклад, у тарифах чи документації).
        multiplier → показує, що навіть якщо два типи транспорту мають однакову базу, коефіцієнт може змінювати реальний тариф (наприклад, TAXI у години пік).

    🔹 Конструктор
        Завжди private.
        Ініціалізує обидва поля для кожної константи.
        Викликається JVM один раз для BUS, TRAIN, TAXI.

    🔹 Приватний метод
        private double calculateTariff() {
            return baseRate * multiplier;
        }

        Інкапсулює бізнес-формулу.
        Використовується і в calculateCost(), і в getTariffMap().
        Тепер він справді має сенс, бо не просто повертає поле, а виконує розрахунок.

        Коли застосовувати?
            Якщо формулу можна буде змінити в одному місці, і вона не повинна бути доступна напряму.

    🔹 Публічний метод
        public double calculateCost(double distanceKm) {
            return calculateTariff() * distanceKm;
        }

        Це чистий API для користувача → ми даємо готовий результат без зайвих деталей.

        Коли застосовувати?
            Коли ми хочемо сховати внутрішні розрахунки і віддавати тільки результат.

    🔹 Статичний метод
        public static Map<TransportType, Double> getTariffMap()
        Використовується як утиліта для побудови EnumMap.
        Дозволяє швидко отримати карту тарифів без перебору values() щоразу.

        Коли застосовувати?
            Якщо потрібен кешований чи часто використовуваний результат (наприклад, для конфігів, API-відповідей).

📌 Висновки
    Поля = дані, що визначають поведінку enum.
    Конструктор = ініціалізує дані, завжди private, викликається JVM.
    Приватні методи = для інкапсуляції формул чи логіки, яку не варто віддавати напряму.
    Публічні методи = API для користувачів enum, викликають приватні методи і повертають результат.
    Статичні методи = утиліти для роботи з усім enum (пошук, кешування, EnumMap).
 */