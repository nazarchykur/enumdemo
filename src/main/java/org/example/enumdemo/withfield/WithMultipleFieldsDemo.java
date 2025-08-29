package org.example.enumdemo.withfield;

import java.util.Arrays;
import java.util.Optional;

/**
 * Приклад №5: Enum із кількома полями (Demo з часовими зонами).
 *
 * ✅ Ідея:
 *   - Кожен елемент enum має кілька полів (region, offset, description)
 *   - Це дозволяє зберігати більше даних для кожної константи
 *   - Є гетери для доступу
 *   - Є метод validateOffset(), щоб перевірити правильність
 *   - Є статичний метод fromRegion(), щоб знайти константу по region
 */
public class WithMultipleFieldsDemo {

    public static void main(String[] args) {
        // 1️⃣ Використання константи
        TimeZoneInfo tz = TimeZoneInfo.EUROPE_KYIV;
        System.out.println("1. Selected zone = " + tz.getRegion());
        System.out.println("   Offset hours = " + tz.getOffsetHours());
        System.out.println("   Description = " + tz.getDescription());

        // 2️⃣ Перевірка offset
        System.out.println("\n2. Validate offset:");
        boolean valid = tz.validateOffset(2); // Kyiv = UTC+2
        System.out.println("   Is offset 2 valid? " + valid);

        // 3️⃣ Використання статичного пошуку by region
        System.out.println("\n3. Find by region:");
        Optional<TimeZoneInfo> found = TimeZoneInfo.fromRegion("America/New_York");
        found.ifPresent(zone -> System.out.println("   Found zone: " + zone.getDescription()));

        // 4️⃣ Перебір усіх констант
        System.out.println("\n4. All available zones:");
        for (TimeZoneInfo zone : TimeZoneInfo.values()) {
            System.out.println("- " + zone.getRegion()
                    + " | offset=" + zone.getOffsetHours()
                    + " | desc=" + zone.getDescription());
        }
    }
}

/**
 * Enum TimeZoneInfo
 *
 * 🔹 Чому тут кілька полів?
 *   - region: технічне ім’я, яке відповідає ZoneId (наприклад, "Europe/Kyiv")
 *   - offsetHours: числове зміщення для швидких обчислень (наприклад, 2 → UTC+2)
 *   - description: зручний для користувача опис
 *
 * 🔹 Конструктор:
 *   - Приймає всі поля й ініціалізує їх для кожної константи
 *   - Завжди private, бо enum створює JVM тільки один раз
 *
 * 🔹 Методи:
 *   - Гетери для доступу до полів
 *   - validateOffset(int offset): перевіряє, чи збігається переданий offset
 *   - fromRegion(String region): статичний метод для пошуку enum по полю region
 */
enum TimeZoneInfo {
    EUROPE_KYIV("Europe/Kyiv", 2, "Kyiv Time (UTC+2)"),
    AMERICA_NEW_YORK("America/New_York", -5, "New York Time (UTC-5)"),
    ASIA_TOKYO("Asia/Tokyo", 9, "Tokyo Time (UTC+9)");

    // 🔹 поля для зберігання даних про часову зону
    private final String region;
    private final int offsetHours;
    private final String description;

    // 🔹 private-конструктор (створюється лише JVM для визначених констант)
    TimeZoneInfo(String region, int offsetHours, String description) {
        this.region = region;
        this.offsetHours = offsetHours;
        this.description = description;
    }

    // 🔹 стандартні getter-и
    public String getRegion() {
        return region;
    }

    public int getOffsetHours() {
        return offsetHours;
    }

    public String getDescription() {
        return description;
    }

    // 🔹 метод для перевірки валідності offset
    public boolean validateOffset(int offset) {
        return this.offsetHours == offset;
    }

    // 🔹 статичний метод для пошуку enum по region
    public static Optional<TimeZoneInfo> fromRegion(String region) {
        return Arrays.stream(values())
                .filter(zone -> zone.region.equalsIgnoreCase(region))
                .findFirst();
    }
}

/*
📘 Теоретичні пояснення
    🔹 Поля
        Використовуємо кілька полів для зберігання різних аспектів однієї сутності (region, offset, description).
        Це робить enum більш “багатим” і ближчим до бізнес-логіки.

    🔹 Конструктор
        Завжди private, бо ми не створюємо enum вручну.
        Викликається JVM для кожної константи.
        Тут він приймає одразу кілька аргументів (region, offset, description).

    🔹 Getter’и
        Єдиний правильний спосіб отримати значення приватних полів.
        Чому getter? → бо ми хочемо інкапсуляцію і можливість додати додаткову логіку в майбутньому.

    🔹 Метод validateOffset
        Використовується для перевірки, чи збігається переданий offset із тим, що зберігається у константі.
        Це демонструє, як enum може містити бізнес-логіку, а не тільки дані.

    🔹 Метод fromRegion
        Повертає enum-константу за полем region.
        Це аналог "пошуку по ключу".
        Використовуємо Optional, щоб уникнути null.
        На практиці такий метод дуже часто потрібен при інтеграціях (отримуємо "Europe/Kyiv" із JSON чи БД → мапимо на enum).

    📌 Висновки
        Enum може мати кілька полів, і це робить його схожим на маленький immutable-клас.
        Конструктор завжди private, викликається тільки JVM для констант.
        Поля читаємо через getter’и → стандартна практика Java.
        Можна додавати методи для бізнес-логіки (validateOffset).
        Використовуємо статичні методи (fromRegion) для пошуку enum по значенням полів.

    👉 Це тепер реальний бізнес-кейс — як тримати довідники (time zones, країни, валюти, ролі тощо) прямо у коді.
*/
