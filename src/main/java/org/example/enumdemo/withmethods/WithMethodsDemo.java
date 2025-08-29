package org.example.enumdemo.withmethods;

import java.util.EnumMap;
import java.util.Map;

/**
 * Приклад №6: Enum із різними методами.
 *
 * ✅ Тут ми демонструємо:
 *   - приватний метод (допоміжна логіка, не видно зовні)
 *   - публічний метод (API для користувачів enum)
 *   - статичний метод (утиліти: пошук, побудова EnumMap)
 *
 * 🔹 Бізнес-кейс: транспортні тарифи
 *   - Кожен вид транспорту має базову ціну
 *   - Є приватний метод для обчислення вартості за кілометр
 *   - Є публічний метод calculateCost(), який викликає приватний
 *   - Є статичний метод getTariffMap(), який повертає EnumMap для швидкого доступу
 */
public class WithMethodsDemo {

    public static void main(String[] args) {
        // 1️⃣ Використання публічного методу
        double distance = 15.0; // км
        double busCost = TransportType.BUS.calculateCost(distance);
        double taxiCost = TransportType.TAXI.calculateCost(distance);
        System.out.println("1. Bus cost = " + busCost);
        System.out.println("1. Taxi cost = " + taxiCost);

        // 2️⃣ Використання статичного методу для отримання мапи тарифів
        Map<TransportType, Double> tariffMap = TransportType.getTariffMap();
        System.out.println("\n2. Tariff map:");
        tariffMap.forEach((k, v) -> System.out.println("- " + k.name() + " = " + v + " per km"));
    }
}

/**
 * Enum TransportType
 *
 * 🔹 Має поле baseRate (ціна за км).
 * 🔹 Використовує приватний метод для внутрішніх обчислень.
 * 🔹 Має публічний метод calculateCost() як API.
 * 🔹 Має статичний метод getTariffMap() для швидкого отримання тарифів у вигляді EnumMap.
 */
enum TransportType {
    BUS(2.5),
    TRAIN(1.8),
    TAXI(10.0);

    private final double baseRate;

    TransportType(double baseRate) {
        this.baseRate = baseRate;
    }

    // 🔹 приватний метод — тільки для внутрішніх розрахунків
    private double calculatePerKm() {
        return baseRate;
    }

    // 🔹 публічний метод — API для користувача
    public double calculateCost(double distanceKm) {
        return calculatePerKm() * distanceKm;
    }

    // 🔹 статичний метод — побудова EnumMap для швидкого доступу до тарифів
    public static Map<TransportType, Double> getTariffMap() {
        EnumMap<TransportType, Double> map = new EnumMap<>(TransportType.class);
        for (TransportType type : values()) {
            map.put(type, type.calculatePerKm());
        }
        return map;
    }
}


/*
📘 Теоретичні пояснення
🔹 1. Приватні методи

    Використовуються для інкапсуляції логіки, яка потрібна тільки всередині enum.
    У прикладі calculatePerKm() приховує деталі розрахунку.
    Ззовні він недоступний, але його використовують публічні методи.

    Коли застосовувати?
        Якщо є повторювана логіка для кількох методів enum.
        Якщо не хочемо, щоб внутрішні деталі "світилися" в API.

🔹 2. Публічні методи

    Це API, яке ми даємо зовнішнім користувачам enum.
    У прикладі calculateCost(double distance) — зрозумілий метод для обчислення вартості поїздки.
    Він викликає приватний метод і повертає результат.

    Коли застосовувати?
        Завжди, коли enum має логіку, якою повинні користуватися інші частини програми.

🔹 3. Статичні методи

    Використовуються як утиліти для роботи з enum у цілому.
    У прикладі getTariffMap() створює EnumMap, де ключ = TransportType, а значення = тариф.
    Це зручно, якщо треба швидко знаходити інформацію без перебору values().

    Коли застосовувати?
        Для пошуку (fromCode, fromName, fromRegion).
        Для побудови структур (EnumMap, списки, сет).
        Для фабрик (create(...) із якихось параметрів).

📌 Висновки
    Приватні методи = інкапсуляція логіки (не видно ззовні).
    Публічні методи = API для користувача enum (чиста точка входу).
    Статичні методи = утиліти для роботи з усім enum (пошук, колекції, фабрики).
    Такий підхід робить enum не просто довідником, а повноцінним міні-класом із власною логікою.
 */